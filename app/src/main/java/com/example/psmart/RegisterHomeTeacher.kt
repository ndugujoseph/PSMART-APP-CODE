package com.example.psmart

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class RegisterHomeTeacher : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_home_teacher)

        val con = findViewById<Button>(R.id.con_teacher)
        val id = findViewById<TextInputLayout>(R.id.txtNin)

        val nin: Editable? = id.editText?.text

        val intent = intent
        val role = intent.getStringExtra("Role")

        con.setOnClickListener {
            val intent = Intent(applicationContext, NewFaceActivity::class.java)
            val extras = Bundle()
            extras.putString("Role", role)
            extras.putString("ID", nin.toString())
            intent.putExtras(extras)
            startActivity(intent)
        }
    }
}