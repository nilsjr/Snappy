package de.nilsdruyen.snappy

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import de.nilsdruyen.snappy.extensions.getRootException
import de.nilsdruyen.snappy.extensions.toParcelable
import de.nilsdruyen.snappy.extensions.toSnappyData
import de.nilsdruyen.snappy.models.SnappyConfig
import de.nilsdruyen.snappy.models.SnappyResult

public class Snappy : ActivityResultContract<SnappyConfig, SnappyResult>() {

  override fun createIntent(context: Context, input: SnappyConfig): Intent {
    return Intent(context, SnappyActivity::class.java).apply {
      putExtra(EXTRA_MODE, SnappyMode.DEFAULT)
      putExtra(EXTRA_CONFIG, input.toParcelable())
    }
  }

  override fun parseResult(resultCode: Int, intent: Intent?): SnappyResult {
    return when (resultCode) {
      RESULT_OK -> SnappyResult.Success(intent.toSnappyData())
      RESULT_CANCELED -> SnappyResult.Canceled
      RESULT_MISSING_PERMISSION -> SnappyResult.Canceled
      RESULT_ERROR -> SnappyResult.Error(intent.getRootException())
      else -> SnappyResult.Error(IllegalStateException("Unknown activity result code $resultCode"))
    }
  }
}