package com.ykanivets.emojibooks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    var books: List<Book>,
    var listener: Listener
) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    override fun getItemCount() = books.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        view = LayoutInflater.from(parent.context).inflate(R.layout.view_item_book, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(books[position]) {
        holder.tvEmoji.text = emoji
        holder.tvTitle.text = title
        holder.tvSubtitle.text = author

        holder.itemView.setOnClickListener { listener.onBookClicked(this) }
        holder.btnDelete.setOnClickListener { listener.onBookDeleteClicked(this) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvEmoji: TextView = view.findViewById(R.id.tvEmoji)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = view.findViewById(R.id.tvAuthor)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    interface Listener {

        fun onBookClicked(book: Book)

        fun onBookDeleteClicked(book: Book)
    }


}
