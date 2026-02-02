package com.codemux.ide

import android.graphics.Typeface
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor

@Composable
fun FileEditorScreen(viewModel: IdeViewModel) {
    if (viewModel.currentFile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No file open. Select a file from the File Manager.")
        }
        return
    }

    var editorInstance by remember { mutableStateOf<CodeEditor?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                CodeEditor(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    typefaceText = Typeface.MONOSPACE
                    setText(viewModel.editorContent)
                    setLineNumberEnabled(true)
                    setEdgeEnabled(false)
                    editorInstance = this
                }
            },
            update = { editor ->
                if (editor.text.toString() != viewModel.editorContent) {
                    editor.setText(viewModel.editorContent)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        FloatingActionButton(
            onClick = {
                editorInstance?.let {
                    viewModel.saveFile(it.text.toString())
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = "Save")
        }
    }
}
