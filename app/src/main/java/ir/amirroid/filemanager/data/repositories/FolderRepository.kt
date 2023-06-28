package ir.amirroid.filemanager.data.repositories

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FolderRepository @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getChildren(file: File) = file.listFiles()?.sortedBy {
        it.isDirectory.not()
    }?.toList() ?: emptyList()

    fun createFolder(file: File?, name: String) {
        if (file == null) return
        val newFile = File(file, name)
        if (newFile.exists()) {
            var number = 1
            while (true) {
                val nName = "$name ($number)"
                val nFile = File(file, nName)
                if (nFile.exists().not()) {
                    nFile.mkdir()
                    break
                } else {
                    number++
                }
            }
        } else newFile.mkdir()
    }

    fun gridMode(): Boolean {
        val sp = context.getSharedPreferences("mode", Context.MODE_PRIVATE)
        return sp.getBoolean("mode", false)
    }

    fun setMode(grid: Boolean) {
        val sp = context.getSharedPreferences("mode", Context.MODE_PRIVATE)
        sp.edit().apply { putBoolean("mode", grid) }.apply()
    }

    fun createFile(file: File?, name: String) {
        if (file == null) return
        val newFile = File(file, name)
        if (newFile.exists()) {
            var number = 1
            while (true) {
                val nName = "$name ($number)"
                val nFile = File(file, nName)
                if (nFile.exists().not()) {
                    nFile.createNewFile()
                    break
                } else {
                    number++
                }
            }
        } else newFile.createNewFile()
    }

    fun getSearchFiles(text: String): List<File> {
        val files = mutableListOf<File>()
        val storageFile = Environment.getExternalStorageDirectory()
        searchFile(text, files, storageFile)
        return files
    }

    private fun searchFile(text: String, list: MutableList<File>, file: File) {
        val listFile = file.listFiles()
        if (listFile.isNullOrEmpty().not()) {
            for (i in listFile!!) {
                if (i.name.contains(text, true)) list.add(i)
                if (i.isDirectory) {
                    searchFile(text, list, i)
                }
            }
        }
    }
}