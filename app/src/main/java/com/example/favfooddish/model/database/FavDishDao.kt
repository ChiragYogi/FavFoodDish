package com.example.favfooddish.model.database

import androidx.room.*
import com.example.favfooddish.model.entites.FavDish
import kotlinx.coroutines.flow.Flow


@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)

    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)

    @Delete
    suspend fun deleteFaDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FAV_DISH_TABLE WHERE type = :filterType")
    fun getFilterDishList(filterType: String): Flow<List<FavDish>>

    @Query("SELECT * FROM FAV_DISH_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>

    @Query("SELECT * FROM FAV_DISH_TABLE WHERE favorite_dish = 1")
    fun getFavoriteDishList(): Flow<List<FavDish>>

}