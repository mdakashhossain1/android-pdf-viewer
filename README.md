# Advanced PDF Viewer

[![](https://jitpack.io/v/mdakashhossain1/android-pdf-viewer.svg)](https://jitpack.io/#mdakashhossain1/android-pdf-viewer)

A full-featured PDF viewer — search, thumbnails, zoom, dark mode, rotate, two-page view,
fullscreen, drag-to-scroll indicator — built on PDF.js and wrapped in an Android library.

---

## Android — JitPack Installation

### Step 1 — Add JitPack to `settings.gradle.kts`

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }   // ← add this
    }
}
```

### Step 2 — Add the dependency to `app/build.gradle.kts`

```kotlin
dependencies {
    implementation("com.github.mdakashhossain1:android-pdf-viewer:1.0.2")
}
```

> Replace `1.0.0` with the [latest release tag](https://github.com/mdakashhossain1/android-pdf-viewer/releases).

---

## Web Usage

Copy three files into your project. No bundler or npm needed.

```
your-project/
├── pdf-viewer.css
├── pdf-viewer.js
└── index.html
```

### Minimal HTML

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="pdf-viewer.css">
  <style>
    html, body { margin: 0; height: 100%; overflow: hidden; }
    #viewer    { width: 100%; height: 100%; }
  </style>
</head>
<body>
  <div id="viewer"></div>
  <script src="pdf-viewer.js"></script>
  <script>
    const viewer = new AdvancedPDFViewer('#viewer', {
      darkMode:    false,
      sidebarOpen: true,
      zoom:        'fitWidth',
    });

    // Load from URL
    viewer.loadURL('https://example.com/document.pdf');
  </script>
</body>
</html>
```

### Web API

```js
const viewer = new AdvancedPDFViewer('#selector', options);

viewer.loadURL(url)                         // load from HTTP/HTTPS URL
viewer.loadFile(file)                       // load from <input type="file"> File object
viewer.loadArrayBuffer(buffer, filename)    // load from ArrayBuffer

viewer.goToPage(n)
viewer.nextPage() / prevPage() / firstPage() / lastPage()
viewer.setZoom('fitWidth' | 'fitPage' | 'actual' | 100)
viewer.zoomIn() / zoomOut()
viewer.search(query)
viewer.searchNext() / searchPrev()
viewer.toggleSidebar()
viewer.toggleDarkMode()
viewer.toggleFullscreen()
viewer.toggleTwoPage()
viewer.rotate(90)          // or -90
viewer.download()
viewer.print()
viewer.destroy()
```

### Web Options

| Option            | Type    | Default      | Description                                |
|-------------------|---------|--------------|--------------------------------------------|
| `darkMode`        | boolean | `false`      | Start in dark mode                         |
| `sidebarOpen`     | boolean | `true`       | Show thumbnail sidebar on load             |
| `sidebarTab`      | string  | `"thumbs"`   | `"thumbs"` or `"outline"`                 |
| `zoom`            | string  | `"fitWidth"` | `"fitWidth"`, `"fitPage"`, `"actual"`, or `"100"` |
| `renderTextLayer` | boolean | `true`       | Enable text selection & search             |
| `thumbSize`       | number  | `160`        | Thumbnail width in px                      |
| `enableDrop`      | boolean | `true`       | Allow drag & drop of PDF files             |
| `enableKeyboard`  | boolean | `true`       | Enable keyboard shortcuts                  |
| `onLoad`          | fn      | —            | `(pdf) => {}` called after PDF loads       |
| `onPageChange`    | fn      | —            | `(page, total) => {}` on page change       |
| `onError`         | fn      | —            | `(err) => {}` on error                     |

---

## Android Usage

### 1. Add the dependency

**Recommended — JitPack** (see top of this file).

**Alternative — local module:** copy the `pdfviewer/` folder into your project and add:

```kotlin
// settings.gradle.kts
include(":pdfviewer")

// app/build.gradle.kts
dependencies {
    implementation(project(":pdfviewer"))
}
```

### 2. Add INTERNET permission

`pdfviewer` already declares it in its own manifest, but add it to your app manifest
too for clarity:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. Add to layout

```xml
<com.arknox.pdf.pdfviewer.PdfViewer
    android:id="@+id/pdfViewer"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 4. Use in Activity / Fragment

```kotlin
class MyActivity : AppCompatActivity() {

