package com.example.todayzero

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.todayzero.expense.ExpenseActivity
import com.example.todayzero.findstore.StoreActivity
import com.example.todayzero.notice.NoticeActivity
import com.example.todayzero.util.replaceFragmentInActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            startActivity(Intent(this, ExpenseActivity::class.java))
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val mainFragment = supportFragmentManager.findFragmentById(R.id.main_contentFrame)
                as MainFragment? ?: MainFragment().also { replaceFragmentInActivity(it, R.id.main_contentFrame) }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_find_store -> {
                startActivity(Intent(this, StoreActivity::class.java))
            }
            R.id.nav_notice -> {
                startActivity(Intent(this, NoticeActivity::class.java))
            }
            R.id.nav_use ->{
                val youtubePage= Uri.parse("https://www.youtube.com/watch?v=p1ZApN4JvRA")
                val webIntent= Intent(Intent.ACTION_VIEW,youtubePage)
                startActivity(webIntent)
            }
            R.id.nav_call->{
                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("help@zpay.or.kr"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[제로페이] 관련 문의사항")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "불편사항, 문의사항을 작성해주세요:)\n")
                emailIntent.type="message/rfc822"
                startActivity(emailIntent) //네트워크 오류 , 설치 오류 예외처리 해야할 듯
            }
            R.id.nav_share->{

            }
            R.id.nav_send->{
                val instaPage= Uri.parse("https://www.instagram.com/zeropay.official/")
                val webIntent= Intent(Intent.ACTION_VIEW,instaPage)
                startActivity(webIntent)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
