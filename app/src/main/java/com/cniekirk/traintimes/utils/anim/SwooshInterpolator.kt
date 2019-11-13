package com.cniekirk.traintimes.utils.anim

import android.animation.TimeInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.PathInterpolator

class SwooshInterpolator(private val time : Float) : TimeInterpolator {

    val path = PathInterpolator(0.55f, 0f, 0f, 1f)
    val bounce = BounceInterpolator()

    override fun getInterpolation(input: Float): Float {

        return if(input < time) {
            path.getInterpolation(input)
        } else {
            bounce.getInterpolation(input)
        }

    }

}