package com.example.movieapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.db.DatabaseProvider
import com.example.movieapp.data.db.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Enum representing current authentication mode
 * LOGIN = existing user
 * REGISTER = create a new account
 */
enum class AuthMode { Login, Register }

/**
 * Data class representing all UI state for the authentication screen
 * Used to bind and reactively update Compose UI
 */
data class AuthUiState(
    val mode: AuthMode = AuthMode.Login,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val userId: Long? = null
)

/**
 * ViewModel responsible for handling authentication logic:
 * Switching between login/register modes
 * Validating input
 * Inserting new users into Room DB
 * Logging in existing users
 */
class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val userDao = DatabaseProvider.get(app).userDao()

    private val _ui = MutableStateFlow(AuthUiState())
    val ui = _ui.asStateFlow()

    fun switchMode() {
        _ui.value = _ui.value.copy(
            mode = if (_ui.value.mode == AuthMode.Login) AuthMode.Register else AuthMode.Login,
            error = null
        )
    }
    /** Field update functions for username, email, and password */
    fun updateUsername(v: String) {_ui.value = _ui.value.copy(username = v)}
    fun updateEmail(v: String) {_ui.value = _ui.value.copy(email = v)}
    fun updatePassword(v: String) {_ui.value = _ui.value.copy(password =v)}

    fun submit() {
        val s = _ui.value

        if (s.email.isBlank() || s.password.length < 4 || (s.mode == AuthMode.Register && s.username.isBlank()))
        {
            _ui.value = s.copy (error = "Please fill all fields (password ≥ 4)")
            return
        }

        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true, error = null)
            try {
                when (s.mode) {
                    AuthMode.Login -> {
                        val user = userDao.login(s.email, s.password)
                        _ui.value = if (user != null)
                            s.copy(isLoggedIn = true, isLoading = false, userId = user.id.toLong())
                        else s.copy(error = "Invalid credentials", isLoading = false)
                    }
                    AuthMode.Register -> {
                        if (userDao.getUserByEmail(s.email) != null) {
                            _ui.value = s.copy(error = "Email already registered.", isLoading = false)

                        } else {
                            val newId = userDao.insertUser(
                                UserEntity(
                                    username = s.username,
                                    email = s.email,
                                    password = s.password
                                )
                            )
                            _ui.value = s.copy(
                                isLoggedIn = true,
                                isLoading = false,
                                userId = newId
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _ui.value = s.copy(error = e.message ?: "Unknown error", isLoading = false)
            }
        }


    }

    fun logout() {
        _ui.value = AuthUiState()
    }
}