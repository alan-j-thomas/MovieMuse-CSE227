package com.example.letterbox2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var loginBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPass)
        loginBtn = findViewById(R.id.loginBtn)

        loginBtn.setOnClickListener {

            val email = etEmail.text.toString()
            val pass = etPass.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()){

                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(this, "Login Successful!!", Toast.LENGTH_SHORT).show()
                            Intent(this, MainActivity::class.java).also {
                                startActivity(it)
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
            else{
                Toast.makeText(this, "Fill all the details!!", Toast.LENGTH_SHORT).show()
            }
        }


    }
}