package ir.amirroid.filemanager.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ir.amirroid.filemanager.utils.formatSize
import ir.amirroid.filemanager.utils.getIcon
import java.io.File

@Composable
fun GridFileView(file: File, isSelect: Boolean, onLongClick: () -> Unit, onClick: () -> Unit) {
    val color by animateColorAsState(
        targetValue = if (isSelect) MaterialTheme.colorScheme.primary.copy(
            0.3f
        ) else Color.Transparent,
        label = "list_color"
    )
    val ins = remember {
        MutableInteractionSource()
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .indication(ins, LocalIndication.current)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
            .background(color)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick.invoke() },
                    onPress = {
                        val press = PressInteraction.Press(it)
                        ins.emit(press)
                        tryAwaitRelease()
                        val release = PressInteraction.Release(press)
                        ins.emit(release)
                    },
                    onTap = { onClick.invoke() }
                )
            },
    ) {
        FileIcon(
            file = file,
            modifier = Modifier
                .fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = file.name, style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (file.isFile)
            Text(
                text = file.length().formatSize(),
                style = TextStyle(
                    fontSize = 10.sp,
                ),
            )
    }
}