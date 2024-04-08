package com.example.test1234.ui.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test1234.data.hmi.Hmi
import com.example.test1234.ui.theme.Test1234Theme

@Composable
fun HmiList(
    isConnected: Boolean,
    hmis: List<Hmi>,
    onGrantPermissionClick: (Hmi) -> Unit,
    onRevokePermissionClick: (Hmi) -> Unit,
    onDialClick: (Hmi) -> Unit,
    onMuteClick: (Hmi) -> Unit,
    onDeleteClick: (Hmi) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = hmis,
            key = { it.id }
        ) { hmi ->
            HmiListItem(
                isConnected = isConnected,
                hmi = hmi,
                onGrantPermissionClick = onGrantPermissionClick,
                onRevokePermissionClick = onRevokePermissionClick,
                onDialClick = onDialClick,
                onMuteClick = onMuteClick,
                onDeleteClick = onDeleteClick,
                modifier = Modifier
                    .padding(start = 36.dp, top = 15.dp, end = 5.dp, bottom = 2.dp)
                    .fillMaxWidth()
            )
            if (hmis.size > 1)
                Divider(
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )
        }
    }
}

@Preview
@Composable
fun HmiListPreview() {
    Test1234Theme(darkTheme = true) {
        HmiList(
            isConnected = true,
            hmis = listOf(
                Hmi(
                    id = "1234567890",
                    operator = "operator",
                    company = "company",
                    tell = "123-4567-8901"
                ),
                Hmi(
                    id = "1234567890-2",
                    operator = "operator-2",
                    company = "company-2",
                    tell = "123-4567-8901-2"
                )
            ),
            onGrantPermissionClick = {},
            onRevokePermissionClick = {},
            onDialClick = {},
            onMuteClick = {},
            onDeleteClick = {}
        )
    }
}