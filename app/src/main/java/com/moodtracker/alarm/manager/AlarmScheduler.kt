package com.moodtracker.alarm.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.moodtracker.deviceInfo.RunTimeInfo
import java.util.Calendar

object AlarmScheduler {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleExactAlarm(
        context: Context,
        hour: Int,
        minute: Int,
        requestCode: Int,
        title: String,
        message: String
    ) {
        val enabled = when (requestCode) {
            1001 -> RunTimeInfo.isMorningReminderEnabled
            1002 -> RunTimeInfo.isEveningReminderEnabled
            else -> false
        }

        if (!enabled || !PermissionsUtils.hasNotificationPermission(context)) return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("requestCode", requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelAllReminders(context: Context) {
        listOf(1001, 1002).forEach { requestCode ->
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(it)
            }
        }
    }
}

