package br.ufpr.musicbookmark.controller

import android.content.Context
import br.ufpr.musicbookmark.database.MusicDAO
import br.ufpr.musicbookmark.model.Music

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: Map<String, String>) : ValidationResult()
}

class MusicController(context: Context) {

    private val dao = MusicDAO(context)

    companion object {
        const val STATUS_LISTENED  = "Ouvida"
        const val STATUS_TO_LISTEN = "Para Ouvir"
        val VALID_STATUSES = setOf(STATUS_LISTENED, STATUS_TO_LISTEN)
    }

    fun validate(music: Music): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (music.title.isBlank())  errors["title"]  = "Título é obrigatório"
        if (music.artist.isBlank()) errors["artist"] = "Artista é obrigatório"
        if (music.year !in 1500..2026) errors["year"] = "Ano deve estar entre 1500 e 2026"
        if (music.genre.isBlank())  errors["genre"]  = "Gênero é obrigatório"
        if (music.rating !in 1..5)  errors["rating"] = "Avaliação deve ser entre 1 e 5"
        if (music.status !in VALID_STATUSES) errors["status"] = "Status inválido"

        return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(errors)
    }

    fun insert(music: Music): Long              = dao.insert(music)
    fun update(music: Music): Int               = dao.update(music)
    fun delete(id: Long): Int                   = dao.delete(id)
    fun getById(id: Long): Music?               = dao.getById(id)
    fun getAll(sortColumn: String): List<Music> = dao.getAll(sortColumn)
    fun search(field: String, query: String): List<Music> = dao.search(field, query)
    fun getDistinctArtists(): List<String>      = dao.getDistinctArtists()
}
