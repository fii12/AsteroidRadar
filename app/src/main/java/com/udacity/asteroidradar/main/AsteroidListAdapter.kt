package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ListItemBinding

class AsteroidListAdapter(val clickListener: AsteroidClickListener) : ListAdapter<Asteroid, AsteroidListAdapter.AsteroidItemView>(AsteroidListDiffCallback()){

    class AsteroidItemView(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clickListener: AsteroidClickListener, item: Asteroid){
            item.let {
                binding.asteroid = item
                binding.executePendingBindings()
                binding.clickListener = clickListener
            }
        }
        companion object {
            fun from(parent: ViewGroup): AsteroidItemView {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)

                return AsteroidItemView(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidItemView {
        return AsteroidItemView.from(parent)
    }

    override fun onBindViewHolder(holder: AsteroidItemView, position: Int) {
        holder.bind(clickListener,getItem(position))
    }


}
class AsteroidListDiffCallback: DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }


}
class AsteroidClickListener(val clickListener : (asteroid:Asteroid) -> Unit){
    fun onClick(asteroid:Asteroid){
        clickListener(asteroid)
    }
}