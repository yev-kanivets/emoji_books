package com.ykanivets.emojibooks

import io.ktor.client.request.*

class BookRepository(token: String) {

    private val httpClient = HttpClientFactory().create(token)

    suspend fun getAll(): List<Book> = httpClient.get(path = "books")

    suspend fun add(book: BookBody): List<Book> = httpClient.post(path = "books") {
        body = book
    }

    suspend fun update(book: Book): List<Book> = httpClient.put(path = "books/${book.id}") {
        body = book.toBookBody()
    }

    suspend fun delete(book: Book): List<Book> = httpClient.delete(path = "books/${book.id}")

    private fun Book.toBookBody() = BookBody(
        emoji = emoji,
        title = title,
        author = author
    )
}
