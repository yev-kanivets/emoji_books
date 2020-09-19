package com.ykanivets.emojibooks

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BookRepository(
    context: Context
) {

    private val dbHelper = DbHelper(context)

    fun getAll(): List<Book> {
        val database = dbHelper.readableDatabase

        val cursor = database.query(DbHelper.TABLE_BOOKS, null, null, null, null, null, null)

        val books = mutableListOf<Book>()
        if (cursor == null) return books

        if (cursor.moveToFirst()) {
            val idColIndex = cursor.getColumnIndex(DbHelper.COLUMN_ID)
            val emojiColIndex = cursor.getColumnIndex(DbHelper.COLUMN_EMOJI)
            val titleColIndex = cursor.getColumnIndex(DbHelper.COLUMN_TITLE)
            val authorColIndex = cursor.getColumnIndex(DbHelper.COLUMN_AUTHOR)

            do {
                val book = Book(
                    id = cursor.getLong(idColIndex),
                    emoji = cursor.getString(emojiColIndex),
                    title = cursor.getString(titleColIndex),
                    author = cursor.getString(authorColIndex)
                )
                books.add(book)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return books
    }

    fun add(book: Book): Boolean {
        val database = dbHelper.writableDatabase

        val id = database.insert(DbHelper.TABLE_BOOKS, null, book.contentValues)

        return id != -1L
    }

    fun update(book: Book): Boolean {
        val database = dbHelper.writableDatabase

        val args = arrayOf(book.id.toString())
        val rowsAffected = database.update(DbHelper.TABLE_BOOKS, book.contentValues, "id=?", args)

        return rowsAffected != 0
    }

    fun delete(book: Book): Boolean {
        val database = dbHelper.writableDatabase

        val args = arrayOf(book.id.toString())
        val rowsAffected = database.delete(DbHelper.TABLE_BOOKS, "id=?", args)

        return rowsAffected != 0
    }

    private val Book.contentValues: ContentValues
        get() = ContentValues().apply {
            put(DbHelper.COLUMN_EMOJI, emoji)
            put(DbHelper.COLUMN_TITLE, title)
            put(DbHelper.COLUMN_AUTHOR, author)
        }
}

private class DbHelper(
    context: Context
) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE $TABLE_BOOKS (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_EMOJI TEXT,
                    $COLUMN_TITLE TEXT,
                    $COLUMN_AUTHOR TEXT);
            """
        )
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {

        const val DB_NAME = "database"
        const val DB_VERSION = 1

        const val TABLE_BOOKS = "DbBook"
        const val COLUMN_ID = "id"
        const val COLUMN_EMOJI = "emoji"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
    }
}
