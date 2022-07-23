package de.nilsdruyen.snappy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nilsdruyen.snappy.components.SnappyScreen
import de.nilsdruyen.snappy.controllers.FileController
import de.nilsdruyen.snappy.models.SnappyImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SnappyViewModel constructor(
  private val fileController: FileController
) : ViewModel() {

  private val _state: MutableStateFlow<SnappyState> = MutableStateFlow(SnappyState())
  val state: StateFlow<SnappyState> by lazy {
    loadImages()
    _state.asStateFlow()
  }

  private fun loadImages() {
    viewModelScope.launch {
      val images = fileController.getImages()
      updateState {
        copy(images = images)
      }
    }
  }

  fun addImage(image: SnappyImage) {
    updateState {
      copy(
        images = images.toMutableList().apply {
          add(image)
        }
      )
    }
  }

  fun removeImage(image: SnappyImage) {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        fileController.deleteImage(image)
      }
    }
    updateState {
      val images = images.toMutableList().apply {
        remove(image)
      }
      if (images.isEmpty()) {
        copy(
          screen = SnappyScreen.Camera,
          images = emptyList()
        )
      } else {
        copy(
          images = images.toMutableList().apply {
            remove(image)
          }
        )
      }
    }
  }

  fun showCamera() {
    _state.value = state.value.copy(screen = SnappyScreen.Camera)
  }

  fun showGallery(page: Int) {
    _state.value = state.value.copy(screen = SnappyScreen.Gallery(page))
  }

  private fun updateState(setState: SnappyState.() -> SnappyState) {
    _state.value = setState(state.value)
  }
}