package com.example.psmart

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class RegisterHomeStudent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_home_student)

        val con = findViewById<Button>(R.id.con_learner)
        val id = findViewById<TextInputLayout>(R.id.txtNumber)

       // val stdNo = id.editText.toString()
        val stdNo: Editable? = id.editText?.text

        val intent = intent
        val role = intent.getStringExtra("Role")


        con.setOnClickListener {
            val intent = Intent(applicationContext, NewFaceActivity::class.java)
            val extras = Bundle()
            extras.putString("Role", role)
            extras.putString("ID", stdNo.toString())
            intent.putExtras(extras)
            startActivity(intent)
        }

    }
}