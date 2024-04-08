package com.example.test1234.service

import android.content.Context
import android.util.Log
import com.example.test1234.data.MqttApi
import kotlinx.coroutines.flow.map

/**
 * Class is used as a proxy between KevinService and remote HMIs
 */

class KevinMqttProxy {
    sealed class Commands {
        data class QueryPermission(
            val hmi: String,
            val company: String,
            val operator: String,
            val tell: String
        ) : Commands() {
            override fun toString() =
                "QueryPermission(hmi='$hmi', company='$company', operator='$operator', tell='$tell')"
        }

        data object OnUnknown : Commands()
    }

    private val mqttApi by lazy { MqttApi() }
    fun connect(context: Context, serverUri: String, clientId: String) {
        mqttApi.connect(
            context = context,
            serverUri = serverUri,
            clientId = clientId,
            subTopic = "e-trak/ls7/commands"
        )
    }

    val isConnected = mqttApi.isConnected

    val commands by lazy {
        mqttApi.messages.map { msg ->
            when (msg.name) {
                "QueryPermission" -> {
                    val hmi = msg.parameters.find { it.name == "hmi" }!!.value
                    val company = msg.parameters.find { it.name == "company" }!!.value
                    val operator = msg.parameters.find { it.name == "operator" }!!.value
                    val tell = msg.parameters.find { it.name == "tell" }!!.value
                    Commands.QueryPermission(
                        hmi = hmi,
                        company = company,
                        operator = operator,
                        tell = tell
                    )
                }

                else -> Commands.OnUnknown
            }
        }
    }

    fun grantPermission(hmi: String) {
        Log.d(TAG, "grantPermission(hmi='$hmi')")
        mqttApi.publish(
            topic = "e-trak/ls7/events/$hmi",
            msg = MqttApi.Msg(
                name = "OnPermissionGranted",
                parameters = listOf(
                    MqttApi.Msg.Parameter(name = "hmi", value = hmi)
                )
            )
        )
    }

    fun revokePermission(hmi: String) {
        Log.d(TAG, "revokePermission(hmi='$hmi')")
        mqttApi.publish(
            topic = "e-trak/ls7/events/$hmi",
            msg = MqttApi.Msg(
                name = "OnPermissionRevoked",
                parameters = listOf(
                    MqttApi.Msg.Parameter(name = "hmi", value = hmi)
                )
            )
        )
    }

    companion object {
        private const val TAG = "e-trak KevinRepository"
    }
}








