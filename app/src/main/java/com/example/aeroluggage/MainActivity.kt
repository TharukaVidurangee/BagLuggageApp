package com.example.aeroluggage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RoomCardAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Delay of 3 seconds before switching to SecondActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }, 3000)

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerView) // Ensure you have a RecyclerView in your layout
        adapter = RoomCardAdapter(
            items = listOf("Room 1", "Room 2"), // Replace with your data model
            onDelete = { position ->
                // Handle delete action
            },
            onSync = { position ->
                // Handle sync action
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(SwipeCallback(adapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
