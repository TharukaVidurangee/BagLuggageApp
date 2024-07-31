package com.example.aeroluggage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button

class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

//        to get reference to the button
        val loginButton: Button = findViewById(R.id.loginButton)

//        set an onClickListener to the button
        val intent = Intent(this, barcode_screen::class.java)
        startActivity(intent)
    }
}

