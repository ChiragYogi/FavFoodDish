package com.example.favfooddish.model.network

import com.example.favfooddish.model.entites.RandomDish
import com.example.favfooddish.utils.Constant
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishApiService {


    private val api = Retrofit.Builder().baseUrl(Constant.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RandomDishApi::class.java)

    // single emit only one value either success or error
    fun getRandomDish(): Single<RandomDish.Recipes>{
        return api.getRandomDishes(
            Constant.API_KEY_VALUE,
            Constant.LIMIT_LICENSE_VALUE,
            Constant.TAGS_VALUE,
            Constant.NUMBER_VALUE)
    }
}