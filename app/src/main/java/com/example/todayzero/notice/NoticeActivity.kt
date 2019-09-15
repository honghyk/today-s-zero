package com.example.todayzero.notice

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.todayzero.R

class NoticeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_act)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
