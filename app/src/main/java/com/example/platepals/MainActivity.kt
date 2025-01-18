package com.example.platepals

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.platepals.model.Model
import com.example.platepals.ui.theme.PlatePalsTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val loginButton: Button = findViewById(R.id.login_btn);
        val registerButton : TextView = findViewById(R.id.open_register_btn)
        val auth = Firebase.auth
        if(auth.currentUser?.email != null){
            login(true,auth.currentUser?.email?:"")
        }

        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.login_username).text.toString()
            val password = findViewById<EditText>(R.id.login_password).text.toString()

            login(false,email,password)
        }

        registerButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    fun login(autoSign:Boolean,email:String, password:String= ""){
        val auth = Firebase.auth

        if( email.isNotEmpty() && (autoSign || password.isNotEmpty())){
            Model.shared.getUserByEmail(email) { user ->
                if(autoSign || user?.password == password ){
                    val formatterPassword = if (autoSign) user?.password else password

                    auth.signInWithEmailAndPassword(email, formatterPassword?:"")
                        .addOnCompleteListener(this){task->
                            if(task.isSuccessful){
                                Toast.makeText(this,"authentication finished successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, PersonalInfoActivity::class.java)
                                startActivity(intent)
                                //TODO move to home page
                            }else{
                                Toast.makeText(this,"authentication failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PlatePalsTheme {
        Greeting("Android")
    }
}