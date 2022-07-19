package de.nilsdruyen.snappysample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import de.nilsdruyen.snappy.Snappy
import de.nilsdruyen.snappy.models.SnappyConfig
import de.nilsdruyen.snappy.models.SnappyResult
import de.nilsdruyen.snappysample.databinding.MainActivityBinding
import de.nilsdruyen.snappysample.file.FileUtils

class MainActivity : ComponentActivity() {

  private lateinit var binding: MainActivityBinding

  private val snappy = registerForActivityResult(Snappy(), ::setResult)
  private val filePermissionResult =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grandResults ->
      if (grandResults.isNotEmpty() && grandResults.all { it.value }) {
        launchSnappy()
      }
    }
  private val settingsResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    when (result.resultCode) {
      RESULT_OK -> {
        launchSnappy()
      }
      else -> {
        Log.i("MainActivity", result.toString())
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = MainActivityBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.createPhotoButton.setOnClickListener {
      if (checkPermission()) {
        launchSnappy()
      } else {
        requestPermission()
      }
    }
  }

  private fun launchSnappy() {
    snappy.launch(SnappyConfig(FileUtils.getDirectory()))
  }

  private fun checkPermission(): Boolean {
    val checkReadPermission = ContextCompat.checkSelfPermission(
      this,
      Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    val checkWritePermission = ContextCompat.checkSelfPermission(
      this,
      Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
    return when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
        Environment.isExternalStorageManager()
      }
      else -> checkReadPermission && checkWritePermission
    }
  }

  private fun requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      try {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.addCategory("android.intent.category.DEFAULT")
        intent.data = Uri.parse("package:${applicationContext.packageName}")
        settingsResult.launch(intent)
      } catch (e: Exception) {
        Log.e("MainActivity", "error ${e.localizedMessage}")
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
        settingsResult.launch(intent)
      }
    } else {
      filePermissionResult.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }
  }

  private fun setResult(result: SnappyResult) {
    Log.i("MainActivity", result.toString())
    showSnackbar(result)
  }

  private fun showSnackbar(result: SnappyResult) {
//    PathUtils.getRealPath(this, result.image) ?: ""
    val text = when (result) {
      is SnappyResult.Success -> {
        result.images.size.toString()
      }
      SnappyResult.Canceled -> "User canceled"
      SnappyResult.PermissionDenied -> "Missing permission"
      is SnappyResult.Error -> "${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}"
    }

    Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE).apply {
      view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.run {
        maxLines = 5
        setTextIsSelectable(true)
      }
    }.show()
  }
}