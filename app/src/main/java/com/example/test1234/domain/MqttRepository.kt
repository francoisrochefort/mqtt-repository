package com.example.test1234.domain

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.test1234.data.MqttApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MqttRepository(
    private val clientId: String
) {
    sealed class Events {
        data object OnPermissionGranted : Events()
        data object OnPermissionRevoked : Events()
        data class OnUnknownEvent(val msg: MqttApi.Msg) : Events()
    }

    private val mqttApi by lazy {
        MqttApi(
            subTopic = "e-trak/ls7/events/$clientId",
            pubTopic = "e-trak/ls7/commands"
        )
    }

    fun queryPermission(
        hmi: String,
        company: String,
        operator: String,
        tell: String
    ) {
        val msg = MqttApi.Msg(
            name = "QueryPermission",
            parameters = listOf(
                MqttApi.Parameter(name = "hmi", hmi),
                MqttApi.Parameter(name = "company", company),
                MqttApi.Parameter(name = "operator", operator),
                MqttApi.Parameter(name = "tell", tell),
            )
        )
        mqttApi.publish(msg = msg)
    }

    @OptIn(DelicateCoroutinesApi::class)
    val events by lazy {
        mqttApi.messages.map { msg ->
            when (msg.name) {
               "OnPermissionGranted" -> Events.OnPermissionGranted
               "OnPermissionRevoked" -> Events.OnPermissionRevoked
                else -> Events.OnUnknownEvent(msg = msg)
            }
        }.shareIn(GlobalScope, SharingStarted.Eagerly, 0)
    }

    val isConnected = mqttApi.isConnected
    fun connect(context: Context, serverUri: String) {
        mqttApi.connect(
            context = context,
            serverUri = serverUri,
            clientId = clientId
        )
    }
}