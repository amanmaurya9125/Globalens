package com.example.Globalens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.Globalens.ViewModel.NewsViewModel


@Composable
fun SplashScreen(navController: NavController,viewModel: NewsViewModel) {
    var isLoading by viewModel._loading_CategoryNews
    var isloading2 by viewModel._loading_BreakingNews

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center){
        if (isLoading && isloading2) {
            CircularProgressIndicator()
        }
        else{
            LaunchedEffect(Unit) {
                navController.navigate("Home") {
                    popUpTo("SplashScreen") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}