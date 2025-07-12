package fr.inventory.emballmois.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.inventory.emballmois.data.model.LoginRequest
import fr.inventory.emballmois.data.model.LoginResponse
import fr.inventory.emballmois.data.repository.AuthRepository
import fr.inventory.emballmois.ui.utils.MessageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    data class Loading(val message: String) : LoginUiState()
    data class Success(val response: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val messageManager: MessageManager,
) : ViewModel() {

    private val _password = MutableStateFlow("")
    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)

    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()

    fun login(password: String) {
        viewModelScope.launch {
            if(password.isBlank()) {
                messageManager.showMessage("Veuillez entrer un mot de passe")
                return@launch
            }
            _loginUiState.value = LoginUiState.Loading("Connexion en cours...")
            val result = authRepository.login(LoginRequest(password))
            result.fold(
                onSuccess = { response ->
                    messageManager.showMessage("Connexion réussie")
                    _loginUiState.value = LoginUiState.Success(response)
                },
                onFailure = { exception ->
                    _password.value = ""
                    _loginUiState.value = LoginUiState.Error("Impossible de se connecter, \nveuillez recommencer.")
                }
            )
        }
    }

    fun updatePassword(password: String){

        _password.value = password
    }
}