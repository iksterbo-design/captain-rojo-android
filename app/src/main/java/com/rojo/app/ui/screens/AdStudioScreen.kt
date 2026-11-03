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
fun AdStudioScreen(
    onRender: () -> Unit = {}
) {
    var plan by remember { mutableStateOf("1. Hook\n2. Body\n3. CTA") }
    var length by remember { mutableStateOf("20s") }
    val lengths = listOf("10s", "20s", "30s")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacing(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(value = plan, onValueChange = { plan = it }, label = { Text("Scene plan") }, modifier = Modifier.weight(1f, false).fillMaxWidth(), minLines = 6)
        var lenExpanded by remember { mutableStateOf(false) }
        OutlinedTextField(value = length, onValueChange = { length = it }, label = { Text("Length") }, modifier = Modifier.fillMaxWidth().height(48.dp), trailingIcon = {
            IconButton(onClick = { lenExpanded = !lenExpanded }) { Icon(Icons.Default.ArrowDropDown, contentDescription = "Length") }
        })
        if (lenExpanded) DropdownMenu(expanded = lenExpanded, onDismissRequest = { lenExpanded = false }) {
            lengths.forEach { l -> DropdownMenuItem(text = { Text(l) }, onClick = { length = l; lenExpanded = false }) }
        }
        PrimCard(title = "Pair video + voiceover", body = "Runway/Veo/Pika -> pipe output -> add piper TTS -> mux", onClick = { /* TODO: call ad_gen tool */ })
        PrimaryButton(
            onClick = onRender,
            text = "Render Ad",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
