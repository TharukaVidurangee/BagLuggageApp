
package com.example.aeroluggage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aeroluggage.databinding.ActivityMainBinding


class barcode_screen : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    //initializing the database helper
    private lateinit var db:TagDatabaseHelper

    //initializing the adapater
    private lateinit var tagAdapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setting up the db
        db = TagDatabaseHelper(this)

        //setting up the tag adapter
        tagAdapter = TagAdapter(db.getAllTags(), this)

        //setting up the recycler view in the linear layout
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tagRecyclerView.adapter = tagAdapter



        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddTag::class.java)
            startActivity(intent)
        }
    }
    //refresh data method, onResume function refresh the data automatically
    override fun onResume() {
        super.onResume()
        tagAdapter.refreshData(db.getAllTags())
    }
}


