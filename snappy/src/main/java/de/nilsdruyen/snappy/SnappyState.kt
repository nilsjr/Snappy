package de.nilsdruyen.snappy

import de.nilsdruyen.snappy.models.SnappyImage

internal data class SnappyState(
  val images: List<SnappyImage> = emptyList(),
)