package com.example.test1234

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.test1234.ui.theme.Test1234Theme
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "e-trak MainActivity"

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
                Log.d(TAG, event.toString())
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
            mqttRepository.queryPermission(
                hmi = "belle",
                company = "sexy",
                operator = "mexicaine",
                tell = "cochonne"
            )
        }
    }
    companion object {

        private const val Timeout = 10000L

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

                val mqttRepository = MqttRepository(clientId = "belle")
                mqttRepository.connect(context = application, serverUri = "tcp://167.114.3.107:1883")
                return MainViewModel(mqttRepository = mqttRepository) as T
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val isGranted by viewModel.isGranted.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isGranted)
                Icons.Default.CheckCircle
            else
                Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = viewModel::onQueryPermission,
            enabled = isConnected
        ) {
            Text(text = stringResource(id = R.string.query_permission))
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test1234Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel: MainViewModel by viewModels { MainViewModel.Factory }
                    MainScreen(viewModel = mainViewModel)
                }
            }
        }
    }
}

