package com.ykanivets.emojibooks

class BookRepository {

    private val books = mutableListOf<Book>(
        Book(0, "\uD83D\uDE31", "The Shining", "Stephen King")
    )

    fun getAll(): List<Book> = books

    fun add(book: Book): Boolean {
        return books.add(book.copy(id = books.size.toLong()))
    }

    fun update(book: Book): Boolean {
        val index = books.indexOfFirst { it.id == book.id }.takeUnless { it == -1 } ?: return false
        books[index] = book
        return true
    }

    fun delete(book: Book): Boolean {
        return books.remove(book)
    }
}
