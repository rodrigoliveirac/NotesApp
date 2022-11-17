package com.rodrigoc.noteapp.di

import android.app.Application
import androidx.room.Room
import com.rodrigoc.LogServiceImpl
import com.rodrigoc.noteapp.LogService
import com.rodrigoc.noteapp.feature_note.data.data_source.NoteDatabase
import com.rodrigoc.noteapp.feature_note.data.repository.NoteRepositoryImpl
import com.rodrigoc.noteapp.feature_note.domain.repository.NoteRepository
import com.rodrigoc.noteapp.feature_note.domain.use_case.*
import com.rodrigoc.noteapp.firebase.AccountService
import com.rodrigoc.noteapp.firebase.AccountServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton //retrieve the corresponding dao with NoteDatabase
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCase(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotesUseCase(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }

    // Firebase features
    @Provides
    @Singleton
    fun provideAccountService(accountServiceImpl: AccountServiceImpl): AccountService {
        return accountServiceImpl
    }

    @Provides
    @Singleton
    fun provideLogService(logServiceImpl: LogServiceImpl): LogService {
        return logServiceImpl
    }

}