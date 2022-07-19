package de.nilsdruyen.snappy.extensions

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider

@MainThread
internal inline fun <reified VM : ViewModel> ComponentActivity.viewModelBuilder(
  noinline viewModelInitializer: () -> VM
): Lazy<VM> {
  return ViewModelLazy(
    viewModelClass = VM::class,
    storeProducer = { viewModelStore },
    factoryProducer = {
      return@ViewModelLazy object : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return viewModelInitializer.invoke() as T
        }
      }
    }
  )
}