package com.example.test1234.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.test1234.domain.MqttRepository
import com.example.test1234.Test1234
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

private const val TAG = "e-trak MainViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(

    val mqttRepository: MqttRepository

) : ViewModel() {

    val isConnected = mqttRepository.isConnected
    private val events = isConnected.flatMapLatest { connected ->
        if (connected) mqttRepository.events else emptyFlow()
    }
    private val _isGranted = MutableStateFlow(false)
    val isGranted = _isGranted.asStateFlow()
    private val mutex = Mutex()
    private lateinit var deferred: CompletableDeferred<Boolean>

    init {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is MqttRepository.Events.OnPermissionGranted -> {
                        _isGranted.value = true
                        deferred.complete(true)
                    }
                    is MqttRepository.Events.OnPermissionRevoked -> {
                        _isGranted.value = false
                        deferred.complete(false)
                    }
                    is MqttRepository.Events.OnUnknown -> Log.d(TAG, event.msg.toString())
                }
            }
        }
    }

    private suspend fun hasPermission(
        hmi: String,
        company: String,
        operator: String,
        tell: String
    ) : Boolean = mutex.withLock {
        deferred = CompletableDeferred()
        mqttRepository.queryPermission(
            hmi = hmi,
            company = company,
            operator = operator,
            tell = tell
        )
        return deferred.await()
    }

    fun onQueryPermissionClick() {
        viewModelScope.launch {
            withTimeout(TIMEOUT) {
                val granted = hasPermission(
                    hmi = "belle",
                    company = "sexy",
                    operator = "mexicaine",
                    tell = "cochonne"
                )
                Log.d(TAG, "granted = $granted")
            }
        }
    }

    companion object {
        private const val TIMEOUT = 10000L

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

                return MainViewModel(mqttRepository = Test1234.appModule.mqttRepository) as T
            }
        }
    }
}