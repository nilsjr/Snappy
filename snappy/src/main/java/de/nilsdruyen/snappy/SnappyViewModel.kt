package de.nilsdruyen.snappy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.nilsdruyen.snappy.components.SnappyScreen
import de.nilsdruyen.snappy.controllers.FileController
import de.nilsdruyen.snappy.models.SnappyImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch

internal class SnappyViewModel constructor(
  private val fileController: FileController,
) : ViewModel() {

  private val _screen = MutableStateFlow<SnappyScreen>(SnappyScreen.Permissions)
  val screen: StateFlow<SnappyScreen> by lazy { _screen.asStateFlow() }

  private val _state: MutableStateFlow<SnappyState> = MutableStateFlow(SnappyState())
  val state: StateFlow<SnappyState> by lazy {
    loadImages()
    _state.asStateFlow()
  }

  private val currentState
    get() = state.value

  private fun loadImages() {
    viewModelScope.launch {
      val images = fileController.getImages()
      updateState {
        copy(images = images)
      }
    }
  }

  fun addImage(image: SnappyImage) {
    val newList = currentState.images + listOf(image)
    updateState {
      copy(images = newList)
    }
  }

  fun removeImage(image: SnappyImage) {
    viewModelScope.launch {
      fileController.deleteImage(image)
      val images = currentState.images.toMutableList().apply {
        remove(image)
      }
      updateState {
        if (images.isEmpty()) {
          _screen.value = SnappyScreen.Camera
          copy(images = emptyList())
        } else {
          copy(images = images)
        }
      }
    }
  }

  fun showCamera() {
    _screen.value = SnappyScreen.Camera
  }

  fun showGallery(page: Int) {
    _screen.value = SnappyScreen.Gallery(page)
  }

  private fun updateState(setState: SnappyState.() -> SnappyState) {
    _state.getAndUpdate(setState)
  }
}