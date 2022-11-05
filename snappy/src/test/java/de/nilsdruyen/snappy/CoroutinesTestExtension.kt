package de.nilsdruyen.snappy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
internal interface CoroutineTest {
  var testScope: TestScope
  var dispatcher: TestDispatcher
}

@ExperimentalCoroutinesApi
internal class CoroutinesTestExtension(
  val testDispatcher: TestDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
) : BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor {

  val testScope = TestScope(testDispatcher)

  override fun beforeAll(context: ExtensionContext?) {
    Dispatchers.setMain(testDispatcher)
  }

  override fun afterAll(context: ExtensionContext?) {
    Dispatchers.resetMain()
  }

  override fun postProcessTestInstance(testInstance: Any?, context: ExtensionContext?) {
    (testInstance as? CoroutineTest)?.let { coroutineTest ->
      coroutineTest.testScope = testScope
      coroutineTest.dispatcher = testDispatcher
    }
  }
}