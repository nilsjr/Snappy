package de.nilsdruyen.snappy.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
internal data class ParcelableSnappyConfig(
  val outputDirectory: File,
  val once: Boolean,
  val withHapticFeedback: Boolean,
) : Parcelable