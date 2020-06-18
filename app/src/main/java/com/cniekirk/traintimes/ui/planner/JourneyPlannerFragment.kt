package com.cniekirk.traintimes.ui.planner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.afollestad.materialdialogs.list.customListAdapter
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentJourneyPlannerBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.journeyplanner.req.Railcard
import com.cniekirk.traintimes.utils.viewBinding
import com.cniekirk.traintimes.ui.adapter.PassengerAdapter
import com.cniekirk.traintimes.ui.adapter.RailcardAdapter
import com.cniekirk.traintimes.ui.viewmodel.JourneyPlannerViewModel
import com.cniekirk.traintimes.ui.viewmodel.JourneyPlannerViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import java.util.*
import javax.inject.Inject

class JourneyPlannerFragment: Fragment(R.layout.fragment_journey_planner), Injectable,
    RailcardAdapter.RailcardClickListener, PassengerAdapter.OnPassengerClickedListener {

    companion object {
        fun newInstance() = JourneyPlannerFragment()
    }

    @Inject
    lateinit var viewModelFactory: JourneyPlannerViewModelFactory

    private val binding by viewBinding(FragmentJourneyPlannerBinding::bind)
    private val viewModel: JourneyPlannerViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private val railcardCodes by lazy(LazyThreadSafetyMode.NONE) { resources.getStringArray(R.array.railcard_codes).toList() }
    private val passengerTypes by lazy(LazyThreadSafetyMode.NONE) { resources.getStringArray(R.array.passenger_types).toList() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_journey_planner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Not really the proper way to use a chip but looks nice
        binding.datetimeChip.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker(requireFutureDateTime = true) { _, dateTime ->
                    viewModel.saveDatetime(dateTime)
                }
            }
        }

        binding.returnDatetimeChip.setOnClickListener {
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                dateTimePicker(requireFutureDateTime = true) { _, dateTime ->
                    viewModel.saveReturnDatetime(dateTime)
                }
            }
        }

        binding.railcardChip.setOnClickListener {
            viewModel.clearRailcards()
            val dlg = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
                .customView(R.layout.custom_railcard_selection)

            val railcardView = dlg.getCustomView()

            railcardView.findViewById<MaterialButton>(R.id.btn_railcard_ok).setOnClickListener {
                if (viewModel.railcards.value!!.isNotEmpty()) {
                    val numRailcards = viewModel.railcards.value?.map { railcard -> railcard.count }?.sum()
                    binding.railcardChip.text = String.format(getString(R.string.railcards_chip_text), numRailcards)
                }
                dlg.dismiss()
            }

            railcardView.findViewById<MaterialButton>(R.id.btn_railcard_cancel).setOnClickListener {
                // Clear the LiveData field in the ViewModel
                viewModel.railcards.postValue(mutableListOf())
                binding.railcardChip.text = getString(R.string.default_railcard)
                dlg.dismiss()
            }

            val railcardList = railcardView.findViewById<RecyclerView>(R.id.railcard_list)
            val railcardDescriptions = resources.getStringArray(R.array.railcards).toList()
            val railcardAdapter = RailcardAdapter(railcardDescriptions, this)

            railcardList.layoutManager = LinearLayoutManager(requireContext())
            railcardList.adapter = railcardAdapter

            dlg.show()
        }

        binding.passengersChip.setOnClickListener {
            viewModel.clearPassengers()
            MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                val passengerAdapter = PassengerAdapter(passengerTypes, this@JourneyPlannerFragment)
                customListAdapter(passengerAdapter)
                title(R.string.passenger_select_title)
                positiveButton {
                    var numPassengers = 0
                    viewModel.adults.value?.let {  adults ->
                        viewModel.children.value?.let { children ->
                            numPassengers = adults + children
                        } ?: run { numPassengers = adults }
                    } ?: run { viewModel.children.value?.let { children -> numPassengers = children } }

                    if (numPassengers > 0) {
                        binding.passengersChip.text = String.format(getString(R.string.passenger_text), numPassengers)
                    }
                    dismiss()
                }
                negativeButton {
                    binding.passengersChip.text = getString(R.string.default_passengers)
                    dismiss()
                }
            }
        }

        binding.searchSelectDepStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDepStation
                    to getString(R.string.dep_search_transition))
            val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
            enterTransition = backward

            val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
            exitTransition = forward
            view.findNavController().navigate(R.id.plannerStationSearchFragment,
                bundleOf("isDeparture" to true), null, extras)
        }

        binding.searchSelectDestStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDestStation
                    to getString(R.string.dep_search_transition))
            val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
            enterTransition = backward

            val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
            exitTransition = forward
            view.findNavController().navigate(R.id.plannerStationSearchFragment,
                bundleOf("isDeparture" to false), null, extras)
        }

        binding.checkboxAddReturn.setOnCheckedChangeListener { _, isChecked ->
            binding.returnDatetimeChip.visibility = if (isChecked) View.VISIBLE else View.GONE
            binding.returnTitle.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.checkboxDirectTrains.setOnCheckedChangeListener { _, isChecked ->
            viewModel.directTrainsOnly.postValue(isChecked)
        }

        binding.btnJourneyPlan.setOnClickListener {
            viewModel.getJourneyPlan()
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            view.findNavController().navigate(R.id.journeyPlannerResultsFragment,
                null, null, null)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Failure.NoDestinationFailure -> {
                    Snackbar.make(binding.root, R.string.error_no_destination, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
                is Failure.MoreRailcardsThanPassengersError -> {
                    Snackbar.make(binding.root, R.string.error_more_railcards, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
            }
        })

        viewModel.chipDateTime.observe(viewLifecycleOwner, Observer {
            binding.datetimeChip.text = it
        })

        viewModel.returnChipDateTime.observe(viewLifecycleOwner, Observer {
            binding.returnDatetimeChip.text = it
        })

        viewModel.depStation.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.searchArrowDep.setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_right, null))
                binding.searchDepText.text = getString(R.string.departing_from)
                binding.searchArrowDep.setOnClickListener(null)
            } else {
                if (binding.searchDepText.text.toString().equals(getString(R.string.departing_from), false)) {
                    binding.searchArrowDep.setImageDrawable(resources.getDrawable(R.drawable.ic_clear, null))
                    binding.searchDepText.text = it.crs
                    binding.searchArrowDep.setOnClickListener {
                        viewModel.clearDepStation()
                    }
                }
            }
        })

        viewModel.destStation.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.searchArrowDest.setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_right, null))
                binding.searchDestText.text = getString(R.string.arriving_at_planner)
                binding.searchArrowDest.setOnClickListener(null)
            } else {
                if (binding.searchDestText.text.toString().equals(getString(R.string.arriving_at_planner), false)) {
                    binding.searchArrowDest.setImageDrawable(resources.getDrawable(R.drawable.ic_clear, null))
                    binding.searchDestText.text = it.crs
                    binding.searchArrowDest.setOnClickListener {
                        viewModel.clearDestStation()
                    }
                }
            }
        })

        viewModel.depStationText()?.let { savedDepStation ->
            viewModel.crsStationCodes.observe(viewLifecycleOwner, Observer { crsList ->
                val crs = crsList.find { crs -> crs.crs.equals(savedDepStation, true) }
                crs?.let {
                    viewModel.saveDepStation(crs)
                }
            })
            viewModel.getCrsCodes()
            binding.searchDepText.text = savedDepStation
            binding.searchArrowDep.setImageDrawable(resources.getDrawable(R.drawable.ic_clear, null))
            binding.searchArrowDep.setOnClickListener {
                viewModel.clearDepStation()
            }
        } ?: run {
            binding.searchArrowDep.setImageDrawable(resources.getDrawable(R.drawable.ic_keyboard_arrow_right, null))
            binding.searchDepText.text = getString(R.string.departing_from)
            binding.searchArrowDep.setOnClickListener(null)
        }

        viewModel.saveDatetime(Calendar.getInstance(Locale.ENGLISH))

        val date: String? = viewModel.handle[getString(R.string.date_key)]
        date?.let { binding.datetimeChip.text = it }
    }

    // Update the railcard status in the ViewModel
    override fun onClick(position: Int, isDecrement: Boolean) {
        if (isDecrement) {
            viewModel.updateRailcards(railcardCodes[position], -1)
        } else {
            viewModel.updateRailcards(railcardCodes[position], 1)
        }
    }

    override fun onPassengerClick(position: Int, isDecrement: Boolean) {
        viewModel.updatePassengers(passengerTypes[position], isDecrement)
    }

}