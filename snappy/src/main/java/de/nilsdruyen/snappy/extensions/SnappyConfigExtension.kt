package de.nilsdruyen.snappy.extensions

import de.nilsdruyen.snappy.models.ParcelableSnappyConfig
import de.nilsdruyen.snappy.models.SnappyConfig

internal fun SnappyConfig.toParcelable(): ParcelableSnappyConfig = ParcelableSnappyConfig(
  outputDirectory = outputDirectory,
  once = once,
  withHapticFeedback = withHapticFeedback,
  packageName = packageName,
)

internal fun ParcelableSnappyConfig.toModel(): SnappyConfig = SnappyConfig(
  outputDirectory = outputDirectory,
  once = once,
  withHapticFeedback = withHapticFeedback,
  packageName = packageName,
)