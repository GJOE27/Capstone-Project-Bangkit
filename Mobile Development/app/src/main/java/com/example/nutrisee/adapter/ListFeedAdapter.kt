package com.example.nutrisee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutrisee.R
import com.example.nutrisee.data.local.Feed

class ListFeedAdapter(private val listFeed: ArrayList<Feed>) : RecyclerView.Adapter<ListFeedAdapter.FeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_feeds, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val (imageProfile, name, caption, imageFeeds, date) = listFeed[position]
        holder.imgProfile.setImageResource(imageProfile)
        holder.tvName.text = name
        holder.tvCaption.text = caption
        holder.imgFeed.setImageResource(imageFeeds)
        holder.tvDate.text = date
    }

    override fun getItemCount(): Int = listFeed.size

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.iv_feed_profile)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvCaption: TextView = itemView.findViewById(R.id.tv_description)
        val imgFeed: ImageView = itemView.findViewById(R.id.iv_feeds)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
    }
}