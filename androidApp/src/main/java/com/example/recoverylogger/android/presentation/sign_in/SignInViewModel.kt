package com.example.recoverylogger.android.presentation.sign_in

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel : ViewModel() {

  private val _state = MutableStateFlow(SignInState())

  // publicly exposed mutable version of the state
  val state = _state.asStateFlow()

  fun onSignInResult(result: SignInResult) {
    _state.update {
      it.copy(
        isSuccess = result.data != null,
        isError = result.errorMessage
      )
    }
  }

  fun resetState() {
    _state.update { SignInState() }
  }

  fun startSignIn(
    googleAuthUIClient: GoogleAuthUIClient,
    launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
  ) {
    viewModelScope.launch {
      val signInIntentSender = googleAuthUIClient.signIn()
      launcher.launch(
        IntentSenderRequest.Builder(
          signInIntentSender ?: return@launch
        ).build()
      )
    }
  }
}
