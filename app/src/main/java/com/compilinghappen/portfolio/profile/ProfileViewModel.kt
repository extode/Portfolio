package com.compilinghappen.portfolio.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compilinghappen.portfolio.Album
import com.compilinghappen.portfolio.Repository
import com.compilinghappen.portfolio.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>>
        get() = _albums


    init {
        viewModelScope.launch {
            val userResponse = withContext(Dispatchers.IO) {
                Repository.getUserInfo()
            }

            val albumsResponse = withContext(Dispatchers.IO) {
                Repository.getPersonalAlbums()
            }

            _user.value = userResponse
            _albums.value = albumsResponse
        }
    }
}
