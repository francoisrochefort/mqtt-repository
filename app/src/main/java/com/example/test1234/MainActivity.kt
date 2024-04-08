package com.example.test1234

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.test1234.ui.main.MainScreen
import com.example.test1234.ui.main.MainViewModel
import com.example.test1234.ui.theme.Test1234Theme
import android.Manifest;
import android.os.Build

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request required permissions
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                0
            )
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
}


