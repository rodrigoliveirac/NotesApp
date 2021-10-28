package com.rodrigoc.noteapp.feature_note.domain.use_case

import com.rodrigoc.noteapp.feature_note.domain.model.Note

data class NoteUseCases(
    val getNotes: GetNotesUseCase,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val getNote: GetNote
)
