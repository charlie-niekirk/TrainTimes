package com.cniekirk.traintimes.utils.extensions

import android.content.res.Resources
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Interpolator
import android.widget.ImageView
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import timber.log.Timber

/**
 * Experimenting with inline / crossinline
 */
inline fun TextInputEditText.onFocusChange(
    crossinline action: (Boolean) -> Unit)
{
    this.setOnFocusChangeListener { _, hasFocus ->
        action(hasFocus)
    }
}

/**
 * Run a function when a View gets measured and laid out on the screen.
 */
fun View.onNextMeasure(runnable: () -> Unit) {
    viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            if (isLaidOut) {
                viewTreeObserver.removeOnPreDrawListener(this)
                runnable()

            } else if (visibility == View.GONE) {
                Timber.w("View's visibility is set to Gone. It'll never be measured: ${resourceName()}")
                viewTreeObserver.removeOnPreDrawListener(this)
            }
            return true
        }
    })
}

fun View.resourceName(): String {
    var name = "<nameless>"
    try {
        name = resources.getResourceEntryName(id)
    } catch (e: Resources.NotFoundException) {
        // Nothing to see here
    }
    return name
}

fun AnimatedVectorDrawable.loop(imageView: ImageView) {
    if (imageView.drawable is AnimatedVectorDrawable) {
        this.registerAnimationCallback(object: Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                this@loop.start()
            }
        })
        this.start()
    }
}

fun AnimatedVectorDrawable.cancel() {
    this.apply {
        clearAnimationCallbacks()
        stop()
        reset()
    }
}

val View.keyboardIsVisible: Boolean
    get() = WindowInsetsCompat
        .toWindowInsetsCompat(rootWindowInsets)
        .isVisible(WindowInsetsCompat.Type.ime())

fun View.alphaAnimate(duration: Float, interpolator: Interpolator) {

}
