package com.compilinghappen.portfolio.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.compilinghappen.portfolio.Album
import com.compilinghappen.portfolio.LoadingOverlay
import com.compilinghappen.portfolio.R
import com.compilinghappen.portfolio.User
import com.compilinghappen.portfolio.ui.theme.PortfolioTheme

@Composable
fun ProfileScreen(
    onAlbumClicked: (Album) -> Unit,
    onCreateNewAlbum: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    profileViewModel.init()

    val user by profileViewModel.user.observeAsState()
    val albums by profileViewModel.albums.observeAsState()

    if (user == null || albums == null) {
        LoadingOverlay(text = stringResource(R.string.loading))
    }
    else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    onCreateNewAlbum()
                    profileViewModel.invalidate()
                }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
        ) {
            ProfileList(albums!!, user!!, onAlbumClicked = {
                profileViewModel.selectAlbum(it)
                onAlbumClicked(it)
            })
        }


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileList(albums: List<Album>, user: User, onAlbumClicked: (Album) -> Unit) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Adaptive(180.dp),
        content = {
            item(span = { GridItemSpan(2) }) {
                Profile(user)
            }

            items(albums) { album ->
                AlbumItem(album, onClick = { onAlbumClicked(album) })
            }
        })
}

@Composable
fun AlbumItem(album: Album, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ImageCard(
        label = album.name,
        likes = album.likes,
        modifier = modifier.padding(8.dp),
        onClick = onClick
    ) {
        AsyncImage(
            model = album.titlePhoto.path,
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Profile(user: User, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colors.surface) {
        Column(
            modifier = modifier.padding(vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = user.avatar,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(128.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(user.name)
        }
    }
}


@Composable
fun ImageCard(
    label: String,
    likes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    image: @Composable () -> Unit
) {
    Card(modifier.clickable { onClick() }) {
        Column(horizontalAlignment = Alignment.End) {
            image()

            Column(
                Modifier.padding(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(label, textAlign = TextAlign.Right)
                Spacer(Modifier.height(8.dp))
                LikeIndicator(likes)
            }
        }
    }
}

@Composable
fun LikeIndicator(likes: Int, modifier: Modifier = Modifier) {
    Row(modifier) {
        Icon(Icons.Filled.Favorite, contentDescription = null)
        Spacer(Modifier.width(4.dp))
        Text(likes.toString())
    }
}

@Preview
@Composable
fun ImageCardPreview() {
    PortfolioTheme {
        ImageCard(label = "Картинка", likes = 123, onClick = {}) {
            Box(
                Modifier
                    .width(150.dp)
                    .height(150.dp)
                    .background(Color.Green)
            )
        }
    }
}
