package com.ykanivets.emojibooks

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where
import kotlin.random.Random

class BookRepository {

    private val realm = Realm.getDefaultInstance()

    fun getAll(): List<Book> {
        val realmBooks = realm.where<RealmBook>().findAll()
        return realm.copyFromRealm(realmBooks).map { it.toBook() }
    }

    fun add(book: Book) {
        realm.beginTransaction()
        realm.copyToRealm(book.toRealmBook())
        realm.commitTransaction()
    }

    fun update(book: Book) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(book.toRealmBook())
        realm.commitTransaction()
    }

    fun delete(book: Book) {
        realm.beginTransaction()
        realm.where<RealmBook>().equalTo("id", book.id).findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }

    private fun RealmBook.toBook() = Book(
        id = id,
        emoji = emoji,
        title = title,
        author = author
    )

    private fun Book.toRealmBook() = RealmBook(
        id = id ?: Random.nextLong(),
        emoji = emoji,
        title = title,
        author = author
    )
}

open class RealmBook(
    @PrimaryKey var id: Long = -1,
    var emoji: String = "",
    var title: String = "",
    var author: String = ""
) : RealmObject()
