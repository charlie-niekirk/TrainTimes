package com.cniekirk.traintimes

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.cniekirk.traintimes.databinding.ActivityMainBinding
import com.cniekirk.traintimes.utils.anim.kb.FluidContentResizer
import com.cniekirk.traintimes.utils.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private val binding by viewBinding(ActivityMainBinding::inflate)

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // If is dark mode
        when ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                Log.e(MainActivity::class.java.simpleName, "Night mode detected")
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                window.statusBarColor = resources.getColor(R.color.colorBackground, null)

            }
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)

        findViewById<BottomNavigationView>(R.id.navigation_bar)
            .setupWithNavController(navController)

        FluidContentResizer.listen(this)

    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()
                || super.onSupportNavigateUp()
    }

    override fun androidInjector() = dispatchingAndroidInjector

}
