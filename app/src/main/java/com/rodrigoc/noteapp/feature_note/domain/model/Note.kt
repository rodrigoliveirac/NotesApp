package com.rodrigoc.noteapp.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rodrigoc.noteapp.ui.theme.*

@Entity
data class Note(
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val timestamp: Long = "".toLong(),
    val color: Int = "".toInt(),
    @PrimaryKey val id: Int? = null,
) {
    companion object {
        val noteColors = listOf(RedOrange, LightGreen, Violet, BabyBlue, RedPink)
    }
}

class InvalidNoteException(message: String): Exception(message)
