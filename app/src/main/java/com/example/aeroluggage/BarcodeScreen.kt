package com.example.aeroluggage

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aeroluggage.databinding.ActivityBarcodeScreenBinding


class BarcodeScreen : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeScreenBinding
    private lateinit var db: TagDatabaseHelper
    private lateinit var tagAdapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setting up the db
        db = TagDatabaseHelper(this)

        //setting up the tag adapter
        tagAdapter = TagAdapter(db.getAllTags(), this)

        //setting up the recycler view in the linear layout
        binding.tagRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.tagRecyclerView.adapter = tagAdapter

        //handling the save button click
        binding.saveButton.setOnClickListener{
            val room = binding.roomEditText.text.toString()
            val bagtag = binding.tagEditText.text.toString()
            if (room.isNotEmpty() && bagtag.isNotEmpty()) {
                val tag = Tag(0, room, bagtag)
                db.insertTag(tag)
                tagAdapter.refreshData(db.getAllTags())
                binding.roomEditText.text.clear()
                binding.tagEditText.text.clear()
                Toast.makeText(this, "Bag Tag saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tagAdapter.refreshData(db.getAllTags())
    }

}



