package ir.amirroid.filemanager.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.amirroid.filemanager.data.helpers.MemoryState
import ir.amirroid.filemanager.data.repositories.MediaRepository
import ir.amirroid.filemanager.data.repositories.MemoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MemoryRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {
    private val _internalStorage = MutableStateFlow(Pair(1L, 0L))
    val internalStorage = _internalStorage.asStateFlow()
    private val _externalStorage = MutableStateFlow<MemoryState?>(null)
    val externalStorage = _externalStorage.asStateFlow()

    private val _recentFiles = MutableStateFlow(emptyList<File>())
    val recentFiles = _recentFiles.asStateFlow()


    val loading = MutableStateFlow(false)


    init {
        getExternalStorage()
        getInternalStorage()
        if (checkPermission()) getRecentFiles()
    }

    fun getRecentFiles() = viewModelScope.launch(Dispatchers.IO) {
        loading.value = true
        _recentFiles.value = mediaRepository.getRecentFile()
        loading.value = false
    }


    fun deleteFiles(file: File) {
        file.delete()
        getRecentFiles()
    }


    fun renameFile(file: File, name: String) {
        try {
            file.renameTo(File(file.parentFile, name))
            getRecentFiles()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getInternalStorage() {
        _externalStorage.value = repository.getStateExternalStorageSize()
    }

    private fun getExternalStorage() {
        _internalStorage.value = Pair(
            repository.getTotalInternalStorageSize(),
            repository.getAvailableInternalStorageSize(),
        )
    }

    fun checkPermission() = repository.permissionCheck()

}