package com.rodrigoc.noteapp.feature_note.presentation.notes

import com.rodrigoc.noteapp.feature_note.domain.model.Note
import com.rodrigoc.noteapp.feature_note.domain.util.NoteOrder
import com.rodrigoc.noteapp.feature_note.domain.util.OrderType

data class NotesState(
    var notes: List<Note> = emptyList(),
    var noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    var isOrderSectionVisible: Boolean = false
)
