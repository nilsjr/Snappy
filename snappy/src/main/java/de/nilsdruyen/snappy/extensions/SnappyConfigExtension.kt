package de.nilsdruyen.snappy.extensions

import de.nilsdruyen.snappy.models.ParcelableSnappyConfig
import de.nilsdruyen.snappy.models.SnappyConfig

internal fun SnappyConfig.toParcelable(): ParcelableSnappyConfig {
  return ParcelableSnappyConfig(outputDirectory)
}

internal fun ParcelableSnappyConfig.toModel(): SnappyConfig {
  return SnappyConfig(outputDirectory)
}