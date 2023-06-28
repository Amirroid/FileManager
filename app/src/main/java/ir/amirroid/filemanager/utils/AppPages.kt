package ir.amirroid.filemanager.utils

sealed class AppPages(val route: String) {
    object HomeScreen : AppPages(Constants.HOME)
    object FolderScreen : AppPages(Constants.FOLDER)
    object SearchScreen : AppPages(Constants.SEARCH)
    object MediaScreen : AppPages(Constants.SEARCH)
}