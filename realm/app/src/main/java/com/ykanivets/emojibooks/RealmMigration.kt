package com.ykanivets.emojibooks

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.kotlin.where

class RealmMigration(
    private val bookRepository: BookRepository
) {

    fun executeIfNeeded() {
        val realm = Realm.getDefaultInstance()

        val realmBooks = realm.where<RealmBook>().findAll()
        val books = realm.copyFromRealm(realmBooks).map { it.toBook() }.takeUnless { it.isEmpty() } ?: return

        books.forEach { bookRepository.update(it) }

        deleteAll(realm)
    }

    private fun deleteAll(realm: Realm) {
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

    private fun RealmBook.toBook() = Book(
        id = id,
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
