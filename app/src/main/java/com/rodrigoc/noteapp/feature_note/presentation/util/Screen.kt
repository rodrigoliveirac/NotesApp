package com.rodrigoc.noteapp.feature_note.presentation.util

sealed class Screen(val route: String) {
    object NotesScreen: Screen("notes_screen")
    object AddEditNoteScreen: Screen("add_edit_note_screen")

    object LoginScreen: Screen("login_screen")
    object SignUpScreen: Screen("signup_screen")
}
