package com.example.favfooddish

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1 - first import drawables
        // 2 - crate bg_gradient in drawable
        // 3 - crate splash_background in drawable
        // create style for splash activity in theme,xml
        // make change in android manifest then create intent to start main activity
       startActivity(Intent(this,MainActivity::class.java))
       finish()

    }

}