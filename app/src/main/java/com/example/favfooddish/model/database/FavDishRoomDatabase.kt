package com.example.favfooddish.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.favfooddish.model.entites.FavDish

@Database(entities = [FavDish::class],version = 1 )
abstract class FavDishRoomDatabase: RoomDatabase() {

    abstract fun favDishDao(): FavDishDao

    companion object{

        @Volatile
        private var INSTANCE: FavDishRoomDatabase? = null

        fun getDataBase(context: Context): FavDishRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishRoomDatabase::class.java,
                    "fav_dish_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}