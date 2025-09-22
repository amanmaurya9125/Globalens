package com.example.Globalens.RoomDatabase.SavedNews

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import retrofit2.http.Url

@Entity(tableName = "Saved_News_Table",
    indices = [Index(value = ["title"], unique = true)])
data class Saved_News_Entity(
    @PrimaryKey
    val url: String = "Url Missing",
    val title : String?,
    val image: String?,
    val publishedAt: String?,
    val source_Name: String?,
    val source_Icon_Url : String?
)