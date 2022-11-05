package de.nilsdruyen.snappy.models

import android.net.Uri
import java.io.File

internal data class SnappyImage(val path: String) {

  val uri: Uri?
    get() = Uri.parse(path)

  val file
    get() = File(path)
}