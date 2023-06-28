package ir.amirroid.filemanager.ui.shapes

import android.content.Context
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import ir.amirroid.filemanager.utils.toDp

class FolderShape(private val context: Context) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val width = size.width
        val height = size.height
        return Outline.Generic(
            Path().apply {
                moveTo(6.toDp(context), 0f)
                lineTo(width * .2f, 0f)
                quadTo(
                    width * .2f, 0f,
                    width * .25f, 6.toDp(context)
                )
                lineTo(width - 6.toDp(context), 6.toDp(context))
                arcTo(
                    RectF(
                        width - 12.toDp(context),
                        6.toDp(context),
                        width,
                        18.toDp(context)
                    ),
                    -90f,
                    90f,
                    false
                )
                lineTo(
                    width, height - 6.toDp(context)
                )
//                quadTo(
//                    width, height - 6.toDp(context),
//                    width - 6.toDp(context), height,
//                )
                arcTo(
                    RectF(
                        width - 12.toDp(context),
                        height - 12.toDp(context),
                        width,
                        height
                    ),
                    0f,
                    90f,
                    false
                )
                lineTo(6.toDp(context), height)
//                quadTo(
//                    6.toDp(context), height,
//                    0f, height - 6.toDp(context)
//                )
                arcTo(
                    RectF(
                        0f,
                        height - 12.toDp(context),
                        12.toDp(context),
                        height
                    ),
                    90f,
                    90f,
                    false
                )
                lineTo(0f, 6.toDp(context))
                arcTo(
                    RectF(
                        0f,
                        0f,
                        12.toDp(context),
                        12.toDp(context)
                    ),
                    180f,
                    90f,
                    false
                )
            }
                .asComposePath()
        )
    }
}