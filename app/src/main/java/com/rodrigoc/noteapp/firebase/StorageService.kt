package com.rodrigoc.noteapp.firebase

import com.google.android.gms.tasks.Task
import com.rodrigoc.noteapp.feature_note.domain.model.Note

interface StorageService {

    fun addListener(
        userId: String,
        onDocumentEvent:(Boolean, Note) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun removeListener()
    fun getNote(noteId: String, onError: (Throwable) -> Unit, onSuccess: (Note) -> Unit)
    fun saveNote(note: Note, onResult: (Throwable?) -> Unit)
    fun updateNote(note: Note, onResult: (Throwable?) -> Unit)
    fun deleteNote(noteId: String, onResult:(Throwable?) -> Unit)
    fun deleteAllForUser(userId: String, onResult: (Throwable?) -> Unit)
}