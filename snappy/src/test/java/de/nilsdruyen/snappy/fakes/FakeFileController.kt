package de.nilsdruyen.snappy.fakes

import de.nilsdruyen.snappy.controllers.FileController
import de.nilsdruyen.snappy.models.SnappyImage

internal class FakeFileController : FileController {

  var images = mutableListOf<SnappyImage>()

  override suspend fun deleteImage(image: SnappyImage) {
    // do nothing
  }

  override suspend fun getImages(): List<SnappyImage> = images

  override suspend fun clearTempImages() {
    // do nothing
  }
}