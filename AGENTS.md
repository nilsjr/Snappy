# Project Analysis & AI Assistant Context

This file provides an overview of the project's structure, tech stack, and conventions. It serves as context to save tokens and assist AI agents in understanding the codebase more efficiently.

## Project Overview
**Snappy** is an Android CameraX library for taking snapshots quickly and easily. It is 100% Kotlin-based and heavily utilizes Jetpack Compose. The library relies on the Android Activity Result API for simplified integration and offers both single and multiple image snapshot modes.

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

## Project Structure
The project is a multi-module Gradle project:

### 1. `snappy` (Library Module)
The core library where all the functionality lives (`de.nilsdruyen.snappy`).
* **`components/`**: Jetpack Compose UI components for the camera interface.
* **`controllers/`**: Logic controllers handling camera state and image capturing.
* **`extensions/`**: Kotlin extension functions for standard library and Android components.
* **`models/`**: Data models, primarily for library configuration (`SnappyConfig`) and results (`SnappyResult`).
* **`utils/`**: Helper utilities.
* **Core Classes**: `Snappy.kt` (ActivityResultContract), `SnappyActivity.kt` (main camera UI host), `SnappyViewModel.kt` (state management).

### 2. `sample` (App Module)
A minimal Android application that implements the `snappy` library to demonstrate its features and different operation modes.

## Development Conventions
* **Dependency Management:** Uses Gradle Version Catalogs (`libs.versions.toml`).
* **Code Style:** Enforced by Detekt and `detekt-formatting` rule sets (`detekt.yml`).
* **State Management:** Uses typical Jetpack Compose unidirectional data flow with ViewModels.
* **Permissions:** Snappy requests its own camera permissions, but storage/file permissions must be handled by the consumer application.