    private lateinit var pdfViewer: PdfViewer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)

        pdfViewer = findViewById(R.id.pdfViewer)

        // Optional configuration
        pdfViewer.configure(
            PdfViewerConfig(
                darkMode    = false,
                sidebarOpen = true,
                zoom        = "fitWidth",
            )
        )

        // Callbacks
        pdfViewer.onReady      = { /* viewer is initialised */ }
        pdfViewer.onLoad       = { pages -> Log.d("PDF", "Loaded $pages pages") }
        pdfViewer.onPageChange = { page, total -> Log.d("PDF", "$page / $total") }
        pdfViewer.onError      = { msg -> Log.e("PDF", msg) }

        // Load a PDF
        pdfViewer.loadUrl("https://example.com/document.pdf")
    }

    // Forward lifecycle events
    override fun onResume()  { super.onResume();  pdfViewer.onResume() }
    override fun onPause()   { super.onPause();   pdfViewer.onPause() }
    override fun onDestroy() { super.onDestroy(); pdfViewer.onDestroy() }
}
```

### Android API

| Method                              | Description                                      |
|-------------------------------------|--------------------------------------------------|
| `configure(PdfViewerConfig)`        | Apply config (reloads viewer if already ready)  |
| `loadUrl(url: String)`              | Load PDF from HTTP/HTTPS URL                    |
| `loadUri(uri: Uri, name?)`          | Load PDF from content URI (file picker etc.)    |
| `loadBytes(bytes: ByteArray, name?)`| Load PDF from raw bytes                         |
| `loadAsset(assetPath: String)`      | Load PDF from `assets/` folder                  |
| `goToPage(n: Int)`                  | Navigate to page number                         |
| `setZoom(value: String)`            | Set zoom: `"fitWidth"`, `"100"`, etc.           |
| `zoomIn()` / `zoomOut()`           | Zoom in or out                                  |
| `toggleDarkMode()`                  | Toggle dark / light mode                        |
| `toggleSidebar()`                   | Show / hide thumbnail sidebar                   |
| `search(query: String)`             | Highlight all matches                           |
| `searchNext()` / `searchPrev()`    | Jump between matches                            |
| `rotate(degrees: Int)`              | Rotate pages (±90)                              |
| `download()`                        | Trigger PDF download                            |
| `destroy()`                         | Release viewer resources                        |

### PdfViewerConfig

```kotlin
PdfViewerConfig(
    darkMode        = false,       // dark mode on/off
    sidebarOpen     = true,        // show sidebar on load
    sidebarTab      = "thumbs",    // "thumbs" or "outline"
    zoom            = "fitWidth",  // "fitWidth" | "fitPage" | "actual" | "100"
    renderTextLayer = true,        // text selection & search support
    thumbSize       = 160,         // thumbnail width in px
)
```

### Load PDF from file picker

```kotlin
private val pickPdf = registerForActivityResult(
    ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let { pdfViewer.loadUri(it) }
}

// Launch picker
pickPdf.launch("application/pdf")
```

---

## Requirements

| Platform | Minimum           |
|----------|-------------------|
| Web      | Any modern browser (Chrome 90+, Firefox 88+, Safari 14+) |
| Android  | API 24 (Android 7.0 Nougat)                              |

> **Note:** PDF.js is loaded from CDN (`cdnjs.cloudflare.com`). Internet access is
> required on first use. For fully offline operation, download PDF.js and host it
> locally (update the CDN URL in `pdf-viewer.js`).

---

## Project Structure

```
pdf-view-lib/
├── lib-web/                 ← web files (use in any web project)
│   ├── index.html
│   ├── pdf-viewer.css
│   └── pdf-viewer.js
├── app/                     ← demo Android app
├── pdfviewer/               ← Android library module
│   └── src/main/
│       ├── assets/pdfviewer/
│       │   ├── viewer.html     ← HTML shell (WebView host)
│       │   ├── pdf-viewer.js   ← copy of web library
│       │   └── pdf-viewer.css  ← copy of web styles
│       └── java/com/arknox/pdf/pdfviewer/
│           ├── PdfViewer.kt        ← main View class
│           └── PdfViewerConfig.kt  ← configuration data class
└── build.gradle.kts
```
