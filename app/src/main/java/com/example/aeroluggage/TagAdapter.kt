package com.example.sqlitedbaeroluggage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.aeroluggage.R
import com.example.aeroluggage.Tag
import com.example.aeroluggage.TagDatabaseHelper

class TagAdapter(private var tags:List<Tag>, context: Context) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private var db: TagDatabaseHelper = TagDatabaseHelper(context)

    class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tagTextView: TextView = itemView.findViewById(R.id.tagTextView)
        val roomTextView: TextView = itemView.findViewById(R.id.roomTextView)
        //        val updateButton: ImageView = itemView.findViewById(R.id.updateSaveButton)      //initialzing the button
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    //setup the item layout view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        return TagViewHolder(view)
    }

    //for the size
    override fun getItemCount(): Int = tags.size

    //to set data on to the element
    //holder -> set the data //position -> to determine which item was clicked
    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        //the scanned/written text should be displayed in the tag text view
        holder.tagTextView.text = tag.bagtag
        holder.roomTextView.text = tag.room

        /*
        //to set the onClickListener
        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateActivity::class.java).apply{
              putExtra("tag_id", tag.id)
            }
            holder.itemView.context.startActivity(intent)
        }*/

        holder.deleteButton.setOnClickListener {
            db.deleteTag(tag.id)
            //refresh the list
            refreshData(db.getAllTags())
            Toast.makeText(holder.itemView.context, "Tag deleted", Toast.LENGTH_SHORT).show()
        }
    }

    //to refresh the data
    fun refreshData(newTags: List<Tag>) {
        tags = newTags
        //reloading process
        notifyDataSetChanged()
    }
}