package br.ufpr.musicbookmark.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME    = "musicbookmark.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME       = "musics"
        const val COL_ID           = "id"
        const val COL_TITLE        = "title"
        const val COL_ARTIST       = "artist"
        const val COL_YEAR         = "year"
        const val COL_GENRE        = "genre"
        const val COL_IS_FAVORITE  = "is_favorite"
        const val COL_STATUS       = "status"
        const val COL_RATING       = "rating"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_NAME (
                $COL_ID          INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE       TEXT    NOT NULL,
                $COL_ARTIST      TEXT    NOT NULL,
                $COL_YEAR        INTEGER NOT NULL,
                $COL_GENRE       TEXT    NOT NULL,
                $COL_IS_FAVORITE INTEGER NOT NULL DEFAULT 0,
                $COL_STATUS      TEXT    NOT NULL,
                $COL_RATING      INTEGER NOT NULL
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}
