package com.cniekirk.traintimes.utils.extensions

import com.google.android.material.textfield.TextInputEditText

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