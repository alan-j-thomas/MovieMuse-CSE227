package com.example.letterbox2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class SignUpPage : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var createBtn: Button
    private lateinit var loginBtn: Button

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)


        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPass)
        createBtn = findViewById(R.id.createBtn)
        loginBtn = findViewById(R.id.loginBtn)

        auth = Firebase.auth

        createBtn.setOnClickListener {

            val email = etEmail.text.toString()
            val pass = etPass.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()){

                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(this, "Account Created Successfully!!", Toast.LENGTH_SHORT).show()
                            Intent(this, MainActivity::class.java).also {
                                startActivity(it)
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                this,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }

        loginBtn.setOnClickListener {
            Intent(this, LoginPage::class.java).also {
                startActivity(it)
            }
        }
    }

}