package ipca.example.smartgarden.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow(auth.currentUser)
    val user: StateFlow<com.google.firebase.auth.FirebaseUser?> = _user

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _user.value = auth.currentUser
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Login failed")
            }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _user.value = auth.currentUser
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Sign up failed")
            }
    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        _user.value = null
        onSuccess()
    }
}
