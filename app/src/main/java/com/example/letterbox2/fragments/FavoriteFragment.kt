package com.example.letterbox2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letterbox2.Movie
import com.example.letterbox2.MovieAdapter
import com.example.letterbox2.MovieDetailsActivity
import com.example.letterbox2.R
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noFavoritesTextView: TextView
    private lateinit var adapter: MovieAdapter
    private val favoriteMovies = mutableListOf<Movie>()
    lateinit var ivNoFav: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.rvFavorites)
        noFavoritesTextView = view.findViewById(R.id.tvNoFavorites)
        ivNoFav = view.findViewById(R.id.ivNoFav)

        adapter = MovieAdapter(favoriteMovies, onItemLongClick = { movie ->
            // Handle long-click for favorite movies, e.g., show a delete dialog
            removeMovieFromFavorites(movie)
        },
            onItemClick = { movieId ->
                // Handle click to open movie details
                openMovieDetails(movieId)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadFavorites()

        return view
    }

    private fun openMovieDetails(movieId: String) {
        val intent = Intent(requireContext(), MovieDetailsActivity::class.java)
        intent.putExtra("MOVIE_ID", movieId)
        startActivity(intent)
    }

    private fun removeMovieFromFavorites(movie: Movie) {
        val db = FirebaseFirestore.getInstance()
        db.collection("favorites").document(movie.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "${movie.title} removed from favorites", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to remove favorite: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadFavorites() {
        val db = FirebaseFirestore.getInstance()
        db.collection("favorites")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Toast.makeText(requireContext(), "Error fetching favorites: ${exception.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    favoriteMovies.clear()

                    for (document in querySnapshot.documents) {
                        val movie = document.toObject(Movie::class.java)
                        if (movie != null) {
                            movie.id = document.id
                            favoriteMovies.add(movie)
                        }
                    }

                    adapter.notifyDataSetChanged()

                    if (favoriteMovies.isEmpty()) {
                        noFavoritesTextView.visibility = View.VISIBLE
                        ivNoFav.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        noFavoritesTextView.visibility = View.GONE
                        ivNoFav.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                }
            }
    }

}
