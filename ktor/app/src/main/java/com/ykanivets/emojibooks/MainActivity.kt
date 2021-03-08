package com.ykanivets.emojibooks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), BookAdapter.Listener {

    private val presenterJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + presenterJob)

    private val bookRepository by lazy { BookRepository(token = intent.getStringExtra(EXTRA_TOKEN)) }
    private val adapter by lazy { BookAdapter(emptyList(), listener = this) }

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
                        bookRepository.add(bookExtra.toBookBody())
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
        startActivityForResult(
            BookActivity.newIntent(this, book.toBookExtra()),
            REQUEST_UPDATE_BOOK
        )
    }

    override fun onBookDeleteClicked(book: Book) = uiScope.launch {
        bookRepository.delete(book)
        refresh()
    }.let {}

    private fun refresh() = uiScope.launch {
        adapter.books = bookRepository.getAll()
        adapter.notifyDataSetChanged()
    }

    private fun Intent.getBookExtra() =
        getParcelableExtra<BookActivity.BookExtra>(BookActivity.EXTRA_BOOK)

    private fun Book.toBookExtra() = BookActivity.BookExtra(
        id = id,
        emoji = emoji,
        title = title,
        author = author
    )

    private fun BookActivity.BookExtra.toBook() = Book(
        id = id!!,
        emoji = emoji,
        title = title,
        author = author
    )

    private fun BookActivity.BookExtra.toBookBody() = BookBody(
        emoji = emoji,
        title = title,
        author = author
    )

    companion object {

        private const val REQUEST_ADD_BOOK = 1
        private const val REQUEST_UPDATE_BOOK = 2

        private const val EXTRA_TOKEN = "TOKEN"

        fun newIntent(
            context: Context,
            token: String
        ) = Intent(
            context,
            MainActivity::class.java
        ).apply {
            putExtra(EXTRA_TOKEN, token)
        }
    }
}
