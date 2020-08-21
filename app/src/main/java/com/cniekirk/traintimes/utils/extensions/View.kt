package com.cniekirk.traintimes.utils.extensions

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

private const val TAG = "View"
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
                Log.w(TAG, "View's visibility is set to Gone. It'll never be measured: ${resourceName()}")
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

val View.keyboardIsVisible: Boolean
    get() = WindowInsetsCompat
        .toWindowInsetsCompat(rootWindowInsets)
        .isVisible(WindowInsetsCompat.Type.ime())