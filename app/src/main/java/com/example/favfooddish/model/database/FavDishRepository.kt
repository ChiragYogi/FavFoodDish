package com.example.favfooddish.model.database

import androidx.annotation.WorkerThread
import com.example.favfooddish.model.entites.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish){
        favDishDao.updateFavDishDetails(favDish)
    }

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish){
        favDishDao.deleteFaDishDetails(favDish)
    }

    fun filterDish(dishType: String):Flow<List<FavDish>> = favDishDao.getFilterDishList(dishType)

    val favDish: Flow<List<FavDish>> = favDishDao.getFavoriteDishList()

    val allDishList: Flow<List<FavDish>> = favDishDao.getAllDishesList()
}