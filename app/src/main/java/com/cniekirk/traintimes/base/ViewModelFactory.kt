package com.cniekirk.traintimes.base

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

interface ViewModelFactory<out V : ViewModel> {
    fun create(handle: SavedStateHandle): V
}