package com.example.favfooddish.application

import android.app.Application
import com.example.favfooddish.model.database.FavDishRepository
import com.example.favfooddish.model.database.FavDishRoomDatabase

class FavDishApplication: Application() {

    private val database by lazy { FavDishRoomDatabase.getDataBase(this) }

    val repository by lazy { FavDishRepository(database.favDishDao()) }

}