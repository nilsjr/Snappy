package de.nilsdruyen.snappy

import androidx.activity.ComponentActivity

internal object Constants {

  internal const val TAG = "Snappy"

  const val RESULT_MISSING_PERMISSION = ComponentActivity.RESULT_FIRST_USER + 1
  const val RESULT_ERROR = ComponentActivity.RESULT_FIRST_USER + 2

  const val EXTRA_MODE = "snappy-mode"
  const val EXTRA_CONFIG = "snappy-config"
  const val EXTRA_IMAGES = "snappy-image-uris"
  const val EXTRA_RESULT_EXCEPTION = "snappy-exception"
}