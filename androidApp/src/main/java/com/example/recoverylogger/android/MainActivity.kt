package com.example.recoverylogger.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.recoverylogger.android.data.FirebaseNotesRepository
import com.example.recoverylogger.android.presentation.navigation.AppNavigator
import com.example.recoverylogger.android.presentation.sign_in.GoogleAuthUIClient
import com.example.recoverylogger.presentation.note.NoteViewModel
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val noteViewModel = NoteViewModel(FirebaseNotesRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme() {
                AppNavigator(
                    googleAuthUiClient = googleAuthUiClient,
                    noteViewModel = noteViewModel
                )
            }
        }
    }
}
