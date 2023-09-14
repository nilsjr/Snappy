package de.nilsdruyen.snappy.models

import java.io.File

public data class SnappyConfig(
  val outputDirectory: File,
  val once: Boolean = false,
  val withHapticFeedback: Boolean = true,
  val packageName: String = "",
)