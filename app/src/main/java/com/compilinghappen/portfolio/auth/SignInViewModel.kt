package com.compilinghappen.portfolio.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compilinghappen.portfolio.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel : ViewModel() {
    var authStatus by mutableStateOf(AuthStatus.NO_STATUS)
        private set

    private val _loadingEvent = MutableLiveData(false)
    val loadingEvent: LiveData<Boolean>
        get() = _loadingEvent

    private val _navigateToAppEvent = MutableLiveData(false)
    val navigateToAppEvent: LiveData<Boolean>
        get() = _navigateToAppEvent



    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            _loadingEvent.value = true
            val status = withContext(Dispatchers.IO) {
                Repository.signIn(login, password)
            }
            _loadingEvent.value = false

            authStatus = status

            if (status == AuthStatus.OK) {
                _navigateToAppEvent.value = true
            }
        }
    }

    fun navigateToAppEventHandled() {
        _navigateToAppEvent.value = false
    }

    fun loadingEventHandled() {
        _loadingEvent.value = false
    }
}