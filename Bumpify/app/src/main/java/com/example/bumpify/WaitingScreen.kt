package com.example.bumpify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView

class WaitingScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_screen)
        val iv_logo = findViewById<ImageView>(R.id.iv_logo)
        iv_logo.alpha = 0f;
        iv_logo.animate().setDuration(4000).alpha(1f).withEndAction {
            val i = Intent(this, LogInActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }

    }
}