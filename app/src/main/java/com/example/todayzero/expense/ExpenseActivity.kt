package com.example.todayzero.expense

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.todayzero.R
import kotlinx.android.synthetic.main.expense_act.*

class ExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense_act)
        setSupportActionBar(expense_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.complete -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.expense_menu, menu)

        //지출 내역 리스트를 클릭해서 Activity를 실행하는 경우 삭제 버튼 활성화
        //지출 내역 추가하는 경우 삭제 버튼 비활성화
        val deleteMenu = menu?.findItem(R.id.delete)
        deleteMenu!!.isVisible = false

        return true
    }
}
