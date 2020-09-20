package com.ykanivets.emojibooks

import com.squareup.sqldelight.db.SqlDriver

class BookRepository(
    sqlDriver: SqlDriver
) {

    private val database = EBDatabase(sqlDriver)

    fun getAll(): List<Book> = database.bookQueries.getAll().executeAsList().map { it.toBook() }

    fun add(book: Book) = database.bookQueries.add(
        emoji = book.emoji,
        title = book.title,
        author = book.author
    )

    fun update(book: Book) = database.bookQueries.update(
        id = book.id,
        emoji = book.emoji,
        title = book.title,
        author = book.author
    )

    fun delete(book: Book) = book.id?.let { database.bookQueries.delete(it) }

    private fun DbBook.toBook() = Book(
        id = id,
        emoji = emoji,
        title = title,
        author = author
    )
}
