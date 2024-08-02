
package com.example.aeroluggage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aeroluggage.databinding.ActivityBarcodeScreenBinding
import com.example.aeroluggage.databinding.ActivityMainBinding
import com.example.sqlitedbaeroluggage.TagAdapter


class barcode_screen : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeScreenBinding

    //initializing the database helper
    private lateinit var db:TagDatabaseHelper

    //initializing the adapater
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


        binding.addButton.setOnClickListener {
            val intent = Intent(this, barcode_screen::class.java)
            startActivity(intent)
        }



        binding.saveButton.setOnClickListener{
            val room = binding.roomEditText.text.toString()
            val bagtag = binding.tagEditText.text.toString()
            val tag = Tag(0, room, bagtag)
            db.insertTag(tag)
            finish()
            Toast.makeText(this, "Bag Tag saved", Toast.LENGTH_SHORT).show()
        }


    }
    //refresh data method, onResume function refresh the data automatically
    override fun onResume() {
        super.onResume()
        tagAdapter.refreshData(db.getAllTags())
    }


}


