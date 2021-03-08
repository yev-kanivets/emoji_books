package com.ykanivets.emojibooks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword("emoji.books@gmail.com", "!Smtv6RN7mr3")
            .addOnCompleteListener { task ->
                task.result?.user?.getIdToken(false)?.addOnCompleteListener {
                    it.result?.token?.let { token ->
                        startActivity(MainActivity.newIntent(this, token))
                        finish()
                    }
                }
            }
    }
}
