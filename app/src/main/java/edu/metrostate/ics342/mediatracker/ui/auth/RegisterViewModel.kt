package edu.metrostate.ics342.mediatracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.metrostate.ics342.mediatracker.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun setDisplayName(newValue: String) { _displayName.value = newValue }
    fun setUsername(newValue: String) { _username.value = newValue }
    fun setEmail(newValue: String) { _email.value = newValue }
    fun setPassword(newValue: String) { _password.value = newValue }

    fun onSignUpClicked() {
        viewModelScope.launch {
            userRepository.createAccount(
                displayName = _displayName.value,
                username = _username.value,
                email = _email.value,
                password = _password.value
            )
        }
    }
}