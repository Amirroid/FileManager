package ir.amirroid.filemanager.ui.components

import androidx.compose.foundation.DefaultMarqueeVelocity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.amirroid.filemanager.utils.formatDate
import ir.amirroid.filemanager.utils.formatSize
import java.io.File
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun InfoDialog(file: File, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        Button(onClick = onDismiss) {
            Text(text = "Ok")
        }
    },
        title = {
            Text(text = "Properties")
        }, text = {
            Column {
                ListItem(headlineText = {
                    Row {
                        Text(
                            text = "Path : "
                        )
                        Text(
                            text = file.path, modifier = Modifier
                                .fillMaxWidth()
                                .basicMarquee(
                                    delayMillis = 2000,
                                    initialDelayMillis = 1000,
                                    velocity = 100.dp,
                                    animationMode = MarqueeAnimationMode.Immediately,
                                    iterations = Int.MAX_VALUE
                                )
                        )
                    }
                })
                ListItem(headlineText = {
                    Text(
                        text = "Date modified : " + file.formatDate(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .basicMarquee(
                                delayMillis = 2000,
                                initialDelayMillis = 1000,
                                velocity = 100.dp,
                                animationMode = MarqueeAnimationMode.Immediately,
                                iterations = Int.MAX_VALUE
                            )
                    )
                })
                ListItem(headlineText = {
                    Text(
                        text = "Readable : " + if (file.canRead()) {
                            "Yes"
                        } else "No"
                    )
                })
                ListItem(headlineText = {
                    Text(
                        text = "Writable : " + if (file.canWrite()) {
                            "Yes"
                        } else "No"
                    )
                })
                if (file.isFile) {
                    ListItem(headlineText = {
                        Text(
                            text = "Size : " + file.length().formatSize()
                        )
                    })
                }
            }
        })
}