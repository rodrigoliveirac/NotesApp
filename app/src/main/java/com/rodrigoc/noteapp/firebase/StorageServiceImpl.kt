package com.rodrigoc.noteapp.firebase

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rodrigoc.noteapp.feature_note.domain.model.Note
import com.rodrigoc.noteapp.feature_note.presentation.notes.NotesState
import javax.inject.Inject

class StorageServiceImpl @Inject constructor() : StorageService {

    private var listenerRegistration: ListenerRegistration? = null

    override fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Note) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val query = Firebase.firestore.collection(NOTE_COLLECTION).whereEqualTo(USER_ID, userId)

        listenerRegistration = query.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            value?.documentChanges?.forEach { changes ->
                val wasDocumentDeleted = changes.type == REMOVED
                val note = changes.document.toObject<Note>().copy(id = changes.document.id.toInt())
                onDocumentEvent(wasDocumentDeleted, note)
            }
        }

    }

    override fun removeListener() {
        listenerRegistration?.remove()
    }

    override fun getNote(noteId: String, onError: (Throwable) -> Unit, onSuccess: (Note) -> Unit) {
        Firebase.firestore
            .collection(NOTE_COLLECTION)
            .document(noteId)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result ->
                val note = result.toObject<Note>()?.copy(id = result.id.toInt())
                onSuccess(note ?: Note())
            }
    }

    override fun saveNote(note: Note, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(NOTE_COLLECTION)
            .add(note)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun updateNote(note: Note, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(NOTE_COLLECTION)
            .document(note.id.toString())
            .set(note)
    }

    override fun deleteNote(noteId: String, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(NOTE_COLLECTION)
            .document(noteId)
            .delete()
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun deleteAllForUser(userId: String, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(NOTE_COLLECTION)
            .whereEqualTo(USER_ID, userId)
            .get()
            .addOnFailureListener { error -> onResult(error) }
            .addOnSuccessListener { result ->
                for (document in result) document.reference.delete()
                onResult(null)
            }
    }

    companion object {
        private const val NOTE_COLLECTION = "Note"
        private const val USER_ID = "userId"
    }
}