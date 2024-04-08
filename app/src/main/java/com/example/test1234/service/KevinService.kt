package com.example.test1234.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.test1234.R
import com.example.test1234.Test1234.Companion.appModule
import com.example.test1234.data.hmi.Hmi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

import kotlinx.coroutines.launch

class KevinService : Service() {

    companion object {

        const val TAG = "e-trak KevinService"

        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "Permission Requests"

        const val CLIENT_ID = "belle"

        // Actions
        const val ACTION_QUERY_PERMISSION = "com.example.test1234.service.KevinService.ACTION_QUERY_PERMISSION"
        const val ACTION_MQTT_CONNECTION_STATE = "com.example.test1234.service.KevinService.ACTION_MQTT_CONNECTION_STATE"

        // Extras
        const val EXTRA_HMI = "com.example.test1234.service.KevinService.EXTRA_HMI"
        const val EXTRA_COMPANY = "com.example.test1234.service.KevinService.EXTRA_COMPANY"
        const val EXTRA_OPERATOR = "com.example.test1234.service.KevinService.EXTRA_OPERATOR"
        const val EXTRA_TELL = "com.example.test1234.service.KevinService.EXTRA_TELL"
        const val EXTRA_CONNECTED = "com.example.test1234.service.KevinService.EXTRA_CONNECTED"

    }

    // Actions
    enum class Actions {
        START,
        GRANT_PERMISSION,
        REVOKE_PERMISSION,
        STOP
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Connect to MQTT when the URI changes
        serviceScope.launch {
            val settingRepository = appModule.settingRepository
            val flow = settingRepository.mqttServerUri
            flow.collect { uri ->
                val kevinRepository = appModule.kevinMqttProxy
                kevinRepository.connect(application, uri, CLIENT_ID)
            }
        }

        // Make the phone vibrate accordingly
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceScope.launch(Dispatchers.IO) {
                val hmiRepository = appModule.hmiRepository
                val hmis = hmiRepository.listAll()
                hmis.collect {
                    val ringing = it.any { scale -> scale.ringing }
                    if (ringing) {
                        val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.cancel()
                        val effect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        vibrator.vibrate(effect)
                    }
                }
            }
        }

        // Handle permission requests
        serviceScope.launch {
            val kevinMqttProxy = appModule.kevinMqttProxy
            val commands = kevinMqttProxy.commands
                .shareIn(serviceScope, SharingStarted.Eagerly, 0)
            commands.collect { cmd ->
                Log.d(TAG, cmd.toString())
                when(cmd) {
                    is KevinMqttProxy.Commands.QueryPermission -> {

                        // Check if the hmi already exists
                        val hmiRepository = appModule.hmiRepository
                        val hmi = hmiRepository.getByHmi(cmd.hmi)
                        if (hmi == null) {

                            // Permission is pending
                            Log.d(TAG, "Permission is pending")
                            hmiRepository.add(
                                Hmi(
                                    id = cmd.hmi,
                                    company = cmd.company,
                                    operator = cmd.operator,
                                    tell = cmd.tell
                                )
                            )
                        } else if (hmi.granted) {

                            // Permission is granted
                            Log.d(TAG, "Permission is granted")
                            kevinMqttProxy.grantPermission(hmi = hmi.id)

                        } else {

                            // Permission is either pending, denied or revoked
                            Log.d(TAG, "Permission is either pending, denied or revoked")
                            kevinMqttProxy.revokePermission(hmi = hmi.id)
                            hmiRepository.unmute(hmi)
                        }
                    }
                    else -> Unit
                }
            }
        }

        // Broadcast MQTT connection state
        serviceScope.launch {
            val kevinMqttProxy = appModule.kevinMqttProxy
            val isConnected = kevinMqttProxy.isConnected
            isConnected.collect { connected ->
                Intent(ACTION_MQTT_CONNECTION_STATE).apply {
                    putExtra(EXTRA_CONNECTED, connected)
                }.also { intent ->
                    sendBroadcast(intent)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action) {

            // Handle START action
            Actions.START.name -> {
                val notification = NotificationCompat.Builder(application, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(getString(R.string.kevin_service_notification_content_title))
                    .setContentText(getString(R.string.kevin_service_notification_content_text))
                    .build()
                startForeground(NOTIFICATION_ID, notification)
            }

            // Handle GRANT_PERMISSION
            Actions.GRANT_PERMISSION.name -> {
                appModule.kevinMqttProxy.grantPermission(
                    hmi = intent.getStringExtra(EXTRA_HMI)!!
                )
            }

            // Handle REVOKE_PERMISSION
            Actions.REVOKE_PERMISSION.name -> {
                appModule.kevinMqttProxy.revokePermission(
                    hmi = intent.getStringExtra(EXTRA_HMI)!!
                )
            }

            // Handle STOP action
            Actions.STOP.name -> {
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}