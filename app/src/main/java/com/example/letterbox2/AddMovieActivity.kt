package com.example.letterbox2
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddMovieActivity : AppCompatActivity() {

    private lateinit var movieThumbnail: ImageView
    private lateinit var movieID: EditText
    private lateinit var uploadThumbnailButton: Button
    private lateinit var movieTitle: EditText
    private lateinit var movieDescription: EditText
    private lateinit var movieRating: RatingBar
    private lateinit var releaseYear: EditText
    private lateinit var movieGenre: EditText
    private lateinit var submitButton: Button

    private var selectedImageUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)

        // Initialize UI components
        movieThumbnail = findViewById(R.id.iv_movie_thumbnail)
        uploadThumbnailButton = findViewById(R.id.btn_upload_thumbnail)
        movieID = findViewById(R.id.et_ID)
        movieTitle = findViewById(R.id.et_movie_title)
        movieDescription = findViewById(R.id.et_movie_description)
        movieRating = findViewById(R.id.rb_movie_rating)
        releaseYear = findViewById(R.id.et_release_year)
        movieGenre = findViewById(R.id.et_movie_genre)
        submitButton = findViewById(R.id.btn_submit_movie)

        // Set up button listeners
        uploadThumbnailButton.setOnClickListener {
            pickImageFromGallery()
        }

        submitButton.setOnClickListener {
            val id = movieID.text.toString()
            val title = movieTitle.text.toString()
            val description = movieDescription.text.toString()
            val rating = movieRating.rating
            val year = releaseYear.text.toString()
            val genre = movieGenre.text.toString()

            if (validateInputs(id, title, description, year, genre)) {
                encodeImageAndSaveDetails(id, title, description, rating, year, genre)
            } else {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                movieThumbnail.setImageURI(selectedImageUri) // Display selected image
            } else {
                Toast.makeText(this, "Failed to select image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encodeImageAndSaveDetails(
        id: String,
        title: String,
        description: String,
        rating: Float,
        year: String,
        genre: String
    ) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Convert the image to a Base64 string
            val inputStream: InputStream? = contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val base64Image = encodeImageToBase64(bitmap)

            // Save movie details to Firestore
            saveMovieToFirestore(id, title, description, rating, year, genre, base64Image)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to process the image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Compress and encode as JPEG
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    @SuppressLint("DefaultLocale")
    private fun saveMovieToFirestore(
        id: String,
        title: String,
        description: String,
        rating: Float,  // Keep rating as a Float
        year: String,
        genre: String,
        base64Image: String
    ) {
        val movieRef = firestore.collection("movies").document()


        val movieData = hashMapOf(
            "Id" to id,
            "title" to title,
            "description" to description,
            "rating" to rating,
            "year" to year,
            "genre" to genre,
            "imageBase64" to base64Image
        )

        movieRef.set(movieData)
            .addOnSuccessListener {
                Toast.makeText(this, "Movie added successfully!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after saving
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save movie details.", Toast.LENGTH_SHORT).show()
            }
    }


    private fun validateInputs(
        id: String,
        title: String,
        description: String,
        year: String,
        genre: String
    ): Boolean {
        return id.isNotEmpty() &&title.isNotEmpty() && description.isNotEmpty() && year.isNotEmpty() && genre.isNotEmpty()
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 101
    }

//    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
//        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
//        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
//    }
//
//    // Example usage
//    val bitmap = decodeBase64ToBitmap(imageBase64)
//    imageView.setImageBitmap(bitmap)
}