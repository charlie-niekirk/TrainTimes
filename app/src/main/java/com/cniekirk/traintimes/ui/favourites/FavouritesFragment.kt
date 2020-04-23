package com.cniekirk.traintimes.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.base.withFactory
import com.cniekirk.traintimes.databinding.FragmentFavouritesBinding
import com.cniekirk.traintimes.di.Injectable
import com.cniekirk.traintimes.utils.viewBinding
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModel
import com.cniekirk.traintimes.ui.viewmodel.HomeViewModelFactory
import javax.inject.Inject

class FavouritesFragment: Fragment(R.layout.fragment_favourites), Injectable {

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory

    private val binding by viewBinding(FragmentFavouritesBinding::bind)
    private val viewModel: HomeViewModel by activityViewModels { withFactory(viewModelFactory, arguments) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }



}