package com.example.favfooddish.ui.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favfooddish.MainActivity
import com.example.favfooddish.R
import com.example.favfooddish.application.FavDishApplication
import com.example.favfooddish.databinding.FragmentDishDetailsBinding
import com.example.favfooddish.model.database.FavDishRoomDatabase
import com.example.favfooddish.model.entites.FavDish
import com.example.favfooddish.utils.Constant
import com.example.favfooddish.viewmodel.FavDishViewModel
import com.example.favfooddish.viewmodel.FavDishViewModelFactory


class DishDetailsFragment : Fragment(R.layout.fragment_dish_details) {

    private var _binding: FragmentDishDetailsBinding? = null
    private val binding get() = _binding!!

    private var favDishDetails: FavDish? = null

    private val args: DishDetailsFragmentArgs by navArgs()
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDishDetailsBinding.bind(view)

        val argsForDish = args.dishDetails

        favDishDetails = argsForDish
        binding.apply {
            Glide.with(this@DishDetailsFragment).load(argsForDish.image).centerCrop()
                .listener(object: RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                       Log.d("FavFoodDish", "Error Loading image",e)
                        return false

                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.toBitmap()?.let {
                            Palette.from(it).generate {
                                val intColor = it?.darkMutedSwatch?.rgb ?: 0
                                binding.rlDishDetailMain.setBackgroundColor(intColor)
                            }
                        }
                        return false
                    }

                }).into(ivDishImage)
            tvTitle.text = argsForDish.title
            tvType.text = argsForDish.type
            tvCategory.text = argsForDish.category
            tvIngredients.text = argsForDish.ingredients
            //tvCookingDirection.text = argsForDish.directionToCook
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                tvCookingDirection.text = Html.fromHtml(
                    argsForDish.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                )
            }else{
                tvCookingDirection.text = Html.fromHtml(argsForDish.directionToCook)
            }
            tvCookingTime.text = resources.getString(R.string.lbl_estimate_cooking_time,argsForDish.cookingTime)

            if (argsForDish.favoriteDish){
                binding.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(),R.drawable.ic_favorite_selected
                ))
            }else {
                binding.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(),R.drawable.ic_favorite_unselected
                ))
             }

        }

        binding.ivFavoriteDish.setOnClickListener {
            argsForDish.favoriteDish = !argsForDish.favoriteDish
            mFavDishViewModel.update(argsForDish)

            if (argsForDish.favoriteDish){
                binding.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(),R.drawable.ic_favorite_selected
                ))
                Toast.makeText(requireActivity(),
                    resources.getString(R.string.msg_added_to_favorites),
                Toast.LENGTH_LONG).show()
            }else {
                binding.ivFavoriteDish.setImageDrawable(ContextCompat.getDrawable(
                    requireActivity(),R.drawable.ic_favorite_unselected
                ))
                Toast.makeText(requireActivity(),
                    resources.getString(R.string.msg_removed_from_favorite),
                    Toast.LENGTH_LONG).show()
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_share_dish ->{

                val type = "text/plain"
                val subject = "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share with"

                favDishDetails?.let {
                    var image = ""
                    if (it.imageSource == Constant.DISH_IMAGE_SOURCE_ONLINE){
                        image = it.image
                    }
                    var cookingInstruction = ""
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        cookingInstruction = Html.fromHtml(
                            it.directionToCook,
                            Html.FROM_HTML_MODE_COMPACT
                        ).toString()
                    }else{
                        cookingInstruction = Html.fromHtml(it.directionToCook).toString()
                    }

                    extraText =
                        "$image \n" +
                            "\n Title: ${it.title} \n\n Type: ${it.type} \n\n" +
                            "Category: ${it.category}" +
                            "\n\n Ingredient:\n ${it.ingredients} \n\n " +
                            "Instructions To Cook: \n $cookingInstruction" +
                            "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }

                val intent = Intent(Intent.ACTION_SEND).let {
                    it.type = type
                    it.putExtra(Intent.EXTRA_SUBJECT,subject)
                    it.putExtra(Intent.EXTRA_TEXT,extraText)
                }
                startActivity(Intent.createChooser(intent,shareWith))
                return true
            }


        }
        return super.onOptionsItemSelected(item)



    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}