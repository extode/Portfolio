package com.compilinghappen.portfolio.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var currentAlbum by mutableStateOf<Album?>(null)

    private var isDataInvalidated = false

    init {
        loadData()
    }

    fun init() {
        loadData()
        isDataInvalidated = false
    }

    private fun loadData() {
        viewModelScope.launch {
            val userResponse = withContext(Dispatchers.IO) {
                Repository.getUserInfo()
            }

            val albumsResponse = withContext(Dispatchers.IO) {
                Repository.getAlbums(userResponse.id!!)
            }

            _user.value = userResponse
            _albums.value = albumsResponse
        }
    }

    fun selectAlbum(album: Album) {
        currentAlbum = album
    }

    fun invalidate() {
        isDataInvalidated = true
        _user.value = null
        _albums.value = null
    }
}
