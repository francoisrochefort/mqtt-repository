package com.example.test1234

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.test1234.ui.main.MainScreen
import com.example.test1234.ui.main.MainViewModel
import com.example.test1234.ui.theme.Test1234Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Reconnect to MQTT server when the URI changes or an activity is being created
        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                val flow = Test1234.appModule.settingRepository.mqttServerUri
                flow.collect { uri ->
                    val kevin = Test1234.appModule.kevin
                    kevin.connect(application, uri, CLIENT_ID)
                    Log.d(TAG, "connected to '$uri' as '$CLIENT_ID'")
                }
            }
        }

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

    companion object {
        private const val TAG = "e-trak MainActivity"
        private const val CLIENT_ID = "belle"
    }
}


