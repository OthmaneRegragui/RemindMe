package com.reot.remindme.Notification.Services
import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.reot.remindme.Notification.Broadcasts.NotificationReceiver

class NotificationService : IntentService("NotificationService") {
    override fun onHandleIntent(intent: Intent?) {
        // Extract the data from the intent
        val serviceName = intent?.getStringExtra("service_name")
        val taskDescription = intent?.getStringExtra("task_description")
        val dateTime = intent?.getStringExtra("date_time")

        // Parse the date and time and calculate the notification time in milliseconds
        // You can use SimpleDateFormat or any other date parsing library for this purpose
        // For simplicity, we'll assume the date/time is given in milliseconds
        val notificationTimeMillis = dateTime?.toLongOrNull() ?: return

        // Get the current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()

        // Calculate the delay time for the notification
        val delayMillis = notificationTimeMillis - currentTimeMillis

        if (delayMillis > 0) {
            // Schedule the notification using AlarmManager
            val notificationIntent = Intent(this, NotificationReceiver::class.java)
            notificationIntent.putExtra("service_name", serviceName)
            notificationIntent.putExtra("task_description", taskDescription)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.setExact(AlarmManager.RTC_WAKEUP, notificationTimeMillis, pendingIntent)
        }
    }
}