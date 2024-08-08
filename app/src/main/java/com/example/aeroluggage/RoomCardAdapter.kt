package com.example.aeroluggage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoomCardAdapter(
    private val items: List<String>, // Replace String with your data model
    private val onDelete: (Int) -> Unit,
    private val onSync: (Int) -> Unit
) : RecyclerView.Adapter<RoomCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tag_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind your data here
        holder.bind(items[position], position, onDelete, onSync)
    }

    override fun getItemCount(): Int = items.size
    fun onSwiped(adapterPosition: Int, action: SwipeAction) {
        TODO("Not yet implemented")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val swipeActionsLayout: LinearLayout = itemView.findViewById(R.id.swipeActionsLayout)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        private val syncButton: Button = itemView.findViewById(R.id.syncButton)

        fun bind(item: String, position: Int, onDelete: (Int) -> Unit, onSync: (Int) -> Unit) {
            // Bind your data here

            deleteButton.setOnClickListener { onDelete(position) }
            syncButton.setOnClickListener { onSync(position) }
        }
    }
}
