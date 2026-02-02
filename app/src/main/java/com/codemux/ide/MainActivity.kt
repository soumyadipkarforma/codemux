package com.codemux.ide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodemuxApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodemuxApp() {
    val navController = rememberNavController()
    val viewModel: IdeViewModel = viewModel()
    var selectedItem by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val items = listOf("Files", "Editor", "Shell")
    val icons = listOf(Icons.Default.Folder, Icons.Default.Edit, Icons.Default.Terminal)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { /* TODO */ },
                    icon = { Icon(Icons.Default.Settings, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Themes") },
                    selected = false,
                    onClick = { /* TODO */ },
                    icon = { Icon(Icons.Default.Palette, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Shortcuts") },
                    selected = false,
                    onClick = { /* TODO */ },
                    icon = { Icon(Icons.Default.Keyboard, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Terminal Preferences") },
                    selected = false,
                    onClick = { /* TODO */ },
                    icon = { Icon(Icons.Default.SettingsApplications, null) }
                )
                NavigationDrawerItem(
                    label = { Text("App Info") },
                    selected = false,
                    onClick = { /* TODO */ },
                    icon = { Icon(Icons.Default.Info, null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Codemux") },
                    navigationIcon = {
                        IconButton(onClick = { 
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(icons[index], contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                navController.navigate(item) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(navController, startDestination = "Files", Modifier.padding(innerPadding)) {
                composable("Files") { FileManagerScreen(viewModel) }
                composable("Editor") { FileEditorScreen(viewModel) }
                composable("Shell") { ShellScreen(viewModel) }
            }
        }
    }
}
