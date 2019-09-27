package com.example.todayzero.expense

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
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
    var update_deal:deal?=null
    var isupdate:Boolean=false

    lateinit var datePickText: TextView
    lateinit var timePickText: TextView
    lateinit var category_list: ArrayList<category>

    lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense_act)
        setSupportActionBar(expense_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        dbHelper= DBHelper(this)
        init()

    }

    fun init() {
        initLayout()


        datePickText = findViewById<TextView>(R.id.date_pick_text).apply {
            setOnClickListener { makeDatePickerDialog() }
        }
        timePickText = findViewById<TextView>(R.id.time_pick_text).apply {
            setOnClickListener { makeTimePickerDialog() }
        }

        category_edit_text.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus == true) {
                expense_layout.setFocusableInTouchMode(true)
                expense_layout.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm!!.hideSoftInputFromWindow(category_edit_text.getWindowToken(), 0) // 키보드 숨기기
                recyclerview.visibility = View.VISIBLE
            } else
                recyclerview.visibility = View.GONE
        }


        initDateAndStore()
    }

    fun initLayout(){
        category_list = arrayListOf(
            category("식비", R.drawable.ic_restaurant),
            category("카페/간식", R.drawable.ic_coffee),
            category("술/유흥", R.drawable.ic_drink),
            category("생활", R.drawable.ic_couch),
            category("쇼핑", R.drawable.ic_shopping),
            category("뷰티/미용", R.drawable.ic_cosmetics),
            category("교통", R.drawable.ic_bus),
            category("자동차", R.drawable.ic_car),
            category("주거/통신", R.drawable.ic_phone),
            category("의료/건강", R.drawable.ic_health),
            category("금융", R.drawable.ic_finance),
            category("문화/여가", R.drawable.ic_hobby),
            category("여행/숙박", R.drawable.ic_trip),
            category("교육/학습", R.drawable.ic_education),
            category("자녀/육아", R.drawable.ic_baby),
            category("경조/선물", R.drawable.ic_gift)
        )
        val layoutManager = GridLayoutManager(this, 4)
        recyclerview.layoutManager = layoutManager
        var adpater = ExpenseCategoryAdapter(category_list)
        recyclerview.adapter = adpater
        adpater.itemClickListener = object : ExpenseCategoryAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: ExpenseCategoryAdapter.ViewHolder,
                view: View,
                data: category,
                position: Int
            ) {
                category_edit_text.setText(data.category_string)
                recyclerview.visibility = View.GONE
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }

    private fun makeDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

            val date = if(month<9){"$year.0${month + 1}.$dayOfMonth"}else{"$year.${month + 1}.$dayOfMonth"}
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
        isupdate = intent.getBooleanExtra(UPDATE_DEAL_TAG,false)

        val currentTime = System.currentTimeMillis()
        val currentDate = Date(currentTime)
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        val timeFormat = SimpleDateFormat("k시 m분", Locale.KOREA)


         datePickText.text = dateFormat.format(currentDate)
         timePickText.text = timeFormat.format(currentDate)


        store = intent.getStringExtra(STORE_NAME_TAG)
        if (store != null)
            place_edit_text.setText(store)
        else
            store = ""

        Log.i("ExpenseActivity",store.toString())

        if(isupdate) {
            update_deal = intent?.getSerializableExtra(UPDATE_DEAL_DATA_TAG) as deal

            zeropay_checkbox.isChecked = update_deal?.isZero == 1
            price_edit_text.setText(update_deal?.price)
            category_edit_text.setText(update_deal?.category)
            place_edit_text.setText(update_deal?.store)
            val st = StringTokenizer(update_deal?.date, " ")
            datePickText.text = st.nextToken()
            var time = ""
            while(st.hasMoreTokens())
                time += st.nextToken()
            timePickText.text = time
            memo_edit_text.setText(update_deal?.memo)

        } else
            update_deal = null


    }

    private fun storeDealInDatabase() {

        val price: String
        val isZero: Int

        if (price_edit_text.text.toString().isEmpty()) {
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
        if(!isupdate){
            dbHelper.insertDeal(deal)
        }
        else{
           dbHelper.updateDeal(update_deal!!.did,deal)
        }


}
    private fun checkDealForm(): Boolean {
        val categoryEdit = category_edit_text.text.toString()
        val storeEdit = place_edit_text.text.toString()

        if (categoryEdit.isEmpty()) {
            Toast.makeText(this, "분류를 선택해주세요", Toast.LENGTH_SHORT).show()
            return false
        }
        if (storeEdit.isEmpty()) {
            Toast.makeText(this, "사용처를 입력해주세요", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(this, MainActivity::class.java)

        when (item!!.itemId) {
            R.id.complete -> {
                if (checkDealForm()) {
                    storeDealInDatabase()
                    startActivity(intent)
                }
            }
            R.id.delete -> {
                dbHelper.deleteDeal(update_deal!!.did)
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
        deleteMenu!!.isVisible = isupdate

        return true
    }

    companion object {
        const val STORE_NAME_TAG = "STORE_NAME"
        const val UPDATE_DEAL_TAG="UPDATE_DEAL"
        const val UPDATE_DEAL_DATA_TAG="UPDATE_DEAL_DATA"
    }
}
