package com.example.androidexercise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
/*
Main Activity just for a splash screen for duration of 3 seconds
 */
class MainActivity : AppCompatActivity() {

    lateinit var splashImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        splashImage = findViewById(R.id.image_splas)
        Handler().postDelayed({
            val intent = Intent(this, Addresses::class.java)
            startActivity(intent)
            finish()
        }, 3000)


    }
}
