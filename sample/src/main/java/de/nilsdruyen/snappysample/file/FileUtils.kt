package de.nilsdruyen.snappysample.file

import android.os.Environment
import java.io.File

object FileUtils {

  fun getDirectory(): File {
    return File(Environment.getExternalStorageDirectory(), "Snappy/")
  }
}