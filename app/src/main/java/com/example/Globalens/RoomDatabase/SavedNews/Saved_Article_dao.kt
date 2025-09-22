package com.example.Globalens.RoomDatabase.SavedNews

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.Globalens.RoomDatabase.News_Article_Entity

@Dao
interface Saved_Article_dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article : News_Article_Entity)

    @Delete
    suspend fun deleteArticle(article: News_Article_Entity)

    @Query("Select * from News_Article_Entity Order by rowid DESC")
    suspend fun getArticle() : List<News_Article_Entity>

    @Query("Select Exists(Select 1 from News_Article_Entity where url = :url)")
    suspend fun isFavorite(url : String) : Boolean
}