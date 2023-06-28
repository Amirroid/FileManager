package ir.amirroid.filemanager.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ir.amirroid.filemanager.utils.getIcon
import java.io.File


@Composable
fun FileIcon(file: File, modifier: Modifier = Modifier) {
    val im = file.getIcon()
    val icon = im.first
    AsyncImage(
        model = if (im.second.startsWith("image")) file.path else icon,
        contentDescription = null,
        placeholder = painterResource(id = icon),
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(4.dp))
            .then(modifier),
        contentScale = if (im.second.startsWith("image")) ContentScale.Crop else ContentScale.Inside
    )
}
