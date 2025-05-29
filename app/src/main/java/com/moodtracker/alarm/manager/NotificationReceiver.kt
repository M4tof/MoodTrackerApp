package com.moodtracker.alarm.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.moodtracker.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (!PermissionsUtils.hasNotificationPermission(context)) return

        val title = intent?.getStringExtra("title") ?: "Mood Tracker"
        val message = intent?.getStringExtra("message") ?: "Time to check your mood 😊"
        val hour = intent?.getIntExtra("hour", 8) ?: 8
        val minute = intent?.getIntExtra("minute", 30) ?: 30
        val requestCode = intent?.getIntExtra("requestCode", 0) ?: 0

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel", "Reminders", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "MoodTracker Reminders" }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_mood_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        AlarmScheduler.scheduleExactAlarm(context, hour, minute, requestCode, title, message)
    }
}

