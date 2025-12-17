package eu.kanade.tachiyomi.ui.reader.loader

import android.graphics.Bitmap
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import logcat.logcat
import mihon.core.archive.PdfReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Loader used to load a chapter from a .pdf file.
 */
internal class PdfPageLoader(private val reader: PdfReader) : PageLoader() {

    override var isLocal: Boolean = true

    override suspend fun getPages(): List<ReaderPage> {
        return (0 until reader.pageCount).map { i ->
            ReaderPage(i).apply {
                stream = {
                    try {
                        val bitmap = reader.renderPage(i)
                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                        ByteArrayInputStream(outputStream.toByteArray())
                    } catch (e: Exception) {
                        logcat { "Error rendering PDF page $i: ${e.message}" }
                        throw e
                    }
                }
                status = Page.State.Ready
            }
        }
    }

    override suspend fun loadPage(page: ReaderPage) {
        check(!isRecycled)
    }

    override fun recycle() {
        super.recycle()
        try {
            reader.close()
        } catch (e: Exception) {
            logcat { "Error closing PDF reader: ${e.message}" }
        }
    }
}
