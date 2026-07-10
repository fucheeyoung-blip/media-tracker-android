package edu.metrostate.ics342.mediatracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.R
import edu.metrostate.ics342.mediatracker.data.LoginResult
import edu.metrostate.ics342.mediatracker.data.RegisterResult
import edu.metrostate.ics342.mediatracker.data.SessionRepository
import edu.metrostate.ics342.mediatracker.data.UserRepository
import edu.metrostate.ics342.mediatracker.data.datastore.DefaultSessionRepository
import edu.metrostate.ics342.mediatracker.data.network.DefaultUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UserRepository = DefaultUserRepository(),
    private val sessionRepository: SessionRepository
) : ViewModel() {

    companion object {
        fun factory(context: android.content.Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val sessionRepository = DefaultSessionRepository(context.applicationContext)
                    return AuthViewModel(sessionRepository = sessionRepository) as T
                }
            }
    }

    sealed class AuthUiState {
        object Idle    : AuthUiState()
        object Loading : AuthUiState()
        object Success : AuthUiState()
        data class Error(val msgResId: Int) : AuthUiState()
    }

    // Login

    private val _email    = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    fun onEmailChange(value: String)    { _email.value    = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun onLoginClick() {
        viewModelScope.launch {
            _loginState.value = AuthUiState.Loading

            if (_email.value.isBlank() || _password.value.isBlank()) {
                _loginState.value = AuthUiState.Error(R.string.error_empty_credentials)
                return@launch
            }

            when (val result = repository.login(_email.value, _password.value)) {
                is LoginResult.Success -> {
                    sessionRepository.saveSession(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken,
                        user = result.user
                    )
                    _loginState.value = AuthUiState.Success
                }
                LoginResult.InvalidCredentials ->
                    _loginState.value = AuthUiState.Error(R.string.error_empty_credentials)
                LoginResult.NetworkError ->
                    _loginState.value = AuthUiState.Error(R.string.error_empty_credentials)
                LoginResult.UnknownError ->
                    _loginState.value = AuthUiState.Error(R.string.error_empty_credentials)
            }
        }
    }

    fun resetLoginState() { _loginState.value = AuthUiState.Idle }

    // Register

    private val _registerState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    fun onRegisterClick(
        displayName: String,
        username: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _registerState.value = AuthUiState.Loading
            when (repository.register(
                email = email,
                password = password,
                username = username,
                displayName = displayName
            )) {
                RegisterResult.Success -> _registerState.value = AuthUiState.Success
                RegisterResult.Conflict -> _registerState.value = AuthUiState.Error(R.string.error_empty_credentials)
                RegisterResult.NetworkError -> _registerState.value = AuthUiState.Error(R.string.error_empty_credentials)
                RegisterResult.UnknownError -> _registerState.value = AuthUiState.Error(R.string.error_empty_credentials)
            }
        }
    }

    fun resetRegisterState() { _registerState.value = AuthUiState.Idle }
}