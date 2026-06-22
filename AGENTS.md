# Project Analysis & AI Assistant Context

This file is the **single source of truth** for AI agents working in this repository.
It provides an overview of the project's structure, tech stack, day-to-day commands and
the workflow conventions that must be followed. (`CLAUDE.md` intentionally only points
here to avoid duplication.)

## Project Overview

**Snappy** is an Android [CameraX](https://developer.android.com/training/camerax)
library for taking snapshots quickly and easily. It is 100% Kotlin-based and heavily
utilizes Jetpack Compose. The library relies on the Android **Activity Result API** for
simplified integration and offers both single-image and multi-image snapshot modes.

Consumers integrate it by registering a `Snappy()` contract, launching it with a
`SnappyConfig`, and receiving a `SnappyResult`. It is published to Maven Central as
`de.nilsdruyen.snappy:snappy`.

## Tech Stack & Tooling

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3, ConstraintLayout Compose)
* **Camera API:** AndroidX CameraX (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`)
* **Concurrency:** Kotlin Coroutines
* **Image Loading:** Coil & Coil-Compose
* **Build System:** Gradle (Kotlin DSL, Version Catalog in `gradle/libs.versions.toml`)
* **Static Analysis:** Detekt (with `detekt-formatting`)
* **Documentation:** Dokka
* **Testing:** JUnit 5 (Jupiter), AssertJ, Turbine, kotlinx-coroutines-test
* **Target/Compile SDK:** 36
* **Min SDK:** 23 (configured in `libs.versions.toml`)

## Module layout

Multi-module Gradle build (`settings.gradle.kts`):

* **`:snappy`** — the published library (`de.nilsdruyen.snappy`). All real
  functionality lives here.
* **`:sample`** — a minimal demo app (`de.nilsdruyen.snappysample`) that exercises the
  library's modes. Not published.

Sources use the legacy `src/main/java/...` path but contain Kotlin.

### Library internals (`:snappy`)

* `Snappy.kt` — the public `ActivityResultContract<SnappyConfig, SnappyResult>`. This
  and the `models/` package are the **public API surface** (`public` visibility);
  everything else is `internal`.
* `SnappyActivity.kt` — the camera host `ComponentActivity`. Reads the parcelized config
  from the launch `Intent`, wires up the `SnappyViewModel`, and sets the Compose
  content. Translates UI callbacks back into `setResult(...)` codes.
* `SnappyViewModel.kt` / `SnappyState.kt` — unidirectional state. Two `StateFlow`s:
  `screen` (which `SnappyScreen` is shown: Permissions / Camera / Gallery) and `state`
  (the list of captured `SnappyImage`s).
* `SnappyMode.kt` — capture mode marker passed through the Intent.
* `components/` — Compose UI: `CameraScreen`, `Camera`, `Gallery`, `ImageList`,
  `CameraPermissions`, `SaveButton`, plus the `ui/` theme (`SnappyTheme`).
* `controllers/` — `FileController` / `FileControllerImpl`: loads, saves and deletes
  captured images via the `ContentResolver`.
* `extensions/` — Kotlin/Android extension helpers (config ↔ parcelable conversion,
  Intent (un)packing, `ImageCapture`, viewModel builder, etc.).
* `models/` — public `SnappyConfig`, `SnappyResult`, `SnappyImage` and the internal
  `ParcelableSnappyConfig`.
* `Constants.kt` — Intent extra keys and the custom result codes (`RESULT_ERROR`,
  `RESULT_MISSING_PERMISSION`).

### Public API contracts

```kotlin
data class SnappyConfig(
  val outputDirectory: File,
  val once: Boolean = false,           // true = single-shot mode
  val withHapticFeedback: Boolean = true,
)

sealed interface SnappyResult {
  data class Success(val images: List<Uri>) : SnappyResult
  object Canceled : SnappyResult
  object PermissionDenied : SnappyResult
  data class Error(val exception: Exception) : SnappyResult
}
```

> Snappy requests **camera** permission itself. File/storage permissions are the
> consuming app's responsibility.

## Common commands

All commands use the Gradle wrapper (`./gradlew`). JDK 17 is required.

```bash
./gradlew detekt                 # static analysis (CI gate)
./gradlew ktlintFormat           # auto-format via the detekt-formatting wrapper
./gradlew :snappy:test           # run the library unit tests (JUnit 5)
./gradlew test                   # run all unit tests
./gradlew :sample:assembleDebug  # build the sample app (CI gate)
./gradlew :snappy:assembleRelease
./gradlew clean
```

Tests live in `snappy/src/test/...` and use JUnit 5 (Jupiter), AssertJ, Turbine and
`kotlinx-coroutines-test`. `SnappyViewModelTest` + `FakeFileController` are the pattern
to follow for ViewModel tests; `CoroutinesTestExtension` provides the test dispatcher.

## Development Conventions

* **Dependencies:** only via the version catalog `gradle/libs.versions.toml`. Never
  hard-code versions in a `build.gradle.kts`.
* **Explicit API mode:** the `:snappy` module is compiled with `-Xexplicit-api=strict`.
  New top-level/public declarations need explicit visibility modifiers and explicit
  return types. Keep anything not part of the public API `internal`.
* **Warnings are errors:** `allWarningsAsErrors` is on. Code must compile clean.
* **Code style:** enforced by Detekt + `detekt-formatting` (`detekt.yml`). `@Composable`
  functions are exempt from `FunctionNaming` (PascalCase is fine). Run
  `./gradlew ktlintFormat` before committing.
* **State management:** Compose unidirectional data flow with `StateFlow` in the
  ViewModel; keep UI stateless and driven by the flows.
* **Permissions:** Snappy requests its own camera permissions, but storage/file
  permissions must be handled by the consumer application.

## Workflow: Gitflow + Conventional Commits

This repo follows **Gitflow** and **Conventional Commits** — both are required.

### Branching (Gitflow)

Long-lived branches:

* `main` — released/stable code.
* `develop` — integration branch; **the default base for new work**.

Supporting branches use Gitflow naming and **must** be prefixed accordingly:

| Prefix      | Branches off | Merges back into     | Used for                                  |
|-------------|--------------|----------------------|-------------------------------------------|
| `feature/`  | `develop`    | `develop`            | New features and most day-to-day work     |
| `bugfix/`   | `develop`    | `develop`            | Fixes for issues found on `develop`       |
| `release/`  | `develop`    | `main` + `develop`   | Release preparation (e.g. `release/1.2.0`)|
| `hotfix/`   | `main`       | `main` + `develop`   | Urgent fixes against production            |

Examples: `feature/multi-image-zoom`, `bugfix/gallery-crash`, `hotfix/null-config`.

* CI (`.github/workflows/build.yml`) runs detekt + the sample build on pushes/PRs
  targeting `develop` and `main`.
* **Open PRs against `develop`** (not `main`) unless explicitly doing a release.

### Commits (Conventional Commits)

Format: `type(scope): summary`. Match the existing history, e.g.
`fix(deps): update junit-framework monorepo to v6.1.0`,
`chore: update androidGradle to v9.2.1 ...`.

Common types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `build`, `ci`.
Useful scopes: `deps`, `snappy`, `sample`, or a feature area. Keep the summary
imperative and concise.

Dependency updates are automated via Renovate (`.github/renovate.json5`).
