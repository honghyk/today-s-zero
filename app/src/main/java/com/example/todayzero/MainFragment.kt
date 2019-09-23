package com.example.todayzero


import android.content.Intent
import android.content.res.ColorStateList
import android.media.Image
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.todayzero.db.DBHelper
import com.example.todayzero.expense.ExpenseActivity
import com.example.todayzero.util.DealAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_frag.*
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {

    lateinit var dbHelper: DBHelper
    lateinit var currentMonthTextView: TextView
    lateinit var prevMonthTextView: TextView
    lateinit var nextMonthTextView: TextView
    lateinit var prevMonthBtn: ImageButton
    lateinit var nextMonthBtn: ImageButton
    var currentMonth = 0
    var currentYear = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.main_frag, container, false)

        requireActivity().window.statusBarColor = requireContext().getColor(R.color.white)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        requireActivity().toolbar.visibility = View.VISIBLE

        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab).apply {
            setImageDrawable(requireActivity().getDrawable(R.drawable.ic_pencil_icon))
            backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.colorPrimary))
            setOnClickListener {
                startActivity(Intent(requireContext(), ExpenseActivity::class.java))
            }
        }
        with(root) {
            currentMonthTextView = findViewById(R.id.current_month_text_view)
            prevMonthTextView = findViewById<TextView>(R.id.prev_month_text_button).apply {
                setOnClickListener { backwardMonth() }
            }
            prevMonthBtn = findViewById<ImageButton>(R.id.prev_month_button).apply {
                setOnClickListener { backwardMonth() }
            }
            nextMonthTextView = findViewById<TextView>(R.id.next_month_text_button).apply {
                setOnClickListener { forwardMonth() }
            }
            nextMonthBtn = findViewById<ImageButton>(R.id.next_month_button).apply {
                setOnClickListener { forwardMonth() }
            }
        }

        initMonth()

        return root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().actionBar?.title = resources.getString(R.string.app_name)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbHelper = DBHelper(requireContext())
        //init()
    }

    fun init() {
        // user정보 등록 후
        //userBalanceTxt.text=dbHelper.getUser().balance.toString()
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        val dealList = dbHelper.getDeals()
        val adapter = DealAdapter(dealList)
        spent_list_view.layoutManager = layoutManager
        spent_list_view.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun initMonth() {
        val c = Calendar.getInstance()
        currentMonth = c.get(Calendar.MONTH)
        currentYear = c.get(Calendar.YEAR)

        currentMonthTextView.text = String.format("%d월", currentMonth)
        val prevMonth = currentMonth - 1
        val nextMonth = currentMonth + 1

        setPrevNextMonthTextBtn(prevMonth, nextMonth)
    }

    fun forwardMonth() {
        currentMonth++
        if(currentMonth == 13) {
            currentYear++
            currentMonth = 1
        }
        currentMonthTextView.text = String.format("%d월", currentMonth)
        val prevMonth = currentMonth - 1
        val nextMonth = currentMonth + 1
        setPrevNextMonthTextBtn(prevMonth, nextMonth)
    }

    fun backwardMonth() {
        currentMonth--
        if(currentMonth == 0) {
            currentYear--
            currentMonth = 12
        }
        currentMonthTextView.text = String.format("%d월", currentMonth)
        val prevMonth = currentMonth - 1
        val nextMonth = currentMonth + 1
        setPrevNextMonthTextBtn(prevMonth, nextMonth)
    }

    fun setPrevNextMonthTextBtn(prevMonth: Int, nextMonth: Int) {
        prevMonthTextView.text = String.format("%d월", prevMonth)
        nextMonthTextView.text = String.format("%d월", nextMonth)

        if(currentMonth == 1) {
            prevMonthTextView.text = "12월"
        }
        if(currentMonth == 12) {
            nextMonthTextView.text = "1월"
        }
    }
}