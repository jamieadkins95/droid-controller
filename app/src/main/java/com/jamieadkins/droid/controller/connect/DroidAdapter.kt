package com.jamieadkins.droid.controller.connect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jamieadkins.droid.controller.databinding.RowDroidBinding
import com.jamieadkins.droid.controller.droid.Droid

class DroidAdapter(
    private val onDroidClicked: (Droid) -> Unit
) : ListAdapter<Droid, DroidAdapter.DroidViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DroidViewHolder {
        val itemBinding = RowDroidBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DroidViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: DroidViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Droid> = object : DiffUtil.ItemCallback<Droid>() {
            override fun areItemsTheSame(old: Droid, new: Droid): Boolean {
                return old.address === new.address
            }

            override fun areContentsTheSame(old: Droid, new: Droid): Boolean = old == new
        }
    }

    inner class DroidViewHolder(private val binding: RowDroidBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(droid: Droid) {
            binding.name.text = droid.name
            binding.address.text = droid.address
            binding.root.setOnClickListener { onDroidClicked.invoke(droid) }
        }
    }
}