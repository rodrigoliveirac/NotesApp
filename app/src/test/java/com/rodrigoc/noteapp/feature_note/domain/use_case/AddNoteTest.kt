package com.rodrigoc.noteapp.feature_note.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.rodrigoc.noteapp.feature_note.domain.model.InvalidNoteException
import com.rodrigoc.noteapp.feature_note.domain.model.Note
import com.rodrigoc.noteapp.feature_note.domain.repository.FakeNotesRepository
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Before
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AddNoteTest {

    private lateinit var addNote: AddNote
    private lateinit var repository: FakeNotesRepository

    @Before
    fun setUp() {
        repository = FakeNotesRepository()
        addNote = AddNote(repository)
    }

    @Test
    fun `if title is blank`() {
        val note = Note("", "", 0.toLong(), 0, 0)
        try {
            runBlocking { addNote.invoke(note) }
            fail()
        } catch (message: InvalidNoteException) {
            assertThat(message.message).matches("The title of the note can't be empty.")
        }
    }

    @Test
    fun `if content is blank`() {
        val note = Note("e", "", 0.toLong(), 0, 0)
        try {
            runBlocking { addNote.invoke(note) }
            fail()
        } catch (message: InvalidNoteException) {
            assertThat(message.message).matches("The content of the note can't be empty.")
        }
    }

    @Test
    fun `if title and content is not blank`() {
        val note = Note("e", "e", 0.toLong(), 0, 0)
        runBlocking { addNote.invoke(note) }
    }

}