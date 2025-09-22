package com.example.Globalens.RoomDatabase.SavedNews

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.Globalens.RoomDatabase.News_Article_Entity

@Database(entities = [News_Article_Entity::class], version = 2)
abstract class Saved_Database_Creation : RoomDatabase() {
    abstract fun savedArticle_Dao(): Saved_Article_dao

    companion object {
        @Volatile
        private var INSTANCE: Saved_Database_Creation? = null
        fun getDatabase_SavedArticle(context: Context): Saved_Database_Creation {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Saved_Database_Creation::class.java,
                    "Saved_Article"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}