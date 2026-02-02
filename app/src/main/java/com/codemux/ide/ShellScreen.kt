package com.codemux.ide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShellScreen(viewModel: IdeViewModel) {
    var command by remember { mutableStateOf("") }
    val output = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(output) { line ->
                Text(
                    text = line,
                    color = Color.Green,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = command,
                onValueChange = { command = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter command...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.Green
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                ),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send)
            )
            IconButton(onClick = {
                if (command.isNotBlank()) {
                    output.add("$ ${viewModel.currentDirectory.path}> $command")
                    executeTermuxCommand(context, command, viewModel.currentDirectory.path)
                    command = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Green)
            }
        }
    }
}

fun executeTermuxCommand(context: Context, command: String, workingDir: String) {
    val intent = Intent("com.termux.tasker.ACTION_EXECUTE")
    intent.setClassName("com.termux.tasker", "com.termux.tasker.ExecuteTaskService")
    
    val bundle = Bundle()
    bundle.putString("executable", "/data/data/com.termux/files/usr/bin/bash")
    bundle.putStringArray("arguments", arrayOf("-c", command))
    bundle.putString("working_directory", workingDir)
    
    intent.putExtra("com.termux.tasker.extra.BUNDLE", bundle)
    intent.putExtra("com.termux.tasker.extra.VERSION_CODE", 5) // Minimal required version
    
    context.startService(intent)
}
