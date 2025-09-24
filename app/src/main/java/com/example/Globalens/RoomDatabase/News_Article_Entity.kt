package com.example.Globalens.RoomDatabase

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "News_Article_Entity",
    indices = [Index(value = ["title"], unique = true)]
    )
data class News_Article_Entity(
    @PrimaryKey
    val url : String = "Url Missing",
    val title : String?,
    val content : String?,
    val image: String?,
    val publishedAt: String?,
    val source_Name: String?,
    val source_Icon_Url : String?,
)

