package de.nilsdruyen.snappysample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Sample(onClick: () -> Unit) {
  Box(Modifier.fillMaxSize()) {
    Button(onClick = onClick, modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)) {
      Text("Take images")
    }
  }
}