package com.example.favfooddish.ui.fragment

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.favfooddish.R
import com.example.favfooddish.application.FavDishApplication
import com.example.favfooddish.databinding.FragmentRandomDishBinding
import com.example.favfooddish.model.entites.FavDish
import com.example.favfooddish.model.entites.RandomDish
import com.example.favfooddish.utils.Constant
import com.example.favfooddish.viewmodel.FavDishViewModel
import com.example.favfooddish.viewmodel.FavDishViewModelFactory
import com.example.favfooddish.viewmodel.RandomDishViewModel


class RandomDishFragment : Fragment(R.layout.fragment_random_dish) {

    private var _binding: FragmentRandomDishBinding? = null
    private val binding get() = _binding!!

    private lateinit var mRandomDishViewModel: RandomDishViewModel
    private val mFavDishesViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    private var mProgressDialog: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentRandomDishBinding.bind(view)

        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)

        mRandomDishViewModel.getRandomRecipeFromApi()

        binding.swipeToRefresh.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromApi()
        }
        randomDishViewModelObserver()



    }

    private fun showCustomDialog(){
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custome_progess_bar)
            it.show()
        }

    }

    private fun hideCustomeDialog(){
        mProgressDialog?.let {
            it.dismiss()
        }
    }

    private fun randomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner,
        {   randomDishResponse ->

            randomDishResponse?.let{
                setRandomDishDataToUi(randomDishResponse.recipes[0])
                if (binding.swipeToRefresh.isRefreshing){
                    binding.swipeToRefresh.isRefreshing = false
                }
                Log.d("FavFoodDish", "${randomDishResponse.recipes[0]}" )
            }


            }


            )
        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner, { error ->

            error?.let {
                Log.d("FavFoodDish","$error")
                if (binding.swipeToRefresh.isRefreshing){
                    binding.swipeToRefresh.isRefreshing = false
                }
            }
        })

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner, { loadRandomDish ->

            loadRandomDish?.let {
                if (loadRandomDish && !binding.swipeToRefresh.isRefreshing){
                    showCustomDialog()
                }else{
                    hideCustomeDialog()
                }
                Log.d("FavFoodDish","$loadRandomDish")
            }
        })
    }

    private fun setRandomDishDataToUi(recipe: RandomDish.Recipe) {
        binding.apply {
            Glide.with(requireActivity()).load(recipe.image).centerCrop().into(ivDishImage)
            tvTitle.text = recipe.title

            var dishType = "other"
            if (recipe.dishTypes.isNotEmpty()) {
                dishType = recipe.dishTypes[0]
                tvType.text = dishType
            }

            tvCategory.text = "other"
            var ingredient = ""
            for (value in recipe.extendedIngredients) {
                if (ingredient.isEmpty()) {
                    ingredient = value.original
                }else{
                    ingredient = ingredient + ", \n" +value.original
                }
            }
            tvIngredients.text = ingredient

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                tvCookingDirection.text = Html.fromHtml(
                    recipe.instructions, Html.FROM_HTML_MODE_COMPACT
                )
            }else{
                tvCookingDirection.text = Html.fromHtml(recipe.instructions)
            }

            ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireActivity(),
                R.drawable.ic_favorite_unselected))
            var addedToFavorite = false


            tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time,
                recipe.readyInMinutes.toString())

            val randomDish = FavDish(
                recipe.image,
                Constant.DISH_IMAGE_SOURCE_ONLINE,
                recipe.title,
                dishType,
                "other",
                ingredient,
                recipe.readyInMinutes.toString(),
                recipe.instructions,
                true
            )

            ivFavoriteDish.setOnClickListener {

                if (addedToFavorite){
                    Toast.makeText(requireActivity(),resources.getString(R.string.msg_already_added_to_favorites)
                        ,Toast.LENGTH_SHORT).show()
                }else{
                    mFavDishesViewModel.insert(randomDish)
                    addedToFavorite = true
                    ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(requireActivity(),
                        R.drawable.ic_favorite_selected))
                    Toast.makeText(requireActivity(),resources.getString(R.string.msg_added_to_favorites)
                        ,Toast.LENGTH_SHORT).show()
                }
                }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}