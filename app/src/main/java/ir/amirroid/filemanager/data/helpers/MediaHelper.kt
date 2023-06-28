package ir.amirroid.filemanager.data.helpers

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import androidx.core.net.toFile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class MediaHelper @Inject constructor(
    @ApplicationContext val context: Context
) {
    @SuppressLint("Recycle")
    @WorkerThread
    fun getImages(): List<File> {
        val table = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val dataC = MediaStore.Images.ImageColumns.DATA
        val projection = arrayOf(
            dataC
        )
        val listFiles = mutableListOf<File>()
        val sort = "${MediaStore.Images.ImageColumns.DATE_ADDED} DESC"
        try {
            val query = context.contentResolver.query(
                table,
                projection,
                null,
                null,
                sort
            )
            if (query != null) {
                if (query.moveToNext()) {
                    do {
                        val data = query.getString(query.getColumnIndexOrThrow(dataC))
                        if (data != null) {
                            listFiles.add(File(data))
                        }
                    } while (query.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listFiles
    }


    @SuppressLint("Recycle")
    @WorkerThread
    fun getVideos(): List<File> {
        val table = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val dataC = MediaStore.Video.VideoColumns.DATA
        val projection = arrayOf(
            dataC
        )
        val listFiles = mutableListOf<File>()
        val sort = "${MediaStore.Video.VideoColumns.DATE_ADDED} DESC"
        try {
            val query = context.contentResolver.query(
                table,
                projection,
                null,
                null,
                sort
            )
            if (query != null) {
                if (query.moveToNext()) {
                    do {
                        val data = query.getString(query.getColumnIndexOrThrow(dataC))
                        if (data != null) {
                            listFiles.add(File(data))
                        }
                    } while (query.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listFiles
    }

    @SuppressLint("Recycle")
    @WorkerThread
    fun getDocuments(): List<File> {
        val table = MediaStore.Files.getContentUri("external")
        val dataC = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(
            dataC
        )
        val listFiles = mutableListOf<File>()
        val mimTypeC = MediaStore.Files.FileColumns.MIME_TYPE
        val selection = "$mimTypeC IN('application/pdf') OR $mimTypeC LIKE 'application/vnd%'"
        val sort = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        try {
            val query = context.contentResolver.query(
                table,
                projection,
                selection,
                null,
                sort
            )
            if (query != null) {
                if (query.moveToNext()) {
                    do {
                        val data = query.getString(query.getColumnIndexOrThrow(dataC))
                        if (data != null) {
                            listFiles.add(File(data))
                        }
                    } while (query.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listFiles
    }

    @SuppressLint("Recycle")
    @WorkerThread
    fun getRecentFiles(): List<File> {
        val table = MediaStore.Files.getContentUri("external")
        val dataC = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(
            dataC
        )
        val listFiles = mutableListOf<File>()
        val sort = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
        try {
            val query = context.contentResolver.query(
                table,
                projection,
                null,
                null,
                sort
            )
            if (query != null) {
                if (query.moveToNext()) {
                    do {
                        val data = query.getString(query.getColumnIndexOrThrow(dataC))
                        if (data != null) {
                            listFiles.add(File(data))
                        }
                    } while (query.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listFiles
    }
}