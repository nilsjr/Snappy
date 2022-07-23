package de.nilsdruyen.snappy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun BoxScope.SaveButton(onClick: () -> Unit) {
  IconButton(
    onClick = onClick,
    modifier = Modifier
      .padding(end = 16.dp, top = 32.dp)
      .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
      .align(Alignment.TopEnd)
  ) {
    Icon(
      imageVector = Icons.Default.Check,
      contentDescription = null,
      tint = Color.White
    )
  }
}