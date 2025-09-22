package com.example.Globalens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.Globalens.ViewModel.NewsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun Saved(viewModel : NewsViewModel,navController: NavController){
    val scope = rememberCoroutineScope()
    BackHandler {
            scope.launch {
                navController.navigate("Home"){
                    launchSingleTop = true
                }
            }
    }
    LaunchedEffect(Unit) {
        viewModel.getSavedArticle()
    }

    val article by viewModel.savedArticle.collectAsState()
    Column(modifier = Modifier.padding(bottom = 85.dp)) {
        SeeMore_Screen(article, viewModel)
    }
}