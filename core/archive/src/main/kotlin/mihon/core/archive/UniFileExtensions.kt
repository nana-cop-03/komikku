package mihon.core.archive

import android.content.Context
import android.os.ParcelFileDescriptor
import com.hippo.unifile.UniFile
import logcat.logcat
import java.io.BufferedOutputStream
import java.io.File

internal fun UniFile.openFileDescriptor(context: Context, mode: String): ParcelFileDescriptor =
    context.contentResolver.openFileDescriptor(uri, mode) ?: error("Failed to open file descriptor: ${filePath ?: uri}")

fun UniFile.archiveReader(context: Context) = openFileDescriptor(context, "r").use { ArchiveReader(it) }

fun UniFile.epubReader(context: Context) = EpubReader(archiveReader(context))

fun UniFile.pdfReader(context: Context): PdfReader {
    val pfd = openFileDescriptor(context, "r")
    return try {
        // Try to construct PdfReader normally
        PdfReader(pfd)
    } catch (e: Exception) {
        // Fallback: PDF descriptor likely not seekable for PdfRenderer; copy to a temporary file (seekable) and open that instead
        logcat { "Falling back to temp file for PDF due to: ${e.message}" }
        try {
            pfd.close()
        } catch (_: Exception) {
        }
        val input = context.contentResolver.openInputStream(uri) ?: throw e
        val tmp = File.createTempFile((nameWithoutExtension ?: "tmp").padEnd(3), ".${extension ?: "pdf"}", context.cacheDir)
        BufferedOutputStream(tmp.outputStream()).use { out ->
            input.use { inp -> inp.copyTo(out) }
        }
        val tmpPfd = ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY)
        PdfReader(tmpPfd, tmp)
    }
}
