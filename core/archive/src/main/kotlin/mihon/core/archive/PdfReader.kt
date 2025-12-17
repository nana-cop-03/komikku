package mihon.core.archive

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.hippo.unifile.UniFile
import logcat.logcat
import java.io.Closeable

/**
 * Wrapper for reading PDF files using Android's built-in PdfRenderer.
 * Can convert PDF pages to image files for use with existing image loaders.
 */
class PdfReader(
    private val parcelFileDescriptor: ParcelFileDescriptor,
    private val tempFile: java.io.File? = null,
) : Closeable {

    private val pdfRenderer = PdfRenderer(parcelFileDescriptor)
    private var isClosed = false

    val pageCount: Int
        get() = if (isClosed) 0 else pdfRenderer.pageCount

    /**
     * Renders a page to a bitmap.
     */
    fun renderPage(pageIndex: Int, width: Int = 1240, height: Int = 1754): Bitmap {
        if (isClosed) {
            throw IllegalStateException("PdfReader is closed")
        }

        try {
            val page = pdfRenderer.openPage(pageIndex)
            return try {
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                // Ensure white background so JPEGs don't have black where transparency exists
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)
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

    /**
     * Extracts all PDF pages as JPEG images to a directory.
     * This allows using the existing DirectoryPageLoader instead of dealing with PdfRenderer threading issues.
     */
    fun extractToDirectory(context: Context, outputDir: UniFile): Int {
        var savedPages = 0

        try {
            // Create directory if it doesn't exist
            if (outputDir.isDirectory || outputDir.createDirectory() != null) {
                // ok
            } else {
                throw Exception("Failed to create output directory")
            }

            for (i in 0 until pageCount) {
                try {
                    val bitmap = renderPage(i)
                    val fileName = String.format("%03d.jpg", i + 1)
                    val file = outputDir.createFile(fileName)

                    if (file != null) {
                        file.openOutputStream().use { stream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                        }
                        savedPages++
                        logcat { "Extracted PDF page $i as $fileName" }
                    }
                    bitmap.recycle()
                } catch (e: Exception) {
                    logcat { "Error extracting page $i: ${e.message}" }
                }
            }
        } catch (e: Exception) {
            logcat { "Error extracting PDF to directory: ${e.message}" }
        }

        return savedPages
    }

    fun getMetadata(): Map<String, String> {
        val meta = mutableMapOf<String, String>()
        return meta
    }

    override fun close() {
        if (isClosed) return

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
        // If we created a temporary file fallback, try to delete it now
        try {
            tempFile?.delete()
        } catch (e: Exception) {
            logcat { "Error deleting temp PDF file: ${e.message}" }
        }
        isClosed = true
    }
}
