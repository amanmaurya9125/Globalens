package com.example.Globalens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.Globalens.RoomDatabase.News_Article_Entity
import com.example.Globalens.ViewModel.NewsViewModel

@Composable
fun SeeMore_Screen(article: List<News_Article_Entity>, viewModel: NewsViewModel) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 15.dp, top = 30.dp, end = 15.dp, bottom = 15.dp)
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
                        .fillMaxWidth(),
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
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
                    color = MaterialTheme.colorScheme.onSurface,
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
                        .padding(end = 5.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${publishTime}",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(end = 10.dp, bottom = 1.dp)

                    )
                    Icon(
                        painter = painterResource(if (isFavorite) R.drawable.saved else R.drawable.unsaved),
                        contentDescription = "Saved",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
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


@Composable
fun Disposable(viewModel: NewsViewModel) {
    var result = viewModel._searchArticles.value
    var isloading by viewModel._loading_SearchNews

    DisposableEffect(Unit) {
        onDispose {
            viewModel._searchArticles.value = emptyList()
            viewModel._loading_SearchNews.value = true
        }
    }


    if (isloading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else if (!isloading && result.isNullOrEmpty()) {
        loader()
    } else {
        SeeMore_Screen(result, viewModel)
    }
}

@Composable
fun loader() {
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
            "No Result Found",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displaySmall
        )
    }
}