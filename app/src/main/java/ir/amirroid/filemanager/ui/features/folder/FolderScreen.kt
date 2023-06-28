package ir.amirroid.filemanager.ui.features.folder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.ReusableComposeNode
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ir.amirroid.filemanager.ui.components.FileView
import ir.amirroid.filemanager.ui.components.GridFileView
import ir.amirroid.filemanager.ui.components.GridListFile
import ir.amirroid.filemanager.ui.components.InfoDialog
import ir.amirroid.filemanager.ui.components.ListFile
import ir.amirroid.filemanager.utils.AppPages
import ir.amirroid.filemanager.utils.FileState
import ir.amirroid.filemanager.utils.Res
import ir.amirroid.filemanager.utils.getMimType
import ir.amirroid.filemanager.viewmodels.FolderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("UnusedContentLambdaTargetStateParameter", "UnusedCrossfadeTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    path: String,
    navigation: NavController
) {
    val viewModel: FolderViewModel = hiltViewModel()
    val context = LocalContext.current
    val files by viewModel.files.collectAsStateWithLifecycle()
    val currentFile by viewModel.currentFile.collectAsStateWithLifecycle()
    val selectedFiles = viewModel.selectedFiles
    val selectorMode by remember {
        derivedStateOf { selectedFiles.isNotEmpty() }
    }
    val gridMode by viewModel.gridMode.collectAsStateWithLifecycle()
    val previewFiles by viewModel.previewsFiles.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var inputFileDialog by remember {
        mutableStateOf(false)
    }
    var infoDialog by remember {
        mutableStateOf(false)
    }
    var fileState by remember {
        mutableStateOf(FileState.IDLE)
    }
    var inputText by remember {
        mutableStateOf(TextFieldValue(""))
    }
    DisposableEffect(key1 = Unit) {
        viewModel.openFolder(File(path))
        onDispose { }
    }
    val scope = rememberCoroutineScope()
    val lazyState = rememberLazyListState().apply {
        scope.launch {
            animateScrollToItem(previewFiles.size)
        }
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
    Column {
        MediumTopAppBar(title = {
            Text(text = if (selectorMode) selectedFiles.size.toString() + " item" else currentFile.name)
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
                            fileState = FileState.RENAME
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
                        val text = "New folder"
                        inputText = TextFieldValue(text).copy(selection = TextRange(0, text.length))
                        fileState = FileState.ADD_FOLDER
                        inputFileDialog = true
                    }) {
                        Icon(
                            painter = painterResource(id = Res.Drawable.newFolder),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        val text = "New file"
                        inputText = TextFieldValue(text).copy(selection = TextRange(0, text.length))
                        fileState = FileState.ADD_FILE
                        inputFileDialog = true
                    }) {
                        Icon(
                            painter = painterResource(id = Res.Drawable.newFile),
                            contentDescription = null
                        )
                    }
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
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 12.dp),
            state = lazyState
        ) {
            items(previewFiles.size) {
                val text = previewFiles[it]
                if (text.isEmpty()) return@items
                val selected = previewFiles.size == it.plus(1)
                ElevatedFilterChip(onClick = {
                    var nPath = ""
                    val nPFiles = previewFiles.filterIndexed { index, _ -> index <= it }
                    for (file in nPFiles) {
                        nPath += file + File.separator
                    }
                    navigation.navigate(
                        AppPages.FolderScreen.route + "?path=" + nPath
                    )
                }, label = {
                    Text(
                        text = text,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }, selected = selected)
                Spacer(modifier = Modifier.width(4.dp))
                if (previewFiles.size != it.plus(1) && text.isNotEmpty()) {
                    Icon(imageVector = Icons.Rounded.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
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
                    when (fileState) {
                        FileState.ADD_FOLDER -> {
                            viewModel.createFolder(inputText.text)
                        }

                        FileState.ADD_FILE -> {
                            viewModel.createFile(inputText.text)
                        }

                        FileState.RENAME -> {
                            viewModel.renameFile(inputText.text)
                            viewModel.refreshData()
                        }

                        else -> Unit
                    }
                    inputFileDialog = false
                }) {
                    Text(text = "Done")
                }
            },
            dismissButton = {
                Button(onClick = { inputFileDialog = false }) {
                    Text(text = "Cancel")
                }
            },

            title = {
                val text = when (fileState) {
                    FileState.ADD_FILE -> "Create a new file"
                    FileState.ADD_FOLDER -> "Create a new folder"
                    FileState.RENAME -> "Rename the file"
                    else -> "Done"
                }
                Text(text = text)
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
