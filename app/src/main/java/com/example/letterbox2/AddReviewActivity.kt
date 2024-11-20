package com.example.letterbox2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class AddReviewActivity : AppCompatActivity() {

    private lateinit var etReviewTitle: EditText
    private lateinit var etReview: EditText
    private lateinit var btnSubmitReview: Button
    private lateinit var db: FirebaseFirestore
    private var movieId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        etReview = findViewById(R.id.etReview)
        etReviewTitle = findViewById(R.id.etReviewTitle)
        btnSubmitReview = findViewById(R.id.btnSubmitReview)
        db = FirebaseFirestore.getInstance()

        // Get the movieId passed via intent
        movieId = intent.getStringExtra("MOVIE_ID")

        btnSubmitReview.setOnClickListener {
            val reviewTitle = etReviewTitle.text.toString().trim()
            val reviewContent = etReview.text.toString().trim()

            if (reviewTitle.isBlank() || reviewContent.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                saveReview(reviewTitle, reviewContent)
            }
        }
    }

    private fun saveReview(reviewTitle: String, reviewContent: String) {
        if (movieId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show()
            return
        }

        val reviewData = hashMapOf(
            "movieId" to movieId,
            "timestamp" to System.currentTimeMillis(),
            "reviewTitle" to reviewTitle,
            "reviewContent" to reviewContent
        )

        db.collection("user_reviews")
            .add(reviewData)
            .addOnSuccessListener {
                Toast.makeText(this, "Review added successfully!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after saving
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to add review: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
