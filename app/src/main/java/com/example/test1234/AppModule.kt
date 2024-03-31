package com.example.test1234

import android.content.Context
import com.example.test1234.domain.MqttRepository

class AppModule(
    private val appContext: Context
) {
    val mqttRepository by lazy {
        val mqttRepository = MqttRepository(clientId = CLIENT_ID)
        mqttRepository.connect(context = appContext, serverUri = SERVER_URI)
        mqttRepository
    }

    companion object {
        private const val CLIENT_ID = "belle"
        private const val SERVER_URI = "tcp://167.114.3.107:1883"
    }
}