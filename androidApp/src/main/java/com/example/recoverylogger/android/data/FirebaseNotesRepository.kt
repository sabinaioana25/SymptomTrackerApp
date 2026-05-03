package com.example.recoverylogger.android.data

import com.example.recoverylogger.domain.model.note.Note
import com.example.recoverylogger.domain.repository.NotesRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseNotesRepository : NotesRepository {

  private val notesCollection = Firebase.firestore.collection("notes")

  override suspend fun insert(note: Note) {
    notesCollection.document(note.id.toString()).set(note).await()
  }

  override suspend fun getAllNotes(): List<Note> {
    return notesCollection.get().await().documents.mapNotNull {
      it.toObject(Note::class.java)
    }
  }

  override suspend fun deleteSingleNote(note: Note) {
    try {
      // Use the ID from the passed-in 'note' object to delete the document
      notesCollection.document(note.id.toString()).delete().await()
    } catch(e: Exception) {
      // It's good practice to log any errors
      println("Error deleting note: ${e.message}")
    }
  }

  override suspend fun clear() {
    try {
      val snapshot = notesCollection.get().await()
      for (document in snapshot.documents) {
        document.reference.delete().await()
      }
    } catch (e: Exception) {
      println("Error clearing notes: ${e.message}")
    }
  }
}
