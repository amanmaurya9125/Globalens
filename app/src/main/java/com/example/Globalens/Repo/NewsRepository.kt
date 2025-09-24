package com.example.Globalens.Repo

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.example.Globalens.API_Services.NewsAPIService
import com.example.Globalens.RoomDatabase.Breaking_Article_DAO
import com.example.Globalens.RoomDatabase.News_Article_Entity
import com.example.Globalens.RoomDatabase.Category_Article_DAO
import com.example.Globalens.RoomDatabase.toEntity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import com.example.Globalens.RoomDatabase.SavedNews.Saved_Article_dao
import com.example.Globalens.SharedPreferences
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class NewsRepository(
    private val context: Context,
    private val api: NewsAPIService,
    private val breaking_Dao: Breaking_Article_DAO,
    private val category_Dao: Category_Article_DAO,
    private val saved_Dao: Saved_Article_dao,
) {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var GoogleIdOption: GetGoogleIdOption
    lateinit var credentialManager: CredentialManager
    private val cache = mutableMapOf<String, List<News_Article_Entity>>()
    lateinit var sharedPreferences: SharedPreferences

    val API_KEY = api().api1()
    val API_KEY2 = api().api2()
    suspend fun get_breakingNews(
        category: String = "",
        query: String = "",
        isNetworkConncted: StateFlow<Boolean>,
    ): Flow<List<News_Article_Entity>> {
        return if (isNetworkConncted.value) {
            val response = api.getNews(
                apikey = API_KEY,
                category = if (category == "All") "" else category.lowercase(),
                query = query
            )

            if (response.isSuccessful) {
                val article = response.body()?.articles ?: emptyList()
                val entities = article.map { it.toEntity() }
                breaking_Dao.deleteAll()
                breaking_Dao.insertArticle(entities)

            } else {
                breaking_Dao.getAll_Articles()
            }
            breaking_Dao.getAll_Articles()

        } else {
            breaking_Dao.getAll_Articles()
        }
    }

    suspend fun get_CategoryNews(
        category: String = "",
        query: String = "",
        isNetworkConncted: StateFlow<Boolean>,
    ): Flow<List<News_Article_Entity>> {
        if (category.isNotEmpty() && cache.containsKey(category)) {
            return flowOf(cache[category]!!)
        }

        return if (isNetworkConncted.value) {
            val response = api.getNews(
                apikey = API_KEY,
                category = if (category == "All") "" else category.lowercase(),
                query = query
            )

            if (response.isSuccessful) {
                val article = response.body()?.articles ?: emptyList()
                val entities = article.map { it.toEntity() }
                category_Dao.deleteAll()
                category_Dao.insertArticle(entities)
                cache[category] = entities
            } else {
                category_Dao.getAll_Articles()
            }
            category_Dao.getAll_Articles()
        } else {
            category_Dao.getAll_Articles()
        }
    }

    suspend fun get_SearchNews(
        query: String,
    ): List<News_Article_Entity> {

        val response = api.getNews(query = query, apikey = API_KEY)
        if (response.isSuccessful) {
            val article = response.body()?.articles ?: emptyList()
            val entities = article.map { it.toEntity() }
            return entities
        } else {
            throw Exception("API Error: ${response.code()}")
        }
    }

    fun SignUp(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { task ->
                task.user?.let { sharedPreferences.saveUserAuth(context, it) }
                onResult(true, null)
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }

    fun Login(email: String, password: String, result: (Boolean, String?) -> Unit) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { task ->
                task.user?.let { sharedPreferences.saveUserAuth(context, it) }
                result(true, null)
            }
            .addOnFailureListener { exception ->
                result(false, exception.message)
            }
    }

    suspend fun Sign_With_Google(): Result<String> {
        firebaseAuth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(context)
        GoogleIdOption = GetGoogleIdOption
            .Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("70967568561-ug021vgd29eoevtbigjl9rfhp2mab5kf.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(GoogleIdOption)
            .build()
        return try {
            val result = credentialManager.getCredential(
                context, request
            )
            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            val googleIdToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user
            if (user != null) SharedPreferences().saveUserAuth(context, user)
            Result.success("SignIn Successful")
        } catch (e: NoCredentialException) {
            Result.failure(Exception("No Google account found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
    }
//    SAVED ARTICLE DATABASE CODE

    suspend fun addSaved(article: News_Article_Entity) = saved_Dao.insertArticle(article)
    suspend fun removedSaved(article: News_Article_Entity) = saved_Dao.deleteArticle(article)

    suspend fun getSaved() = saved_Dao.getArticle()
    suspend fun isFavorite(url: String) = saved_Dao.isFavorite(url)
}
