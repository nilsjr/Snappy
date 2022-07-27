<p align="center">
  <img width="100" height="100" src="https://raw.githubusercontent.com/nilsjr/snappy/gh-pages/images/ic_launcher_round.png">
</p>

**Snappy** is an android camerax library for taking snapshot fast & simple. Easy to integrate, 100% Kotlin & jetpack
compose driven.

- Activity Result API usage
- Android Jetpack CameraX for displaying preview
- Coil for image loading
- Different modes (single image & multiple images)

## Download 

Available on `mavenCentral()`

[![Maven Central](https://img.shields.io/maven-central/v/de.nilsdruyen.snappy/snappy)](https://search.maven.org/search?q=g:de.nilsdruyen.snappy)

```kotlin
implementation("de.nilsdruyen.snappy:snappy:0.0.1")
```

## Requirements

- AndroidX
- MinSdk 21

---

<center><b>⚠️Important</b></center>

**File/Storage permissions should be requested by your app. Only camera permissions are requested by Snappy**

---

## Usage

To use Snappy in our app you need to add following code snippets.

<details open>
  <summary>Kotlin</summary>

```kotlin
import de.nilsdruyen.snappy.Snappy
import de.nilsdruyen.snappy.models.SnappyConfig
import de.nilsdruyen.snappy.models.SnappyResult

// setup snappy activity result launcher
private val launcher = registerForActivityResult(Snappy()) { result ->
  // do something
}

// or in compose
val launcher = rememberLauncherForActivityResult(Snappy()) { result ->
  when (result) {
    is SnappyResult.Success -> {
      // do something
    }
    else -> {
      // 
    }
  }
}

launcher.launch(SnappyConfig(File("")))
```

</details>

**SnappyConfig**

```kotlin
import de.nilsdruyen.snappy.models.SnappyConfig

val config = SnappyConfig(
  outputDirectory = File("path/"),    // no default
  once = true,                        // default = false
  withHapticFeedback = true,          // default = true
)
```

**SnappyResult**

```kotlin
import de.nilsdruyen.snappy.models.SnappyResult

public sealed interface SnappyResult {

  public data class Success(val images: List<Uri>) : SnappyResult
  public object Canceled : SnappyResult
  public object PermissionDenied : SnappyResult
  public data class Error(val exception: Exception) : SnappyResult
}
```

<details>
  <summary>Java</summary>

```java
import de.nilsdruyen.snappy.Snappy;
import de.nilsdruyen.snappy.models.SnappyConfig;
import de.nilsdruyen.snappy.models.SnappyResult;

class Activity {

  // setup snappy activity result launcher
  private ActivityResultLauncher<SnappyConfig> snappy = registerForActivityResult(new Snappy(), (result) -> {
    if (result instanceof SnappyResult.Success) {
      List<Uri> images = ((SnappyResult.Success) result).component1();

    }
  });

  // launch snappy activity
  private void launch() {
    snappy.launch(new SnappyConfig(new File("path"), true, true));
  }
}
```

</details>

## Screenshots

Single snapshot mode

| Single snapshot mode |
| --- |
| <img src="https://raw.githubusercontent.com/nilsjr/snappy/gh-pages/images/snappy_single.jpg" alt="drawing" width="200"/> |

Multiple snapshot mode
| No images | With images | Image gallery |
| --- | --- | --- |
| <img src="https://raw.githubusercontent.com/nilsjr/snappy/gh-pages/images/snappy_multi_no_image.jpg" alt="drawing" width="200"/> | <img src="https://raw.githubusercontent.com/nilsjr/snappy/gh-pages/images/snappy_multi_images.jpg" alt="drawing" width="200"/> | <img src="https://raw.githubusercontent.com/nilsjr/snappy/gh-pages/images/snappy_multi_gallery.jpg" alt="drawing" width="200"/> |

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md)

## License

    The MIT License (MIT)

    Copyright (C) 2022 Nils Druyen

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
    associated documentation files (the "Software"), to deal in the Software without restriction,
    including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
    and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
    subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial
    portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
    LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
    DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
    OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.