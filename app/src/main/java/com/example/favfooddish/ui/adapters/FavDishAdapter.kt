package com.example.favfooddish.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.favfooddish.R
import com.example.favfooddish.databinding.ItemDishLayoutBinding
import com.example.favfooddish.model.entites.FavDish
import com.example.favfooddish.ui.adapters.FavDishAdapter.MyViewHolder
import com.example.favfooddish.ui.addupdatedish.AddUpdateDishActivity
import com.example.favfooddish.ui.fragment.AllDishesFragment
import com.example.favfooddish.ui.fragment.FavoriteDishFragment
import com.example.favfooddish.utils.Constant

class FavDishAdapter(private val fragment: Fragment,
                     private val listener: OnItemClickListener):
    RecyclerView.Adapter<MyViewHolder>() {
    private var dish: List<FavDish> = listOf()

    inner class MyViewHolder(private val binding: ItemDishLayoutBinding):
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {

                    val dish = dish[position]
                    listener.onItemClickDish(dish)

                }
            }
            binding.ibMore.setOnClickListener {
                val popup = PopupMenu(fragment.context, it)
                popup.menuInflater.inflate(R.menu.menu_adepter, popup.menu)

                popup.setOnMenuItemClickListener {
                    if (it.itemId == R.id.action_edit_dish) {
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val dish = dish[position]
                            val intent =
                                Intent(
                                    fragment.requireActivity(),
                                    AddUpdateDishActivity::class.java
                                )
                            intent.putExtra(Constant.EXTRA_DISH_DETAILS, dish)
                            fragment.requireActivity().startActivity(intent)
                        }


                    } else if (it.itemId == R.id.action_delete_dish) {
                        if (fragment is AllDishesFragment){
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val dish = dish[position]
                                fragment.deleteDish(dish)
                            }
                        }
                    }
                    true
                }
                popup.show()
            }

        }


            fun bind(item: FavDish) = with(binding) {

                Glide.with(fragment).load(item.image).into(ivDishImage)
                tvDishTitle.text = item.title
                if (fragment is AllDishesFragment){
                    ibMore.visibility = View.VISIBLE
                }else if (fragment is FavoriteDishFragment){
                    ibMore.visibility = View.GONE
                }

            }


        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val binding = ItemDishLayoutBinding.
       inflate(LayoutInflater.from(fragment.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dish = dish[position]
        holder.bind(dish)
    }

    override fun getItemCount(): Int {
       return dish.size
    }

    fun swapDishList(list: List<FavDish>){
        dish = list
        notifyDataSetChanged()
    }
    interface OnItemClickListener{
        fun onItemClickDish(favDish: FavDish)
    }
}