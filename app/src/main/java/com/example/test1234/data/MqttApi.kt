package com.example.test1234.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import info.mqtt.android.service.MqttAndroidClient
import info.mqtt.android.service.QoS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttApi {
    companion object {
        private const val TAG = "e-trak MqttApi"
    }
    class NotConnectedToMqttException : Exception()
    data class Msg(val name: String, val parameters: List<Parameter> = emptyList()) {
        data class Parameter(val name: String, val value: String) {
            override fun toString() = "$name=$value"
        }
        override fun toString() = "$name(${parameters.joinToString(", ")})"
    }

    private lateinit var client: MqttAndroidClient
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()
    private lateinit var subTopic: String
    private lateinit var pubTopic: String

    fun connect(context: Context, serverUri: String, clientId: String, subTopic: String, pubTopic: String) {
        Log.d(TAG, "connect()")
        client = MqttAndroidClient(context = context, serverURI = serverUri, clientId = clientId)
        this.subTopic = subTopic
        this.pubTopic = pubTopic
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = false
        }
        client.connect(
            options = options,
            userContext = null,
            callback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val bufferOpts = DisconnectedBufferOptions().apply {
                        isBufferEnabled = true
                        bufferSize = 100
                        isPersistBuffer = false
                        isDeleteOldestMessages = false
                    }
                    client.setBufferOpts(bufferOpts)
                    subscribe()
                    _isConnected.value = true
                    Log.d(TAG, "connect=onSuccess")
                }
                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    _isConnected.value = false
                    Log.d(TAG, "connect=onFailure")
                }
            }
        )
    }

    private fun subscribe() {
        Log.d(TAG, "subscribe()")
        client.subscribe(subTopic, QoS.AtMostOnce.value,
            null,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.d(TAG, "subscribe=onSuccess")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "subscribe=onFailure")
                }
            }
        )
    }

    fun publish(msg: Msg) {
        Log.d(TAG, "publish(msg=$msg)")
        val json = Gson().toJson(msg)
        Log.d(TAG, "publish(json=$json)")
        val message = MqttMessage()
        message.payload = json.toByteArray()
        if (client.isConnected) {
            client.publish(pubTopic, message)
            if (!client.isConnected)
                throw NotConnectedToMqttException()
        } else
            throw NotConnectedToMqttException()
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val messages by lazy {
        _isConnected.flatMapLatest { connected ->
            if (connected) {
                callbackFlow {
                    val callback = object : MqttCallbackExtended {
                        override fun connectionLost(cause: Throwable?) {
                            Log.d(TAG, "MqttCallback::connectionLost()")
                            _isConnected.value = false
                        }
                        override fun messageArrived(topic: String?, message: MqttMessage?) {
                            Log.d(TAG, "MqttCallback::messageArrived()")
                            if (message != null)
                                trySend(Gson().fromJson(String(message.payload), Msg::class.java))
                        }
                        override fun deliveryComplete(token: IMqttDeliveryToken?) {
                            Log.d(TAG, "MqttCallback::deliveryComplete()")
                        }
                        override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                            _isConnected.value = true
                            if (reconnect)
                                subscribe()
                        }
                    }
                    client.setCallback(callback)
                    awaitClose {
                        Log.d(TAG, "callbackFlow::awaitClose()")
                    }
                }
            }
            else
                emptyFlow()
        }
    }
}