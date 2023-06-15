package com.example.nutrisee.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nutrisee.data.remote.response.ListImageResult
import com.example.nutrisee.databinding.ListArchivesBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ArchiveAdapter(private val context: Context, private val clickListener: OnItemClickAdapter) :
    RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>(){

    private val listImage = ArrayList<ListImageResult>()
    private val formatDate = SimpleDateFormat("EEEE, d MMMM | HH:mm", Locale("in"))

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ListImageResult>) {
        listImage.clear()
        listImage.addAll(list.sortedByDescending { it.createdAt })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
        val binding = ListArchivesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArchiveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {
        val item = listImage[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = listImage.size

    inner class ArchiveViewHolder(private val binding: ListArchivesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listImage: ListImageResult) {
            binding.apply {
                val split = listImage.name.split("_")
                val formatted = split.take(2).joinToString(" ")
                val format = formatted.replace("_", " ").replaceFirstChar { it.uppercase() }

                val dateString = listImage.createdAt
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(dateString)
                val formattedDate = formatDate.format(date)

                tvArchiveName.text = format
                tvArchiveDate.text = formattedDate
                Glide.with(context)
                    .load(listImage.url)
                    .into(ivArchives)
                root.setOnClickListener {
                    val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as Activity,
                        Pair(binding.ivArchives, "image"),
                        Pair(binding.tvArchiveName, "name"),
                        Pair(binding.tvArchiveDate, "date")
                    )
                    clickListener.onItemClicked(listImage, optionsCompat)
                }
            }
        }
    }

    interface OnItemClickAdapter {
        fun onItemClicked(listImage: ListImageResult, optionsCompat: ActivityOptionsCompat)
    }
}