package com.example.test1234.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.test1234.data.hmi.Hmi

@Composable
fun MainContent(
    isConnected: Boolean,
    mqttServerUri: String,

    onHmiClick: (Hmi) -> Unit,
    onHmiLongClick: (Hmi) -> Unit,
    selectionMode: MainViewModel.SelectionMode,
    selection: List<Hmi>,
    selectAll: (hmis: List<Hmi>) -> Unit,

    hmis: List<Hmi>,

    onMuteAllClick: () -> Unit,
    onGrantAllPermissionClick: () -> Unit,
    onRevokeAllPermissionClick: () -> Unit,
    onDeleteAllClick: () -> Unit,

    onGrantPermissionClick: (Hmi) -> Unit,
    onRevokePermissionClick: (Hmi) -> Unit,
    onDialClick: (Hmi) -> Unit,
    onMuteClick: (Hmi) -> Unit,
    onDeleteClick: (Hmi) -> Unit,

    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = selectionMode == MainViewModel.SelectionMode.MULTIPLE
            ) {
                SelectionTopBar(
                    isConnected = isConnected,

                    onMuteAllClick = onMuteAllClick,
                    onGrantAllPermissionClick = onGrantAllPermissionClick,
                    onRevokeAllPermissionClick = onRevokeAllPermissionClick,
                    onDeleteAllClick = onDeleteAllClick
                )
            }
            AnimatedVisibility(
                visible = selectionMode == MainViewModel.SelectionMode.NORMAL
            ) {
                TopBar(mqttServerUri = mqttServerUri)
            }
        }
    ) { paddingValues ->
        if (hmis.isNotEmpty())
            HmiList(
                isConnected = isConnected,

                onHmiClick = onHmiClick,
                onHmiLongClick = onHmiLongClick,
                selectionMode = selectionMode,
                selection = selection,
                selectAll = selectAll,

                hmis = hmis,
                onGrantPermissionClick = onGrantPermissionClick,
                onRevokePermissionClick = onRevokePermissionClick,
                onDialClick = onDialClick,
                onMuteClick = onMuteClick,
                onDeleteClick = onDeleteClick,
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            )
        else
            EmptyHmiList(modifier = modifier)
    }
}