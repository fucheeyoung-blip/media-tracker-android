package edu.metrostate.ics342.mediatracker.ui.library

import android.app.Application
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.LibraryStatus
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.network.DefaultLibraryRepository
import edu.metrostate.ics342.mediatracker.data.network.LibraryResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelRollbackTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: DefaultLibraryRepository
    private lateinit var viewModel: LibraryViewModel

    private val fakeMedia = Media(
        id = 1,
        mediaType = "movie",
        title = "Severance"
    )

    private val fakeItem = LibraryItem(
        userId = "u1",
        mediaId = 1,
        status = LibraryStatus.WANT_TO,
        addedAt = "2026-01-01T00:00:00Z",
        updatedAt = "2026-01-01T00:00:00Z",
        media = fakeMedia
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

        coEvery { repository.getLibrary(LibraryStatus.WANT_TO) } returns
                LibraryResult.Success(listOf(fakeItem))

        val application = mockk<Application>(relaxed = true)
        viewModel = LibraryViewModel(application, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `removeItem rolls back when network call fails`() = runTest(testDispatcher) {
        advanceUntilIdle()

        val seeded = viewModel.uiState.value as LibraryUiState.Success
        assertEquals(1, seeded.items.size)

        coEvery { repository.removeFromLibrary(1) } throws IOException("network down")

        viewModel.removeItem(1)

        val optimisticState = viewModel.uiState.value as LibraryUiState.Success
        assertTrue(optimisticState.items.isEmpty())

        advanceUntilIdle()

        val finalState = viewModel.uiState.value as LibraryUiState.Success
        assertEquals(1, finalState.items.size)
        assertEquals(1, finalState.items.first().mediaId)
    }
}