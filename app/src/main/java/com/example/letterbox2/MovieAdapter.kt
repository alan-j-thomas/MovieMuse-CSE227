package com.example.letterbox2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MovieAdapter(
    private val movieList: MutableList<Movie>,
    private val onItemLongClick: (Movie) -> Unit,
    private val onItemClick: (String) -> Unit // Pass movie ID on click
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = movieList.size

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewMovieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        private val imageViewMoviePoster: ImageView = itemView.findViewById(R.id.imageViewMoviePoster)
        private val favoriteIcon: ImageButton = itemView.findViewById(R.id.ibFavorite)


        init {
            favoriteIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movie = movieList[position]
                    movie.isFavorite = !movie.isFavorite // Toggle favorite state
                    updateFavoriteIcon(movie.isFavorite) // Update the icon

                    // Update Firestore
                    if (movie.isFavorite) {
                        addMovieToFavorites(movie)
                    } else {
                        removeMovieFromFavorites(movie)
                    }
                }
            }




        }


        fun bind(movie: Movie) {
            textViewMovieTitle.text = movie.title
            imageViewMoviePoster.setImageBitmap(decodeBase64ToBitmap(movie.imageBase64))

            // To handle long click
            itemView.setOnLongClickListener {
                onItemLongClick(movie)
                true
            }

            // To handle single click
            itemView.setOnClickListener {
                onItemClick(movie.id)
            }

        }

        private fun decodeBase64ToBitmap(base64String: String): Bitmap {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }

        private fun addMovieToFavorites(movie: Movie) {
            val db = FirebaseFirestore.getInstance()
            db.collection("favorites").document(movie.id)
                .set(movie)
                .addOnSuccessListener {
                Toast.makeText(itemView.context, "${movie.title} Added To Favorites!", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                    Toast.makeText(itemView.context, "Error Occurred. Please try again!", Toast.LENGTH_SHORT).show()
                }
            favoriteIcon.setImageResource(R.drawable.favorite2)
        }

        private fun removeMovieFromFavorites(movie: Movie) {
            val db = FirebaseFirestore.getInstance()
            db.collection("favorites").document(movie.id).delete()
                .addOnSuccessListener {
                    Toast.makeText(
                        itemView.context,
                        "${movie.title} Removed from Favorites!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        itemView.context,
                        "Failed to remove ${movie.title} from favorites.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            if (isFavorite) {
                favoriteIcon.setImageResource(R.drawable.favorite2)
            } else {
                favoriteIcon.setImageResource(R.drawable.favorite1)
            }
        }

        private fun openMovieDetails(movieId: String) {
            val intent = Intent(itemView.context, MovieDetailsActivity::class.java)
            intent.putExtra("MOVIE_ID", movieId)
            itemView.context.startActivity(intent)
        }
    }
}
