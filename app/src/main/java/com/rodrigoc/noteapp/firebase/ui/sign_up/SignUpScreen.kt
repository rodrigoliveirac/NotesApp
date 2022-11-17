package com.rodrigoc.noteapp.firebase.ui.sign_up

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rodrigoc.noteapp.R
import com.rodrigoc.noteapp.core.composable.BasicToolbar
import com.rodrigoc.noteapp.core.ext.basicButton
import com.rodrigoc.noteapp.core.ext.fieldModifier
import com.rodrigoc.noteapp.firebase.ui.composable.BasicButton
import com.rodrigoc.noteapp.firebase.ui.composable.EmailField
import com.rodrigoc.noteapp.firebase.ui.composable.PasswordField
import com.rodrigoc.noteapp.firebase.ui.composable.RepeatPasswordField

@Composable
fun SignUpScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val fieldModifier = Modifier.fieldModifier()

    BasicToolbar(R.string.create_account)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(uiState.email, viewModel::onEmailChange, fieldModifier)
        PasswordField(uiState.password, viewModel::onPasswordChange, fieldModifier)
        RepeatPasswordField(uiState.repeatPassword, viewModel::onRepeatPasswordChange, fieldModifier)

        BasicButton(R.string.create_account, Modifier.basicButton()) {

            viewModel.onSignUpClick(navController)
        }
    }
}