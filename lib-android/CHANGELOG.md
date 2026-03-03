# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.2] – 2026-03-04

### Fixed
- **compileSdk/targetSdk** — Updated to SDK 36 for AndroidX dependency compatibility

---

## [1.0.0] – 2026-03-04

### Added
- **`PdfViewer`** — drop-in `View` (WebView wrapper) for Android that renders PDF files using PDF.js
- **`PdfViewerConfig`** — Kotlin data class for viewer configuration (dark mode, sidebar, zoom, text layer, thumb size)
- Load PDF from multiple sources:
  - `loadUrl(url)` — HTTP/HTTPS remote URL
  - `loadUri(uri)` — content URI from file picker / `ACTION_GET_CONTENT`
  - `loadBytes(bytes, name?)` — raw `ByteArray`
  - `loadAsset(path)` — file in app `assets/` folder
- Full-text search with regex, case-sensitive, and whole-word options
- Thumbnail sidebar with lazy rendering (IntersectionObserver)
- Document outline / bookmark tree panel
- Zoom: fit-width, fit-page, actual size, 10–400% manual
- Smooth pinch-to-zoom with scroll-position correction on release
- Page rotation ±90°
- Two-page spread view
- Dark mode / light mode toggle
- Fullscreen mode with auto-hiding toolbar (tap to reveal)
- Drag-to-scroll indicator (draggable card, maps drag to viewport scroll)
- Resizable viewer height (drag handle)
- PDF download and canvas-based print
- Drag-and-drop PDF files onto the viewer (web variant)
- Right-click context menu (web variant)
- Keyboard shortcut overlay (web variant)
- Toast notifications
- Status bar (current page, total pages, zoom level)
- Kotlin→JS bridge via `evaluateJavascript`
- JS→Kotlin callbacks via `@JavascriptInterface`
- Action queue — commands issued before viewer ready are flushed on init
- PDF.js **bundled locally** — no internet required for rendering
- `onReady`, `onLoad(pages)`, `onPageChange(page, total)`, `onError(msg)` callbacks
- File picker integration: `onActivityResult` forwarding
- Fullscreen integration: `WindowInsetsController` (API 30+) + legacy flags
- Console log forwarding to Logcat (`PdfViewer` tag)
- Lifecycle helpers: `onResume()`, `onPause()`, `onDestroy()`
- JitPack publishing: `com.github.mdakashhossain1:android-pdf-viewer:1.0.2`

---

## [Unreleased]

### Planned
- Annotation layer (highlight, underline, strikethrough)
- Text copy from selection
- Persistent bookmarks (save/restore across sessions)
- Password-protected PDF support
- Accessibility improvements (TalkBack, content descriptions)
- Compose wrapper (`PdfViewerComposable`)

---

[1.0.2]: https://github.com/mdakashhossain1/android-pdf-viewer/releases/tag/v1.0.2
[1.0.0]: https://github.com/mdakashhossain1/android-pdf-viewer/releases/tag/v1.0.0
[Unreleased]: https://github.com/mdakashhossain1/android-pdf-viewer/compare/v1.0.2...HEAD
