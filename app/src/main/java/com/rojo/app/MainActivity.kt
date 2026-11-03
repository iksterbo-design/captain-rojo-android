package com.rojo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single Activity — Hilt-backed. Four destinations threaded through a bottom bar.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaptainRojoTheme {
                val nav by remember { mutableStateOf(0) }
                Shell(nav) { idx -> nav = idx }
            }
        }
    }
}

@Composable
private fun CaptainRojoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(surface = Color(0xFFFBF8F2))) {
        content()
    }
}

@Composable
private fun Shell(selected: Int, onSelect: (Int) -> Unit) {
    val tabs = listOf("Logo Studio", "Ad Studio", "Play Link", "Health")
    val icons = listOf(Icons.Default.PictureInPicture, Icons.Default.VideoCamera, Icons.Default.Sync, Icons.Default.HealthAndSafety)
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Surface(modifier = Modifier.fillMaxWidth().height(72.dp), shadowElevation = 6.dp) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp)) {
                tabs.forEachIndexed { i, title ->
                    BottomNavigationItem(
                        label = { Text(title) },
                        icon = { Icon(icons[i], contentDescription = null) },
                        selected = selected == i,
                        onClick = { onSelect(i) },
                        modifier = Modifier.height(64.dp)
                    )
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            when (selected) {
                0 -> LogoStudioScreen()
                1 -> AdStudioScreen()
                2 -> PlayLinkScreen()
                else -> HealthScreen()
            }
        }
    }
}
