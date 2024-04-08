package com.example.test1234.service

import android.content.BroadcastReceiver
import android.content.Context
import kotlinx.coroutines.flow.callbackFlow
import android.content.Intent
import android.content.IntentFilter
import com.example.test1234.data.hmi.Hmi
import com.example.test1234.service.KevinService.Companion.ACTION_MQTT_CONNECTION_STATE
import com.example.test1234.service.KevinService.Companion.ACTION_QUERY_PERMISSION
import com.example.test1234.service.KevinService.Companion.EXTRA_COMPANY
import com.example.test1234.service.KevinService.Companion.EXTRA_CONNECTED
import com.example.test1234.service.KevinService.Companion.EXTRA_HMI
import com.example.test1234.service.KevinService.Companion.EXTRA_OPERATOR
import com.example.test1234.service.KevinService.Companion.EXTRA_TELL
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

/**
 * Class is used as a proxy between KevinService and the local application
 */

@OptIn(DelicateCoroutinesApi::class)
class KevinServiceProxy(
    private val context: Context
) {
    val commands by lazy {
        callbackFlow {
            val receiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val cmd = when(intent.action) {

                        // Handle ACTION_QUERY_PERMISSION
                        ACTION_QUERY_PERMISSION -> KevinMqttProxy.Commands.QueryPermission(
                            hmi = intent.getStringExtra(EXTRA_HMI)!!,
                            company = intent.getStringExtra(EXTRA_COMPANY)!!,
                            operator = intent.getStringExtra(EXTRA_OPERATOR)!!,
                            tell = intent.getStringExtra(EXTRA_TELL)!!
                        )
                        else -> KevinMqttProxy.Commands.OnUnknown
                    }
                    trySend(cmd)
                }
            }
            context.registerReceiver(
                receiver,
                IntentFilter().apply {
                    addAction(ACTION_QUERY_PERMISSION)
                }
            )
            awaitClose {
                context.unregisterReceiver(receiver)
            }
        }.shareIn(GlobalScope, SharingStarted.Eagerly, 0)
    }

    val isConnected by lazy {
        callbackFlow {
            val receiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (intent.action ==  ACTION_MQTT_CONNECTION_STATE)
                        trySend(intent.getBooleanExtra(EXTRA_CONNECTED, false))
                }
            }
            context.registerReceiver(
                receiver,
                IntentFilter().apply {
                    addAction(ACTION_MQTT_CONNECTION_STATE)
                }
            )
            awaitClose {
                context.unregisterReceiver(receiver)
            }
        }.stateIn(GlobalScope, SharingStarted.Eagerly, false)
    }

    fun grantPermission(hmi: Hmi) {
        Intent(context, KevinService::class.java).apply {
            action = KevinService.Actions.GRANT_PERMISSION.name
            putExtra(EXTRA_HMI, hmi.id)
        }.also { intent ->
            context.startService(intent)
        }
    }

    fun revokePermission(hmi: Hmi) {
        Intent(context, KevinService::class.java).apply {
            action = KevinService.Actions.REVOKE_PERMISSION.name
            putExtra(EXTRA_HMI, hmi.id)
        }.also { intent ->
            context.startService(intent)
        }
    }
}