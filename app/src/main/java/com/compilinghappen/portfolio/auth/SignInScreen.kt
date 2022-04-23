package com.compilinghappen.portfolio.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compilinghappen.portfolio.LoadingOverlay
import com.compilinghappen.portfolio.R
import com.compilinghappen.portfolio.ui.theme.PortfolioTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(signUpSucceeded: () -> Unit, switchedToRegister: () -> Unit, signInViewModel: SignInViewModel = viewModel()) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val navigateToApp by signInViewModel.navigateToAppEvent.observeAsState(false)

    val isLoading by signInViewModel.loadingEvent.observeAsState(false)

    if (navigateToApp) {
        signUpSucceeded()
        signInViewModel.navigateToAppEventHandled()
        return
    }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(
                Modifier
                    .width(96.dp)
                    .height(96.dp)
                    .background(MaterialTheme.colors.primary, CircleShape)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (signInViewModel.authStatus == AuthStatus.INVALID_CREDENTIALS)
                    Text("Неправильное имя пользователя или пароль")
                else if (signInViewModel.authStatus == AuthStatus.UNKNOWN_ERROR)
                    Text("Неизвесная ошибка")

                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text(stringResource(R.string.login)) },
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    label = { Text(stringResource(R.string.password)) },
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = { signInViewModel.signIn(login, password) },
                        modifier = Modifier.width(128.dp)
                    ) {
                        Text(stringResource(R.string.sign_in))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedButton(onClick = switchedToRegister) {
                        Text(stringResource(R.string.register))
                    }
                }
            }
        }

        if (isLoading) {
            LoadingOverlay(stringResource(R.string.logging_in))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    PortfolioTheme {
        SignInScreen({}, {})
    }
}
