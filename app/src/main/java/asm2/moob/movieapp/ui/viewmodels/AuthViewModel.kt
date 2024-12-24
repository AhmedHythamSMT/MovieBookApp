package asm2.moob.movieapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val auth = FirebaseAuth.getInstance()

    init {
        // Check if user is already logged in
        auth.currentUser?.let {
            _authState.value = AuthState.Success(it)
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState.Success(it.user!!)
                _toastMessage.value = "Successfully logged in"
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Login failed")
                _toastMessage.value = it.message ?: "Login failed"
            }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState.Success(it.user!!)
                _toastMessage.value = "Successfully registered"
            }
            .addOnFailureListener {
                _authState.value = AuthState.Error(it.message ?: "Registration failed")
                _toastMessage.value = it.message ?: "Registration failed"
            }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _authState.value = AuthState.Initial
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Logout failed")
            }
        }
    }

    fun checkAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Success(currentUser)
        } else {
            _authState.value = AuthState.Initial
        }
    }
} 