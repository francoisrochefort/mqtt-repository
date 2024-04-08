package com.example.test1234.ui.main

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.test1234.Test1234
import com.example.test1234.data.hmi.Hmi
import com.example.test1234.domain.hmi.HmiRepository
import com.example.test1234.service.KevinServiceProxy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(

    private val hmiRepository: HmiRepository,
    private val kevinServiceProxy: KevinServiceProxy,
    application: Application

) : AndroidViewModel(application = application) {

    sealed class Events {
        data class OnError(val e: Exception) : Events()
    }

    private val _events = Channel<Events>()
    val events = _events.receiveAsFlow()
    val isConnected = kevinServiceProxy.isConnected
    val hmis = hmiRepository.listAll()

    fun onDeleteClick(hmi: Hmi) {
        viewModelScope.launch(Dispatchers.IO) {
            hmiRepository.delete(hmi = hmi)
        }
    }

    fun onMuteClick(hmi: Hmi) {
        viewModelScope.launch(Dispatchers.IO) {
            hmiRepository.mute(hmi)
        }
    }

    fun onDialClick(hmi: Hmi) {
        try {
            val context: Context = getApplication()
            val uri = Uri.parse("tel:${hmi.tell}")
            val intent = Intent(Intent.ACTION_DIAL, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(context, intent, null)
        }
        catch (e: Exception) {
            viewModelScope.launch {
                _events.send(Events.OnError(e = e))
            }
        }
    }

    fun onGrantPermissionClick(hmi: Hmi) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                hmiRepository.grantPermission(hmi = hmi)
                kevinServiceProxy.grantPermission(hmi = hmi)
            }
            catch (e: Exception) {
                _events.send(Events.OnError(e = e))
            }
        }
    }

    fun onRevokePermissionClick(hmi: Hmi) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                hmiRepository.revokePermission(hmi = hmi)
                kevinServiceProxy.revokePermission(hmi = hmi)
            }
            catch (e: Exception) {
                _events.send(Events.OnError(e = e))
            }
        }
    }

    companion object {
        private const val TAG = "e-trak MainViewModel"

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val savedStateHandle = extras.createSavedStateHandle()
                return MainViewModel(
                    hmiRepository = Test1234.appModule.hmiRepository,
                    kevinServiceProxy = Test1234.appModule.kevinServiceProxy,
                    application = application
                ) as T
            }
        }
    }
}