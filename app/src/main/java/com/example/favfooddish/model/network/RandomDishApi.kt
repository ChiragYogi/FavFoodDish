package com.example.favfooddish.model.network

import com.example.favfooddish.model.entites.RandomDish
import com.example.favfooddish.utils.Constant
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RandomDishApi {

    @GET(Constant.API_ENDPOINT)
    fun getRandomDishes(
        @Query(Constant.API_KEY) apiKey: String,
        @Query(Constant.LIMIT_LICENSE) limitLicence: Boolean,
        @Query(Constant.TAGS) tags: String,
        @Query(Constant.NUMBER) number: Int
    ): Single<RandomDish.Recipes>
}