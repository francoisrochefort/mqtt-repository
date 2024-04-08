package com.example.test1234.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.test1234.data.hmi.Hmi

@Composable
fun MainContent(
    isConnected: Boolean,
    hmis: List<Hmi>,
    onGrantPermissionClick: (Hmi) -> Unit,
    onRevokePermissionClick: (Hmi) -> Unit,
    onDialClick: (Hmi) -> Unit,
    onMuteClick: (Hmi) -> Unit,
    onDeleteClick: (Hmi) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {

        },
        bottomBar = {

        }
    ) { paddingValues ->
        if (hmis.isNotEmpty())
            HmiList(
                isConnected = isConnected,
                hmis = hmis,
                onGrantPermissionClick = onGrantPermissionClick,
                onRevokePermissionClick = onRevokePermissionClick,
                onDialClick = onDialClick,
                onMuteClick = onMuteClick,
                onDeleteClick = onDeleteClick,
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )
        else
            EmptyHmiList(modifier = modifier)
    }
}