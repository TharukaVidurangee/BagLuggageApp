package com.example.aeroluggage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, ActivityDrawer::class.java)
            startActivity(intent)
        }
    }
}
