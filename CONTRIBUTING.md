# Contributing to Advanced PDF Viewer

Thank you for taking the time to contribute! Here's everything you need to get started.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Project Structure](#project-structure)
- [Code Style](#code-style)
- [Submitting a Pull Request](#submitting-a-pull-request)
- [Reporting Bugs](#reporting-bugs)
- [Requesting Features](#requesting-features)

---

## Code of Conduct

This project follows a simple rule: be respectful. Constructive feedback, inclusive language, and collaboration are expected from everyone.

---

## How to Contribute

You can help in many ways:

| Way | Examples |
|-----|---------|
| Bug fixes | Fix crashes, rendering glitches, incorrect behavior |
| Features | New viewer capabilities, API methods, platform support |
| Docs | Improve README, add code samples, fix typos |
| Tests | Add unit/instrumentation tests |
| Performance | Reduce memory use, improve render speed |

---

## Development Setup

### Requirements

| Tool | Version |
|------|---------|
| Android Studio | Ladybug (2024.2) or newer |
| JDK | 11+ |
| Android SDK | API 24–35 |
| Kotlin | 2.1.0 (via AGP 9.0.1) |

### Clone and open

```bash
git clone https://github.com/mdakashhossain1/android-pdf-viewer.git
cd android-pdf-viewer
```

Open the project root in Android Studio. Sync Gradle when prompted.

### Run the demo app

Select the `app` run configuration and press **Run** (or `Shift+F10`).
The demo app loads a sample PDF and exercises the main API surface.

### Sync web assets (optional)

If you edit `pdf-viewer.js` or `pdf-viewer.css` at the web layer, copy them into the Android assets folder:

```bash
cp pdf-viewer.js  pdfviewer/src/main/assets/pdfviewer/pdf-viewer.js
cp pdf-viewer.css pdfviewer/src/main/assets/pdfviewer/pdf-viewer.css
```

---

## Project Structure

```
android-pdf-viewer/
├── pdfviewer/                          ← Android library module (publish this)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── assets/pdfviewer/
│       │   ├── viewer.html             ← HTML shell loaded in WebView
│       │   ├── pdf-viewer.js           ← PDF viewer logic (PDF.js based)
│       │   ├── pdf-viewer.css          ← All viewer styles
│       │   ├── pdf.min.js              ← PDF.js 3.11 (bundled, no CDN)
│       │   └── pdf.worker.min.js       ← PDF.js worker (bundled)
│       └── java/com/arknox/pdf/pdfviewer/
│           ├── PdfViewer.kt            ← Main View (WebView wrapper)
│           └── PdfViewerConfig.kt      ← Configuration data class
└── app/                                ← Demo application
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/.../MainActivity.kt    ← Demo Activity
        └── res/layout/activity_main.xml
```

---

## Code Style

### Kotlin

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- 4-space indentation, no tabs
- Max line length: **120 characters**
- Prefer `val` over `var`; prefer expression bodies for simple functions
- Use named arguments for functions with multiple boolean parameters

### JavaScript (pdf-viewer.js)

- 2-space indentation
- Single quotes for strings
- JSDoc comments for public methods
- No framework dependencies — vanilla JS only

### CSS (pdf-viewer.css)

- BEM-style class naming with `pv-` prefix (e.g., `.pv-toolbar`, `.pv-btn`)
- Group properties: layout → box model → typography → visual → animation

### Git commits

Use conventional commit prefixes:

```
feat: add annotation layer support
fix: resolve blank page on rotation in two-page mode
docs: update README Android API table
refactor: extract zoom logic into separate method
test: add instrumentation test for loadUri
chore: bump PDF.js to 3.12
```

---

## Submitting a Pull Request

1. **Fork** the repository and create your branch from `main`:
   ```bash
   git checkout -b feat/my-new-feature
   ```

2. **Make your changes** with focused, atomic commits.

3. **Test** on a real device or emulator (API 24 minimum):
   - Basic load (URL, bytes, asset)
   - Zoom, rotate, search, dark mode, fullscreen
   - File picker flow (if changed)

4. **Push** your branch and open a Pull Request against `main`.

5. Fill in the **PR template** — description, test plan, screenshots if UI changed.

6. A maintainer will review and may request changes. Once approved, it will be squash-merged.

### PR checklist

- [ ] Changes are scoped to the described feature/fix
- [ ] New public API is documented in README
- [ ] No new Lint errors introduced (`./gradlew :pdfviewer:lint`)
- [ ] Tested on API 24 and latest API level
- [ ] CHANGELOG.md updated under `[Unreleased]`

---

## Reporting Bugs

Use the **Bug Report** issue template. Include:

- Android version and device model
- Library version (`implementation("com.github.mdakashhossain1:android-pdf-viewer:X.Y.Z")`)
- Minimal reproducible code snippet
- Logcat output (filter by tag `PdfViewer`)
- Expected vs actual behavior

---

## Requesting Features

Use the **Feature Request** issue template. Describe:

- The use case driving the request
- What the API / behavior should look like
- Any relevant prior art or references

---

## Questions

Open a [Discussion](https://github.com/mdakashhossain1/android-pdf-viewer/discussions) for general questions rather than filing an issue.
