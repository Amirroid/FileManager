package ir.amirroid.filemanager.ui.features.search

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ir.amirroid.filemanager.ui.components.GridListFile
import ir.amirroid.filemanager.ui.components.InfoDialog
import ir.amirroid.filemanager.ui.components.ListFile
import ir.amirroid.filemanager.utils.Res
import ir.amirroid.filemanager.utils.getMimType
import ir.amirroid.filemanager.viewmodels.SearchViewModel


@SuppressLint("UnusedCrossfadeTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(navigation: NavController) {
    val viewModel: SearchViewModel = hiltViewModel()
    val text by viewModel.text.collectAsStateWithLifecycle()
    val files by viewModel.files.collectAsStateWithLifecycle()
    val selectedFiles = viewModel.selectedFiles
    val gridMode by viewModel.gridMode.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val selectorMode by remember {
        derivedStateOf { selectedFiles.isNotEmpty() }
    }
    var inputFileDialog by remember {
        mutableStateOf(false)
    }
    var inputText by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var infoDialog by remember {
        mutableStateOf(false)
    }
    val requestFocus = FocusRequester()
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        try {
            keyboard?.show()
            requestFocus.requestFocus()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    BackHandler {
        when {
            selectorMode -> {
                viewModel.selectedFiles.clear()
            }

            else -> {
                navigation.popBackStack()
            }
        }
    }
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (selectorMode) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.selectedFiles.clear()
                        }) {
                            Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                        }
                    },
                    actions = {
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
                                context.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        "share to..."
                                    )
                                )
                            }) {
                                Icon(
                                    painter = painterResource(id = Res.Drawable.share),
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
                    })
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        navigation.popBackStack()
                    }, modifier = Modifier.padding(horizontal = 12.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                    OutlinedTextField(value = text, onValueChange = {
                        viewModel.text.value = it
                        viewModel.refreshData()
                    }, shape = CircleShape,
                        placeholder = {
                            Text(text = "Search...")
                        }, leadingIcon = {
                            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                        }, modifier = Modifier
                            .weight(1f)
                            .focusRequester(requestFocus),
                        singleLine = true
                    )
                    IconButton(onClick = {
                        viewModel.setGridMode(gridMode.not())
                    }, modifier = Modifier.padding(horizontal = 12.dp)) {
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
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
        Crossfade(targetState = selectorMode, label = "") {
            when {
                loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                gridMode -> {
                    GridListFile(
                        scrollBehavior = null,
                        files = files,
                        selectedFiles = selectedFiles,
                        selectorMode = selectorMode,
                        context = context,
                        onLongClick = {
                            viewModel.toggleSelect(it)
                        },
                    ) {
                        navigation.navigate(it)
                    }
                }

                else -> {
                    ListFile(
                        scrollBehavior = null,
                        files = files,
                        selectedFiles = selectedFiles,
                        selectorMode = selectorMode,
                        context = context,
                        onLongClick = {
                            viewModel.toggleSelect(it)
                        },
                    ) {
                        navigation.navigate(it)
                    }
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
                    inputFileDialog = false
                }) {
                    Text(text = "Rename")
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
    if (infoDialog) InfoDialog(file = selectedFiles.first()) {
        infoDialog = false
    }
}