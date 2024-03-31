package com.example.test1234

import android.content.Context
import kotlinx.coroutines.flow.map

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
    val events by lazy {
        mqttApi.messages.map { msg ->
            when (msg.name) {
               "OnPermissionGranted" -> Events.OnPermissionGranted
               "OnPermissionRevoked" -> Events.OnPermissionRevoked
                else -> Events.OnUnknownEvent(msg = msg)
            }
        }
    }
    val isConnected = mqttApi.isConnected
    fun connect(context: Context, serverUri: String) {
        mqttApi.connect(
            context = context,
            serverUri = serverUri,
            clientId = clientId
        )
    }
    suspend fun queryPermission(
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
}