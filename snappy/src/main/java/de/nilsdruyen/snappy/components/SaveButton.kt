package de.nilsdruyen.snappy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
internal fun SaveButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
  IconButton(
    onClick = onClick,
    modifier = modifier
      .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
  ) {
    Icon(
      imageVector = Icons.Default.Check,
      contentDescription = null,
      tint = Color.White
    )
  }
}