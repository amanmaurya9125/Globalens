package com.example.Globalens.RoomDatabase

import android.content.Context
import kotlinx.coroutines.flow.Flow

object DataBase_Access {
    private lateinit var dao: Breaking_Database_Creation

    fun init(context: Context) {
        dao = Breaking_Database_Creation.getDatabase_BreakingArticle(context)
    }

    suspend fun insertArticle(article: List<News_Article_Entity>) {
        dao.breaking_ArticleDao().insertArticle(article)
    }

    fun getArticle(): Flow<List<News_Article_Entity>> {
        return dao.breaking_ArticleDao().getAll_Articles()
    }

    suspend fun deleteAll() {
        dao.breaking_ArticleDao().deleteAll()
    }
}