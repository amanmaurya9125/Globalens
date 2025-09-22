package com.example.Globalens.ViewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.Globalens.CheckNetwork
import com.example.Globalens.Repo.NewsRepository
import com.example.Globalens.RoomDatabase.News_Article_Entity
import com.example.Globalens.RoomDatabase.SavedNews.Saved_News_Entity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class NewsViewModel(private val newsRepo: NewsRepository, private val network: CheckNetwork) :
    ViewModel() {
    val isNetworkConnected: StateFlow<Boolean> = network.isConnected.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    var _breakingArticles = MutableStateFlow<List<News_Article_Entity>>(emptyList())
    val breakingArticles: StateFlow<List<News_Article_Entity>> = _breakingArticles


    var _categoryArticles = MutableStateFlow<List<News_Article_Entity>>(emptyList())
    val categoryArticles: StateFlow<List<News_Article_Entity>> = _categoryArticles

    val _searchArticles = mutableStateOf<List<News_Article_Entity>>(emptyList())
    val _discoverArticles = mutableStateOf<List<News_Article_Entity>>(emptyList())

    val _loading_BreakingNews = mutableStateOf(true)
    val _loading_CategoryNews = mutableStateOf(true)
    val _loading_SearchNews = mutableStateOf(true)
    val discover_SearchNews = mutableStateOf(true)

    private val _loading_BreakingNews_Refresh = MutableStateFlow(false)
    val loadingBreakingNews_Refresh: StateFlow<Boolean> = _loading_BreakingNews_Refresh

    private val _loading_CategoryNews_Refresh = MutableStateFlow(false)
    val loadingCategoryNews_Refresh: StateFlow<Boolean> = _loading_CategoryNews_Refresh

    private val _savedArticle = MutableStateFlow<List<News_Article_Entity>>(emptyList())
    val savedArticle : StateFlow<List<News_Article_Entity>> = _savedArticle

    private val _signupState = MutableLiveData<Result<Boolean>?>()
    val signupState : MutableLiveData<Result<Boolean>?> = _signupState

    private var _authState = MutableStateFlow<String?>(null)
    val authState : StateFlow<String?> = _authState

    fun getBreakingNews(category: String = "world") {
        viewModelScope.launch {
            try {
                val result = newsRepo.get_breakingNews(
                    category = category,
                    isNetworkConncted = isNetworkConnected
                )
                val article = result.first()
                _breakingArticles.value = article
                _discoverArticles.value = article
            } catch (e: Exception) {
                Log.e("getBreakingNews", "${e.printStackTrace()}")
            } finally {
                _loading_BreakingNews.value = false
            }
        }
    }

    fun loadCategory(category: String = "All") {
        viewModelScope.launch {
            try {
                val result =
                    newsRepo.get_CategoryNews(category, isNetworkConncted = isNetworkConnected)
                val article = result.first()
                _categoryArticles.value = article
            } catch (e: Exception) {
                Log.e("LoadCategory Error", e.message.toString())
            } finally {
                _loading_CategoryNews.value = false
            }
        }

    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            try {
                val result = newsRepo.get_SearchNews(query = query)
                _searchArticles.value = result

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading_SearchNews.value = false
            }
        }
    }
    fun discoverNews(query: String) {
        viewModelScope.launch {
            try {
                val result = newsRepo.get_SearchNews(query = query)
                if(result.isNotEmpty()) {
                    _discoverArticles.value = result
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    val isRefreshing: StateFlow<Boolean> =
        combine(loadingBreakingNews_Refresh, loadingCategoryNews_Refresh) { breaking, category ->
            breaking || category   // true if either one is loading
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            false
        )

    fun refreshAll() {
        viewModelScope.launch {
            _loading_BreakingNews_Refresh.value = true
            _loading_CategoryNews_Refresh.value = true
            val job1 = launch { getBreakingNews() }
            val job2 = launch { loadCategory() }

            // Wait for both to finish
            joinAll(job1, job2)
            delay(1800)

            _loading_BreakingNews_Refresh.value = false
            _loading_CategoryNews_Refresh.value = false
            Log.d("_loading_BreakingNews_Refresh All", "${_loading_BreakingNews_Refresh.value}")
            Log.d("_loading_CategoryNews_Refresh All", "${_loading_CategoryNews_Refresh.value}")

//             finally {
//                _loading_BreakingNews.value = false
//                _loading_CategoryNews.value = false
//                Log.d("Refresh AllC","${_loading_CategoryNews_Refresh.value}")
//                Log.d("Refresh AllB","${_loading_BreakingNews_Refresh.value}")
//            }
        }
    }


    fun signUp(email : String,password: String){
        viewModelScope.launch{
newsRepo.SignUp(email,password) { success,error ->
    if (success){
        _signupState.value = Result.success(true)
    }
    else{
        _signupState.value = Result.failure(Exception(error))
    }
}
}
    }

    fun login(email: String,password: String){
        viewModelScope.launch{
            newsRepo.Login(email,password){success , error ->
                if (success){
                    _signupState.value = Result.success(true)
                }
                else{
                    _signupState.value = Result.failure(Exception(error))
                }
            }
        }
    }
    fun clearState() {
        _signupState.value = null
    }
fun logout(){
    newsRepo.signOut()
}
    suspend fun sign_With_Google(context: Context) : Result<String>{
           return newsRepo.Sign_With_Google()
    }


    fun toggelFavorite(article : News_Article_Entity,isCurrentlyFavorite : Boolean){
        viewModelScope.launch{
            if (isCurrentlyFavorite){
                newsRepo.removedSaved(article)
            }
            else{
                newsRepo.addSaved(article)
            }
        }
    }

    fun getSavedArticle(){
        viewModelScope.launch{
            try {
                val result = newsRepo.getSaved()
                _savedArticle.value = result
            }catch (e: Exception){
                Log.e("Error",e.message.toString())
            }
        }
    }
    suspend fun isFavorite(url : String): Boolean{
        return newsRepo.isFavorite(url)
    }
}