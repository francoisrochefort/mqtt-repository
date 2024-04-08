package com.example.test1234

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import com.example.test1234.di.AppModule
import com.example.test1234.service.KevinService
import com.example.test1234.service.KevinService.Companion.CHANNEL_ID

class Test1234 : Application() {

    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()

        appModule = AppModule(this)

        // Create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Permission Requests",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Start Kevin service
        Intent(applicationContext, KevinService::class.java).also { intent ->
            intent.action = KevinService.Actions.START.name
            startService(intent)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}