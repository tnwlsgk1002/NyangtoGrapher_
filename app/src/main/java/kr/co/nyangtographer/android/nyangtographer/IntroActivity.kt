package kr.co.nyangtographer.android.nyangtographer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val torchText : TextView = findViewById(R.id.tv_torch_text)
        // blink 애니메이션
        val anim = AnimationUtils.loadAnimation(this,R.anim.blink_animation)
        // 애니메이션 재생
        torchText.startAnimation(anim)

        val linearLayoutMainActivity : LinearLayout = findViewById(R.id.ll_activity_main)
        linearLayoutMainActivity.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}