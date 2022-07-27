package de.nilsdruyen.snappy

import androidx.activity.ComponentActivity

internal const val TAG = "SnappyLog"

internal const val RESULT_MISSING_PERMISSION = ComponentActivity.RESULT_FIRST_USER + 1
internal const val RESULT_ERROR = ComponentActivity.RESULT_FIRST_USER + 2

internal const val EXTRA_MODE = "snappy-mode"
internal const val EXTRA_CONFIG = "snappy-config"
internal const val EXTRA_IMAGES = "snappy-image-uris"
internal const val EXTRA_RESULT_EXCEPTION = "snappy-exception"

internal const val PREFIX = "Snappy"

internal const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
internal const val PHOTO_EXTENSION = ".jpg"