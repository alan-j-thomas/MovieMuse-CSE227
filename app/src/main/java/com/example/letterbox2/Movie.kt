package com.example.letterbox2

data class Movie(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val rating: Float = 0.0f,
    val year: String = "",
    val genre: String = "",
    val imageBase64: String = "",
    var isFavorite: Boolean = false
)
