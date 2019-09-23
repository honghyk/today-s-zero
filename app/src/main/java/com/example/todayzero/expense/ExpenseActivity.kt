package com.example.todayzero.expense

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.example.todayzero.MainActivity
import com.example.todayzero.R
import com.example.todayzero.db.DBHelper
import com.example.todayzero.db.deal
import kotlinx.android.synthetic.main.expense_act.*
import java.text.SimpleDateFormat
import java.util.*

class ExpenseActivity : AppCompatActivity() {
    var store: String? = ""

    lateinit var datePickText: TextView
    lateinit var timePickText: TextView


    lateinit var dbHelper:DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense_act)
        setSupportActionBar(expense_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dbHelper=DBHelper(this)

        datePickText = findViewById<TextView>(R.id.date_pick_text).apply {
            setOnClickListener{ makeDatePickerDialog() }
        }
        timePickText = findViewById<TextView>(R.id.time_pick_text).apply {
            setOnClickListener { makeTimePickerDialog() }
        }

        initDateAndStore()
    }

    private fun makeDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                val date = "$year.${month + 1}.$dayOfMonth"
                datePickText.text = date
            }, year, month, day).show()
    }

    private fun makeTimePickerDialog() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val time = "${hourOfDay}시 ${minute}분"
            timePickText.text = time
        }, hour, minute, false).show()
    }

    private fun initDateAndStore() {
        val currentTime = System.currentTimeMillis()
        val currentDate = Date(currentTime)
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        val timeFormat = SimpleDateFormat("k시 m분", Locale.KOREA)

        date_pick_text.text = dateFormat.format(currentDate)
        time_pick_text.text = timeFormat.format(currentDate)

        store = intent.getStringExtra(STORE_NAME_TAG)
        if(store != null)
            place_edit_text.setText(store)
        else
            store = ""
}

private fun storeDealInDatabase() {

    val price: String
    val isZero: Int

    if(price_edit_text.text.toString().isEmpty()) {
        price = "0"
    } else {
        price = price_edit_text.text.toString()
    }
    store = place_edit_text.text.toString()
    val category = category_edit_text.text.toString()
    val memo = memo_edit_text.text.toString()
    val date = datePickText.text.toString() + " " + timePickText.text.toString()

    if(zeropay_checkbox.isChecked)
        isZero = 1
    else
        isZero = 0

    val deal = deal("", date, store!!, price, category, memo, isZero)
    dbHelper.insertDeal(deal)
}

private fun checkDealForm(): Boolean {
    val categoryEdit = category_edit_text.text.toString()
    val storeEdit = place_edit_text.text.toString()

    if(categoryEdit.isEmpty()) {
        Toast.makeText(this, "분류를 선택해주세요", Toast.LENGTH_SHORT).show()
        return false
    }
    if(storeEdit.isEmpty()) {
        Toast.makeText(this, "사용처를 입력해주세요", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(this, MainActivity::class.java)

        when(item!!.itemId) {
            R.id.complete -> {
                if(checkDealForm()) {
                    storeDealInDatabase()
                    startActivity(intent)
                }
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

    companion object {
        const val STORE_NAME_TAG = "STORE_NAME"
    }
}
