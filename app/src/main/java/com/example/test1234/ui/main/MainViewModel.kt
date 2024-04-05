package com.example.test1234.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.test1234.domain.kevin.Kevin
import com.example.test1234.Test1234
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

class MainViewModel(

    val kevin: Kevin

) : ViewModel() {

    val isConnected = kevin.isConnected
    private val _isGranted = MutableStateFlow(false)
    val isGranted = _isGranted.asStateFlow()
    private val mutex = Mutex()
    private lateinit var deferred: CompletableDeferred<Boolean>

    init {
        viewModelScope.launch {
            kevin.events.collect { event ->
                Log.d(TAG, event.toString())
                when (event) {
                    is Kevin.Events.OnPermissionGranted -> {
                        _isGranted.value = true
                        deferred.complete(true)
                    }
                    is Kevin.Events.OnPermissionRevoked -> {
                        _isGranted.value = false
                        deferred.complete(false)
                    }
                    else -> Unit
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
        kevin.queryPermission(
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
        private const val TAG = "e-trak MainViewModel"
        private const val TIMEOUT = 10000L

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()
                return MainViewModel(kevin = Test1234.appModule.kevin) as T
            }
        }
    }
}