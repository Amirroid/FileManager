package ir.amirroid.filemanager.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.filemanager.data.repositories.FolderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: FolderRepository) : ViewModel() {
    val text = MutableStateFlow("")

    private val _files = MutableStateFlow(emptyList<File>())
    val files = _files.asStateFlow()


    val gridMode = MutableStateFlow(repository.gridMode())


    var selectedFiles = mutableStateListOf<File>()

    val loading = MutableStateFlow(false)


    fun toggleSelect(file: File) {
        if (file in selectedFiles) {
            selectedFiles.remove(file)
        } else {
            selectedFiles.add(file)
        }
    }

    fun refreshData() = viewModelScope.launch(Dispatchers.IO) {
        loading.value = true
        selectedFiles.clear()
        if (text.value.isNotEmpty()) {
            val data = async { repository.getSearchFiles(text.value) }
            _files.value = data.await()
        } else {
            _files.value = emptyList()
        }
        loading.value = false
    }

    fun renameFile(name: String) {
        try {
            val file = selectedFiles.first()
            file.renameTo(File(file.parentFile, name))
            refreshData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkAll() {
        selectedFiles.clear()
        selectedFiles.addAll(_files.value)
    }


    fun deleteFiles() {
        for (file in selectedFiles) {
            file.delete()
        }
        refreshData()
    }

    fun setGridMode(grid: Boolean) {
        gridMode.value = grid
    }
}