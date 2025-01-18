package com.example.platepals

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val registerButton : Button = findViewById(R.id.register_btn)
        registerButton.setOnClickListener{
            val username : EditText = findViewById(R.id.register_username)
            val password : EditText = findViewById(R.id.register_password)

            //TODO inbal - save user
            // TODO yahli - open home activity
        }
    }
}