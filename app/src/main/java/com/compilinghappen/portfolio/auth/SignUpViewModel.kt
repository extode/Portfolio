package com.compilinghappen.portfolio.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compilinghappen.portfolio.Repository
import com.compilinghappen.portfolio.User
import kotlinx.coroutines.launch
import java.util.*

class SignUpViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var navigateToApp by mutableStateOf(false)

    fun register(login: String, password: String, name: String) {
        viewModelScope.launch {
            isLoading = true

            Repository.signUp(login, password)
            Repository.updateUserData(User(id = null, name = name, about = "", birthDate = "24.12.2000", avatar = null))
            /*Repository.getUserInfo().also {
                Log.d("USER_INFO", it.name)
            }*/

            isLoading = false
            navigateToApp = true
        }
    }
}