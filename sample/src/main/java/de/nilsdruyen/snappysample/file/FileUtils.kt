package de.nilsdruyen.snappysample.file

import android.os.Environment
import java.io.File

object FileUtils {

  fun getSnappyDirectory(): File = File(Environment.getExternalStorageDirectory(), "Snappy/")

  fun getDownloadDir() = File(Environment.DIRECTORY_DOWNLOADS)
}