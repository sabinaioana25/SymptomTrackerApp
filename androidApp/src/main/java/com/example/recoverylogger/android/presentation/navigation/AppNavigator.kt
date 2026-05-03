package com.example.recoverylogger.android.presentation.navigation

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.recoverylogger.android.NoteScreen
import com.example.recoverylogger.android.presentation.entry.EntryLogAndroidViewModel
import com.example.recoverylogger.android.presentation.profile.ProfileScreen
import com.example.recoverylogger.android.presentation.questionnaire.QuestionnaireAndroidViewModel
import com.example.recoverylogger.android.presentation.sign_in.GoogleAuthUIClient
import com.example.recoverylogger.android.presentation.sign_in.SignInScreen
import com.example.recoverylogger.android.presentation.sign_in.SignInViewModel
import com.example.recoverylogger.presentation.entry.EntryLogContent
import com.example.recoverylogger.presentation.note.NoteViewModel
import com.example.recoverylogger.presentation.questionnaire.QuestionnaireScreen
import com.example.recoverylogger.presentation.questionnaire.SuccessScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigator(
  googleAuthUiClient: GoogleAuthUIClient,
  noteViewModel: NoteViewModel
) {
  val navController = rememberNavController()
  val context = LocalContext.current
  val activity = context as? androidx.activity.ComponentActivity

  NavHost(
    navController = navController,
    startDestination = Route.ENTRY_FLOW
  ) {

    // ── Entry flow graph (Questionnaire + Success share a ViewModel) ────
    navigation(startDestination = Route.QUESTIONNAIRE, route = Route.ENTRY_FLOW) {
      composable(Route.QUESTIONNAIRE) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
          navController.getBackStackEntry(Route.ENTRY_FLOW)
        }
        val viewModel = hiltViewModel<QuestionnaireAndroidViewModel>(parentEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(state.isSubmitted) {
          if (state.isSubmitted) {
            navController.navigate(Route.SUCCESS) {
              popUpTo(Route.QUESTIONNAIRE) { inclusive = true }
            }
          }
        }

        QuestionnaireScreen(
          state = state,
          onIntent = viewModel::processIntent
        )
      }

      composable(Route.SUCCESS) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
          navController.getBackStackEntry(Route.ENTRY_FLOW)
        }
        val viewModel = hiltViewModel<QuestionnaireAndroidViewModel>(parentEntry)
        val state by viewModel.state.collectAsStateWithLifecycle()

        state.lastSubmittedEntry?.let { entry ->
          SuccessScreen(
            entry = entry,
            onDone = {
              navController.navigate(Route.PROFILE) {
                popUpTo(Route.ENTRY_FLOW) { inclusive = true }
              }
            }
          )
        }
      }
    }

    // ── Entry log (history) ─────────────────────────────────────────────
    composable(Route.ENTRY_LOG) {
      val viewModel = hiltViewModel<EntryLogAndroidViewModel>()
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()

      EntryLogContent(
        uiState = uiState,
        onRetry = viewModel::loadEntries,
        onBack = { navController.popBackStack() }
      )
    }

    composable(Route.SIGN_IN) {
      val viewModel = viewModel<SignInViewModel>()
      val state by viewModel.state.collectAsStateWithLifecycle()

      val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
          if (result.resultCode == RESULT_OK) {
            viewModel.viewModelScope.launch {
              val signInResult = googleAuthUiClient.signInWithIntent(
                intent = result.data ?: return@launch
              )
              viewModel.onSignInResult(signInResult)
            }
          }
        }
      )

      LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
          Toast.makeText(
            context,
            "sign in ok",
            Toast.LENGTH_LONG
          ).show()

          navController.navigate(Route.NOTE) {
            popUpTo(Route.SIGN_IN) {
              inclusive = true
            }
          }
          viewModel.resetState()
        }
      }

      SignInScreen(
        state = state,
        onSignInClick = {
          viewModel.startSignIn(googleAuthUiClient, launcher)
        }
      )
    }

    composable(Route.PROFILE) {
      ProfileScreen(
        userData = googleAuthUiClient.getSignedInUser(),
        onSignOut = {
          activity?.lifecycleScope?.launch {
            googleAuthUiClient.signOut()
            Toast.makeText(
              context,
              "Signed out",
              Toast.LENGTH_LONG
            ).show()
            navController.navigate(Route.SIGN_IN)
          }
        },
        onGoToNotes = { navController.navigate(Route.NOTE) },
        onViewEntries = { navController.navigate(Route.ENTRY_LOG) }
      )
    }

    composable(Route.NOTE) {
     NoteScreen(
       viewModel = noteViewModel,
       onProfileClick = {
         navController.navigate(Route.PROFILE)
       }
     )
    }
  }
}
