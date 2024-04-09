package com.example.test1234.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.test1234.R
import com.example.test1234.data.hmi.Hmi
import com.example.test1234.ui.theme.Test1234Theme

@Composable
fun HmiList(
    isConnected: Boolean,

    onHmiClick: (Hmi) -> Unit,
    onHmiLongClick: (Hmi) -> Unit,
    selectionMode: MainViewModel.SelectionMode,
    selection: List<Hmi>,
    selectAll: (hmis: List<Hmi>) -> Unit,

    hmis: List<Hmi>,
    onGrantPermissionClick: (Hmi) -> Unit,
    onRevokePermissionClick: (Hmi) -> Unit,
    onDialClick: (Hmi) -> Unit,
    onMuteClick: (Hmi) -> Unit,
    onDeleteClick: (Hmi) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AnimatedVisibility(visible = selectionMode == MainViewModel.SelectionMode.MULTIPLE) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadioButton(
                    selected = hmis.size == selection.size,
                    onClick = {
                        selectAll(hmis)
                    }
                )
                Text(
                    text = stringResource(id = R.string.ui_main_hmi_list_all),
                    fontSize = 10.sp
                )
            }
        }
        LazyColumn {
            items(
                items = hmis,
                key = { it.id }
            ) { hmi ->
                HmiListItem(
                    isConnected = isConnected,
                    hmi = hmi,

                    onHmiClick = onHmiClick,
                    onHmiLongClick = onHmiLongClick,
                    selectionMode = selectionMode,
                    selected = hmi in selection,

                    onGrantPermissionClick = onGrantPermissionClick,
                    onRevokePermissionClick = onRevokePermissionClick,
                    onDialClick = onDialClick,
                    onMuteClick = onMuteClick,
                    onDeleteClick = onDeleteClick,
                    modifier = Modifier.fillMaxWidth()
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
}

@Preview
@Composable
fun HmiListPreview() {
    Test1234Theme(darkTheme = true) {
        HmiList(
            isConnected = true,
            onHmiClick = {},
            onHmiLongClick = {},
            selectionMode = MainViewModel.SelectionMode.NORMAL,
            selection = listOf(),
            selectAll = { },

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