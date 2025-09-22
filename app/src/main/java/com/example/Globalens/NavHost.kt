package com.example.Globalens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.Globalens.API_Services.RetrofitInstance
import com.example.Globalens.Repo.NewsRepository
import com.example.Globalens.Repo.ViewModelFactory
import com.example.Globalens.RoomDatabase.Breaking_Article_DAO
import com.example.Globalens.RoomDatabase.Breaking_Database_Creation
import com.example.Globalens.RoomDatabase.Category_Article_DAO
import com.example.Globalens.RoomDatabase.Category_Databse_Creation
import com.example.Globalens.RoomDatabase.SavedNews.Saved_Article_dao
import com.example.Globalens.RoomDatabase.SavedNews.Saved_Database_Creation
import com.example.Globalens.ViewModel.NewsViewModel
import com.example.Globalens.SignUp_Login.LoginScreen
import com.example.Globalens.SignUp_Login.SignUpScreen

@Composable
fun NavigationGraph() {
    val context = LocalContext.current
    val networkStatus = remember { CheckNetwork(context)}
    val viewModel: NewsViewModel
    val Breaking_DAO : Breaking_Article_DAO
    val Category_DAO : Category_Article_DAO
val SavedDao : Saved_Article_dao

    Breaking_DAO = Breaking_Database_Creation.getDatabase_BreakingArticle(context).breaking_ArticleDao()
    Category_DAO = Category_Databse_Creation.getCategory_DatabaseArticle(context).category_Database_dao()
SavedDao = Saved_Database_Creation.getDatabase_SavedArticle(context).savedArticle_Dao()
        viewModel = viewModel(factory = ViewModelFactory(NewsRepository(context,RetrofitInstance.api, breaking_Dao = Breaking_DAO, category_Dao = Category_DAO, saved_Dao = SavedDao), network = networkStatus))
        val navController = rememberNavController()
        LaunchedEffect(key1 = Unit) {
            viewModel.getBreakingNews("world")
            viewModel.loadCategory("All")
        }

        NavHost(navController = navController, startDestination = "SplashScreen") {
            composable("SplashScreen") {
                SplashScreen(navController, viewModel)
            }
            composable("Home") {
                MainScreen(viewModel, navController)
            }
            composable("webView/{url}") { NavBackStackEntry ->
                val url = NavBackStackEntry.arguments?.getString("url") ?: ""
                FullArticle_WebView_Screen(url)
            }

            composable("SeeMore_Screen/{type}") {
                val type = it.arguments?.getString("type") ?: ""
                if (type.equals("breakingNews")) {
                    val article = viewModel.breakingArticles.collectAsState()
                    SeeMore_Screen(article.value,viewModel)
                } else {
                    val article by viewModel.categoryArticles.collectAsState()
                    SeeMore_Screen(article,viewModel)
                }
            }

            composable("searchResult") {
                Disposable(viewModel)
            }

            composable("signup"){
                SignUpScreen(navController,viewModel)
            }
            composable("login"){
                LoginScreen(navController,viewModel)
            }
            composable("savedArticle"){
                viewModel.getSavedArticle()
                val article = viewModel.savedArticle.collectAsState()
                SeeMore_Screen(article.value,viewModel)
            }
        }
    }
