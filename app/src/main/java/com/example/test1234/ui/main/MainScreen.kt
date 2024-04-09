package com.example.test1234.ui.main

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.BackHandler
import androidx.compose.ui.res.stringResource
import com.example.test1234.R

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectionMode by viewModel.selectionMode.collectAsState()
    val selection by viewModel.selection.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState(initial = false)
    val mqttServerUri by viewModel.mqttServerUri.collectAsState(initial = stringResource(id = R.string.ui_main_main_screen_loading))
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

    BackHandler(
        enabled = selectionMode == MainViewModel.SelectionMode.MULTIPLE,
        onBack = viewModel::onBack
    )

    MainContent(
        isConnected = isConnected,
        mqttServerUri = mqttServerUri,

        onHmiClick = viewModel::onHmiClick,
        onHmiLongClick = viewModel::onHmiLongClick,
        selectionMode = selectionMode,
        selection = selection,
        selectAll = viewModel::selectAll,

        hmis = hmis,

        onMuteAllClick = viewModel::onMuteAllClick,
        onGrantAllPermissionClick = viewModel::onGrantAllPermissionClick,
        onRevokeAllPermissionClick = viewModel::onRevokeAllPermissionClick,
        onDeleteAllClick = viewModel::onDeleteAllClick,

        onGrantPermissionClick = viewModel::onGrantPermissionClick,
        onRevokePermissionClick = viewModel::onRevokePermissionClick,
        onDialClick = viewModel::onDialClick,
        onMuteClick = viewModel::onMuteClick,
        onDeleteClick = viewModel::onDeleteClick
    )
}