# Contributing to Rush

Thank you for your interest in contributing to Rush! We appreciate your support.

## Project Structure
Rush is organized into several modules to support Kotlin Multiplatform (KMP) and maintain a clean separation of concerns:

- **`:androidApp`**: The main Android application module. It contains Android-specific configurations, entry points, and orchestrates dependency injection.
- **`:desktopApp`**: The main Desktop (Linux) application module. Can also be used for hot-reload with `desktopApp:hotRunJvm --auto -PmainClass=com.shub39.rush.MainKt` 
- **`:shared:core`**: A Kotlin Multiplatform module containing shared interfaces, data models, enums, and basic utility functions.
- **`:shared:logic`**: A Kotlin Multiplatform module containing the core business logic, including repository implementations, networking, database management, and shared dependency injection.
- **`:shared:ui`**: A Compose Multiplatform module containing UI components, themes, screens, and ViewModels. Most UI-related changes should happen here.
- **`:androidLibs`**: Contains Android-specific libraries:
    - `:androidLibs:romanization`: Handles romanization of various languages.
    - `:androidLibs:visualizer-helper`: Helpers for audio visualization.

## How to Contribute
1. **Fork the Repository:** Fork the repository to your own GitHub account.
2. **Create a Branch:** Create a new branch from `dev` for your feature or bug fix. Use a 
   descriptive name, such as `feat/add-new-feature` or `fix/resolve-issue-123`.
3. **Make Your Changes:** 
    - **Shared Code:** Prefer putting core definitions in `:shared:core`, business logic implementations in `:shared:logic`, and UI/ViewModels in `:shared:ui` within `commonMain` to ensure they are available on all platforms.
    - **Platform-Specific Code:** Only use platform-specific source sets (`androidMain`, `jvmMain`, etc.) when absolutely necessary using `expect`/`actual` or platform-specific APIs.
4. **Submit a Pull Request:** Once you're ready, submit a pull request from your branch to the `dev`
   branch of the main repository. Please reference the issue you created in your pull request 
   description.

## Pull Request Guidelines
*   Ensure your code follows the existing style and conventions.
*   Write clear and concise commit messages.
*   Make sure your changes are well-tested.
*   Run spotless `./gradlew spotlessApply` before making a commit to ensure code formatting.

## Compose Guidelines
* **Modifier:** Always expose a `Modifier` parameter for each composable to allow customization from the caller.
* **Previews:** Write Previews for significant composables (components, screens, etc.) in `shared:ui`. 
* **Theming:** Use `@PreviewWrapper(RushPreviewWrapper::class)` for previews to ensure consistent theming across all previews.
* **Location:** Keep previews in the same file as the composable, preferably as a private function at the bottom of the file.

I'll review your pull request as soon as possible. Thank you for your contribution!
