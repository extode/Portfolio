package com.compilinghappen.portfolio.albumdetails

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.compilinghappen.portfolio.Album
import com.compilinghappen.portfolio.LoadingOverlay
import com.compilinghappen.portfolio.Tag

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun AlbumDetailsScreen(albumId: Int, editable: Boolean = false, albumDetailsViewModel: AlbumDetailsViewModel = viewModel()) {
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            albumDetailsViewModel.uploadImage(context, it)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Нет доступа!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!editable)
                return@Scaffold

            FloatingActionButton(onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) -> {
                        galleryLauncher.launch("image/*")
                    }
                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    ) {
        albumDetailsViewModel.init(albumId)

        if (albumDetailsViewModel.isLoading) {
            LoadingOverlay(text = "Загрузка")
        } else {
            val images = albumDetailsViewModel.images
            if (images.isNotEmpty()) {
                /*LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    cells = GridCells.Adaptive(200.dp),
                    content = {
                        item(span = { GridItemSpan(2) }) {
                            AlbumDetailsView(albumDetailsViewModel.album)
                        }

                        items(images) { image ->
                            AsyncImage(
                                image.path,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(8.dp)
                                    .fillMaxSize()
                            )
                        }
                    })*/
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        AlbumDetailsView(
                            albumDetailsViewModel.album,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    items(images) { image ->
                        AsyncImage(
                            image.path,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 140.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        "Как так, здесь нет ещё ни одной картинки. Добавьте её :3",
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumDetailsView(album: Album, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(album.name, style = MaterialTheme.typography.h5)
            Spacer(Modifier.height(8.dp))

            Text(album.description, style = MaterialTheme.typography.body1)
            Spacer(Modifier.height(8.dp))

            //Text("Теги", style = MaterialTheme.typography.body1)
            //TagsView(album.tags)
        }
    }
}

@Composable
fun TagsView(
    tags: List<Tag>,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colors.primary,
) {
    val text = tags.joinToString { "#${it.name} * " }

    Text(
        text = text,
        modifier = modifier,
        color = textColor
    )
}
