package de.nilsdruyen.snappy.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import de.nilsdruyen.snappy.SnappyViewModel
import de.nilsdruyen.snappy.models.SnappyImage

@Composable
internal fun ImageList(viewModel: SnappyViewModel) {
  val state by viewModel.state.collectAsState()

  Crossfade(state.images.isEmpty()) {
    if (it) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.Black.copy(0.3f))
          .height(96.dp)
      ) {
        Text(text = "No images yet", Modifier.align(Alignment.Center), color = Color.White)
      }
    } else {
      ImageList(
        images = state.images,
        showImage = viewModel::showGallery,
      )
    }
  }
}

@Composable
private fun ImageList(
  images: List<SnappyImage>,
  showImage: (Int) -> Unit,
) {
  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color.Black.copy(0.4f))
      .height(96.dp),
    contentPadding = PaddingValues(4.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    itemsIndexed(images) { index, image ->
      Image(image) { showImage(index) }
    }
  }
}

@Composable
private fun Image(
  image: SnappyImage,
  showImage: () -> Unit = {},
) {
  AsyncImage(
    model = image.uri,
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier
      .aspectRatio(0.8f)
      .clip(RoundedCornerShape(8.dp))
      .clickable {
        showImage()
      },
  )
}