package com.example.platepals

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.platepals.model.Model
import com.example.platepals.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class PersonalInfoActivity : AppCompatActivity() {
    private var userInfo : User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_personal_info)
        val auth = Firebase.auth

        val logout = findViewById<Button>(R.id.logoutBtn)
        logout.setOnClickListener{
            auth.signOut()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

        val backButton : Button = findViewById(R.id.backBtn)
        backButton.setOnClickListener {
            super.onBackPressed()
        }

        Model.shared.getUserByEmail(auth.currentUser?.email?:"") { user ->
            userInfo = user

            val rating = if(user?.ratingCount?.toInt() == 0)  0 else (user?.ratingSum?.toInt() ?: 1) / (user?.ratingCount?.toInt() ?: 1)

            val displayFragment = DisplayUserInfoFragment.newInstance(user?.email ?: "", rating, user?.avatarUrl ?: "")
            showFragment(displayFragment)
        }

    }

    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)  // Replace container with the fragment
            .commit()
    }

}