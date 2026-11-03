package com.rojo.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LogoStudioScreen(
    onGenerate: () -> Unit = {}
) {
    var prompt by remember { mutableStateOf("A minimal wordmark for 'Captain Rojo'") }
    var style by remember { mutableStateOf("minimal") }
    val styles = listOf("minimal", "luxury", "badge", "monogram")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacing(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("Prompt") }, modifier = Modifier.weight(1f, false).fillMaxWidth(), singleLine = true)
        var expanded by remember { mutableStateOf(false) }
        OutlinedTextField(value = style, onValueChange = { style = it }, label = { Text("Style") }, modifier = Modifier.fillMaxWidth().height(48.dp), trailingIcon = {
            IconButton(onClick = { expanded = !expanded }) { Icon(Icons.Default.ArrowDropDown, contentDescription = "Style") }
        })
        if (expanded) DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            styles.forEach { s -> DropdownMenuItem(text = { Text(s) }, onClick = { style = s; expanded = false }) }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacing(8.dp)) {
            styles.forEach { s -> Chip(text = s, selected = (s == style), onClick = { style = s }) }
        }
        PrimaryButton(
            onClick = onGenerate,
            text = "Generate Logo",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
