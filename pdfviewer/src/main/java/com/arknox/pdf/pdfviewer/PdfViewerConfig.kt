package com.arknox.pdf.pdfviewer

/**
 * Configuration for [PdfViewer].
 *
 * Pass an instance to [PdfViewer.configure] before the viewer is ready.
 *
 * @param darkMode         Start in dark mode. Default false.
 * @param sidebarOpen      Show sidebar on load. Default true.
 * @param sidebarTab       Initial sidebar tab: "thumbs" or "outline". Default "thumbs".
 * @param zoom             Initial zoom: "fitWidth", "fitPage", "actual", or a number string like "100". Default "fitWidth".
 * @param enableKeyboard   Enable keyboard shortcuts. Default false (recommended off in WebView).
 * @param thumbSize        Thumbnail width in px. Default 160.
 * @param renderTextLayer  Render text layer (required for search & copy). Default true.
 * @param onReady          Called once the viewer JS is fully initialised and ready to load PDFs.
 * @param onLoad           Called when a PDF finishes loading; receives page count.
 * @param onPageChange     Called when the visible page changes; receives current page and total.
 * @param onError          Called on any error; receives an error message string.
 */
data class PdfViewerConfig(
    val darkMode:         Boolean = false,
    val sidebarOpen:      Boolean = true,
    val sidebarTab:       String  = "thumbs",
    val zoom:             String  = "fitWidth",
    val enableKeyboard:   Boolean = false,
    val thumbSize:        Int     = 160,
    val renderTextLayer:  Boolean = true,
    val onReady:          (() -> Unit)?                  = null,
    val onLoad:           ((pages: Int) -> Unit)?        = null,
    val onPageChange:     ((page: Int, total: Int) -> Unit)? = null,
    val onError:          ((msg: String) -> Unit)?       = null,
) {
    /** Serialise to a JSON literal suitable for passing to window._pvInit(). */
    internal fun toJson(): String =
        """{"darkMode":$darkMode,"sidebarOpen":$sidebarOpen,"sidebarTab":"$sidebarTab",""" +
        """"zoom":"$zoom","enableKeyboard":$enableKeyboard,"enableDrop":false,""" +
        """"thumbSize":$thumbSize,"renderTextLayer":$renderTextLayer,""" +
        """"workerSrc":"pdf.worker.min.js","cMapUrl":null,"cMapPacked":false}"""
}
