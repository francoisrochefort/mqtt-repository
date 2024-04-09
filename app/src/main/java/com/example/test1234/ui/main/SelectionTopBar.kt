package com.example.test1234.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(
    isConnected: Boolean,

    onMuteAllClick: () -> Unit,
    onGrantAllPermissionClick: () -> Unit,
    onRevokeAllPermissionClick: () -> Unit,
    onDeleteAllClick: () -> Unit,
) {
    TopAppBar(
        title = { },
        actions = {
            IconButton(onClick = onMuteAllClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onGrantAllPermissionClick,
                enabled = isConnected
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onRevokeAllPermissionClick,
                enabled = isConnected
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null
                )
            }
            IconButton(onClick = onDeleteAllClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    )
}