package ir.amirroid.filemanager.ui.components

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.amirroid.filemanager.utils.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CircleProgress(
    progress: Float,
    color: Color,
    totalColor: Color = MaterialTheme.colorScheme.background,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current
) {
    val progressA = remember {
        Animatable(0f)
    }.apply {
        scope.launch {
            animateTo(progress, tween(1000))
        }
    }
    val measurable = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .defaultMinSize(36.dp)
            .then(modifier)
            .aspectRatio(1f)
    ) {
        drawArc(
            totalColor.copy(0.4f),
            0f,
            360f,
            false,
            style = Stroke(
                4.toDp(context),
            )
        )
        drawArc(
            color,
            -90f,
            progressA.value * 360f,
            false,
            style = Stroke(
                5.toDp(context),
                cap = StrokeCap.Round
            )
        )
        val present = (progressA.value * 100).toInt().toString().plus("%")
        val textSize = measurable.measure(present).size
        drawText(
            measurable,
            present,
            style = TextStyle(
                textAlign = TextAlign.Center
            ),
            topLeft = Offset(
                size.width.div(2) - textSize.width.div(2),
                size.height.div(2) - textSize.height.div(2)
            ),
        )
    }
}