package com.rojo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Health screen. Tool environment report and "Re-run check" action. The
 * loading/error state and the callback are passed in and are bound by
 * Delegate C's ViewModel.
 *
 * @param onReRun  called when "Re-run check" is tapped.
 * @param loading  true while the health check is in progress.
 * @param error    non-null error message shown under the button.
 */
@Composable
fun HealthScreen(
    onReRun: () -> Unit,
    loading: Boolean,
    error: String,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Environment report", style = MaterialTheme.typography.titleMedium)

        InfoRow("ffmpeg", "$HOME/AppData/Local/Microsoft/WinGet/Links/ffmpeg.EXE")
        InfoRow("piper", "available")
        InfoRow("cloudflared", "available")

        Button(
            onClick = { onReRun() },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Re-run check")
        }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
