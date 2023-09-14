package de.nilsdruyen.snappy.extensions

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import androidx.core.net.toUri
import de.nilsdruyen.snappy.EXTRA_IMAGES
import de.nilsdruyen.snappy.EXTRA_RESULT_EXCEPTION

internal fun Intent?.toSnappyData(packageName: String): List<Uri> {
  val name = "$packageName.$EXTRA_IMAGES"
  return when {
    SDK_INT >= 33 -> this?.parcelableList(name, String::class.java)?.map(String::toUri).orEmpty()
    else -> this?.parcelableList(name, Uri::class.java).orEmpty()
  }
}

@Suppress("DEPRECATION")
internal fun Intent?.getRootException(): Exception {
  if (SDK_INT >= 33) {
    this?.getSerializableExtra(EXTRA_RESULT_EXCEPTION, Exception::class.java)
  } else {
    this?.getSerializableExtra(EXTRA_RESULT_EXCEPTION)
  }.let {
    return if (it is Exception) it else IllegalStateException("Could retrieve root exception")
  }
}

@Suppress("DEPRECATION")
private inline fun <reified T> Intent.parcelableList(key: String, clazz: Class<T>): List<T>? = when {
  SDK_INT >= 33 -> getParcelableArrayListExtra(key, clazz)
  else -> getParcelableArrayListExtra(key)
}