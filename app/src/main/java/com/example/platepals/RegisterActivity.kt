package com.example.platepals

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.platepals.model.Model
import com.example.platepals.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val registerButton : Button = findViewById(R.id.register_btn)
        registerButton.setOnClickListener {
            val auth = Firebase.auth
            val email = findViewById<EditText>(R.id.register_username).text.toString()
            val password = findViewById<EditText>(R.id.register_password).text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                Model.shared.upsertUser(User(email,password), null) { success ->
                    if(success){
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this){task->
                                if(task.isSuccessful){
                                    val intent = Intent(this, EditPostActivity::class.java)
                                    startActivity(intent)
                                    //TODO move to home page
                                }else{
                                    Toast.makeText(this,"authentication failed", Toast.LENGTH_SHORT).show()
                                }
                            }

                    }else{
                        Toast.makeText(this,"error creating user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}