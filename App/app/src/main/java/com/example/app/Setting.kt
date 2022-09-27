package com.example.app

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton

class Setting : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val noticepoint= findViewById<ImageButton>(R.id.noticepoint)
        noticepoint.setOnClickListener {
            val intent = Intent(this, NoticeMatter::class.java)
            startActivity(intent)
        }
    }

    fun onDialogClicked3(view: View){
        val name = Name(this)
        name.show()
    }
}