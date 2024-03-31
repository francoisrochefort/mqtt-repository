package com.example.test1234

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private const val TAG = "e-trak MainScreen"

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val isGranted by viewModel.isGranted.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isGranted)
                Icons.Default.CheckCircle
            else
                Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = viewModel::onQueryPermission,
            enabled = isConnected
        ) {
            Text(text = stringResource(id = R.string.query_permission))
        }
    }
}