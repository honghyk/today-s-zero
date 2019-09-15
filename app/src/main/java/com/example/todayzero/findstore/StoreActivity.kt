package com.example.todayzero.findstore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.todayzero.R
import com.example.todayzero.util.replaceFragmentInActivity
import kotlinx.android.synthetic.main.store_act.*

class StoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_act)

        setSupportActionBar(store_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //구/동 선택 -> 가맹점 리스트 -> 선택한 가맹점 위치
        //GuDongFragment -> StoreListFragment -> StoreMapFragment 순서로 호출
        replaceFragmentInActivity(GuDongFragment(), R.id.store_contentFrame)

    }
}
