package ir.amirroid.filemanager.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.core.content.FileProvider
import ir.amirroid.filemanager.utils.AppPages
import ir.amirroid.filemanager.utils.getMimType
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListFile(
    scrollBehavior: TopAppBarScrollBehavior?,
    files: List<File>,
    selectedFiles: List<File>,
    selectorMode: Boolean,
    context: Context,
    onLongClick: (File) -> Unit,
    onNavigate: (String) -> Unit,
) {

    LazyColumn(
        modifier = Modifier
            .wrapContentHeight()
            .then(if (scrollBehavior == null) Modifier else Modifier.nestedScroll(scrollBehavior.nestedScrollConnection))
    ) {
        items(files.size, key = { it }) {
            val file = files[it]
            val selected = file in selectedFiles
            FileView(file = file, isSelect = selected, onLongClick = {
                onLongClick.invoke(file)
            }) {
                if (selectorMode) {
                    onLongClick.invoke(file)
                } else {
                    if (file.isDirectory) {
                        onNavigate.invoke(
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
            }
            if (files.size != it.plus(1)) {
                Divider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridListFile(
    scrollBehavior: TopAppBarScrollBehavior?,
    files: List<File>,
    selectedFiles: List<File>,
    selectorMode: Boolean,
    context: Context,
    onLongClick: (File) -> Unit,
    onNavigate: (String) -> Unit,
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .wrapContentHeight()
            .then(if (scrollBehavior == null) Modifier else Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)),

        ) {
        items(files.size, key = { it }) {
            val file = files[it]
            val selected = file in selectedFiles
            GridFileView(file = file, isSelect = selected, onLongClick = {
                onLongClick.invoke(file)
            }) {
                if (selectorMode) {
                    onLongClick.invoke(file)
                } else {
                    if (file.isDirectory) {
                        onNavigate.invoke(
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
            }
        }
    }
}
