package com.example.aeroluggage

import Tag
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TagAdapter(private var tags: List<Tag>, private val context: Context) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private var db: TagDatabaseHelper = TagDatabaseHelper(context)

    class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagTextView: TextView = itemView.findViewById(R.id.tagTextView)
        val roomTextView: TextView = itemView.findViewById(R.id.roomTextView)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.dateTimeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        return TagViewHolder(view)
    }

    override fun getItemCount(): Int = tags.size

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        holder.tagTextView.text = tag.bagtag
        holder.roomTextView.text = tag.room
        holder.dateTimeTextView.text = tag.dateTime

        holder.deleteButton.setOnClickListener {
            db.deleteTag(tag.id)
            refreshData(db.getAllTags())
            Toast.makeText(holder.itemView.context, "Tag deleted", Toast.LENGTH_SHORT).show()
        }


    }

    fun refreshData(newTags: List<Tag>) {
        tags = newTags
        notifyDataSetChanged()
    }
}
