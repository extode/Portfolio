package com.compilinghappen.portfolio.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.compilinghappen.portfolio.Album
import com.compilinghappen.portfolio.LoadingOverlay


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(onAlbumClicked: (Album) -> Unit, homeViewModel: HomeViewModel = viewModel()) {
    val posts by homeViewModel.posts.observeAsState(null)

    if (posts == null) {
        LoadingOverlay(text = "Загрузка...")
    } else {
        LazyVerticalGrid(
            cells = GridCells.Adaptive(250.dp)
        ) {
            items(posts!!) {
                PostView(
                    it,
                    onLike = { homeViewModel.likeAlbum(it) },
                    onClick = { onAlbumClicked(it.album) })
            }
        }
    }
}

@Composable
fun PostView(post: Post, onClick: () -> Unit, onLike: () -> Unit) {
    Card(
        Modifier
            .clickable { onClick() }
            .padding(8.dp)) {
        Column(modifier = Modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.End) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = post.user.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Text(
                    post.user.name,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    overflow = TextOverflow.Clip,
                    softWrap = false,
                    style = MaterialTheme.typography.body1
                )
            }

            Spacer(Modifier.height(8.dp))

            AsyncImage(
                post.album.titlePhoto.path,
                contentDescription = null,
                modifier = Modifier
                    //.aspectRatio(1f)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(4.dp))

            Text(
                post.album.name,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(16.dp))

            if (!post.state.liked) {
                OutlinedButton(
                    onClick = onLike, modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    LikeButtonContent(state = post.state)
                }
            } else {
                Button(
                    onClick = onLike, modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    LikeButtonContent(state = post.state)
                }
            }


        }
    }
}

@Composable
fun LikeButtonContent(state: PostUiState) {
    Icon(Icons.Filled.Favorite, contentDescription = null)
    Spacer(Modifier.width(4.dp))
    Text(text = state.likesCount.toString())
}
