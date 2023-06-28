package ir.amirroid.filemanager.data.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.amirroid.filemanager.utils.Constants
import java.io.File
import javax.inject.Inject

class MemoryHelpers @Inject constructor(
    @ApplicationContext val context: Context
) {
    private fun availableExternalStorage() =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    private val storageManager = context.getSystemService(StorageManager::class.java)


    fun getTotalInternalStorageSize(): Long {
        val path = Environment.getDataDirectory().path
        val stat = StatFs(path)
        val blockSize = stat.blockSizeLong
        val totalBlock = stat.blockCountLong
        return blockSize * totalBlock
    }


    fun getAvailableInternalStorageSize(): Long {
        val path = Environment.getDataDirectory().path
        val stat = StatFs(path)
        val blockSize = stat.blockSizeLong
        val availableBlock = stat.availableBlocksLong
        return blockSize * availableBlock
    }

    fun getStateExternalStorageSize(): MemoryState {
        try {
            if (availableExternalStorage()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val file: File = storageManager.storageVolumes.apply {
                        removeAt(0)
                    }.first().directory ?: return MemoryState.NotAvailable
                    val path = file.path
                    val stat = StatFs(path)
                    val blockSize = stat.blockSizeLong
                    val totalBlock = stat.blockCountLong
                    val availableBlock = stat.availableBlocksLong
//                    Toast.makeText(
//                        context,
//                        "available size $availableBlock || total size $totalBlock  || block size $blockSize",
//                        Toast.LENGTH_LONG
//                    ).show()
                    return MemoryState.Available(
                        blockSize * totalBlock,
                        blockSize * availableBlock,
                        file
                    )
                } else {
                    val storage = File("/storage")
                    for (i in storage.listFiles() ?: emptyArray()) {
                        if (Environment.isExternalStorageRemovable(i)) {
                            val file: File = i ?: return MemoryState.NotAvailable
                            val path = file.path
                            val stat = StatFs(path)
                            val blockSize = stat.blockSizeLong
                            val totalBlock = stat.blockCountLong
                            val availableBlock = stat.availableBlocksLong
                            return MemoryState.Available(
                                blockSize * totalBlock,
                                blockSize * availableBlock,
                                file
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return MemoryState.NotAvailable
    }

    @SuppressLint("NewApi")
    fun manageAllData() = Environment.isExternalStorageManager()

    fun checkPermission() =
        Constants.permissions.map { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
            .all { it }
}

sealed class MemoryState(
    val totalSize: Long,
    val availableSize: Long,
    val file: File? = null
) {
    class Available(
        totalSize: Long,
        availableSize: Long,
        file: File
    ) : MemoryState(totalSize, availableSize, file)

    object NotAvailable : MemoryState(0L, 0L)
}