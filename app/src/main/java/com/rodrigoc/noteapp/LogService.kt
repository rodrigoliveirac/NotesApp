package com.rodrigoc.noteapp

interface LogService {
    fun logNonFatalCrash(throwable: Throwable?)
}
