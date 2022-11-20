package com.rodrigoc.noteapp.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.rodrigoc.noteapp.LogService
import com.rodrigoc.noteapp.NotesAppViewModel
import com.rodrigoc.noteapp.core.ext.idFromParameter
import com.rodrigoc.noteapp.feature_note.domain.model.InvalidNoteException
import com.rodrigoc.noteapp.feature_note.domain.model.Note
import com.rodrigoc.noteapp.feature_note.domain.use_case.NoteUseCases
import com.rodrigoc.noteapp.firebase.AccountService
import com.rodrigoc.noteapp.firebase.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    logService: LogService,
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle,
    private val storageService: StorageService,
    private val accountService: AccountService
) : NotesAppViewModel(logService) {

    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter title..."
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter some content..."
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNoteId: Int? = null
    private var userId: String = ""

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if(noteId != -1) {
                viewModelScope.launch {
                    noteUseCases.getNote(noteId)?.also { note ->
                        currentNoteId = note.id
                        userId = note.userId
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteContent.value = noteContent.value.copy(
                            text = note.content,
                            isHintVisible = false
                        )
                        _noteColor.value = note.color
                    }

                    storageService.getNote(noteId.toString().idFromParameter(), ::onError) {
                        var note = Note(
                            userId  = userId,
                            id = currentNoteId,
                            title = _noteTitle.value.text,
                            content = _noteContent.value.text,
                            color = _noteColor.value
                        )
                        note = it
                    }
                }
            }
        }
    }
    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            noteTitle.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value = noteContent.value.copy(
                    text = event.value
                )
            }
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value = noteContent.value.copy(
                    isHintVisible = !event.focusState.isFocused &&
                            noteContent.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }
            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    val note = Note(
                        title = _noteTitle.value.text,
                        content = _noteContent.value.text,
                        timestamp = System.currentTimeMillis(),
                        color = noteColor.value,
                        id = currentNoteId,
                        userId = userId,
                    )
                    val e = note.copy(userId = accountService.getUserId())
                    try {
                        noteUseCases.addNote(
                            note
                        )
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBack(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                    if(e.id.toString().isBlank()) saveNote(note) else updateNote(note)
                    _eventFlow.emit(UiEvent.SaveNote)
                }
            }
        }
    }

    private fun updateNote(note: Note) {
        val updateNoteTrace = Firebase.performance.newTrace("UPDATE_TASK_TRACE")
        updateNoteTrace.start()

        storageService.updateNote(note) { error ->
            updateNoteTrace.stop()
            viewModelScope.launch {
                if (error == null) _eventFlow.emit(UiEvent.SaveNote) else onError(error)
            }
        }
    }

    private fun saveNote(note: Note) {
        val saveNoteTrace = Firebase.performance.newTrace("save_note_trace")
        saveNoteTrace.start()
        storageService.saveNote(note) { error ->
            saveNoteTrace.stop()
            viewModelScope.launch {
                if (error == null) _eventFlow.emit(UiEvent.SaveNote) else onError(error)
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackBack(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}