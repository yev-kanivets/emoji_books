package com.ykanivets.emojibooks

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_book.etAuthor
import kotlinx.android.synthetic.main.activity_book.etEmoji
import kotlinx.android.synthetic.main.activity_book.etTitle
import kotlinx.android.synthetic.main.activity_book.fabDone
import kotlinx.android.synthetic.main.activity_book.tilAuthor
import kotlinx.android.synthetic.main.activity_book.tilEmoji
import kotlinx.android.synthetic.main.activity_book.tilTitle
import kotlinx.android.synthetic.main.activity_book.toolbar

class BookActivity : AppCompatActivity() {

    private val book by lazy { intent.getParcelableExtra<BookExtra>(EXTRA_BOOK) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        initToolbar()
        initEditTexts()
        initFab()
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        title = getString(if (book == null) R.string.add_book else R.string.update_book)
    }

    private fun initEditTexts() {
        etEmoji.setText(book?.emoji)
        etTitle.setText(book?.title)
        etAuthor.setText(book?.author)

        etEmoji.addTextChangedListener(ClearErrorTextWatcher(tilEmoji))
        etTitle.addTextChangedListener(ClearErrorTextWatcher(tilTitle))
        etAuthor.addTextChangedListener(ClearErrorTextWatcher(tilAuthor))

        etEmoji.filters = arrayOf(EmojiInputFilter(), InputFilter.LengthFilter(2))
        etEmoji.requestFocus()
    }

    private fun initFab() = fabDone.setOnClickListener {
        val emoji = etEmoji.text?.toString().takeUnless { it.isNullOrBlank() }
        val title = etTitle.text?.toString().takeUnless { it.isNullOrBlank() }
        val author = etAuthor.text?.toString().takeUnless { it.isNullOrBlank() }

        if (emoji == null) tilEmoji.error = getString(R.string.cant_be_empty)
        if (title == null) tilTitle.error = getString(R.string.cant_be_empty)
        if (author == null) tilAuthor.error = getString(R.string.cant_be_empty)

        if (emoji == null || title == null || author == null) return@setOnClickListener

        val bookExtra = BookExtra(
            id = book?.id,
            emoji = emoji,
            title = title,
            author = author
        )

        setResult(RESULT_OK, Intent().putExtra(EXTRA_BOOK, bookExtra))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> true.also { onBackPressed() }
        else -> super.onOptionsItemSelected(item)
    }

    private class ClearErrorTextWatcher(
        private val til: TextInputLayout
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            til.isErrorEnabled = false
            til.error = null
        }
    }

    private class EmojiInputFilter : InputFilter {

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence {
            for (index in start until end) {
                val type = Character.getType(source[index])
                if (type !in listOf(
                        Character.SURROGATE,
                        Character.OTHER_SYMBOL,
                        Character.NON_SPACING_MARK,
                        Character.SIZE
                    ).map { it.toInt() }
                ) return ""
            }
            return source
        }
    }

    companion object {

        const val EXTRA_BOOK = "extra_book"

        fun newIntent(
            context: Context,
            bookExtra: BookExtra? = null
        ) = Intent(
            context,
            BookActivity::class.java
        ).apply {
            putExtra(EXTRA_BOOK, bookExtra)
        }
    }

    @Parcelize
    data class BookExtra(
        val id: Long?,
        val emoji: String,
        val title: String,
        val author: String
    ) : Parcelable
}
