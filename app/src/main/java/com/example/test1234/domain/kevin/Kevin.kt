package com.example.test1234.domain.kevin

import android.content.Context
import com.example.test1234.data.MqttApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class Kevin {
    sealed class Events {
        data object OnPermissionGranted : Events()
        data object OnPermissionRevoked : Events()
        data class OnUnknown(val msg: MqttApi.Msg) : Events()
    }
    private val mqttApi by lazy { MqttApi() }
     fun connect(context: Context, serverUri: String, clientId: String) {
        mqttApi.connect(
            context = context,
            serverUri = serverUri,
            clientId = clientId,
            subTopic = "e-trak/ls7/events/$clientId",
            pubTopic = "e-trak/ls7/commands"
        )
    }
    val isConnected = mqttApi.isConnected
    @OptIn(DelicateCoroutinesApi::class)
    val events by lazy {
        mqttApi.messages.map { msg ->
            when (msg.name) {
                "OnPermissionGranted" -> Events.OnPermissionGranted
                "OnPermissionRevoked" -> Events.OnPermissionRevoked
                 else -> Events.OnUnknown(msg = msg)
            }
        }.shareIn(GlobalScope, SharingStarted.Eagerly, 0)
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
                MqttApi.Msg.Parameter(name = "hmi", hmi),
                MqttApi.Msg.Parameter(name = "company", company),
                MqttApi.Msg.Parameter(name = "operator", operator),
                MqttApi.Msg.Parameter(name = "tell", tell),
            )
        )
        mqttApi.publish(msg = msg)
    }
}








