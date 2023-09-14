package de.nilsdruyen.snappysample

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import de.nilsdruyen.snappy.Snappy
import de.nilsdruyen.snappy.models.SnappyConfig
import de.nilsdruyen.snappy.models.SnappyResult
import de.nilsdruyen.snappysample.file.FileUtils

const val TAG = "SnappySample"

@Composable
fun Sample(onClick: () -> Unit) {
  val images = remember { mutableStateOf(emptyList<Uri>()) }
  Column(Modifier.fillMaxSize()) {
    Options(onClick, {
      Log.i(TAG, "images taken: $it")
      images.value = it
    }, Modifier.weight(0.5f))
    Images(images.value, Modifier.weight(0.5f))
  }
}

@Composable
fun Options(onClick: () -> Unit, setImages: (List<Uri>) -> Unit, modifier: Modifier = Modifier) {
  val packageName = LocalContext.current.packageName
  val launcher = rememberLauncherForActivityResult(Snappy()) { result ->
    when (result) {
      is SnappyResult.Success -> {
        setImages(result.images)
      }
      is SnappyResult.Error -> {
        setImages(emptyList())
      }
      else -> {}
    }
  }

  Column(modifier, verticalArrangement = Arrangement.Center) {
    SnappyButton(text = "Take multiple images") {
      onClick()
    }
    Spacer(modifier = Modifier.height(16.dp))
    SnappyButton(text = "Take single image") {
      launcher.launch(
        SnappyConfig(
          outputDirectory = FileUtils.getSnappyDirectory(),
          once = true,
          packageName = packageName,
        )
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    SnappyButton(text = "Take single image custom") {
      launcher.launch(
        SnappyConfig(
          outputDirectory = FileUtils.getSnappyDirectory("/Custom"),
          once = true,
          packageName = packageName,
        )
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    SnappyButton(text = "Take images (custom)") {
      launcher.launch(
        SnappyConfig(
          outputDirectory = FileUtils.getSnappyDirectory("/Custom"),
          once = false,
          withHapticFeedback = false,
          packageName = packageName,
        )
      )
    }
  }
}

@Composable
fun SnappyButton(text: String, onClick: () -> Unit) {
  Button(
    onClick = onClick,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    Text(text = text, color = MaterialTheme.colorScheme.onPrimary)
  }
}

@Composable
fun Images(images: List<Uri>, modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxWidth()) {
    if (images.isEmpty()) {
      Text(
        text = "no images provided",
        modifier = Modifier
          .wrapContentSize()
          .align(Alignment.Center),
        color = MaterialTheme.colorScheme.onSurface,
      )
    } else {
      LazyRow(
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        items(images) { uri ->
          AsyncImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .fillMaxHeight()
              .aspectRatio(.8f)
              .clip(RoundedCornerShape(8.dp))
              .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
          )
        }
      }
    }
  }
}