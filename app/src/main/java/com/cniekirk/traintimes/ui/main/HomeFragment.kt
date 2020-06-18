package com.cniekirk.traintimes.ui.main

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentHomeBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.ui.adapter.DepartureListAdapter
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import com.cniekirk.traintimes.utils.viewBinding
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : Fragment(R.layout.fragment_home), Injectable,
    DepartureListAdapter.DepartureItemClickListener {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    private val avd by lazy(LazyThreadSafetyMode.NONE) { binding.loadingIndicator.drawable as AnimatedVectorDrawable }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.services.observe(viewLifecycleOwner, Observer { service ->
            val depAdapter =
                DepartureListAdapter(
                    service,
                    this
                )
            binding.homeServicesList.adapter = depAdapter
            postponeEnterTransition()
            binding.homeServicesList.viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
            avd.clearAnimationCallbacks()
            avd.stop()
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
                binding.searchDestText.text = getString(R.string.arriving_at)
                binding.searchArrowDest.setOnClickListener(null)
            } else {
                if (binding.searchDestText.text.toString().equals(getString(R.string.arriving_at), false)) {
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

        viewModel.failure.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Failure.NoCrsFailure -> {
                    avd.clearAnimationCallbacks()
                    avd.stop()
                    Snackbar.make(binding.root, "No station selected!", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(resources.getColor(R.color.colorRed, null))
                        .show()
                }
            }
        })

//        exitTransition = Hold().apply { duration = 270 }
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("HOME", "Keys: ${viewModel.handle.keys()}")

        binding.homeServicesList.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.homeServicesList.layoutManager = layoutManager
        binding.homeServicesList.adapter =
            DepartureListAdapter(
                emptyList(),
                this
            )
        binding.homeServicesList.addItemDecoration(DividerItemDecoration(home_services_list.context, layoutManager.orientation))

        binding.searchSelectDepStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDepStation
                    to getString(R.string.dep_search_transition))
            val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
            enterTransition = backward

            val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
            exitTransition = forward
            view.findNavController().navigate(R.id.stationSearchFragment,
                bundleOf("isDeparture" to true), null, extras)
        }

        binding.searchSelectDestStation.setOnClickListener {
            val extras = FragmentNavigatorExtras(binding.searchSelectDestStation
                    to getString(R.string.dep_search_transition))
            val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
            enterTransition = backward

            val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
            exitTransition = forward
            view.findNavController().navigate(R.id.stationSearchFragment,
                bundleOf("isDeparture" to false), null, extras)
        }

        binding.searchButton.setOnClickListener {
            startLoadingAnim()
            // Remove old items to make the UX more seamless
            binding.homeServicesList.adapter =
                DepartureListAdapter(
                    emptyList(),
                    this
                )
            viewModel.getTrains()
            //viewModel.getJourneyPlan()
        }

        binding.homeBtnSettings.setOnClickListener {
            val backward =  MaterialSharedAxis(MaterialSharedAxis.Z,  false)
            enterTransition = backward

            val forward =  MaterialSharedAxis(MaterialSharedAxis.Z,  true)
            exitTransition = forward
            view.findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    }

    /**
     * Start the loading animation, looping it with the listeners
     */
    private fun startLoadingAnim() {
        if (loading_indicator.drawable is AnimatedVectorDrawable) {
            avd.registerAnimationCallback(object: Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    avd.start()
                }
            })
            avd.start()
        }
    }

    override fun onClick(position: Int, itemBackground: View, destinationText: MaterialTextView) {

        val bgName = "${getString(R.string.departure_background_transition)}-$position"

        val navigateBundle = bundleOf("backgroundTransName" to bgName)
        viewModel.services.value?.let { services ->
            viewModel.setServiceId(services[position].serviceID)
        }

        val extras = FragmentNavigatorExtras(
            (itemBackground as ConstraintLayout) to bgName
        )

        view?.findNavController()?.navigate(R.id.serviceDetailFragment,
            navigateBundle, null, extras)
    }

    override fun onPause() {
        binding.homeServicesList.adapter = null
        super.onPause()
    }

}
