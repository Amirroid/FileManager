package ir.amirroid.filemanager.ui.features.media

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ir.amirroid.filemanager.ui.components.GridListFile
import ir.amirroid.filemanager.ui.components.InfoDialog
import ir.amirroid.filemanager.ui.components.ListFile
import ir.amirroid.filemanager.utils.AppPages
import ir.amirroid.filemanager.utils.FileState
import ir.amirroid.filemanager.utils.MediaTypes
import ir.amirroid.filemanager.utils.Res
import ir.amirroid.filemanager.utils.getMimType
import ir.amirroid.filemanager.viewmodels.FolderViewModel
import kotlinx.coroutines.launch
import java.io.File


@SuppressLint("UnusedCrossfadeTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaScreen(
    type: MediaTypes,
    navigation: NavController
) {
    val viewModel: FolderViewModel = hiltViewModel()
    val context = LocalContext.current
    val files by viewModel.files.collectAsStateWithLifecycle()
    val selectedFiles = viewModel.selectedFiles
    val selectorMode by remember {
        derivedStateOf { selectedFiles.isNotEmpty() }
    }
    val gridMode by viewModel.gridMode.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var inputFileDialog by remember {
        mutableStateOf(false)
    }
    var infoDialog by remember {
        mutableStateOf(false)
    }
    var inputText by remember {
        mutableStateOf(TextFieldValue())
    }
    DisposableEffect(key1 = Unit) {
        viewModel.getAllMedia(type)
        onDispose { }
    }
    BackHandler {
        when {
            selectorMode -> {
                viewModel.refreshData()
            }

            else -> {
                navigation.popBackStack()
            }
        }
    }
    val title = when (type) {
        MediaTypes.DOCUMENTS -> "Documents"
        MediaTypes.VIDEO -> "Videos"
        MediaTypes.IMAGE -> "Images"
    }
    Column {
        MediumTopAppBar(title = {
            Text(text = if (selectorMode) selectedFiles.size.toString() + " item" else title)
        }, scrollBehavior = scrollBehavior,
            navigationIcon = {
                IconButton(onClick = {
                    when {
                        selectorMode -> {
                            viewModel.refreshData()
                        }

                        else -> {
                            navigation.popBackStack()
                        }
                    }
                }) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = null)
                }
            },
            actions = {
                if (selectorMode) {
                    if (selectedFiles.size == 1) {
                        IconButton(onClick = {
                            val name = selectedFiles.first().name
                            inputText =
                                TextFieldValue(name).copy(selection = TextRange(0, name.length))
                            inputFileDialog = true
                        }) {
                            Icon(
                                painter = painterResource(id = Res.Drawable.rename),
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = {
                            infoDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null
                            )
                        }
                    }
                    if (selectedFiles.size == 1 && selectedFiles.first().isFile) {
                        val file = selectedFiles.first()
                        IconButton(onClick = {
                            val fileProvider = FileProvider.getUriForFile(
                                context,
                                context.packageName + ".provider",
                                file
                            )
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.setDataAndType(fileProvider, file.getMimType())
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.startActivity(Intent.createChooser(intent, "share to..."))
                        }) {
                            Icon(
                                painter = painterResource(id = Res.Drawable.share),
                                contentDescription = null
                            )
                        }
                    }
                    IconButton(onClick = {
                        viewModel.deleteFiles()
                    }) {
                        Icon(Icons.Rounded.Delete, null)
                    }
                    IconButton(onClick = {
                        viewModel.checkAll()
                    }) {
                        Icon(painter = painterResource(Res.Drawable.checkList), null)
                    }
                } else {
                    IconButton(onClick = {
                        viewModel.setGridMode(gridMode.not())
                    }) {
                        Crossfade(targetState = gridMode, label = "") {
                            if (gridMode) {
                                Icon(
                                    painter = painterResource(id = Res.Drawable.list),
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = Res.Drawable.grid),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            })
        Crossfade(targetState = selectorMode, label = "") {
            if (gridMode) {
                GridListFile(
                    scrollBehavior,
                    files,
                    selectedFiles,
                    selectorMode,
                    context,
                    {
                        viewModel.toggleSelect(it)
                    }
                ) {
                    navigation.navigate(it)
                }
            } else {
                ListFile(
                    scrollBehavior,
                    files,
                    selectedFiles,
                    selectorMode,
                    context,
                    {
                        viewModel.toggleSelect(it)
                    }
                ) {
                    navigation.navigate(it)
                }
            }
        }
    }
    if (inputFileDialog) {
        AlertDialog(
            onDismissRequest = { inputFileDialog = false },
            confirmButton = {
                Button(onClick = {
                    viewModel.renameFile(inputText.text)
                    viewModel.refreshData()
                    inputFileDialog = false
                }) {
                    Text(text = "Create")
                }
            },
            dismissButton = {
                Button(onClick = { inputFileDialog = false }) {
                    Text(text = "Cancel")
                }
            },

            title = {
                Text(text = "Rename the file")
            },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text(text = "name") }
                )
            }
        )
    }
    if (infoDialog) {
        InfoDialog(file = selectedFiles.first()) {
            infoDialog = false
        }
    }
}