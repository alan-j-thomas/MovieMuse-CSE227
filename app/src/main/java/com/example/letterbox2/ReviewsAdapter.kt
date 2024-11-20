package com.example.letterbox2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewsAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewTitle: TextView = itemView.findViewById(R.id.tv_review_title)
        val reviewContent: TextView = itemView.findViewById(R.id.tv_review_content)
        val timestamp: TextView = itemView.findViewById(R.id.tv_review_timestamp)
        //val img: ImageView = itemView.findViewById(R.id.iv_reviewer_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewTitle.text = review.reviewTitle
        holder.reviewContent.text = review.reviewContent
        holder.timestamp.text = review.timestamp
    }

    override fun getItemCount(): Int = reviews.size
}
