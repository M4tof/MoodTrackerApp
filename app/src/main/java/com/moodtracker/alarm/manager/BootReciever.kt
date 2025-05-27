package com.moodtracker.alarm.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.moodtracker.deviceInfo.RunTimeInfo

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Retrieve stored reminder times and reschedule alarms
            val morningHour = RunTimeInfo.morningReminderTime.toInt()
            val morningMinute = ((RunTimeInfo.morningReminderTime % 1) * 100).toInt()
            AlarmScheduler.scheduleExactAlarm(
                context,
                morningHour,
                morningMinute,
                1001,
                "Good Morning 🌅",
                "Time for your morning mood check!"
            )

            val eveningHour = RunTimeInfo.eveningReminderTime.toInt()
            val eveningMinute = ((RunTimeInfo.eveningReminderTime % 1) * 100).toInt()
            AlarmScheduler.scheduleExactAlarm(
                context,
                eveningHour,
                eveningMinute,
                1002,
                "Evening Check 🌙",
                "How was your day? Log your mood now!"
            )
        }
    }
}
