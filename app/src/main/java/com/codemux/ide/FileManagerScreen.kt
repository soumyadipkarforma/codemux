package com.codemux.ide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileManagerScreen(viewModel: IdeViewModel) {
    val files = viewModel.currentDirectory.listFiles()?.sortedWith(
        compareBy<File> { !it.isDirectory }.thenBy { it.name.lowercase() }
    ) ?: emptyList()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedFileForMenu by remember { mutableStateOf<File?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                if (viewModel.currentDirectory.path != "/storage/emulated/0/sworkspace") {
                    FileItem("..", isDirectory = true, onLongClick = {}) {
                        viewModel.currentDirectory = viewModel.currentDirectory.parentFile ?: viewModel.currentDirectory
                    }
                }
            }
            items(files) { file ->
                FileItem(
                    name = file.name,
                    isDirectory = file.isDirectory,
                    onLongClick = { selectedFileForMenu = file }
                ) {
                    if (file.isDirectory) {
                        viewModel.currentDirectory = file
                    } else {
                        viewModel.openFile(file)
                    }
                }
            }
        }
        
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }

        if (showCreateDialog) {
            CreateFileDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, isFolder ->
                    val newFile = File(viewModel.currentDirectory, name)
                    if (isFolder) newFile.mkdirs() else newFile.createNewFile()
                    showCreateDialog = false
                    // Refresh triggered by state change if we had a more reactive file system watcher
                }
            )
        }

        selectedFileForMenu?.let { file ->
            FileContextMenu(
                file = file,
                onDismiss = { selectedFileForMenu = null },
                onAction = { action ->
                    // Handle actions: Rename, Delete, ZIP, GZIP
                    when (action) {
                        "Delete" -> file.delete()
                        // "Rename" -> ...
                    }
                    selectedFileForMenu = null
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileItem(name: String, isDirectory: Boolean, onLongClick: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isDirectory) Icons.Default.Folder else Icons.Default.Description,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = name, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun CreateFileDialog(onDismiss: () -> Unit, onCreate: (String, Boolean) -> Unit) {
    var name by remember { mutableStateOf("") }
    var isFolder by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New") },
        text = {
            Column {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFolder, onCheckedChange = { isFolder = it })
                    Text("Folder")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(name, isFolder) }) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun FileContextMenu(file: File, onDismiss: () -> Unit, onAction: (String) -> Unit) {
    DropdownMenu(expanded = true, onDismissRequest = onDismiss) {
        DropdownMenuItem(text = { Text("Rename") }, onClick = { onAction("Rename") })
        DropdownMenuItem(text = { Text("Delete") }, onClick = { onAction("Delete") })
        DropdownMenuItem(text = { Text("Compress to ZIP") }, onClick = { onAction("ZIP") })
        DropdownMenuItem(text = { Text("Compress to GZIP") }, onClick = { onAction("GZIP") })
    }
}
