**Snappy** is an android camerax library for taking snapshot fast & simple.

- Activity Result API usage
- Compose driven ui
- Android Jetpack CameraX for displaying preview
- Coil for image loading

## Usage

```kotlin
implementation("de.nilsdruyen.snappy:snappy:0.0.1")
```

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

### Requirements

- AndroidX
- MinSdk 21

## Customization

tbd.

## Screenshots

tbd.

## contributing

See [CONTRIBUTING](CONTRIBUTING.md)

## questions

- release management (changelogs, mavencentral)
- Readme badge mavenCentral
- Readme important file/storage permission has to be granted before
- Roadmap? v2 with color theming? more options torch, switch cam?

## features

- delete images when canceled
- config builder pattern
- improve ui (flash, haptic feedback)

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