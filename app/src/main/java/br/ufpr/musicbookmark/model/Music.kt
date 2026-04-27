package br.ufpr.musicbookmark.model

data class Music(
    val id: Long = 0,
    val title: String,
    val artist: String,
    val year: Int,
    val genre: String,
    val isFavorite: Boolean = false,
    val status: String,
    val rating: Int
)
