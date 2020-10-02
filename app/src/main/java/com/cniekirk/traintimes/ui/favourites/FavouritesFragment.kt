package com.cniekirk.traintimes.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentFavouritesBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.ui.adapter.FavouritesAdapter
import com.cniekirk.traintimes.utils.viewBinding
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import com.cniekirk.traintimes.utils.anim.DepartureListItemAnimtor
import kotlinx.android.synthetic.main.fragment_dep_board_results.*
import kotlinx.android.synthetic.main.fragment_favourites.*
import javax.inject.Inject

class FavouritesFragment: Fragment(R.layout.fragment_favourites), Injectable, FavouritesAdapter.FavouritesClickListener {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentFavouritesBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.favourites.observe(viewLifecycleOwner, {

            if (it.isNotEmpty()) {
                binding.emptyFavouriteText.visibility = View.INVISIBLE
                binding.favouritesList.adapter = FavouritesAdapter(it, this)
            }

        })

        viewModel.getFavourites()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.favouritesList.itemAnimator = DepartureListItemAnimtor(0)
            .withInterpolator(FastOutSlowInInterpolator())
            .withAddDuration(250)
            .withRemoveDuration(250)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.favouritesList.layoutManager = layoutManager
        binding.favouritesList.adapter = FavouritesAdapter(emptyList(), this)
        binding.favouritesList.addItemDecoration(DividerItemDecoration(favourites_list.context, layoutManager.orientation))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onClick(position: Int) =
        binding.root.findNavController().navigate(R.id.depBoardResultsFragment,
            bundleOf("favourites" to position, "isFromSearch" to true))

}