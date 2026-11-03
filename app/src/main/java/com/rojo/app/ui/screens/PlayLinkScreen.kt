package com.rojo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Play Link screen. Email/device/exact-error form fields are local state;
 * the "Link Falcon" action and the loading/error state are passed in and are
 * bound by Delegate C's ViewModel.
 *
 * @param onLink  called when "Link Falcon" is tapped.
 * @param loading true while the link operation is in progress.
 * @param error   non-null error message shown under the button.
 */
@Composable
fun PlayLinkScreen(
    onLink: () -> Unit,
    loading: Boolean,
    error: String,
) {
    var email by remember { mutableStateOf("") }
    var device by remember { mutableStateOf("") }
    var exactError by remember { mutableStateOf("") } // local form state; skeleton's `error` slot is now the `error` param

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Google Play email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = device,
            onValueChange = { device = it },
            label = { Text("Device (e.g. Pixel 8 / Android 14)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = exactError,
            onValueChange = { exactError = it },
            label = { Text("Exact error (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = { onLink() },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Link Falcon")
        }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}
