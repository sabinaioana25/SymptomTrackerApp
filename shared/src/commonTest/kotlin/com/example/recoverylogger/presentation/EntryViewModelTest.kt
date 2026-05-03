package com.example.recoverylogger.presentation

import com.example.recoverylogger.domain.model.entry.Entry
import com.example.recoverylogger.domain.model.question.Answer
import com.example.recoverylogger.domain.model.question.YesNoValue
import com.example.recoverylogger.domain.usecase.GetEntriesUseCase
import com.example.recoverylogger.presentation.entry.EntryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EntryViewModelTest {

    private val fakeRepository = FakeEntryRepository()
    private val getEntriesUseCase = GetEntriesUseCase(fakeRepository)

    @BeforeTest
    fun setup() {
        fakeRepository.reset()
    }

    // -------------------------------------------------------------------------
    // Loading state
    // -------------------------------------------------------------------------
    @Test
    fun `initial state has isLoading true before coroutine completes`() = runTest {
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        // Coroutine launched in init{} hasn't run yet - dispatcher is paused
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `isLoading is false after entries load`() = runTest {
        fakeRepository.entries = listOf(makeEntry("1"))
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    // -------------------------------------------------------------------------
    // Success state
    // -------------------------------------------------------------------------

    @Test
    fun `entries are mapped and returned on success`() = runTest {
        fakeRepository.entries = listOf(
            makeEntry(
                id = "abc",
                responses = mapOf(
                    "q1_heartburn" to Answer.Scale(4),
                    "q2_swallowing" to Answer.YesNo(YesNoValue.NO),
                    "q5_bloating" to Answer.Choice("Mild")
                )
            )
        )
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.entries.size)
        assertEquals("abc", state.entries.first().id)
        assertEquals(4, state.entries.first().heartburnScore)
        assertFalse(state.entries.first().hasDifficultySwallowing!!)
        assertEquals("Mild", state.entries.first().bloatingLevel)
        assertNull(state.error)
    }

    @Test
    fun `empty list is valid success - no error`() = runTest {
        fakeRepository.entries = emptyList()
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.entries.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    // -------------------------------------------------------------------------
    // Error state
    // -------------------------------------------------------------------------

    @Test
    fun `error message shown when repository throws`() = runTest {
        fakeRepository.shouldThrow = true
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Network error", state.error)
        assertFalse(state.isLoading)
        assertTrue(state.entries.isEmpty())
    }

    // -------------------------------------------------------------------------
    // Retry
    // -------------------------------------------------------------------------

    @Test
    fun `retry after error clears error and loads entries`() = runTest {
        fakeRepository.shouldThrow = true
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        fakeRepository.shouldThrow = false
        fakeRepository.entries = listOf(makeEntry("1"))
        viewModel.loadEntries()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertEquals(1, state.entries.size)
    }

    // -------------------------------------------------------------------------
    // Mapping edge cases
    // -------------------------------------------------------------------------

    @Test
    fun `hasConcerns true when q8 has text`() = runTest {
        fakeRepository.entries = listOf(makeEntry(responses = mapOf("q8_concerns" to Answer.Text("Some discomfort"))))
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.entries.first().hasConcerns)
    }

    @Test
    fun `hasConcerns false when q8 is blank`() = runTest {
        fakeRepository.entries = listOf(makeEntry(responses = mapOf("q8_concerns" to Answer.Text("   "))))
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.entries.first().hasConcerns)
    }

    @Test
    fun `isSynced is always false`() = runTest {
        fakeRepository.entries = listOf(makeEntry())
        val viewModel = EntryViewModel(getEntriesUseCase, this)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.entries.first().isSynced)
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private fun makeEntry(
        id: String = "test-id",
        responses: Map<String, Answer> = emptyMap()
    ) = Entry(
        id = id,
        userId = "user-1",
        entryDate = 1_714_300_800_000L, // Mon 28 Apr 2024
        responses = responses
    )
}
