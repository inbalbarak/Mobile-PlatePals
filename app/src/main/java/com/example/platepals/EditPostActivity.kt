package com.example.platepals

class EditPostActivity : AppCompatActivity() {
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