package com.rojo.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shared UI components — single source of truth for buttons/cards/headers so
 * every screen reuses the same look and feel (simplify-code).
 */
@Composable
fun AppBar(title: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Icon(Icons.Default.Speed, contentDescription = null)
    }
}

@Composable
fun PrimaryButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
    Button(onClick = onClick, modifier = modifier.height(48.dp)) { Text(text) }
}

@Composable
fun PrimCard(title: String, body: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp), onClick = onClick, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Chip(
        text = { Text(text, style = if (selected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelSmall) },
        selected = selected,
        onClick = onClick,
        colors = ChipDefaults.chipColors(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
    )
}
