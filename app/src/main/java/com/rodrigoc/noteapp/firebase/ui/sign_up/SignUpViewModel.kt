package com.rodrigoc.noteapp.firebase.ui.sign_up

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import com.rodrigoc.noteapp.LogService
import com.rodrigoc.noteapp.NotesAppViewModel
import com.rodrigoc.noteapp.R.string
import com.rodrigoc.noteapp.core.ext.isValidEmail
import com.rodrigoc.noteapp.core.ext.isValidPassword
import com.rodrigoc.noteapp.core.ext.passwordMatches
import com.rodrigoc.noteapp.core.snackbar.SnackbarManager
import com.rodrigoc.noteapp.feature_note.presentation.util.Screen
import com.rodrigoc.noteapp.firebase.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : NotesAppViewModel(logService) {

    var uiState = mutableStateOf(SignUpState())
        private set

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onSignUpClick(navController: NavController) {

        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(string.email_error)
            return
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(string.password_error)
            return
        }

        if (!password.passwordMatches(uiState.value.repeatPassword)) {
            SnackbarManager.showMessage(string.password_match_error)
            return
        }

        viewModelScope.launch(showErrorExceptionHandler) {
            val createAccountTrace = Firebase.performance.newTrace(CREATE_ACCOUNT_TRACE)
            createAccountTrace.start()

            accountService.linkAccount(email, password) { error ->
                createAccountTrace.stop()

                if (error == null) {
                    navController.navigate(Screen.LoginScreen.route)
                } else onError(error)
            }
        }
    }

    companion object {
        private const val CREATE_ACCOUNT_TRACE = "createAccount"
    }
}