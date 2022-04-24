package com.compilinghappen.portfolio.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compilinghappen.portfolio.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumCreationViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var status by mutableStateOf(0)

    fun create(name: String, description: String, tags: String) {
        viewModelScope.launch {
            isLoading = true

            val isOk = withContext(Dispatchers.IO) {
                val tagsList = tags.split(",").map { it.trim() }
                Repository.createAlbum(name, description, tagsList)
            }

            isLoading = false

            status = if (isOk) 1 else 2
        }
    }
}