package de.nilsdruyen.snappy.components

internal sealed interface CameraUiAction {
  object OnCameraClick : CameraUiAction
}

internal sealed interface SnappyScreen {
  object Permissions : SnappyScreen
  object Camera : SnappyScreen
  data class Gallery(val page: Int) : SnappyScreen
}