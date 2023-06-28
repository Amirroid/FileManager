package ir.amirroid.filemanager.data.repositories

import ir.amirroid.filemanager.data.helpers.MediaHelper
import ir.amirroid.filemanager.utils.MediaTypes
import java.io.File
import javax.inject.Inject

class MediaRepository @Inject constructor(
    private val helper: MediaHelper
) {
    fun getAllFiles(type: MediaTypes): List<File> {
        return when (type) {
            MediaTypes.IMAGE -> {
                helper.getImages()
            }

            MediaTypes.VIDEO -> {
                helper.getVideos()
            }

            MediaTypes.DOCUMENTS -> {
                helper.getDocuments()
            }
        }
    }

    fun getRecentFile(): List<File> {
        return helper.getRecentFiles()
    }
}