package com.aizid.playmov.ui.profile

import com.aizid.playmov.data.pref.PrefManager
import com.aizid.playmov.remote.dto.account.AccountDetails
import com.aizid.playmov.remote.dto.account.RoleDef
import com.aizid.playmov.testutil.FakeAccountRepo
import com.aizid.playmov.utils.network.DataSource
import com.aizid.playmov.utils.network.FailureData
import com.aizid.playmov.utils.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

class FakeProfilePrefManager(
    private var token: String = "token_123",
    private var accId: String = "acc_001",
    private var accName: String = "John Doe"
) : PrefManager(null) {
    override suspend fun getAuthToken(): String = token
    override suspend fun setAuthToken(value: String) { token = value }
    override suspend fun getAccountId(): String = accId
    override suspend fun setAccountId(value: String) { accId = value }
    override suspend fun getAccountName(): String = accName
    override suspend fun setAccountName(value: String) { accName = value }
    override suspend fun clearAuth() {
        token = ""
        accId = ""
        accName = ""
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadProfile_loadsAccountDetailsSuccessfully() = runTest(testDispatcher) {
        val fakeRepo = FakeAccountRepo().apply {
            detailsResult = Resource.Success(
                AccountDetails(
                    accId = "acc_001",
                    name = "John Doe",
                    email = "john@example.com",
                    roles = listOf(RoleDef("r1", "USER"))
                ),
                DataSource.REMOTE
            )
        }
        val fakePref = FakeProfilePrefManager(accId = "acc_001")
        val viewModel = ProfileViewModel(fakeRepo, fakePref)

        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        val details = viewModel.uiState.value.accountDetails
        assertNotNull(details)
        assertEquals("acc_001", details.accId)
        assertEquals("John Doe", details.name)
        assertEquals("john@example.com", details.email)
        assertEquals(1, details.roles.size)
        assertEquals("USER", details.roles[0].roleDefName)
    }

    @Test
    fun loadProfile_whenNoAccountId_usesCachedName() = runTest(testDispatcher) {
        val fakeRepo = FakeAccountRepo()
        val fakePref = FakeProfilePrefManager(accId = "", accName = "Cached User")
        val viewModel = ProfileViewModel(fakeRepo, fakePref)

        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        val details = viewModel.uiState.value.accountDetails
        assertNotNull(details)
        assertEquals("Cached User", details.name)
    }

    @Test
    fun loadProfile_setsErrorMessageOnFailure() = runTest(testDispatcher) {
        val fakeRepo = FakeAccountRepo().apply {
            detailsResult = Resource.Failure(FailureData(500, "Network error"))
        }
        val fakePref = FakeProfilePrefManager(accId = "acc_001", accName = "")
        val viewModel = ProfileViewModel(fakeRepo, fakePref)

        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Network error", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun logout_clearsAuthAndSetsFlag() = runTest(testDispatcher) {
        val fakeRepo = FakeAccountRepo()
        val fakePref = FakeProfilePrefManager()
        val viewModel = ProfileViewModel(fakeRepo, fakePref)

        testScheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoggedOut)

        viewModel.logout()
        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLoggedOut)
    }
}
