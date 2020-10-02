package com.cniekirk.traintimes.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.databinding.ActivityTracktimesWidgetConfigureBinding
import com.cniekirk.traintimes.utils.viewBinding

private const val TAG = "WidgetConfigureActivity"

class WidgetConfigureActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityTracktimesWidgetConfigureBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // If is dark mode
        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                Log.e(TAG, "Night mode detected")
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                window.statusBarColor = resources.getColor(R.color.colorBackground, null)

            }
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)



    }



}