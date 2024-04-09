package com.example.test1234.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test1234.data.hmi.Hmi
import com.example.test1234.ui.theme.Test1234Theme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HmiListItem(
    onHmiClick: (Hmi) -> Unit,
    onHmiLongClick: (Hmi) -> Unit,
    selectionMode: MainViewModel.SelectionMode,
    selected: Boolean,

    isConnected: Boolean,

    hmi: Hmi,
    onGrantPermissionClick: (Hmi) -> Unit,
    onRevokePermissionClick: (Hmi) -> Unit,
    onDialClick: (Hmi) -> Unit,
    onMuteClick: (Hmi) -> Unit,
    onDeleteClick: (Hmi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(start = if (selectionMode == MainViewModel.SelectionMode.NORMAL) 16.dp else 0.dp)
            .combinedClickable(
                onClick = { onHmiClick(hmi) },
                onLongClick = { onHmiLongClick(hmi) }
            )
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = selectionMode == MainViewModel.SelectionMode.MULTIPLE
            ) {
                RadioButton(
                    selected = selected,
                    onClick = { onHmiClick(hmi) },
                )
            }
            Column {
                Text(text = hmi.company)
                Text(
                    text = hmi.tell,
                    fontSize = 10.sp
                )
                Text(
                    text = hmi.operator,
                    fontSize = 10.sp
                )
                Text(
                    text = hmi.id,
                    fontSize = 10.sp
                )
            }
        }
        AnimatedVisibility(
            visible = selectionMode == MainViewModel.SelectionMode.NORMAL
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onDialClick(hmi) }) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null
                    )
                }
                if (hmi.ringing) {
                    IconButton(onClick = { onMuteClick(hmi) }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null
                        )
                    }
                }
                IconButton(
                    onClick = { onGrantPermissionClick(hmi) },
                    enabled = isConnected
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = { onRevokePermissionClick(hmi) },
                    enabled = isConnected
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null
                    )
                }
                IconButton(onClick = { onDeleteClick(hmi) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            }
        }
        if (selectionMode == MainViewModel.SelectionMode.MULTIPLE)
            Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun HmiListItemNormalPreview() {
    Test1234Theme(darkTheme = true) {
        HmiListItem(
            isConnected = true,
            onHmiClick = {},
            onHmiLongClick = {},
            selectionMode = MainViewModel.SelectionMode.NORMAL,
            selected = true,
            hmi = Hmi(
                id = "1234567890",
                operator = "operator",
                company = "company",
                tell = "123-4567-8901"
            ),
            onGrantPermissionClick = {},
            onRevokePermissionClick = {},
            onDialClick = {},
            onMuteClick = {},
            onDeleteClick = {}
        )
    }
}

@Preview
@Composable
fun HmiListItemMultiplePreview() {
    Test1234Theme(darkTheme = true) {
        HmiListItem(
            isConnected = true,
            onHmiClick = {},
            onHmiLongClick = {},
            selectionMode = MainViewModel.SelectionMode.MULTIPLE,
            selected = true,
            hmi = Hmi(
                id = "1234567890",
                operator = "operator",
                company = "company",
                tell = "123-4567-8901"
            ),
            onGrantPermissionClick = {},
            onRevokePermissionClick = {},
            onDialClick = {},
            onMuteClick = {},
            onDeleteClick = {}
        )
    }
}