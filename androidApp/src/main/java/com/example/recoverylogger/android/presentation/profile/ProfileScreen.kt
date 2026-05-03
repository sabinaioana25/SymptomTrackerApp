package com.example.recoverylogger.android.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recoverylogger.android.presentation.sign_in.UserData

@Composable
fun ProfileScreen(
    userData: UserData?,
    onSignOut: () -> Unit,
    onGoToNotes: () -> Unit,
    onViewEntries: () -> Unit
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(16.dp)
      ) {
        if (userData?.username != null) {
          Text(
            text = userData.username,
            textAlign = TextAlign.Center,
            fontSize = 36.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(16.dp))
        }

        Button(onClick = onGoToNotes) {
          Text(text = "My Notes")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onViewEntries) {
          Text(text = "View All Entries")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = onSignOut) {
          Text(text = "Sign out")
        }
      }
    }
}
