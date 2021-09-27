package com.example.favfooddish.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.favfooddish.databinding.ItemCustomListBinding
import com.example.favfooddish.ui.addupdatedish.AddUpdateDishActivity
import com.example.favfooddish.ui.fragment.AllDishesFragment

class CustomListItemAdepter(
    private val activity: Activity,
    private val fragment: Fragment?,
    private val list: List<String>,
    private val selection: String
) : RecyclerView.Adapter<CustomListItemAdepter.ViewHolder>() {

    class ViewHolder(binding: ItemCustomListBinding) : RecyclerView.ViewHolder(binding.root) {


        val tvText = binding.tvText


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ItemCustomListBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvText.text = item

        holder.itemView.setOnClickListener {
            if (activity is AddUpdateDishActivity){
                activity.selectedListItem(item,selection)
            }
            if (fragment is AllDishesFragment){
                fragment.filterItemSelection(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}