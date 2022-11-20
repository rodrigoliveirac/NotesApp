package com.rodrigoc.noteapp.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.rodrigoc.noteapp.LogService
import com.rodrigoc.noteapp.NotesAppViewModel
import com.rodrigoc.noteapp.feature_note.domain.model.Note
import com.rodrigoc.noteapp.feature_note.domain.use_case.NoteUseCases
import com.rodrigoc.noteapp.feature_note.domain.util.NoteOrder
import com.rodrigoc.noteapp.feature_note.domain.util.OrderType
import com.rodrigoc.noteapp.feature_note.presentation.add_edit_note.AddEditNoteViewModel
import com.rodrigoc.noteapp.firebase.AccountService
import com.rodrigoc.noteapp.firebase.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class NotesViewModel @Inject constructor(
    logService: LogService,
    private val noteUseCases: NoteUseCases,
    private val accountService: AccountService,
    private val storageService: StorageService,
) : NotesAppViewModel(logService) {

    //private val _state = mutableStateOf(NotesState())

    //    private val _state = mutableStateMapOf<String, NotesState>()
    // var state = mutableStateMapOf<String, NotesState>()
    // private set

//    private var _value = _state.values.map {
//        it
//    }.lastOrNull()
//
//    val value = _value

    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state

    private var recentlyDeletedNote: Note? = null

    private var getNotesJob: Job? = null

    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }

    fun addListener() {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.addListener(accountService.getUserId(), ::onDocumentEvent, ::onError)
        }
    }

    fun removeListener() {
        viewModelScope.launch(showErrorExceptionHandler) { storageService.removeListener() }
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.Order -> {
                if (state.value.noteOrder::class == event.noteOrder::class && state.value.noteOrder.orderType == event.noteOrder.orderType) {
                    return
                }
                getNotes(event.noteOrder)
            }
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note) //because that's an operator function we overrated the invoke operator, we can call this 'deleteNote' like a function, even though that's a class
                    recentlyDeletedNote = event.note
                    storageService.deleteNote(event.note.id.toString()) { error ->
                        if (error != null) onError(error)
                    }
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    noteUseCases.addNote(recentlyDeletedNote ?: return@launch)
                    saveOnFirebase()
                    recentlyDeletedNote = null
                }
            }
            is NotesEvent.ToggleOrderSection -> {
                _state.value = _state.value.copy(
                    isOrderSectionVisible = !_state.value.isOrderSectionVisible
                )
            }
        }
    }

    private fun saveOnFirebase() {
        if (recentlyDeletedNote != null) {
            storageService.saveNote(note = recentlyDeletedNote!!) { error ->
                val saveNoteTrace = Firebase.performance.newTrace("save_note_trace")
                saveNoteTrace.start()
                saveNoteTrace.stop()
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = noteUseCases.getNotes(noteOrder)
            .onEach { notes ->
                _state.value = state.value.copy(
                    notes = notes,
                    noteOrder = noteOrder
                )
            }.launchIn(viewModelScope)
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, note: Note) {
        val sd = _state.value.notes as MutableList
        if (wasDocumentDeleted) _state.value else sd[note.id!!] = note
    }
}