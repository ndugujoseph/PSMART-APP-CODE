package com.example.psmart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegisterWelcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_welcome)

        actionBar?.setHomeButtonEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        var role = ""

        val learner = findViewById<Button>(R.id.button_learner)
        learner.setOnClickListener {
             role = "Learner"

            val intent = Intent(applicationContext, RegisterHomeStudent::class.java)
            intent.putExtra("Role", role)
            startActivity(intent)
        }

        val teacher = findViewById<Button>(R.id.button_teacher)
        teacher.setOnClickListener {
            role = "Teacher"
            val intent = Intent(applicationContext, RegisterHomeTeacher::class.java)
            intent.putExtra("Role", role)
            startActivity(intent)
        }

    }
}