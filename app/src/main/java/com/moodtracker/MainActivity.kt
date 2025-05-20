package com.moodtracker

import StatisticsScreen
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moodtracker.deviceInfo.RunTimeInfo
import com.moodtracker.screens.EntryScreen
import com.moodtracker.screens.NewReadingScreen
import com.moodtracker.screens.SettingsScreen
import com.moodtracker.ui.theme.MoodTrackerTheme
import com.moodtracker.viewmodels.DatabaseViewmodel
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {

    private val viewModel: DatabaseViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        splashScreen.setOnExitAnimationListener { splashViewProvider ->
            val splashIconView = splashViewProvider.iconView
            val splashImageView = splashIconView as? android.widget.ImageView
            val scaleType = splashImageView?.scaleType ?: android.widget.ImageView.ScaleType.CENTER

            val fadeOut = ObjectAnimator.ofFloat(splashIconView, android.view.View.ALPHA, 1f, 0f)
            fadeOut.duration = 300

            val rootView = splashViewProvider.view as android.view.ViewGroup

            val defaultIcon = android.widget.ImageView(this).apply {
                setImageResource(R.drawable.icon)
                layoutParams = splashIconView.layoutParams
                setScaleType(scaleType)
                alpha = 0f
            }

            rootView.addView(defaultIcon)

            val fadeIn = ObjectAnimator.ofFloat(defaultIcon, android.view.View.ALPHA, 0f, 1f)
            fadeIn.duration = 300

            fadeIn.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    defaultIcon.postDelayed({

                        val today = "%04d-%02d-%02d".format(
                            RunTimeInfo.year,
                            RunTimeInfo.month + 1, // Calendar.MONTH is 0-based
                            RunTimeInfo.day
                        )
                        lifecycleScope.launch {
                            viewModel.closeOlderEntries(today)
                        }

                        // Prepare "exploding" emotion icons
                        val emotionIcons = listOf(
                            R.drawable.angry,
                            R.drawable.excited,
                            R.drawable.happy,
                            R.drawable.neutral,
                            R.drawable.sad,
                            R.drawable.pleased,
                            R.drawable.upset
                        )

                        val views = emotionIcons.map { resId ->
                            android.widget.ImageView(this@MainActivity).apply {
                                setImageResource(resId)
                                layoutParams = defaultIcon.layoutParams
                                alpha = 0f
                                x = defaultIcon.x
                                y = defaultIcon.y
                                setScaleType(scaleType)
                                rootView.addView(this)
                            }
                        }

                        rootView.removeView(defaultIcon)

                        // Animate in the emotion icons (fade in + explode outward)
                        val animators = views.mapIndexed { index, imageView ->
                            val angle = Math.toRadians((360.0 / views.size * index))
                            val radius = 600f // how far to spread

                            val dx = (cos(angle) * radius).toFloat()
                            val dy = (sin(angle) * radius).toFloat()

                            val fadeIn = ObjectAnimator.ofFloat(imageView, android.view.View.ALPHA, 0f, 1f)
                            val moveX = ObjectAnimator.ofFloat(imageView, android.view.View.TRANSLATION_X, 0f, dx)
                            val moveY = ObjectAnimator.ofFloat(imageView, android.view.View.TRANSLATION_Y, 0f, dy)

                            AnimatorSet().apply {
                                playTogether(fadeIn, moveX, moveY)
                                duration = 300
                                interpolator = AccelerateDecelerateInterpolator()
                            }
                        }

                        AnimatorSet().apply {
                            playTogether(animators)
                            start()
                        }

                        rootView.postDelayed({
                            splashViewProvider.remove()
                        }, 700)


                    }, 300)
                }
            })

            fadeOut.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    fadeIn.start()
                }
            })

            fadeOut.start()
        }




        super.onCreate(savedInstanceState)
        RunTimeInfo.initialize(this)
        enableEdgeToEdge()
        setContent {
            MoodTrackerTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "entry",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("entry") {
                            EntryScreen(
                                onNewReadingClick = { navController.navigate("new_reading") },
                                onStatisticsClick = { navController.navigate("statistics") },
                                onOptionsClick = { navController.navigate("settings_screen") },
                            )
                        }
                        composable("new_reading") { NewReadingScreen() }
                        composable("settings_screen") { SettingsScreen() }
                        composable("statistics") {  StatisticsScreen(viewModel) }
                    }
                }
            }
        }
    }
}
