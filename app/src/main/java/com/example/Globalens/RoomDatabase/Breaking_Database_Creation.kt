package com.example.Globalens.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.Globalens.API_Services.Article

@Database(entities = [News_Article_Entity::class], version = 8)
abstract class Breaking_Database_Creation : RoomDatabase() {
    abstract fun breaking_ArticleDao(): Breaking_Article_DAO

    companion object {
        @Volatile
        private var INSTANCE: Breaking_Database_Creation? = null
        fun getDatabase_BreakingArticle(context: Context): Breaking_Database_Creation {
          return INSTANCE ?: synchronized(this){
              val instance =  Room.databaseBuilder(
                    context.applicationContext,
                    Breaking_Database_Creation::class.java,
                    "Breaking_News_DB"
                ).fallbackToDestructiveMigration()
                  .build()
              INSTANCE = instance
              instance
          }
          }
        }
    }

fun Article.toEntity() : News_Article_Entity{
    return News_Article_Entity(
        url = this.url.toString(),
        title = this.title,
        content = this.content,
        image = this.image,
        publishedAt = this.publishedAt,
        source_Name = this.source?.name,
        source_Icon_Url = this.source?.url
    )
}