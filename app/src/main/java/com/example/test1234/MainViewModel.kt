package com.example.test1234

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "e-trak MainViewModel"

class MainViewModel(

    val mqttRepository: MqttRepository

) : ViewModel() {

    private val events = mqttRepository.events
    val isConnected = mqttRepository.isConnected
    private val _isGranted = MutableStateFlow(false)
    val isGranted = _isGranted.asStateFlow()

    init {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is MqttRepository.Events.OnPermissionGranted -> _isGranted.value = true
                    is MqttRepository.Events.OnPermissionRevoked -> _isGranted.value = false
                    is MqttRepository.Events.OnUnknownEvent -> Log.d(TAG, event.msg.toString())
                }
            }
        }
    }

    fun onQueryPermission() {
        viewModelScope.launch {
            withTimeout(TIMEOUT) {
                mqttRepository.queryPermission(
                    hmi = "belle",
                    company = "sexy",
                    operator = "mexicaine",
                    tell = "cochonne"
                )
            }
        }
    }
    companion object {

        private const val TIMEOUT = 10000L
        private const val CLIENT_ID = "belle"
        private const val SERVER_URI = "tcp://167.114.3.107:1883"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                val mqttRepository = MqttRepository(clientId = CLIENT_ID)
                mqttRepository.connect(context = application, serverUri = SERVER_URI)
                return MainViewModel(mqttRepository = mqttRepository) as T
            }
        }
    }
}