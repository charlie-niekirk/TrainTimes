package com.cniekirk.traintimes.domain.model

sealed class State {
    object Loading: State()
    object Idle: State()
}