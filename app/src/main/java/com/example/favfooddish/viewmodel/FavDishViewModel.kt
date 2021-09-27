package com.example.favfooddish.viewmodel

import androidx.lifecycle.*
import com.example.favfooddish.model.database.FavDishRepository
import com.example.favfooddish.model.entites.FavDish
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: FavDishRepository): ViewModel() {


    fun insert(favDish: FavDish) = viewModelScope.launch {
        repository.insertFavDishData(favDish)
    }

    fun update(favDish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(favDish)
    }

    fun delete(favDish: FavDish) = viewModelScope.launch {
        repository.deleteFavDishData(favDish)
    }

    fun filterDishList(dishType: String):LiveData<List<FavDish>> =
        repository.filterDish(dishType).asLiveData()

    val allDishList: LiveData<List<FavDish>> = repository.allDishList.asLiveData()

    val favoriteDishes: LiveData<List<FavDish>> = repository.favDish.asLiveData()
}

class FavDishViewModelFactory(private val repository: FavDishRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

