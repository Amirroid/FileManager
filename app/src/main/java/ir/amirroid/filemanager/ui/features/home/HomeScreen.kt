package ir.amirroid.filemanager.ui.features.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ir.amirroid.filemanager.data.helpers.MemoryState
import ir.amirroid.filemanager.ui.components.CircleProgress
import ir.amirroid.filemanager.ui.components.FileView
import ir.amirroid.filemanager.ui.components.InfoDialog
import ir.amirroid.filemanager.ui.shapes.FolderShape
import ir.amirroid.filemanager.utils.AppPages
import ir.amirroid.filemanager.utils.Constants
import ir.amirroid.filemanager.utils.Res
import ir.amirroid.filemanager.utils.formatSize
import ir.amirroid.filemanager.utils.getMimType
import ir.amirroid.filemanager.utils.toDpOffset
import ir.amirroid.filemanager.viewmodels.HomeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigation: NavController
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = hiltViewModel()
    val internalStorage by viewModel.internalStorage.collectAsStateWithLifecycle()
    val externalStorage by viewModel.externalStorage.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        viewModel.getRecentFiles()
    }
    val recentFiles by viewModel.recentFiles.collectAsStateWithLifecycle()
    var selectedFile by remember {
        mutableStateOf<File?>(null)
    }
    val density = LocalDensity.current
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    var infoDialog by remember {
        mutableStateOf(false)
    }
    var inputText by remember {
        mutableStateOf(TextFieldValue())
    }
    var inputFileDialog by remember {
        mutableStateOf(false)
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.1f))
                    .wrapContentHeight()
                    .padding(bottom = 24.dp, top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            openNavigationWithPermission(
                                viewModel.checkPermission(),
                                navigation,
                                context,
                                launcher,
                                AppPages.SearchScreen.route
                            )
                        }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                    Text(text = "Search...")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val brushFolder = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer,
                        )
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                            .aspectRatio(4 / 2.2f)
                            .clip(FolderShape(context))
                            .background(brushFolder)
                            .clickable {
                                openNavigationWithPermission(
                                    viewModel.checkPermission(),
                                    navigation,
                                    context,
                                    launcher,
                                    AppPages.FolderScreen.route + "?path=" + Environment.getExternalStorageDirectory().path
                                )
                            },
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.Black.copy(0.1f))
                                .fillMaxSize()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            val progress =
                                (internalStorage.first - internalStorage.second) / internalStorage.first.toFloat()
                            CircleProgress(
                                progress = progress.coerceIn(0f, 1f),
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Internal storage", fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = AnnotatedString
                                        .Builder()
                                        .apply {
                                            withStyle(
                                                SpanStyle(
                                                    color = if (progress >= 0.9f) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            ) {
                                                append(
                                                    (internalStorage.first - internalStorage.second).formatSize()
                                                )
                                            }
                                            withStyle(
                                                SpanStyle(
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                )
                                            ) {
                                                append(
                                                    " / " + internalStorage.first.formatSize()
                                                )
                                            }
                                        }
                                        .toAnnotatedString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    when (externalStorage) {
                        is MemoryState.Available -> {
                            externalStorage.let { s ->
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .weight(1f)
                                        .aspectRatio(4 / 2.2f)
                                        .clip(FolderShape(context))
                                        .background(brushFolder)
                                        .clickable {
                                            openNavigationWithPermission(
                                                viewModel.checkPermission(),
                                                navigation,
                                                context,
                                                launcher,
                                                AppPages.FolderScreen.route + "?path=" + externalStorage?.file
                                            )
                                        },
                                ) {
                                    val progress =
                                        (s!!.totalSize - s.availableSize) / s.totalSize.toFloat()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircleProgress(
                                            progress = progress,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "External storage", fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Divider()
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = AnnotatedString
                                                    .Builder()
                                                    .apply {
                                                        withStyle(SpanStyle(color = if (progress >= 0.9f) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer)) {
                                                            append(
                                                                s.totalSize
                                                                    .minus(s.availableSize)
                                                                    .formatSize()
                                                            )
                                                        }
                                                        withStyle(
                                                            SpanStyle(
                                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                            )
                                                        ) {
                                                            append(
                                                                " / " + s.totalSize.formatSize()
                                                            )
                                                        }
                                                    }
                                                    .toAnnotatedString(),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is MemoryState.NotAvailable -> {
                            Box(modifier = Modifier.weight(1f))
                        }

                        else -> {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MediaButton(icon = Res.Drawable.picture) {
                    navigation.navigate(
                        AppPages.MediaScreen.route + "?type=0"
                    )
                }
                MediaButton(icon = Res.Drawable.video) {
                    navigation.navigate(
                        AppPages.MediaScreen.route + "?type=1"
                    )
                }
                MediaButton(icon = Res.Drawable.document) {
                    navigation.navigate(
                        AppPages.MediaScreen.route + "?type=2"
                    )
                }
                MediaButton(icon = Res.Drawable.download) {
                    navigation.navigate(
                        AppPages.FolderScreen.route + "?path=" + Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                        ).path
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
        }
        if (loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) { CircularProgressIndicator() }
            }
        } else {
            item {
                Text(
                    text = "Resents", modifier = Modifier
                        .padding(12.dp)
                        .alpha(0.6f)
                )
            }
        }
        items(recentFiles.size) {
            val file = recentFiles[it]
            Box {
                FileView(file = file, isSelect = false, onLongClick = { offset1 ->
                    offset = offset1
                    selectedFile = file
                }) {
                    if (file.isDirectory) {
                        navigation.navigate(
                            AppPages.FolderScreen.route + "?path=${file.path}"
                        )
                    } else {
                        val fileProvider = FileProvider.getUriForFile(
                            context,
                            context.packageName + ".provider",
                            file
                        )
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(fileProvider, file.getMimType())
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivity(Intent.createChooser(intent, "open file with..."))
                    }
                }
                DropdownMenu(
                    expanded = selectedFile != null && selectedFile == file,
                    onDismissRequest = { selectedFile = null },
                    offset = offset.toDpOffset(density)
                ) {
                    DropdownMenuItem(text = {
                        Text(text = "Delete")
                    }, onClick = {
                        viewModel.deleteFiles(file)
                        selectedFile = null
                    })
                    DropdownMenuItem(text = {
                        Text(text = "Rename")
                    }, onClick = {
                        val name = file.name
                        inputText = TextFieldValue(name).copy(selection = TextRange(0, name.length))
                        inputFileDialog = true
                    })
                    DropdownMenuItem(text = {
                        Text(text = "Properties")
                    }, onClick = {
                        infoDialog = true
                    })
                }
            }
            if (recentFiles.size != it.plus(1)) {
                Divider()
            }
        }

    }
    if (inputFileDialog) {
        AlertDialog(
            onDismissRequest = { inputFileDialog = false; selectedFile = null },
            confirmButton = {
                Button(onClick = {
                    viewModel.renameFile(selectedFile!!, inputText.text)
                    inputFileDialog = false
                    selectedFile = null
                }) {
                    Text(text = "Rename")
                }
            },
            dismissButton = {
                Button(onClick = {
                    inputFileDialog = false
                    selectedFile = null
                }) {
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
    if (infoDialog && selectedFile != null) {
        InfoDialog(file = selectedFile!!) {
            infoDialog = false
            selectedFile = null
        }
    }
}

@Composable
fun MediaButton(
    icon: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(64.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick.invoke() }
                .padding(18.dp), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null
            )
        }
    }
}


fun openNavigationWithPermission(
    isPermission: Boolean,
    navigation: NavController,
    context: Context,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    navigationRoute: String
) {
    if (isPermission) {
        navigation.navigate(navigationRoute)
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent =
                    Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.setData(
                    Uri.fromParts(
                        "package",
                        context.packageName.toString(),
                        null
                    )
                )
                context.startActivity(intent)
            } catch (e: Exception) {
                val intent =
                    Intent()
                intent.setData(
                    Uri.fromParts(
                        "package",
                        context.packageName.toString(),
                        null
                    )
                )
                context.startActivity(intent)
                e.printStackTrace()
            }
        } else {
            launcher.launch(Constants.permissions)
        }
    }
}