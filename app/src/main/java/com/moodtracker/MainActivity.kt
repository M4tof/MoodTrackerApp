package com.moodtracker

import StatisticsScreen
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moodtracker.alarm.manager.AlarmScheduler
import com.moodtracker.alarm.manager.PermissionsUtils
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

    private fun explodeIconsAndRemoveSplash(
        rootView: ViewGroup,
        defaultIcon: ImageView,
        splashViewProvider: SplashScreenViewProvider
    ) {
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
            ImageView(this).apply {
                setImageResource(resId)
                layoutParams = defaultIcon.layoutParams
                alpha = 0f
                x = defaultIcon.x
                y = defaultIcon.y
                scaleType = defaultIcon.scaleType
                rootView.addView(this)
            }
        }

        rootView.removeView(defaultIcon)

        val animators = views.mapIndexed { index, imageView ->
            val angle = Math.toRadians((360.0 / views.size * index))
            val radius = 600f
            val dx = (cos(angle) * radius).toFloat()
            val dy = (sin(angle) * radius).toFloat()

            val fadeIn = ObjectAnimator.ofFloat(imageView, View.ALPHA, 0f, 1f)
            val moveX = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, 0f, dx)
            val moveY = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, 0f, dy)

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        splashScreen.setOnExitAnimationListener { splashViewProvider ->
            val splashIconView = splashViewProvider.iconView
            val splashImageView = splashIconView as? ImageView
            val scaleType = splashImageView?.scaleType ?: ImageView.ScaleType.CENTER

            val fadeOut = ObjectAnimator.ofFloat(splashIconView, View.ALPHA, 1f, 0f).apply {
                duration = 300
            }

            val rootView = splashViewProvider.view as ViewGroup

            val defaultIcon = ImageView(this).apply {
                setImageResource(R.drawable.icon)
                layoutParams = splashIconView.layoutParams
                setScaleType(scaleType)
                alpha = 0f
            }
            rootView.addView(defaultIcon)

            val fadeIn = ObjectAnimator.ofFloat(defaultIcon, View.ALPHA, 0f, 1f).apply {
                duration = 300
            }

            fadeIn.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    defaultIcon.postDelayed({
                        // Initialize RunTimeInfo synchronously before proceeding
                        lifecycleScope.launch {
                            RunTimeInfo.initializeBlocking(this@MainActivity)
                            val today = "%04d-%02d-%02d".format(RunTimeInfo.year, RunTimeInfo.month + 1, RunTimeInfo.day)
                            viewModel.closeOlderEntries(today)

                            // Schedule alarms only if permissions granted
                            if (PermissionsUtils.hasNotificationPermission(this@MainActivity)) {
                                val morningHour = RunTimeInfo.morningReminderTime.toInt()
                                val morningMinute = ((RunTimeInfo.morningReminderTime % 1) * 100).toInt()
                                AlarmScheduler.scheduleExactAlarm(
                                    this@MainActivity, morningHour, morningMinute, 1001,
                                    "Good Morning 🌅", "Time for your morning mood check!"
                                )

                                val eveningHour = RunTimeInfo.eveningReminderTime.toInt()
                                val eveningMinute = ((RunTimeInfo.eveningReminderTime % 1) * 100).toInt()
                                AlarmScheduler.scheduleExactAlarm(
                                    this@MainActivity, eveningHour, eveningMinute, 1002,
                                    "Evening Check 🌙", "How was your day? Log your mood now!"
                                )
                            }

                            explodeIconsAndRemoveSplash(rootView, defaultIcon, splashViewProvider)
                        }
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

        // Do NOT reschedule alarms here to avoid race condition with uninitialized RunTimeInfo
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
                        composable("statistics") { StatisticsScreen(viewModel) }
                    }
                }
            }
        }
    }
}



