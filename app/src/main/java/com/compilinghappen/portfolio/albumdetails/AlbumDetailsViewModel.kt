package com.compilinghappen.portfolio.albumdetails

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compilinghappen.portfolio.Album
import com.compilinghappen.portfolio.Photo
import com.compilinghappen.portfolio.Repository
import com.compilinghappen.portfolio.User
import com.ipaulpro.afilechooser.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AlbumDetailsViewModel : ViewModel() {
    var images = mutableStateListOf<Photo>()
    var isLoading by mutableStateOf(false)

    lateinit var album: Album

    //private lateinit var user: User

    private var _albumId = -1

    fun init(albumId: Int) {
        if (_albumId != albumId) {
            _albumId = albumId

            viewModelScope.launch {
                isLoading = true
                //loadUser()
                loadImages()
                isLoading = false
            }
        }
    }

    private suspend fun loadUser() {
        //user = Repository.getUserInfo()
    }

    private suspend fun loadImages() {
        images = withContext(Dispatchers.IO) {
            album = Repository.getAlbumById(_albumId)!!
            album.photos.toMutableStateList()
        }
    }

    fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            isLoading = true
            withContext(Dispatchers.IO) {
                val instr = context.contentResolver.openInputStream(uri)
                Repository.uploadImageToAlbum(instr, _albumId)
            }
            loadImages()
            isLoading = false
        }
    }
}