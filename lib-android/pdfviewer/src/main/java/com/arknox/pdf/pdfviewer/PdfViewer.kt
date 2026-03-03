package com.arknox.pdf.pdfviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Base64
import android.view.View
import android.view.WindowInsets
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout

/**
 * PdfViewer — WebView-based PDF viewer backed by AdvancedPDFViewer (pdf-viewer.js).
 *
 * Usage in XML:
 *   <com.arknox.pdf.pdfviewer.PdfViewer
 *       android:id="@+id/pdfViewer"
 *       android:layout_width="match_parent"
 *       android:layout_height="match_parent" />
 *
 * Usage in code:
 *   pdfViewer.configure(PdfViewerConfig(
 *       onReady = { pdfViewer.loadUrl("https://example.com/doc.pdf") },
 *       onLoad  = { pages -> Log.d("PDF", "$pages pages") },
 *   ))
 *
 * For file-picker support, forward Activity results:
 *   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
 *       super.onActivityResult(requestCode, resultCode, data)
 *       pdfViewer.onActivityResult(requestCode, resultCode, data)
 *   }
 */
class PdfViewer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {

    companion object {
        const val FILE_CHOOSER_REQUEST_CODE = 7001
    }

    private var config = PdfViewerConfig()
    private var viewerReady = false
    private val pendingActions = mutableListOf<() -> Unit>()
    private val main = Handler(Looper.getMainLooper())

    // File chooser state
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null

