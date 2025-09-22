package com.example.Globalens.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [News_Article_Entity::class], version = 4)
abstract class Category_Databse_Creation : RoomDatabase() {
    abstract fun category_Database_dao(): Category_Article_DAO

    companion object {
        @Volatile
        private var INSTANCE: Category_Databse_Creation? = null
        fun getCategory_DatabaseArticle(context: Context): Category_Databse_Creation {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Category_Databse_Creation::class.java,
                    "Category_News_DB"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance

            }
        }
    }
}
