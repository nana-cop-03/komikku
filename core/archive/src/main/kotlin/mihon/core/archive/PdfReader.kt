package mihon.core.archive

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import com.shockwave.pdfium.PdfiumCore
import com.shockwave.pdfium.util.Size
import java.io.Closeable

/**
 * Wrapper for reading PDF files.
 */
class PdfReader(private val parcelFileDescriptor: ParcelFileDescriptor) : Closeable {

    private val pdfiumCore = PdfiumCore()
    private val pdfDocument = pdfiumCore.newDocument(parcelFileDescriptor)

    val pageCount: Int = pdfiumCore.getPageCount(pdfDocument)

    /**
     * Renders a page to a bitmap.
     */
    fun renderPage(pageIndex: Int, width: Int = 1240, height: Int = 1754): Bitmap {
        val page = pdfiumCore.openPage(pdfDocument, pageIndex)
        val size = pdfiumCore.getPageSize(page)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageIndex, 0, 0, width, height)
        pdfiumCore.closePage(page)
        return bitmap
    }

    /**
     * Gets the size of a page.
     */
    fun getPageSize(pageIndex: Int): Size {
        val page = pdfiumCore.openPage(pdfDocument, pageIndex)
        val size = pdfiumCore.getPageSize(page)
        pdfiumCore.closePage(page)
        return size
    }

    /**
     * Gets metadata from the PDF.
     */
    fun getMetadata(): Map<String, String> {
        val meta = mutableMapOf<String, String>()
        // Pdfium doesn't directly support metadata, but we can try to get title etc.
        // For now, return empty
        return meta
    }

    override fun close() {
        pdfiumCore.closeDocument(pdfDocument)
        parcelFileDescriptor.close()
    }
}