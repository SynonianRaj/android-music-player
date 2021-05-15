package com.synonian.raj.sanampurisong

import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.synonian.raj.sanampurisong.GlobalVariable.manager

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val insta = findViewById<ImageButton>(R.id.insta)
        insta.setOnClickListener {
            val uri = Uri.parse("https://www.instagram.com/codex.code")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()
        super.onDestroy()
    }

}