package com.example.letterbox2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letterbox2.R
import com.example.letterbox2.Review
import com.example.letterbox2.ReviewsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class ThirdFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewsAdapter: ReviewsAdapter
    private lateinit var tvNoReview: TextView
    private val db = FirebaseFirestore.getInstance()
    private val reviewsList = mutableListOf<Review>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)

        recyclerView = view.findViewById(R.id.recycler_reviews)
        tvNoReview = view.findViewById(R.id.tvNoReview)

        reviewsAdapter = ReviewsAdapter(reviewsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = reviewsAdapter

        val movieId = arguments?.getString("MOVIE_ID") ?: ""
        Log.d("ThirdFragment", "Movie ID received: $movieId")

        fetchReviews(movieId)

        return view
    }

    private fun fetchReviews(movieId: String) {
        if (movieId.isEmpty()) {
            Log.w("ThirdFragment", "Movie ID is empty")
            tvNoReview.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            return
        }

        Log.d("ThirdFragment", "Fetching reviews for movieId: $movieId")

        db.collection("user_reviews")
            .whereEqualTo("movieId", movieId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                reviewsList.clear()
                for (document in querySnapshot) {
                    Log.d("ThirdFragment", "Fetched document: ${document.id} -> ${document.data}")
                    val review = document.toObject(Review::class.java)
                    if (review != null) {
                        reviewsList.add(review)
                    } else {
                        Log.w("ThirdFragment", "Document missing required fields: ${document.id}")
                    }
                }

                reviewsAdapter.notifyDataSetChanged()

                if (reviewsList.isEmpty()) {
                    tvNoReview.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    Log.d("ThirdFragment", "No reviews found")
                } else {
                    tvNoReview.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    Log.d("ThirdFragment", "Reviews fetched successfully")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ThirdFragment", "Error fetching reviews", exception)
                Toast.makeText(requireContext(), "Error fetching reviews: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
