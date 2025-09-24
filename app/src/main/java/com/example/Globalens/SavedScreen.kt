package com.example.Globalens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.example.Globalens.ViewModel.NewsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun Saved(viewModel: NewsViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    BackHandler {
        scope.launch {
            navController.navigate("Home") {
                launchSingleTop = true
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getSavedArticle()
    }

    val article by viewModel.savedArticle.collectAsState()
    if (!article.isEmpty()) {
        Column(modifier = Modifier.padding(bottom = 85.dp)) {
            SeeMore_Screen(article, viewModel)
        }
    } else {
        result()
    }
}

@Composable
fun result() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.not_foundd),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .size(140.dp)
        )
        Text(
            "No Saved Article",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displaySmall
        )
    }
}