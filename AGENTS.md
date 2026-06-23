# AGENTS.md

This file provides guidance to AI coding agents (Claude Code, and others) when working with code in this repository.

## What this is

**Snappy** is an Android CameraX library (published to Maven Central as `de.nilsdruyen.snappy:snappy`) for taking photos quickly and simply. It is 100% Kotlin and heavily Jetpack Compose driven, offering both single- and multiple-image snapshot modes. Consumers integrate it through the **Activity Result API** — they never touch the camera UI directly. The repo is a two-module Gradle build: `:snappy` (the library) and `:sample` (a demo app that consumes it).

## Tech stack

- **Language:** Kotlin (source under `src/main/java/...` despite being Kotlin)
- **UI:** Jetpack Compose (Material 3, ConstraintLayout Compose)
- **Camera:** AndroidX CameraX (`camera-core`, `camera-camera2`, `camera-lifecycle`, `camera-view`)
- **Concurrency:** Kotlin Coroutines
- **Image loading:** Coil & Coil-Compose
- **Build:** Gradle (Kotlin DSL) with a version catalog in `gradle/libs.versions.toml`
- **Static analysis:** Detekt (+ `detekt-formatting`)
- **Docs:** Dokka
- **Testing:** JUnit 5 (Jupiter), AssertJ, Turbine, kotlinx-coroutines-test
- **SDK:** minSdk **23**, compile/target SDK **36** (defined in `libs.versions.toml`; the README's "MinSdk 21" is stale)

## Commands

Use the Gradle wrapper (`./gradlew`, or `gradlew.bat` on Windows).

- Build everything: `./gradlew build`
- Build only the library: `./gradlew :snappy:assemble`
- Install the sample on a device/emulator: `./gradlew :sample:installDebug`
- Unit tests (JUnit 5 / Jupiter): `./gradlew :snappy:test`
- Single test class: `./gradlew :snappy:test --tests "de.nilsdruyen.snappy.SnappyViewModelTest"`
- Single test method: `./gradlew :snappy:test --tests "de.nilsdruyen.snappy.SnappyViewModelTest.*addImage*"`
- Static analysis: `./gradlew detekt`
- Auto-format (detekt ktlint wrapper, autocorrects in place): `./gradlew ktlintFormat`
- Dependency update report: `./gradlew dependencyUpdates`

## Build / language constraints to respect

These are enforced and will fail the build if violated:

- **`allWarningsAsErrors` is on** for all modules — a compiler warning breaks the build.
- **`-Xexplicit-api=strict`** is applied to `:snappy` (but not `:sample`). Every public/internal declaration in the library needs an explicit visibility modifier and explicit return types. Most library internals are deliberately `internal`; only the consumer-facing API (`Snappy`, `SnappyConfig`, `SnappyResult`, etc.) is `public`.
- JVM target is **17**; Kotlin progressive mode is on.
- Tests must use **JUnit 5** (`useJUnitPlatform`, `failFast = true`). Assertions use **AssertJ**, Flow testing uses **Turbine**, coroutines use `kotlinx-coroutines-test`.
- Dependencies are declared exclusively through the **version catalog** at `gradle/libs.versions.toml` — add/upgrade there, not inline in build files.
- Detekt + `detekt-formatting` config is `detekt.yml` (2-space indent; `@Composable` functions are exempt from `FunctionNaming`; `@Parcelize` classes exempt from `LongParameterList`).

## Architecture / control flow

The whole library is a single Activity launched and parsed through one `ActivityResultContract`. Understanding this round-trip is the key to the codebase:

1. **`Snappy`** (`Snappy.kt`) — the `ActivityResultContract<SnappyConfig, SnappyResult>`. `createIntent` serializes the `SnappyConfig` into a `ParcelableSnappyConfig` extra and launches `SnappyActivity`. `parseResult` maps result codes back into the `SnappyResult` sealed interface.
2. **`SnappyActivity`** — host `ComponentActivity`. Reconstructs the config from the intent, builds a `SnappyViewModel` (manually, via the `viewModelBuilder` extension — there is **no DI framework**), and sets the Compose content. It provides the config down the tree through `LocalSnappyConfig` (a `CompositionLocal`). Success/cancel/error are reported back by calling `setResult(...)` + `finish()` with the constant result codes in `Constants.kt`.
3. **`CameraScreen`** — switches between three screens via a `Crossfade` over `SnappyScreen` (`Permissions` → `Camera` → `Gallery`). `SnappyScreen` is the *navigation* state; `SnappyState` is the *data* state (the list of captured images). Both are separate `StateFlow`s on the ViewModel.
4. **`SnappyViewModel`** (`internal`) — owns navigation (`screen`) and data (`state`) flows and mediates all image mutations through a `FileController`. State is updated immutably via `getAndUpdate { copy(...) }`. Uses standard Compose unidirectional data flow.
5. **`FileController` / `FileControllerImpl`** — abstraction over capturing/loading/deleting images on disk through the `ContentResolver`. The interface exists so tests can substitute `FakeFileController`; this is the main seam for unit testing the ViewModel.

The `extensions/` package holds the glue: Intent ↔ model conversions (`IntentExtension`, `SnappyConfigExtension`), the `viewModelBuilder` helper, and `ImageCapture` coroutine wrappers (`ImageCaptureExtension`).

### Module layout

- **`:snappy`** (`de.nilsdruyen.snappy`) — the library. `components/` (Compose camera UI), `controllers/` (camera/file logic), `extensions/` (Kotlin/Android extension functions), `models/` (`SnappyConfig`, `SnappyResult`, etc.), `utils/`. Core classes: `Snappy.kt`, `SnappyActivity.kt`, `SnappyViewModel.kt`.
- **`:sample`** (`de.nilsdruyen.snappysample`) — minimal app demonstrating the library's modes.

### Result codes

Custom result codes live in `Constants.kt` (`RESULT_MISSING_PERMISSION = 21`, `RESULT_ERROR = 20`) alongside the intent-extra keys. Keep `Snappy.parseResult` and `SnappyActivity`'s `setResult` calls in sync when changing these.

## Conventions

- The library requests **only the camera permission** itself (handled in `CameraPermissions`). Storage/file permissions are the consumer app's responsibility.
- `resourcePrefix = "snappy"` is set on the library — prefix any added resources accordingly.
