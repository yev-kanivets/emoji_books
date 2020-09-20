package com.ykanivets.emojibooks

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookRepository(
    context: Context
) {

    private val bookDao = Room.databaseBuilder(
        context.applicationContext,
        BookRoomDatabase::class.java,
        "database"
    ).build().bookDao()

    suspend fun getAll() = withContext(Dispatchers.IO) { bookDao.getAll().map { it.toBook() } }

    suspend fun add(book: Book) = bookDao.add(book.toDbBook())

    suspend fun update(book: Book) = bookDao.update(book.toDbBook())

    suspend fun delete(book: Book) = bookDao.delete(book.toDbBook())

    private fun DbBook.toBook() = Book(
        id = id,
        emoji = emoji,
        title = title,
        author = author
    )

    private fun Book.toDbBook() = DbBook(
        id = id ?: -1,
        emoji = emoji,
        title = title,
        author = author
    )
}

@Entity(tableName = "DbBook")
private class DbBook(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "emoji") val emoji: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "author") val author: String
)

@Dao
private interface BookDao {

    @Query("SELECT * from DbBook")
    fun getAll(): List<DbBook>

    @Insert
    suspend fun add(book: DbBook)

    @Update
    suspend fun update(book: DbBook)

    @Delete
    suspend fun delete(book: DbBook)
}

@Database(entities = [DbBook::class], version = 1, exportSchema = false)
private abstract class BookRoomDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
}
