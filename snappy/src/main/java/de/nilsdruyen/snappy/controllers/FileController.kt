package de.nilsdruyen.snappy.controllers

import de.nilsdruyen.snappy.models.SnappyImage

internal interface FileController {

  suspend fun deleteImage(image: SnappyImage)
}