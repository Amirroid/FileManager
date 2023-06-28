package ir.amirroid.filemanager.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import ir.amirroid.filemanager.utils.formatSize
import ir.amirroid.filemanager.utils.getIcon
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileView(file: File, isSelect: Boolean, onLongClick: (Offset) -> Unit, onClick: () -> Unit) {
    val color by animateColorAsState(
        targetValue = if (isSelect) MaterialTheme.colorScheme.primary.copy(
            0.3f
        ) else Color.Transparent,
        label = "list_color"
    )
    val ins = remember {
        MutableInteractionSource()
    }
    ListItem(headlineText = {
        Text(text = file.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }, leadingContent = {
        FileIcon(file)
    }, modifier = Modifier
        .indication(ins, LocalIndication.current)
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = onLongClick,
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
        colors = ListItemDefaults.colors(
            containerColor = color,
        ),
        trailingContent = {
            if (file.isFile) {
                Text(text = file.length().formatSize())
            }
        }
    )
}