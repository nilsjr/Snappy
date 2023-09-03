package de.nilsdruyen.snappy.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPagerIndicator
import de.nilsdruyen.snappy.SnappyViewModel
import de.nilsdruyen.snappy.models.SnappyImage

@Composable
internal fun Gallery(viewModel: SnappyViewModel, page: Int) {
  val state by viewModel.state.collectAsState()

  BackHandler {
    viewModel.showCamera()
  }

  Gallery(images = state.images, page, viewModel::removeImage)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Gallery(images: List<SnappyImage>, page: Int, removeImage: (SnappyImage) -> Unit) {
  val pagerState = rememberPagerState { images.size }

  Column(
    Modifier
      .fillMaxSize()
      .navigationBarsPadding()
      .background(Color.Black)
  ) {
    HorizontalPager(
      state = pagerState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      contentPadding = PaddingValues(24.dp),
      pageSpacing = 8.dp
    ) { page ->
      ImagePage(image = images[page], removeImage)
    }
    HorizontalPagerIndicator(
      pagerState = pagerState,
      pageCount = images.size,
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 16.dp, top = 8.dp),
      activeColor = Color.White,
    )
  }

  LaunchedEffect(Unit) {
    pagerState.scrollToPage(page)
  }
}

@Composable
private fun ImagePage(image: SnappyImage, removeImage: (SnappyImage) -> Unit) {
  Box(modifier = Modifier.fillMaxSize()) {
    AsyncImage(
      model = image.uri,
      contentDescription = null,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(8.dp)),
    )
    IconButton(
      onClick = {
        removeImage(image)
      },
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 16.dp)
        .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
    ) {
      Icon(
        imageVector = Icons.Default.Delete,
        contentDescription = null,
        tint = Color.White
      )
    }
  }
}