package com.example.test1234.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.test1234.data.hmi.Hmi
import com.example.test1234.ui.theme.Test1234Theme

@Composable
fun HmiListItem(
    isConnected: Boolean,
    hmi: Hmi,
    onGrantPermissionClick: (Hmi) -> Unit,
    onRevokePermissionClick: (Hmi) -> Unit,
    onDialClick: (Hmi) -> Unit,
    onMuteClick: (Hmi) -> Unit,
    onDeleteClick: (Hmi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = hmi.company)
        Text(
            text = hmi.tell,
            fontSize = 12.sp
        )
        Text(
            text = hmi.operator,
            fontSize = 12.sp
        )
        Text(
            text = hmi.id,
            fontSize = 12.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onDialClick(hmi) }
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null
                )
            }
            if (hmi.ringing) {
                IconButton(
                    onClick = { onMuteClick(hmi) }
                ) {
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
            IconButton(
                onClick = { onDeleteClick(hmi) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
fun HmiListItemPreview() {
    Test1234Theme(darkTheme = true) {
        HmiListItem(
            isConnected = true,
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