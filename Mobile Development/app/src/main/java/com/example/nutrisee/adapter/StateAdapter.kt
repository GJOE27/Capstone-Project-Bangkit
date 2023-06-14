package com.example.nutrisee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrisee.databinding.ActivityLoadingScreenBinding

class StateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<StateAdapter.StateViewHolder>() {
    class StateViewHolder(private val binding: ActivityLoadingScreenBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            with(binding) {
                when (loadState) {
                    is LoadState.Error -> {
                        progressBar.isVisible = false
                    }

                    is LoadState.Loading -> {
                        progressBar.isVisible = true
                    }

                    else -> {
                        progressBar.isVisible = false
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: StateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, loadState: LoadState
    ): StateViewHolder {
        val binding =
            ActivityLoadingScreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StateViewHolder(binding, retry)
    }
}