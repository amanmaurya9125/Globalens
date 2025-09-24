package com.example.Globalens.RoomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Breaking_Article_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(articles : List<News_Article_Entity>)

    @Query("Select * From News_Article_Entity Order BY publishedAt DESC")
    fun getAll_Articles(): Flow<List<News_Article_Entity>>

    @Query("Delete from News_Article_Entity")
    suspend fun deleteAll()
}

@Dao
interface Category_Article_DAO{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(articles : List<News_Article_Entity>)

    @Query("Select * From News_Article_Entity Order By publishedAt DESC")
    fun getAll_Articles() : Flow<List<News_Article_Entity>>

    @Query("Delete from News_Article_Entity")
    suspend fun deleteAll()

}
