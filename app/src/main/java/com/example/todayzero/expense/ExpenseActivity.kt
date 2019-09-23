package com.example.todayzero.expense

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.todayzero.MainActivity
import com.example.todayzero.R
import com.example.todayzero.db.DBHelper
import com.example.todayzero.db.deal
import kotlinx.android.synthetic.main.expense_act.*
import kotlinx.android.synthetic.main.store_list_frag.*

class ExpenseActivity : AppCompatActivity() {


    lateinit var dbHelper:DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense_act)
        setSupportActionBar(expense_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()

    }

    fun init(){
        dbHelper=DBHelper(this)
        expense_save_btn.setOnClickListener {
            //일단 제로페이. 제로페이 여부 ui 생성 이후 바꾸기
           val deal= deal("",date_edit_text.text.toString(),place_edit_text.text.toString(),price_edit_text.text.toString(),category_edit_text.text.toString(),memo_edit_text.text.toString(),0)
           dbHelper.insertDeal(deal)

        }
        expense_food.setOnClickListener {
            category_edit_text.setText(expense_food_text.text.toString())
            expense_category.visibility = View.GONE
        }
        category_edit_text.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus == true){
                expense_layout.setFocusableInTouchMode(true)
                expense_layout.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm!!.hideSoftInputFromWindow(category_edit_text.getWindowToken(), 0) // 키보드 숨기기
                expense_category.visibility = View.VISIBLE
            }
            else
                expense_category.visibility = View.GONE
        }


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(this, MainActivity::class.java)

        when(item!!.itemId) {
            R.id.complete -> {
                startActivity(intent)
            }
            R.id.delete -> {
                startActivity(intent)
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
