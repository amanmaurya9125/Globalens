package com.example.Globalens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.Globalens.RoomDatabase.News_Article_Entity
import com.example.Globalens.ViewModel.NewsViewModel
import kotlinx.coroutines.launch

@Composable
fun Discover(viewModel: NewsViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    BackHandler {
        scope.launch {
            navController.navigate("Home") {
                launchSingleTop = true
            }
        }
    }
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var isSearchVisible by remember { mutableStateOf(true) }
    val article by viewModel._discoverArticles
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(listState) {
        var lastindex = 0
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { it ->
                isSearchVisible = it <= lastindex
                lastindex = it
            }
    }

    Column(modifier = Modifier.padding(top = 30.dp, start = 15.dp, end = 15.dp, bottom = 15.dp)) {
        AnimatedVisibility(visible = isSearchVisible) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search News...", fontSize = 21.sp) },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { viewModel.discoverNews(query) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 5.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.discoverNews(query)
                        keyboardController?.hide()
                    }
                ),
                textStyle = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .border(
                        width = 2.dp,
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(25.dp)
                    ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 85.dp)
        ) {
            items(article.size) {
                var data = article[it]
                var isFavorite by remember { mutableStateOf(false) }
                val publishTime = convertTimeZone(data.publishedAt.toString())
                LaunchedEffect(Unit) { isFavorite = viewModel.isFavorite(data.url) }
                Column(
                    modifier = Modifier
                        .padding(top = 15.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .height(240.dp)
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = data.image,
                            contentDescription = "Source Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                                    context.startActivity(intent)
                                }
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(11.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        AsyncImage(
                            model = "https://www.google.com/s2/favicons?domain=${data.source_Icon_Url}&sz=64",
                            contentDescription = "Source Icon",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(35.dp)
                                .clip(RoundedCornerShape(25.dp))
                        )
                        Text(
                            "${data.source_Name}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                    Text(
                        "${data.title}",
                        fontSize = 21.sp,
                        maxLines = 4,
                        fontWeight = FontWeight.W400,
                        lineHeight = 28.sp,
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                            context.startActivity(intent)
                        }
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 21.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${publishTime}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier
                                .padding(end = 12.dp, bottom = 1.dp)

                        )
                        Icon(
                            painter = painterResource(if (isFavorite) R.drawable.saved else R.drawable.unsaved),
                            contentDescription = "Saved",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    val article = News_Article_Entity(
                                        url = data.url,
                                        title = data.title,
                                        image = data.image,
                                        publishedAt = data.publishedAt,
                                        source_Name = data.source_Name,
                                        source_Icon_Url = data.source_Icon_Url,
                                        content = data.content
                                    )
                                    viewModel.toggelFavorite(article, isFavorite)
                                    isFavorite = !isFavorite
                                }
                        )

                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

            }
        }
    }
}

//LazyColumn(state = listState) {
//    items(50) { index ->
//        Text(
//            text = "Item $index",
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )
//    }