package com.compilinghappen.portfolio.home

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

class PostUiState(liked: Boolean = false, likesCount: Int = 0) {
    var liked by mutableStateOf(liked)
    var likesCount by mutableStateOf(likesCount)
}

data class Post(val user: User, val album: Album, val state: PostUiState = PostUiState())

class HomeViewModel : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>>
        get() = _posts

    init {
        viewModelScope.launch {
            _posts.value = withContext(Dispatchers.IO) {
                Repository.getTrending().map { album ->
                    Post(
                        user = loadUserForAlbum(album),
                        album = album,
                        state = PostUiState(likesCount = album.likes)
                    )
                }
            }
        }
    }

    fun likeAlbum(post: Post) {
        if (!post.state.liked) {
            post.state.liked = true
            viewModelScope.launch {
                Repository.likeAlbum(post.album.id)
                post.state.likesCount++
            }
        }
    }

    private suspend fun loadUserForAlbum(album: Album): User {
        return Repository.getUserById(album.userId)!!
    }
}