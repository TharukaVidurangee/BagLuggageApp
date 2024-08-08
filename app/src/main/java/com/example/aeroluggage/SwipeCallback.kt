package com.example.aeroluggage

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeCallback(
    private val adapter: RoomCardAdapter
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val background = ColorDrawable()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val action = if (direction == ItemTouchHelper.LEFT) {
            SwipeAction.DELETE
        } else {
            SwipeAction.SYNC
        }
        adapter.onSwiped(viewHolder.adapterPosition, action)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        background.color = if (dX > 0) Color.GREEN else Color.RED
        background.setBounds(
            itemView.left,
            itemView.top,
            itemView.right + dX.toInt(),
            itemView.bottom
        )
        background.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}

enum class SwipeAction {
    DELETE, SYNC
}