    // Fullscreen state
    private var fullscreenView: View? = null
    private var fullscreenCallback: WebChromeClient.CustomViewCallback? = null

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        setup()
    }

    fun configure(cfg: PdfViewerConfig): PdfViewer {
        config = cfg
        return this
    }

    // ── Setup ────────────────────────────────────────────────────────────────

    @SuppressLint("SetJavaScriptEnabled")
    private fun setup() {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        @Suppress("DEPRECATION")
        settings.allowFileAccessFromFileURLs = true
        @Suppress("DEPRECATION")
        settings.allowUniversalAccessFromFileURLs = true

        addJavascriptInterface(Bridge(), "AndroidBridge")

        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.evaluateJavascript(
                    "window._pvInit && window._pvInit(${config.toJson()});", null
                )
            }
        }

        webChromeClient = PdfWebChromeClient()

        super.loadUrl("file:///android_asset/pdfviewer/viewer.html")
    }

    // ── WebChromeClient — file chooser + fullscreen ───────────────────────────

    private inner class PdfWebChromeClient : WebChromeClient() {

        /** Forward WebView console.log / warn / error → Android Logcat (tag: PdfViewer) */
        override fun onConsoleMessage(msg: ConsoleMessage): Boolean {
            val level = when (msg.messageLevel()) {
                ConsoleMessage.MessageLevel.ERROR   -> Log.ERROR
                ConsoleMessage.MessageLevel.WARNING -> Log.WARN
                else                                -> Log.DEBUG
            }
            Log.println(level, "PdfViewer", "${msg.message()}  [${msg.sourceId()}:${msg.lineNumber()}]")
            return true
        }

        /** Called when JS requests a file via <input type="file"> */
        override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>,
            fileChooserParams: FileChooserParams,
        ): Boolean {
            // Cancel any previous pending callback
            fileChooserCallback?.onReceiveValue(null)
            fileChooserCallback = filePathCallback

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
            }
            return try {
                (context as? Activity)?.startActivityForResult(
                    Intent.createChooser(intent, "Open PDF"),
                    FILE_CHOOSER_REQUEST_CODE
                )
                true
            } catch (e: ActivityNotFoundException) {
                fileChooserCallback = null
                false
            }
        }

        /** Called when JS calls element.requestFullscreen() */
        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            if (fullscreenView != null) {
                callback.onCustomViewHidden()
                return
            }
            fullscreenView = view
            fullscreenCallback = callback

            val activity = context as? Activity ?: return
            val decor = activity.window.decorView as FrameLayout
            decor.addView(
                view,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                )
            )
            hideSystemUi(activity)
        }

        /** Called when JS calls document.exitFullscreen() */
        override fun onHideCustomView() {
            val activity = context as? Activity ?: return
            val decor = activity.window.decorView as FrameLayout
            fullscreenView?.let { decor.removeView(it) }
            fullscreenView = null
            showSystemUi(activity)
            fullscreenCallback?.onCustomViewHidden()
            fullscreenCallback = null
        }
    }

    // ── System UI helpers ────────────────────────────────────────────────────

    private fun hideSystemUi(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }

    private fun showSystemUi(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.show(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
        } else {
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    // ── File-chooser result (call this from your Activity.onActivityResult) ──

    /**
     * Forward your Activity's onActivityResult here so file-open works.
     *
     *   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
     *       super.onActivityResult(requestCode, resultCode, data)
     *       pdfViewer.onActivityResult(requestCode, resultCode, data)
     *   }
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != FILE_CHOOSER_REQUEST_CODE) return
        val results = if (resultCode == Activity.RESULT_OK && data?.data != null) {
            arrayOf(data.data!!)
        } else {
            null
        }
        fileChooserCallback?.onReceiveValue(results)
        fileChooserCallback = null
    }

    // ── Load methods ─────────────────────────────────────────────────────────

    override fun loadUrl(url: String) {
        runWhenReady { js("window._pvBridge&&window._pvBridge.loadURL('${url.jsEsc()}');") }
    }

    fun loadUri(uri: Uri) {
        Thread {
            try {
                val bytes = context.contentResolver.openInputStream(uri)
                    ?.use { it.readBytes() }
                    ?: error("Cannot open URI: $uri")
                dispatchLoad(bytes, uri.lastPathSegment ?: "document.pdf")
            } catch (e: Exception) {
                main.post { config.onError?.invoke(e.message ?: "loadUri failed") }
            }
        }.start()
    }

    fun loadBytes(bytes: ByteArray, name: String = "document.pdf") {
        Thread { dispatchLoad(bytes, name) }.start()
    }

    fun loadAsset(path: String) {
        Thread {
            try {
                val bytes = context.assets.open(path).use { it.readBytes() }
                dispatchLoad(bytes, path.substringAfterLast('/'))
            } catch (e: Exception) {
                main.post { config.onError?.invoke(e.message ?: "loadAsset failed") }
            }
        }.start()
    }

    private fun dispatchLoad(bytes: ByteArray, name: String) {
        val b64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        main.post {
            runWhenReady {
                js("window._pvBridge&&window._pvBridge.loadBase64('$b64','${name.jsEsc()}');")
            }
        }
    }

    // ── Navigation & controls ─────────────────────────────────────────────────

    fun goToPage(page: Int)    { runWhenReady { js("window._pvBridge&&window._pvBridge.goToPage($page);") } }
    fun setZoom(value: String) { runWhenReady { js("window._pvBridge&&window._pvBridge.setZoom('${value.jsEsc()}');") } }
    override fun zoomIn():  Boolean { runWhenReady { js("window._pvBridge&&window._pvBridge.zoomIn();") };  return true }
    override fun zoomOut(): Boolean { runWhenReady { js("window._pvBridge&&window._pvBridge.zoomOut();") }; return true }
    fun toggleDarkMode()   { runWhenReady { js("window._pvBridge&&window._pvBridge.toggleDarkMode();") } }
    fun toggleSidebar()    { runWhenReady { js("window._pvBridge&&window._pvBridge.toggleSidebar();") } }
    fun search(query: String) { runWhenReady { js("window._pvBridge&&window._pvBridge.search('${query.jsEsc()}');") } }
    fun searchNext()       { runWhenReady { js("window._pvBridge&&window._pvBridge.searchNext();") } }
    fun searchPrev()       { runWhenReady { js("window._pvBridge&&window._pvBridge.searchPrev();") } }
    fun rotate(degrees: Int) { runWhenReady { js("window._pvBridge&&window._pvBridge.rotate($degrees);") } }
    fun download()         { runWhenReady { js("window._pvBridge&&window._pvBridge.download();") } }

    override fun destroy() {
        evaluateJavascript("window._pvBridge&&window._pvBridge.destroy();", null)
        super.destroy()
    }

    // ── JS → Kotlin bridge ───────────────────────────────────────────────────

    private inner class Bridge {
        @JavascriptInterface
        fun onViewerReady() {
            main.post {
                viewerReady = true
                pendingActions.forEach { it() }
                pendingActions.clear()
                config.onReady?.invoke()
            }
        }
        @JavascriptInterface fun onLoad(pages: Int)                  { main.post { config.onLoad?.invoke(pages) } }
        @JavascriptInterface fun onPageChange(page: Int, total: Int) { main.post { config.onPageChange?.invoke(page, total) } }
        @JavascriptInterface fun onError(msg: String)                { main.post { config.onError?.invoke(msg) } }
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private fun runWhenReady(action: () -> Unit) {
        if (viewerReady) action() else pendingActions += action
    }

    private fun js(script: String) = evaluateJavascript(script, null)

    private fun String.jsEsc(): String =
        replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
}
