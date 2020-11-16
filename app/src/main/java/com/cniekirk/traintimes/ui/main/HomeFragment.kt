package com.cniekirk.traintimes.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentHomeBinding
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.ui.adapter.RecentQueriesAdapter
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
    RecentQueriesAdapter.RecentQueryClickListener {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
        reenterTransition = backward

        val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
        exitTransition = forward

        enterTransition = forward
        returnTransition = backward
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.connectionStateEmitter.observe(viewLifecycleOwner, { connected ->
            connected?.let {
                if (connected) {
                    val sb = Snackbar.make(binding.root, R.string.reconnected, Snackbar.LENGTH_SHORT)
                    sb.anchorView = binding.snackbarLocation
                    sb.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                    sb.show()
                } else {
                    val sb = Snackbar.make(binding.root, R.string.disconnected, Snackbar.LENGTH_LONG)
                    sb.anchorView = binding.snackbarLocation
                    sb.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorRed))
                    sb.show()
                }
            }
        })

        viewModel.depStation.observe(viewLifecycleOwner, {
            if (it == null) {
                binding.searchArrowDep.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_keyboard_arrow_right, null))
                binding.searchDepText.text = getString(R.string.departing_from)
                binding.searchArrowDep.setOnClickListener(null)
                if (viewModel.destStation.value == null) {
                    binding.btnSwapStations.visibility = View.GONE
                }
            } else {
                if (!binding.btnSwapStations.isVisible)
                    binding.btnSwapStations.visibility = View.VISIBLE
                binding.searchArrowDep.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_clear, null))
                binding.searchDepText.text = it.stationName
                binding.searchArrowDep.setOnClickListener {
                    viewModel.clearDepStation()
                }
            }
        })

        viewModel.destStation.observe(viewLifecycleOwner, {
            if (it == null) {
                binding.searchArrowDest.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_keyboard_arrow_right, null))
                binding.searchDestText.text = getString(R.string.arriving_at)
                binding.searchArrowDest.setOnClickListener(null)
                if (viewModel.depStation.value == null) {
                    binding.btnSwapStations.visibility = View.GONE
                }
            } else {
                if (!binding.btnSwapStations.isVisible)
                    binding.btnSwapStations.visibility = View.VISIBLE
                binding.searchArrowDest.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_clear, null))
                binding.searchDestText.text = it.stationName
                binding.searchArrowDest.setOnClickListener {
                    viewModel.clearDestStation()
                }
            }
        })

        viewModel.depStationText()?.let { savedDepStation ->
            viewModel.crsStationCodes.observe(viewLifecycleOwner, { crsList ->
                val crs = crsList.find { crs -> crs.crs.equals(savedDepStation, true) }
                crs?.let {
                    viewModel.saveDepStation(crs)
                }
            })
            viewModel.getCrsCodes()
            binding.searchDepText.text = savedDepStation
            binding.searchArrowDep.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_clear, null))
            binding.searchArrowDep.setOnClickListener {
                viewModel.clearDepStation()
            }
        } ?: run {
            binding.searchArrowDep.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_keyboard_arrow_right, null))
            binding.searchDepText.text = getString(R.string.departing_from)
            binding.searchArrowDep.setOnClickListener(null)
        }

        viewModel.recentQueries.observe(viewLifecycleOwner, {
            // Create adapter etc.
            binding.emptyHomeText.visibility = View.GONE
            binding.homeRecentSearches.visibility = View.VISIBLE
            binding.recentSearchList.layoutManager = LinearLayoutManager(requireContext())
            binding.recentSearchList.adapter = RecentQueriesAdapter(it, this)
        })

        viewModel.canProceedToSearch.observe(viewLifecycleOwner, { canSearch ->
            if (canSearch) {
                binding.root.findNavController().navigate(R.id.depBoardResultsFragment,
                    bundleOf("isFromSearch" to true))
            } else {
                // Snackbar error
                Snackbar.make(binding.rootMotion, R.string.no_stations_selected, Snackbar.LENGTH_SHORT).apply {
                    anchorView = binding.snackbarLocation
                    setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorRed))
                }.show()
            }
        })

        viewModel.failure.observe(viewLifecycleOwner, {
            when (it) {
                is Failure.NoRecentQueriesFailure -> {
                    // Log maybe?
                    Timber.e("No recent queries")
                }
                else -> {
                    Timber.e("Unknown error observing failure state")
                }
            }
        })

        viewModel.getRecentSearches()

//        exitTransition = Hold().apply { duration = 270 }
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchSelectDepStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDepStation
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.stationSearchFragment,
                bundleOf("isDeparture" to true), null, extras)
        }

        binding.searchSelectDestStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDestStation
                    to getString(R.string.dep_search_transition))
            view.findNavController().navigate(R.id.stationSearchFragment,
                bundleOf("isDeparture" to false), null, extras)
        }

        binding.searchButton.setOnClickListener {
            // Only if it's possible
            viewModel.attemptServiceSearch()
        }

        binding.homeBtnSettings.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        binding.btnSwapStations.setOnClickListener {
            viewModel.depStation.value?.let {
                viewModel.saveDestStation(it)
                if (viewModel.destStation.value == null) {
                    viewModel.clearDepStation()
                }
            }
            viewModel.destStation.value?.let {
                viewModel.saveDepStation(it)
                if (viewModel.depStation.value == null) {
                    viewModel.clearDestStation()
                }
            }
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recentSearchList.layoutManager = layoutManager
        binding.recentSearchList.adapter = RecentQueriesAdapter(emptyList(), this)
        binding.recentSearchList.addItemDecoration(DividerItemDecoration(recent_search_list.context, layoutManager.orientation))


    }

    // Recent queries
    override fun onClick(position: Int) =
        binding.root.findNavController().navigate(R.id.depBoardResultsFragment,
            bundleOf("recentQueries" to position, "isFromSearch" to true))

}
