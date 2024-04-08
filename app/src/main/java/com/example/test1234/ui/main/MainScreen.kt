package com.example.test1234.ui.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val isConnected by viewModel.isConnected.collectAsState(initial = false)
    val hmis by viewModel.hmis.collectAsState(initial = emptyList())

    LaunchedEffect(key1 = true) {
        viewModel.events.collect { event ->
            when (event) {
                is MainViewModel.Events.OnError -> Toast.makeText(
                    context, event.e.message, Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    MainContent(
        isConnected = isConnected,
        hmis = hmis,
        onGrantPermissionClick = viewModel::onGrantPermissionClick,
        onRevokePermissionClick = viewModel::onRevokePermissionClick,
        onDialClick = viewModel::onDialClick,
        onMuteClick = viewModel::onMuteClick,
        onDeleteClick = viewModel::onDeleteClick
    )
}