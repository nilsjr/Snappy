package de.nilsdruyen.snappy

import de.nilsdruyen.snappy.components.SnappyScreen
import de.nilsdruyen.snappy.models.SnappyImage

internal data class SnappyState(
  val screen: SnappyScreen = SnappyScreen.Permissions,
  val images: List<SnappyImage> = emptyList(),
)