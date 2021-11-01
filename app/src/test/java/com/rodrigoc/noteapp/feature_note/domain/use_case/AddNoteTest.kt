package com.rodrigoc.noteapp.feature_note.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.rodrigoc.noteapp.feature_note.domain.model.InvalidNoteException
import com.rodrigoc.noteapp.feature_note.domain.model.Note
import com.rodrigoc.noteapp.feature_note.domain.repository.FakeNotesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import kotlin.jvm.Throws

//TODO:
class AddNoteTest {

    private lateinit var note: Note
    private lateinit var addNote: AddNote
    private lateinit var repository: FakeNotesRepository

    @Before
    fun setUp() {
        repository = FakeNotesRepository()
    }
}