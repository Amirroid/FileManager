package ir.amirroid.filemanager.viewmodels

import android.os.Environment
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.filemanager.data.repositories.FolderRepository
import ir.amirroid.filemanager.data.repositories.MediaRepository
import ir.amirroid.filemanager.utils.MediaTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FolderViewModel @Inject constructor(
    private val repository: FolderRepository,
    private val mediaRepository: MediaRepository,
) : ViewModel() {
    private val _files = MutableStateFlow<List<File>>(emptyList())
    val files = _files.asStateFlow()


    private val _currentFile = MutableStateFlow(Environment.getExternalStorageDirectory())
    val currentFile = _currentFile.asStateFlow()


    val gridMode = MutableStateFlow(repository.gridMode())


    var selectedFiles = mutableStateListOf<File>()


    val previewsFiles = MutableStateFlow<MutableList<String>>(mutableListOf())


    fun openFolder(file: File) = viewModelScope.launch(Dispatchers.IO) {
        previewsFiles.value = mutableListOf()
        previewsFiles.value = previewsFiles.value.apply {
            addAll(
                file.path.split(File.separator)
            )
        }
        _currentFile.value = file
        refreshFiles()
    }

    fun refreshData() {
        selectedFiles.clear()
    }

    fun toggleSelect(file: File) {
        if (file in selectedFiles) {
            selectedFiles.remove(file)
        } else {
            selectedFiles.add(file)
        }
    }

    fun checkAll() {
        selectedFiles.clear()
        selectedFiles.addAll(_files.value)
    }

    fun createFolder(name: String) {
        repository.createFolder(_currentFile.value, name)
        refreshFiles()
    }

    fun renameFile(name: String) {
        try {
            val file = selectedFiles.first()
            file.renameTo(File(file.parentFile, name))
            refreshFiles()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllMedia(type: MediaTypes) = viewModelScope.launch(Dispatchers.IO) {
        _files.value = mediaRepository.getAllFiles(
            type
        )
    }

    fun deleteFiles() {
        for (file in selectedFiles) {
            file.delete()
        }
        refreshFiles()
        refreshData()
    }

    fun setGridMode(grid: Boolean) {
        repository.setMode(grid)
        gridMode.value = grid
    }

    private fun refreshFiles() = viewModelScope.launch(Dispatchers.IO) {
        _files.value = repository.getChildren(_currentFile.value)
    }

    fun createFile(name: String) {
        repository.createFile(_currentFile.value, name)
        refreshFiles()
    }
}