package ipca.example.smartgarden.presentation.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                val currentUser = auth.currentUser
                if (currentUser != null && !currentUser.isEmailVerified) {
                    onError("Please verify your email address. A verification email has been sent.")
                    currentUser.sendEmailVerification()
                    auth.signOut()
                } else {
                    _user.value = auth.currentUser
                    onSuccess()
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Login failed")
            }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                auth.currentUser?.sendEmailVerification()
                onError("Account created! Please check your email for verification.")
                auth.signOut()
            }
            .addOnFailureListener {
                onError(it.message ?: "Sign up failed")
            }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank()) {
            onError("Please enter your email")
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to send reset email") }
    }

    private fun reauthenticate(password: String, onComplete: (Result<Unit>) -> Unit) {
        val user = auth.currentUser
        val email = user?.email
        if (user != null && email != null) {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential)
                .addOnSuccessListener { onComplete(Result.success(Unit)) }
                .addOnFailureListener { onComplete(Result.failure(it)) }
        } else {
            onComplete(Result.failure(Exception("User not found")))
        }
    }

    fun updatePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        reauthenticate(currentPassword) { result ->
            if (result.isSuccess) {
                auth.currentUser?.updatePassword(newPassword)
                    ?.addOnSuccessListener { onSuccess() }
                    ?.addOnFailureListener { onError(it.message ?: "Failed to update password") }
            } else {
                onError(result.exceptionOrNull()?.message ?: "Re-authentication failed")
            }
        }
    }

    fun deleteAccount(password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        reauthenticate(password) { result ->
            if (result.isSuccess) {
                auth.currentUser?.delete()
                    ?.addOnSuccessListener { 
                        _user.value = null
                        onSuccess() 
                    }
                    ?.addOnFailureListener { onError(it.message ?: "Failed to delete account") }
            } else {
                onError(result.exceptionOrNull()?.message ?: "Re-authentication failed")
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        _user.value = null
        onSuccess()
    }
}
