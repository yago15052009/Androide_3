package com.example.atvsqlitenoandroid.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MovieDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_MOVIES (
                $COL_ID      INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE   TEXT    NOT NULL,
                $COL_DIR     TEXT    NOT NULL,
                $COL_YEAR    INTEGER NOT NULL,
                $COL_RATING  REAL    NOT NULL,
                $COL_NOTES   TEXT    NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOVIES")
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "movies.db"
        const val DB_VERSION = 1
        const val TABLE_MOVIES = "movies"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_DIR = "director"
        const val COL_YEAR = "year"
        const val COL_RATING = "rating"
        const val COL_NOTES = "notes"
    }
}
