package com.rodrigoc.noteapp.feature_note.presentation

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rodrigoc.noteapp.core.snackbar.SnackbarManager
import com.rodrigoc.noteapp.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.rodrigoc.noteapp.feature_note.presentation.notes.NotesScreen
import com.rodrigoc.noteapp.feature_note.presentation.util.Screen
import com.rodrigoc.noteapp.firebase.ui.login.LoginScreen
import com.rodrigoc.noteapp.firebase.ui.sign_up.SignUpScreen
import com.rodrigoc.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    val appState = rememberAppState()
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(
                                hostState = it,
                                modifier = Modifier.padding(8.dp),
                                snackbar = { snackbarData ->
                                    Snackbar(
                                        snackbarData,
                                        contentColor = MaterialTheme.colors.onPrimary
                                    )
                                }
                            )
                        },
                        scaffoldState = appState.scaffoldState
                    ) { innerPaddingModifier ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.LoginScreen.route,
                            modifier = Modifier.padding(innerPaddingModifier)
                        ) {
                            composable(route = Screen.LoginScreen.route) {
                                LoginScreen(navController = navController)
                            }
                            composable(route = Screen.SignUpScreen.route) {
                                SignUpScreen(navController = navController)
                            }
                            composable(route = Screen.NotesScreen.route) {
                                NotesScreen(navController = navController)
                            }
                            composable(
                                route = Screen.AddEditNoteScreen.route +
                                        "?noteId={noteId}&noteColor={noteColor}",
                                arguments = listOf(
                                    navArgument(
                                        name = "noteId"
                                    ) {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    },
                                    navArgument(
                                        name = "noteColor"
                                    ) {
                                        type = NavType.IntType
                                        defaultValue = -1
                                    },
                                )
                            ) {
                                val color = it.arguments?.getInt("noteColor") ?: -1
                                AddEditNoteScreen(
                                    navController = navController,
                                    noteColor = color
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(scaffoldState, navController, snackbarManager, resources, coroutineScope) {
    NotesAppState(scaffoldState, snackbarManager, resources, coroutineScope)
}

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}