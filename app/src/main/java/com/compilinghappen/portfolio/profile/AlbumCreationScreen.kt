package com.compilinghappen.portfolio.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compilinghappen.portfolio.Album
import com.compilinghappen.portfolio.LoadingOverlay
import com.compilinghappen.portfolio.R

@Composable
fun AlbumCreationScreen(onCreated: () -> Unit, acViewModel: AlbumCreationViewModel = viewModel()) {
    if (acViewModel.status == 1) {
        onCreated()
        return
    }

    Column {
        Card(Modifier.height(100.dp).fillMaxWidth()) {
            Box(contentAlignment = Alignment.Center) {
                Text("Создание альбома", style = MaterialTheme.typography.h5)
            }
        }

        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            var name by rememberSaveable { mutableStateOf("") }
            var description by rememberSaveable { mutableStateOf("") }
            var tags by rememberSaveable { mutableStateOf("") }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            /*OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Теги через запятую") },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )*/

            Button(onClick = { acViewModel.create(name, description, tags) }) {
                Text("Создать")
            }

            if (acViewModel.status == 2) {
                Text("Ошибка при создании альбома")
            }
        }

        if (acViewModel.isLoading) {
            LoadingOverlay("Создание...")
        }
    }

}
