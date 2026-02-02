package com.codemux.ide

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.io.File

class IdeViewModel : ViewModel() {
    var currentDirectory by mutableStateOf(File("/storage/emulated/0/sworkspace"))
    var currentFile by mutableStateOf<File?>(null)
    var editorContent by mutableStateOf("")

    init {
        if (!currentDirectory.exists()) {
            currentDirectory.mkdirs()
        }
    }

    fun openFile(file: File) {
        currentFile = file
        editorContent = if (file.exists()) file.readText() else ""
    }

    fun saveFile(content: String) {
        currentFile?.writeText(content)
        editorContent = content
    }
}
