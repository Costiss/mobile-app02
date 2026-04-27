package br.ufpr.musicbookmark.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import br.ufpr.musicbookmark.database.DBHelper.Companion.COL_ARTIST
import br.ufpr.musicbookmark.database.DBHelper.Companion.COL_GENRE
import br.ufpr.musicbookmark.database.DBHelper.Companion.COL_ID
import br.ufpr.musicbookmark.database.DBHelper.Companion.COL_IS_FAVORITE
import br.ufpr.musicbookmark.database.DBHelper.Companion.COL_RATING
import br.ufpr.musicbookmark.database.DBHelper.Companion.COL_STATUS
import br.ufpr.musicbookmark.database.DBHelper.Companion.COL_TITLE
import br.ufpr.musicbookmark.database.DBHelper.Companion.COL_YEAR
import br.ufpr.musicbookmark.database.DBHelper.Companion.TABLE_NAME
import br.ufpr.musicbookmark.model.Music

class MusicDAO(context: Context) {

    private val dbHelper = DBHelper(context)

    private val allowedColumns = setOf(COL_TITLE, COL_ARTIST, COL_YEAR, COL_GENRE, COL_RATING)

    fun insert(music: Music): Long {
        val db = dbHelper.writableDatabase
        val values = toContentValues(music)
        return db.insert(TABLE_NAME, null, values)
    }

    fun update(music: Music): Int {
        val db = dbHelper.writableDatabase
        val values = toContentValues(music)
        return db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(music.id.toString()))
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun getById(id: Long): Music? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, null, "$COL_ID = ?", arrayOf(id.toString()), null, null, null)
        return cursor.use {
            if (it.moveToFirst()) cursorToMusic(it) else null
        }
    }

    fun getAll(sortColumn: String = COL_TITLE): List<Music> {
        require(sortColumn in allowedColumns) { "Invalid sort column: $sortColumn" }
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, sortColumn)
        return cursor.use { buildList { while (it.moveToNext()) add(cursorToMusic(it)) } }
    }

    fun search(field: String, query: String): List<Music> {
        require(field in allowedColumns) { "Invalid search field: $field" }
        val db = dbHelper.readableDatabase
        val cursor = if (field == COL_YEAR || field == COL_RATING) {
            db.query(TABLE_NAME, null, "$field = ?", arrayOf(query), null, null, COL_TITLE)
        } else {
            db.query(TABLE_NAME, null, "$field LIKE ?", arrayOf("%$query%"), null, null, COL_TITLE)
        }
        return cursor.use { buildList { while (it.moveToNext()) add(cursorToMusic(it)) } }
    }

    fun getDistinctArtists(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(true, TABLE_NAME, arrayOf(COL_ARTIST), null, null, null, null, COL_ARTIST, null)
        return cursor.use { buildList { while (it.moveToNext()) add(it.getString(0)) } }
    }

    private fun toContentValues(music: Music) = ContentValues().apply {
        put(COL_TITLE, music.title)
        put(COL_ARTIST, music.artist)
        put(COL_YEAR, music.year)
        put(COL_GENRE, music.genre)
        put(COL_IS_FAVORITE, if (music.isFavorite) 1 else 0)
        put(COL_STATUS, music.status)
        put(COL_RATING, music.rating)
    }

    private fun cursorToMusic(cursor: Cursor) = Music(
        id         = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
        title      = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
        artist     = cursor.getString(cursor.getColumnIndexOrThrow(COL_ARTIST)),
        year       = cursor.getInt(cursor.getColumnIndexOrThrow(COL_YEAR)),
        genre      = cursor.getString(cursor.getColumnIndexOrThrow(COL_GENRE)),
        isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_FAVORITE)) == 1,
        status     = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
        rating     = cursor.getInt(cursor.getColumnIndexOrThrow(COL_RATING))
    )
}
