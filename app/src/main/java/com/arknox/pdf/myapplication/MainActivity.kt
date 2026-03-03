package com.arknox.pdf.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.arknox.pdf.pdfviewer.PdfViewer
import com.arknox.pdf.pdfviewer.PdfViewerConfig

class MainActivity : AppCompatActivity() {

    private lateinit var pdfViewer: PdfViewer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_main)

        pdfViewer = findViewById(R.id.pdfViewer)

        pdfViewer.configure(
            PdfViewerConfig(
                darkMode    = false,
                sidebarOpen = true,
                zoom        = "fitWidth",
                onReady = {
                    // Load Mozilla's public sample PDF once the viewer is initialised
                    pdfViewer.loadUrl(
                        "https://mozilla.github.io/pdf.js/web/compressed.tracemonkey-pldi-09.pdf"
                    )
                },
                onLoad = { pages ->
                    Toast.makeText(this, "Loaded — $pages pages", Toast.LENGTH_SHORT).show()
                },
                onPageChange = { page, total ->
                    title = "PDF Viewer  $page / $total"
                },
                onError = { msg ->
                    Toast.makeText(this, "Error: $msg", Toast.LENGTH_LONG).show()
                },
            )
        )
    }

    // Required for the file-open button to work inside the PDF viewer
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        pdfViewer.onActivityResult(requestCode, resultCode, data)
    }
}
