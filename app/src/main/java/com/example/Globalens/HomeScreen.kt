package com.example.Globalens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.Globalens.RoomDatabase.News_Article_Entity
import com.example.Globalens.ViewModel.NewsViewModel
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.zIndex
import com.example.Globalens.Drawer.Drawer
import com.example.Globalens.Theme.ThemeMode
import com.example.Globalens.Theme.Theme_Preferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: NewsViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val networkStatus = remember { CheckNetwork(context) }
    val isNetworkConnected by networkStatus.isConnected.collectAsState()

    var user = remember { mutableStateOf(false) }
    val userPrefs = SharedPreferences().getUserAuth(context)
    var selectedIndex by remember { mutableStateOf(0) }
    val themeFlow = Theme_Preferences.getTheme(context.applicationContext)
        .collectAsState(initial = ThemeMode.Default)
    if (userPrefs["uid"] != null) {
        user.value = true
    }

    // 🔴 Show "No Internet" banner
    if (!isNetworkConnected) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
                .background(Color.Red)
                .padding(8.dp)
        ) {
            Text(
                "No Internet Connection",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Scaffold(
        bottomBar = {
            Bottom_Navigation(
                selectedIndex = selectedIndex,
                onItemSelected = {
                    selectedIndex = it
                }
            )
        }
    ) { innerpadding ->
        when (selectedIndex) {
            0 -> HomeScreen(viewModel, navController)
            1 -> Discover(viewModel, navController)
            2 -> Saved(viewModel, navController)
            3 -> ProfileScreen(navController, viewModel, themeFlow = themeFlow.value)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NewsViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val categoryArticles by viewModel.categoryArticles.collectAsState()
    val breakingNewsArticle by viewModel.breakingArticles.collectAsState()
    val state by viewModel.isRefreshing.collectAsState()
    val scope = rememberCoroutineScope()
    var user = remember { mutableStateOf(false) }
    val userPrefs = SharedPreferences().getUserAuth(context)
    if (userPrefs["uid"] != null) {
        user.value = true
    }
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Drawer(navController, drawerState, viewModel)
            }
        }
    ) {
        Scaffold(
            topBar = { HomeScreen_TopBar(viewModel, navController, drawerState) },
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = state,
                onRefresh = { viewModel.refreshAll() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // 🔹 Breaking News Section
                    HomeScreen_Heading(
                        heading = stringResource(R.string.breaking_news),
                        show_more = stringResource(R.string.show_more),
                        type = "breakingNews",
                        navController = navController
                    )
                    Card_LazyColumn(breakingNewsArticle)

                    // 🔹 Category Section
                    HomeScreen_Category(viewModel)

                    // 🔹 Recommended Section
                    HomeScreen_Heading(
                        heading = stringResource(R.string.recommended),
                        show_more = stringResource(R.string.show_more),
                        navController = navController
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                    Recommended_News_LazyColumn(categoryArticles, navController, viewModel)
                }
            }
        }
    }
}

@Composable
fun HomeScreen_TopBar(
    viewModel: NewsViewModel,
    navController: NavController,
    drawerState: DrawerState,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 30.dp, end = 8.dp, bottom = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hamburger menu
        Row(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(30.dp)
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        if (drawerState.isClosed) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }

                },
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    Icons.Default.Menu, contentDescription = "Menu",
                    tint = Color.Black
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    viewModel.searchNews(query)
                    navController.navigate("searchResult")
                })
        }

        // Right-side icons
        Row(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(30.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Search Icon
            IconButton(
                onClick = { Toast.makeText(context, "Not In Function", Toast.LENGTH_SHORT).show() },
                modifier = Modifier
                    .background(Color.White, shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    Icons.Default.Notifications, contentDescription = "Notifications",
                    tint = Color.Black
                )

            }
        }
    }
}

@Composable
fun Card_LazyColumn(article: List<News_Article_Entity>) {

    LazyRow {
        items(article.size) {
            val articlee = article[it]
            HomeScreen_BreakingNews(
                source_name = articlee.source_Name.toString(),
                title = articlee.title.toString(),
                image = articlee.image.toString(),
                source_Icon = articlee.url.toString(),
                url = articlee.url.toString()
            )
        }
    }
}

@Composable
fun HomeScreen_Heading(
    heading: String,
    show_more: String,
    type: String = "",
    navController: NavController,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(vertical = 3.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            heading,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp
        )
        Text(
            show_more,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable {
                navController.navigate("SeeMore_Screen/${type}")
            }
        )
    }
}

@Composable
fun HomeScreen_BreakingNews(
    image: String = "",
    source_Icon: String = "",
    source_name: String = "",
    title: String = "",
    url: String = "",
) {
    val context = LocalContext.current
    Card(
        onClick = {}, modifier = Modifier
            .padding(start = 10.dp, top = 8.dp, end = 1.dp, bottom = 8.dp)
            .width(320.dp)
            .height(210.dp)
            .clip(RoundedCornerShape(25.dp))

    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }

        ) {
            AsyncImage(
                model = image,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()

            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xCC000000)
                            )
                        )
                    )

            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 13.dp, horizontal = 15.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = "https://www.google.com/s2/favicons?domain=${source_Icon}&sz=64",
                        contentDescription = "Company Logo",
                        modifier = Modifier
                            .size(25.dp)
                            .clip(RoundedCornerShape(15.dp))
                    )
                    Text(
                        source_name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    maxLines = 3,
                    textAlign = TextAlign.Left,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 6.dp)

                )
            }

        }
    }
}

