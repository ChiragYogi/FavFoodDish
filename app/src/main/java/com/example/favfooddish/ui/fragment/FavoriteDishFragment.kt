package com.example.favfooddish.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.favfooddish.MainActivity

import com.example.favfooddish.R
import com.example.favfooddish.application.FavDishApplication
import com.example.favfooddish.databinding.FragmentFavoriteDishBinding
import com.example.favfooddish.model.entites.FavDish

import com.example.favfooddish.ui.adapters.FavDishAdapter
import com.example.favfooddish.viewmodel.FavDishViewModel
import com.example.favfooddish.viewmodel.FavDishViewModelFactory

class FavoriteDishFragment : Fragment(R.layout.fragment_favorite_dish),
    FavDishAdapter.OnItemClickListener {


    private var _binding: FragmentFavoriteDishBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mAdepter: FavDishAdapter

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavoriteDishBinding.bind(view)

        mAdepter = FavDishAdapter(this,this)
        binding.rvFavDishList.apply {
            layoutManager = GridLayoutManager(requireActivity(), 2)
            adapter = mAdepter
            setHasFixedSize(true)
        }

        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner) { dishes ->
            dishes.let { dishList ->
                if (dishList.isNotEmpty()) {
                    binding.rvFavDishList.visibility = View.VISIBLE
                    binding.tvNoDishAddedYet.visibility = View.GONE
                    mAdepter.swapDishList(dishList)
                } else {
                    binding.rvFavDishList.visibility = View.GONE
                    binding.tvNoDishAddedYet.visibility = View.VISIBLE
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onItemClickDish(favDish: FavDish) {
        val action = FavoriteDishFragmentDirections.actionNavigationFavoriteToDishDetailsFragment(favDish)
        findNavController().navigate(action)

        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
    }
}