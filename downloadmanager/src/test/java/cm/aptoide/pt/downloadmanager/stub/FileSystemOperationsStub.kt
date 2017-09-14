package cm.aptoide.pt.downloadmanager.stub

import cm.aptoide.pt.downloadmanager.external.FilePaths
import cm.aptoide.pt.downloadmanager.external.FileSystemOperations
import java.io.File
import java.util.*

class FileSystemOperationsStub(private var filePaths: FilePaths) :
        FileSystemOperations {

    override fun deleteFile(path: String): Boolean {
        val file = File(path)
        if (file.exists()) {
            return file.delete()
        }
        return false
    }

    override fun cleanCache(): Long {
        val paths = listOf(File(filePaths.downloadsStoragePath), File(filePaths.apkPath),
                File(filePaths.obbPath))
        var totalBytesDeleted: Long = 0
        paths.forEach { path -> deleteDirectory(path) }
        return totalBytesDeleted
    }

    private fun deleteDirectory(path: File): Long {
        var totalBytesDeleted: Long = 0
        if (path.exists()) {
            val filesToDelete = Stack<File>()
            filesToDelete.push(path)
            while (!filesToDelete.isEmpty()) {
                val file = filesToDelete.pop()
                if (file.isDirectory) {
                    val listFiles = file.listFiles()
                    if (listFiles.isEmpty()) {
                        totalBytesDeleted += file.length()
                        file.delete()
                    } else {
                        for (child in listFiles) {
                            filesToDelete.push(child)
                        }
                    }
                } else {
                    totalBytesDeleted += file.length()
                    file.delete()
                }
            }
        }
        return totalBytesDeleted
    }
}
