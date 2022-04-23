package com.compilinghappen.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.compilinghappen.portfolio.ui.theme.PortfolioTheme

@Composable
fun LoadingOverlay(text: String, alpha: Float = 0.6f) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface.copy(alpha = alpha))
    ) {
        CircularProgressIndicator()
        Text(text)
    }
}


@Preview(showBackground = true)
@Composable
fun LoadingOverlayPreview() {
    PortfolioTheme {
        LoadingOverlay("Loading...")
    }
}