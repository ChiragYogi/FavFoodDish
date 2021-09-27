package com.example.favfooddish.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.favfooddish.MainActivity
import com.example.favfooddish.R
import com.example.favfooddish.application.FavDishApplication
import com.example.favfooddish.databinding.DialogCustomListBinding
import com.example.favfooddish.databinding.FragmentAllDishesBinding
import com.example.favfooddish.model.entites.FavDish
import com.example.favfooddish.ui.adapters.CustomListItemAdepter
import com.example.favfooddish.viewmodel.FavDishViewModel
import com.example.favfooddish.viewmodel.FavDishViewModelFactory
import com.example.favfooddish.ui.adapters.FavDishAdapter
import com.example.favfooddish.ui.addupdatedish.AddUpdateDishActivity
import com.example.favfooddish.utils.Constant


class AllDishesFragment : Fragment(R.layout.fragment_all_dishes),
    FavDishAdapter.OnItemClickListener {


    private var _binding: FragmentAllDishesBinding? = null

    private val binding get() = _binding!!
    private lateinit var mCustomDialog: Dialog

    private val mFavDishesViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    private lateinit var mAdepter: FavDishAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAllDishesBinding.bind(view)
        mAdepter = FavDishAdapter(this, this)

        binding.rvDishList.apply {
            layoutManager = GridLayoutManager(requireActivity(), 2)
            adapter = mAdepter
            setHasFixedSize(true)
        }

        observeAllDish()

        setHasOptionsMenu(true)
    }

    private fun observeAllDish(){
        mFavDishesViewModel.allDishList.observe(viewLifecycleOwner) { dishes ->
            dishes.let { dishList ->
                if (dishList.isNotEmpty()) {
                    binding.rvDishList.visibility = View.VISIBLE
                    binding.tvNoDishAddedYet.visibility = View.GONE
                    mAdepter.swapDishList(dishList)
                } else {
                    binding.rvDishList.visibility = View.GONE
                    binding.tvNoDishAddedYet.visibility = View.VISIBLE
                }
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_dish__action_bar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.add_new_dish -> {

                startActivity(
                    Intent(
                        requireActivity(), AddUpdateDishActivity::class.java
                    )
                )
                return true
            }
            R.id.action_filter_dish -> {
                filterListDialog()
                return true
            }
        }

        return super.onOptionsItemSelected(item)

    }

    fun filterItemSelection(selection: String){
        mCustomDialog.dismiss()

        if (selection == Constant.ALL_ITEMS){
            observeAllDish()
        } else{
            mFavDishesViewModel.filterDishList(selection).observe(viewLifecycleOwner){ dishList ->
                dishList.let {
                    if (dishList.isNotEmpty()) {
                        binding.rvDishList.visibility = View.VISIBLE
                        binding.tvNoDishAddedYet.visibility = View.GONE
                        mAdepter.swapDishList(dishList)
                    } else {
                        binding.rvDishList.visibility = View.GONE
                        binding.tvNoDishAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.showBottomNavigationView()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }

    private fun filterListDialog(){
        mCustomDialog = Dialog(requireActivity())
        val mBinding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        mCustomDialog.setContentView(mBinding.root)
        mBinding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishType = Constant.dishTypes()
        dishType.add(0,Constant.ALL_ITEMS)
        mBinding.rvList.layoutManager = LinearLayoutManager(requireActivity())

        val adepter = CustomListItemAdepter(requireActivity(),this,dishType,Constant.FILTER_SELECTION)
        mBinding.rvList.adapter = adepter
        mCustomDialog.show()
    }

    fun deleteDish(dish: FavDish){

        val builder = AlertDialog.Builder(requireActivity()).apply {
            setTitle(resources.getString(R.string.title_delete_dish))
            setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
            setIcon(android.R.drawable.ic_dialog_alert)
            setPositiveButton(resources.getString(R.string.lbl_yes)){ dialogInterface,_ ->
                mFavDishesViewModel.delete(favDish = dish)
                dialogInterface.dismiss()
            }
            setNegativeButton(resources.getString(R.string.lbl_no)){ dialogInterface,_ ->
                dialogInterface.dismiss()
            }
        }

        val alertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()


    }

    override fun onItemClickDish(favDish: FavDish) {
        val action = AllDishesFragmentDirections.actionNavigationAllDishesToDishDetailsFragment(favDish)
        findNavController().navigate(action)

        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigationView()
        }

    }
}
