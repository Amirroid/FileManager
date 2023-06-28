package ir.amirroid.filemanager.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import java.io.File
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.Date

fun Int.toDp(context: Context) = context.resources.displayMetrics.density * this


fun Long.formatSize() = when {
    this == Long.MIN_VALUE || this < 0 -> "N/A"
    this < 1024L -> "$this B"
    this <= 0xfffccccccccccccL shr 40 -> "%.1f KB".format(this.toDouble() / (0x1 shl 10))
    this <= 0xfffccccccccccccL shr 30 -> "%.1f MB".format(this.toDouble() / (0x1 shl 20))
    this <= 0xfffccccccccccccL shr 20 -> "%.1f GB".format(this.toDouble() / (0x1 shl 30))
    this <= 0xfffccccccccccccL shr 10 -> "%.1f TB".format(this.toDouble() / (0x1 shl 40))
    this <= 0xfffccccccccccccL -> "%.1f PiB".format((this shr 10).toDouble() / (0x1 shl 40))
    else -> "%.1f EiB".format((this shr 20).toDouble() / (0x1 shl 40))
}

fun File.getIcon(): Pair<Int, String> {
    try {
        if (isDirectory) return Pair(Res.Drawable.folder, "folder")

        val mimType = getMimType()
        val image = when {
            mimType.startsWith("image", true) -> {
                Res.Drawable.picture
            }

            mimType.startsWith("video", true) -> {
                Res.Drawable.video
            }

            mimType.startsWith("audio", true) -> {
                Res.Drawable.music
            }

            mimType.endsWith("pdf", true) -> {
                Res.Drawable.pdf
            }

            mimType.startsWith("font", true) -> {
                Res.Drawable.font
            }

            name.endsWith(".zip") || name.endsWith(".rar") -> {
                Res.Drawable.zip
            }

            else -> {
                Res.Drawable.file
            }
        }
        return Pair(image, mimType)
    } catch (e: Exception) {
        return Pair(Res.Drawable.file, "file")
    }
}

fun File.getMimType() = URLConnection.guessContentTypeFromName(this.path) ?: "file"


fun File.formatDate(): String {
    val date = Date(lastModified())
    return date.formatDate()
}

@SuppressLint("SimpleDateFormat")
private fun Date.formatDate(): String {
    return SimpleDateFormat("yyyy/MM/dd").format(this)
}


fun Offset.toDpOffset(density: Density) =
    DpOffset(
        with(density) { x.toDp() },
        with(density) { y.toDp() },
    )

