package mihon.core.archive

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import logcat.logcat
import java.io.Closeable

/**
 * Wrapper for reading PDF files using Android's built-in PdfRenderer.
 * Thread-safe wrapper around PdfRenderer to handle page rendering.
 */
class PdfReader(private val parcelFileDescriptor: ParcelFileDescriptor) : Closeable {

    private val pdfRenderer = PdfRenderer(parcelFileDescriptor)
    private val renderLock = Any()
    private var isClosed = false

    val pageCount: Int
        get() = if (!isClosed) pdfRenderer.pageCount else 0

    /**
     * Renders a page to a bitmap with proper synchronization and error handling.
     * Ensures only one page is open at a time to prevent IllegalStateException.
     */
    fun renderPage(pageIndex: Int, width: Int = 1240, height: Int = 1754): Bitmap {
        if (isClosed) {
            throw IllegalStateException("PdfReader is closed")
        }

        synchronized(renderLock) {
            try {
                val page = pdfRenderer.openPage(pageIndex)
                return try {
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmap
                } finally {
                    page.close()
                }
            } catch (e: Exception) {
                logcat { "Error rendering PDF page $pageIndex: ${e.message}" }
                throw e
            }
        }
    }

    fun getMetadata(): Map<String, String> {
        val meta = mutableMapOf<String, String>()
        return meta
    }

    override fun close() {
        synchronized(renderLock) {
            if (!isClosed) {
                try {
                    pdfRenderer.close()
                } catch (e: Exception) {
                    logcat { "Error closing PdfRenderer: ${e.message}" }
                }
                try {
                    parcelFileDescriptor.close()
                } catch (e: Exception) {
                    logcat { "Error closing ParcelFileDescriptor: ${e.message}" }
                }
                isClosed = true
            }
        }
    }
}
