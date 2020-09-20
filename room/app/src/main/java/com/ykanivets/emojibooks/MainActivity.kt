package com.ykanivets.emojibooks

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.fabAddBook
import kotlinx.android.synthetic.main.activity_main.recyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), BookAdapter.Listener {

    private val bookRepository by lazy { BookRepository(this) }

    private val adapter by lazy { BookAdapter(emptyList(), listener = this) }

    private val job = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecycler()
        initFab()

        refresh()
    }

    private fun initRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun initFab() = fabAddBook.setOnClickListener {
        startActivityForResult(BookActivity.newIntent(this), REQUEST_ADD_BOOK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_ADD_BOOK -> data?.getBookExtra()?.let { bookExtra ->
                    uiScope.launch {
                        bookRepository.add(bookExtra.toBook())
                        refresh()
                    }
                }
                REQUEST_UPDATE_BOOK -> data?.getBookExtra()?.let { bookExtra ->
                    uiScope.launch {
                        bookRepository.update(bookExtra.toBook())
                        refresh()
                    }
                }
            }
        }
    }

    override fun onBookClicked(book: Book) {
        startActivityForResult(BookActivity.newIntent(this, book.toBookExtra()), REQUEST_UPDATE_BOOK)
    }

    override fun onBookDeleteClicked(book: Book) = uiScope.launch {
        bookRepository.delete(book)
        refresh()
    }.let { Unit }

    private fun refresh()  = uiScope.launch {
        adapter.books = bookRepository.getAll()
        adapter.notifyDataSetChanged()
    }

    private fun Intent.getBookExtra() = getParcelableExtra<BookActivity.BookExtra>(BookActivity.EXTRA_BOOK)

    private fun Book.toBookExtra() = BookActivity.BookExtra(
        id = id,
        emoji = emoji,
        title = title,
        author = author
    )

    private fun BookActivity.BookExtra.toBook() = Book(
        id = id,
        emoji = emoji,
        title = title,
        author = author
    )

    companion object {

        private const val REQUEST_ADD_BOOK = 1
        private const val REQUEST_UPDATE_BOOK = 2
    }
}
