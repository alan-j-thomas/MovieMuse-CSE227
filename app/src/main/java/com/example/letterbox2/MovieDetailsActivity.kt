package com.example.letterbox2

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var imageViewPoster: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvRelease: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        imageViewPoster = findViewById(R.id.imageViewPoster)
        tvTitle = findViewById(R.id.tvTitle)
        tvRating = findViewById(R.id.tvRating)
        tvRelease = findViewById(R.id.tvReleaseYr)
        tvDescription = findViewById(R.id.tvDescription)

        // Get movie ID from intent
        val movieId = intent.getStringExtra("MOVIE_ID") ?: return
        fetchMovieDetails(movieId)
        val btnReview: Button = findViewById(R.id.dropReview)
        btnReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            intent.putExtra("MOVIE_ID", movieId)
            startActivity(intent)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun fetchMovieDetails(movieId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("movies").document(movieId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val movie = document.toObject(Movie::class.java)
                    if (movie != null) {
                        tvTitle.text = movie.title
                        tvRating.text = "Rating: ${movie.rating}"
                        tvRelease.text = "Rating: ${movie.year}"
                        tvDescription.text = movie.description

                        // Decode base64 to bitmap
                        val bitmap = decodeBase64ToBitmap(movie.imageBase64)
                        imageViewPoster.setImageBitmap(bitmap)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching movie details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
