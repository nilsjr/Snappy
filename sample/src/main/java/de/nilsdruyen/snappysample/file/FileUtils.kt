package de.nilsdruyen.snappysample.file

import android.os.Environment
import java.io.File

object FileUtils {

  fun getSnappyDirectory(custom: String = ""): File = File(Environment.getExternalStorageDirectory(), "Snappy$custom/")
}