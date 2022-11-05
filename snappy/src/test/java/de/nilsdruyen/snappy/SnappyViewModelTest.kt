package de.nilsdruyen.snappy

import app.cash.turbine.test
import de.nilsdruyen.snappy.fakes.FakeFileController
import de.nilsdruyen.snappy.models.SnappyImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
internal class SnappyViewModelTest {

  private val fileController = FakeFileController()
  private val tested = SnappyViewModel(fileController)

  @BeforeEach
  fun setup() {
    fileController.images.clear()
  }

  @Test
  fun `Init viewmodel and loadImages`() = runTest {
    val images = mutableListOf(SnappyImage("file://test.jpg"))
    fileController.images = images
    tested.state.test {
      runCurrent()
      assertThat(awaitItem()).isEqualTo(SnappyState(emptyList()))
      assertThat(awaitItem()).isEqualTo(SnappyState(images = images))
      ensureAllEventsConsumed()
    }
  }

  @Test
  fun `Init viewmodel and addImage`() = runTest {
    val image = SnappyImage("file://test.jpg")
    tested.state.test {
      runCurrent()
      assertThat(awaitItem()).isEqualTo(SnappyState(emptyList()))

      tested.addImage(image)
      runCurrent()

      assertThat(awaitItem()).isEqualTo(SnappyState(images = listOf(image)))
      ensureAllEventsConsumed()
    }
  }

  @Test
  fun `Init viewmodel and addImage multiple times`() = runTest {
    val images = listOf(
      SnappyImage("file://test.jpg"),
      SnappyImage("file://test2.jpg"),
    )
    tested.state.test {
      runCurrent()
      assertThat(awaitItem()).isEqualTo(SnappyState(emptyList()))

      tested.addImage(images.first())
      runCurrent()

      assertThat(awaitItem()).isEqualTo(SnappyState(images = listOf(images.first())))

      tested.addImage(images[1])
      runCurrent()

      assertThat(awaitItem()).isEqualTo(SnappyState(images = images))
      ensureAllEventsConsumed()
    }
  }

  @Test
  fun `Init viewmodel and removeImage`() = runTest {
    val images = listOf(
      SnappyImage("file://test.jpg"),
      SnappyImage("file://test2.jpg"),
    )
    fileController.images = images.toMutableList()

    tested.state.test {
      runCurrent()
      assertThat(awaitItem()).isEqualTo(SnappyState(emptyList()))
      assertThat(awaitItem()).isEqualTo(SnappyState(images = images))

      tested.removeImage(images.first())
      runCurrent()

      assertThat(awaitItem()).isEqualTo(SnappyState(images = images.drop(1)))
      ensureAllEventsConsumed()
    }
  }

  @Test
  fun `Init viewmodel and remove multiple images`() = runTest {
    val images = listOf(
      SnappyImage("file://test.jpg"),
      SnappyImage("file://test2.jpg"),
    )
    fileController.images = images.toMutableList()

    tested.state.test {
      runCurrent()
      assertThat(awaitItem()).isEqualTo(SnappyState(emptyList()))
      assertThat(awaitItem()).isEqualTo(SnappyState(images = images))

      tested.removeImage(images.last())
      runCurrent()

      assertThat(awaitItem()).isEqualTo(SnappyState(images = images.dropLast(1)))

      tested.removeImage(images.first())
      runCurrent()

      assertThat(awaitItem()).isEqualTo(SnappyState(images = emptyList()))
      ensureAllEventsConsumed()
    }
  }
}