@Composable
fun HomeScreen_Category(viewModel: NewsViewModel) {
    var category = listOf(
        R.drawable.world to "All",
        R.drawable.balance to "Politics",
        R.drawable.books to "Education",
        R.drawable.sports to "Sports",
        R.drawable.entertainment to "Entertainment",
        R.drawable.health to "Health",
        R.drawable.bussiness to "Business",
        R.drawable.tecnology to "Technology"
    )

    var selected_Category by remember { mutableStateOf("All") }

    LazyRow {
        items(category) { (icon, name) ->
            var changeName = when {
                name.equals("Politics") -> "nation"
                name.equals("Education") -> "science"
                else -> name
            }
            val isselected = changeName == selected_Category
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 12.dp, end = 10.dp, bottom = 7.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isselected) Color(0xFF3055FA) else Color(0xF3EDEBEB))
                    .clickable {
                        selected_Category = changeName
                        viewModel.loadCategory(selected_Category)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(icon), contentDescription = "Politics",
                        tint = Color.Unspecified
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isselected) Color.White else Color.DarkGray,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Recommended_News_LazyColumn(
    article: List<News_Article_Entity>,
    navController: NavController,
    viewModel: NewsViewModel,
) {
    LazyColumn(modifier = Modifier.padding(bottom = 80.dp)) {
        items(article.size) {
            val data = article[it]
            HomeScreen_RecommendedNews(
                source_Icon = data.source_Icon_Url.toString(),
                source_name = data.source_Name.toString(),
                image = data.image.toString(),
                title = data.title.toString(),
                publishDate = data.publishedAt.toString(),
                url = data.url.toString(),
                content = data.content.toString(),
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun HomeScreen_RecommendedNews(
    source_Icon: String = "",
    source_name: String = "",
    image: String = "",
    title: String = "",
    publishDate: String = "",
    url: String = "",
    content: String = "",
    navController: NavController,
    viewModel: NewsViewModel,
) {
    var isFavorite by remember { mutableStateOf(false) }
    val publishTime = convertTimeZone(publishDate)

    LaunchedEffect(Unit) { isFavorite = viewModel.isFavorite(url) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(top = 1.dp, start = 10.dp, end = 11.dp)
            .clickable {
                navController.navigate("webView/${Uri.encode(url)}")
            },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(width = 118.dp, height = 130.dp),
            shape = RoundedCornerShape(12)
        ) {
            AsyncImage(
                model = image,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = "https://www.google.com/s2/favicons?domain=${source_Icon}&sz=64",
                        contentDescription = "Source Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(25.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                    Text(
                        source_name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Row(
                    modifier = Modifier.padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(if (isFavorite) R.drawable.saved else R.drawable.unsaved),
                        contentDescription = "Saved",
                        modifier = Modifier
                            .size(22.dp)
                            .clickable {
                                val article = News_Article_Entity(
                                    url = url,
                                    title = title,
                                    image = image,
                                    publishedAt = publishDate,
                                    source_Name = source_name,
                                    source_Icon_Url = source_Icon,
                                    content = content
                                )
                                viewModel.toggelFavorite(article, isFavorite)
                                isFavorite = !isFavorite
                            }
                    )
                }

            }
            Text(
                title,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Start,
                maxLines = 3,
                modifier = Modifier
                    .padding(start = 2.dp, top = 5.dp, end = 2.dp, bottom = 2.dp)
            )
            Text(
                publishTime,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,

                )
        }
    }
}

@Composable
fun Bottom_Navigation(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf("Home", "Discover", "Saved", "Profile")
    val icons = listOf(
        R.drawable.home,
        R.drawable.discover,
        R.drawable.save,
        R.drawable.profile
    )

    NavigationBar(contentColor = Color.White) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(id = icons[index]),
                        contentDescription = item
                    )
                },
                label = {
                    Text(text = item, fontSize = 15.sp)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1A73E8),
                    selectedTextColor = Color(0xFF1A73E8),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

fun convertTimeZone(convertTime: String): String {
    val time = convertTime
    if (time.isNotEmpty()) {
        val utc = ZonedDateTime.parse(time)
        val ist = utc.withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
        val formatted = ist.format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))
        return formatted
    } else {
        return ""
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = query,
        onValueChange = {
            onQueryChange(it)
        },
        placeholder = {
            Text("Search News...")
        },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                keyboardController?.hide()
            }
        ),
        modifier = Modifier
            .width(230.dp)
            .height(55.dp)
            .padding(horizontal = 3.dp)
            .border(width = 2.dp, color = Color.DarkGray, shape = RoundedCornerShape(25.dp)),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}
