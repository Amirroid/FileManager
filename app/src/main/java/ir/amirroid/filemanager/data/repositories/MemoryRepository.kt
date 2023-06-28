package ir.amirroid.filemanager.data.repositories

import android.os.Build
import ir.amirroid.filemanager.data.helpers.MemoryHelpers
import javax.inject.Inject

class MemoryRepository @Inject constructor(
    private val memoryHelpers: MemoryHelpers
) {
    fun getAvailableInternalStorageSize() = memoryHelpers.getAvailableInternalStorageSize()
    fun getTotalInternalStorageSize() = memoryHelpers.getTotalInternalStorageSize()
    fun getStateExternalStorageSize() = memoryHelpers.getStateExternalStorageSize()
    fun permissionCheck() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        memoryHelpers.manageAllData()
    } else {
        memoryHelpers.checkPermission()
    }
}