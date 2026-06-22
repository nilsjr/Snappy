# CLAUDE.md

This file gives Claude Code (claude.ai/code) the context it needs to work in this
repository efficiently. For a high-level project overview and tech-stack details,
see [AGENTS.md](AGENTS.md) — this file focuses on day-to-day commands, architecture
and the workflow conventions that must be followed.

## What this project is

**Snappy** is an Android [CameraX](https://developer.android.com/training/camerax)
library for taking snapshots fast & simple. It is 100% Kotlin and Jetpack Compose
driven and is published to Maven Central as `de.nilsdruyen.snappy:snappy`.

Consumers integrate it through the Android **Activity Result API**: register a
`Snappy()` contract, launch it with a `SnappyConfig`, and receive a `SnappyResult`.
It supports a single-image mode and a multi-image mode.

See [AGENTS.md](AGENTS.md) for the full tech stack and tooling list.

## Module layout

Multi-module Gradle build (`settings.gradle.kts`):

- **`:snappy`** — the published library (`de.nilsdruyen.snappy`). All real
  functionality lives here.
- **`:sample`** — a minimal demo app (`de.nilsdruyen.snappysample`) that exercises
  the library's modes. Not published.

Sources use the legacy `src/main/java/...` path but contain Kotlin.

### Library internals (`:snappy`)

- `Snappy.kt` — the public `ActivityResultContract<SnappyConfig, SnappyResult>`.
  This and the `models/` package are the **public API surface** (`public` visibility);
  everything else is `internal`.
- `SnappyActivity.kt` — the camera host `ComponentActivity`. Reads the parcelized
  config from the launch `Intent`, wires up the `SnappyViewModel`, and sets the
  Compose content. Translates UI callbacks back into `setResult(...)` codes.
- `SnappyViewModel.kt` / `SnappyState.kt` — unidirectional state. Two `StateFlow`s:
  `screen` (which `SnappyScreen` is shown: Permissions / Camera / Gallery) and
  `state` (the list of captured `SnappyImage`s).
- `SnappyMode.kt` — capture mode marker passed through the Intent.
- `components/` — Compose UI: `CameraScreen`, `Camera`, `Gallery`, `ImageList`,
  `CameraPermissions`, `SaveButton`, plus the `ui/` theme (`SnappyTheme`).
- `controllers/` — `FileController` / `FileControllerImpl`: loads, saves and deletes
  captured images via the `ContentResolver`.
- `extensions/` — Kotlin/Android extension helpers (config ↔ parcelable conversion,
  Intent (un)packing, `ImageCapture`, viewModel builder, etc.).
- `models/` — public `SnappyConfig`, `SnappyResult`, `SnappyImage` and the internal
  `ParcelableSnappyConfig`.
- `Constants.kt` — Intent extra keys and the custom result codes
  (`RESULT_ERROR`, `RESULT_MISSING_PERMISSION`).

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
`kotlinx-coroutines-test`. `SnappyViewModelTest` + `FakeFileController` are the
pattern to follow for ViewModel tests; `CoroutinesTestExtension` provides the test
dispatcher.

## Conventions that matter when editing

- **Dependencies**: only via the version catalog `gradle/libs.versions.toml`. Never
  hard-code versions in a `build.gradle.kts`.
- **Explicit API mode**: the `:snappy` module is compiled with
  `-Xexplicit-api=strict`. New top-level/public declarations need explicit
  visibility modifiers and explicit return types. Keep anything not part of the
  public API `internal`.
- **Warnings are errors**: `allWarningsAsErrors` is on. Code must compile clean.
- **Code style**: enforced by Detekt + `detekt-formatting` (`detekt.yml`).
  `@Composable` functions are exempt from `FunctionNaming` (PascalCase is fine).
  Run `./gradlew ktlintFormat` before committing.
- **State management**: Compose unidirectional data flow with `StateFlow` in the
  ViewModel; keep UI stateless and driven by the flows.

## Workflow: Gitflow + Conventional Commits

This repo follows **Gitflow** and **Conventional Commits** — both are required.

### Branching (Gitflow)

- `main` — released/stable code.
- `develop` — integration branch; **the default base for new work**.
- Feature work branches **off `develop`** and is merged back into `develop` via PR.
- CI (`.github/workflows/build.yml`) runs detekt + the sample build on pushes/PRs
  targeting `develop` and `main`.
- **Open PRs against `develop`** (not `main`) unless explicitly doing a release.

### Commits (Conventional Commits)

Format: `type(scope): summary`. Match the existing history, e.g.
`fix(deps): update junit-framework monorepo to v6.1.0`,
`chore: update androidGradle to v9.2.1 ...`.

Common types: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `build`, `ci`.
Useful scopes: `deps`, `snappy`, `sample`, or a feature area. Keep the summary
imperative and concise.

Dependency updates are automated via Renovate (`.github/renovate.json5`).
