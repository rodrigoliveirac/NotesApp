package com.rodrigoc.noteapp.feature_note.domain.use_case

data class NoteUseCases(
    val getNotes: GetNotesUseCase,
    val deleteNote: DeleteNote,
    val addNote: AddNote
    )
