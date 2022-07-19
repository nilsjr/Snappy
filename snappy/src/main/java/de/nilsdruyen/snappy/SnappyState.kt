package de.nilsdruyen.snappy

import de.nilsdruyen.snappy.components.SnappyScreen
import de.nilsdruyen.snappy.models.SnappyConfig
import de.nilsdruyen.snappy.models.SnappyImage
import java.io.File

internal data class SnappyState(
  val config: SnappyConfig = SnappyConfig(File("")),
  val screen: SnappyScreen = SnappyScreen.Permissions,
  val images: List<SnappyImage> = emptyList(),
)