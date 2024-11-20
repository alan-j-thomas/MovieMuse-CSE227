package com.example.letterbox2.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letterbox2.AddMovieActivity
import com.example.letterbox2.Movie
import com.example.letterbox2.MovieAdapter
import com.example.letterbox2.MovieDetailsActivity
import com.example.letterbox2.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class FilmFragment : Fragment() {
    private val movieList = mutableListOf<Movie>()
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_film, container, false)

        // Floating Action Button
        val fab = view.findViewById<FloatingActionButton>(R.id.addBtn)
        fab.setOnClickListener {
            openActivity(AddMovieActivity::class.java)
        }

        // RecyclerView Setup
        recyclerView = view.findViewById(R.id.rvMovies)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        movieAdapter = MovieAdapter(
            movieList = movieList,
            onItemClick = { movieId ->
                openMovieDetails(movieId) // Open movie details
            },
            onItemLongClick = { movie ->
                showDeleteDialog(movie) // Show delete dialog
            }
        )
        recyclerView.adapter = movieAdapter

        // Fetch data from Firestore
        fetchMoviesFromFirestore()

        return view
    }

    private fun showDeleteDialog(movie: Movie) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Movie")
            .setMessage("Are you sure you want to delete this movie?")
            .setPositiveButton("Yes") { _, _ ->
                deleteMovieFromFirestore(movie)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteMovieFromFirestore(movie: Movie) {
        val db = FirebaseFirestore.getInstance()
        db.collection("movies").document(movie.id).delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Movie deleted successfully", Toast.LENGTH_SHORT).show()

                // Remove movie from RecyclerView
                val position = movieList.indexOf(movie)
                if (position != -1) {
                    movieList.removeAt(position)
                    movieAdapter.notifyItemRemoved(position)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error deleting movie: ${exception.message}", Toast.LENGTH_SHORT).show()
            }

        // Also delete from favorites
        db.collection("favorites").document(movie.id)
            .delete()
    }

    private fun fetchMoviesFromFirestore() {
        FirebaseFirestore.getInstance().collection("movies")
            .addSnapshotListener { querySnapshot, exception ->
                if (exception != null) {
                    Toast.makeText(requireContext(), "Error fetching movies: ${exception.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (querySnapshot != null) {
                    movieList.clear()

                    for (document in querySnapshot.documents) {
                        val movie = document.toObject(Movie::class.java)
                        if (movie != null) {
                            movie.id = document.id
                            movieList.add(movie)
                        }
                    }

                    movieAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun openActivity(activityClass: Class<*>) {
        val intent = Intent(activity, activityClass)
        startActivity(intent)
    }

    private fun openMovieDetails(movieId: String) {
        val intent = Intent(requireContext(), MovieDetailsActivity::class.java)
        intent.putExtra("MOVIE_ID", movieId)
        startActivity(intent)
    }
}
