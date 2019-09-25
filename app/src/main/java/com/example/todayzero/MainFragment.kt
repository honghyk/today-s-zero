package com.example.todayzero


import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.media.Image
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.todayzero.db.DBHelper
import com.example.todayzero.db.deal
import com.example.todayzero.expense.ExpenseActivity
import com.example.todayzero.util.DealAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_frag.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {

    companion object {
        lateinit var spentListView:RecyclerView
        lateinit var expenseTxt:TextView
    }
    lateinit var layoutManager: LinearLayoutManager
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

        Log.i("life","oncreateView")

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
            spentListView=findViewById(R.id.spent_list_view)
            expenseTxt=findViewById(R.id.userExpenseTxt)
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            spentListView.layoutManager=layoutManager

            currentMonthTextView = findViewById(R.id.current_month_text_view)
            prevMonthTextView = findViewById<TextView>(R.id.prev_month_text_button).apply {
                setOnClickListener { backwardMonth()
                    init()
                }
            }
            prevMonthBtn = findViewById<ImageButton>(R.id.prev_month_button).apply {
                setOnClickListener { backwardMonth()
                    init()
                }
            }
            nextMonthTextView = findViewById<TextView>(R.id.next_month_text_button).apply {
                setOnClickListener { forwardMonth()
                     init()
                }
            }
            nextMonthBtn = findViewById<ImageButton>(R.id.next_month_button).apply {
                setOnClickListener { forwardMonth()
                      init()
                }
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
        dbHelper=DBHelper(this.requireContext())
        init()
    }


    fun init() {
        userExpenseTxt.text=dbHelper.getUser().expenditure.toString()

       var date=if(currentMonth<10){"$currentYear.0$currentMonth"}else{"$currentYear.$currentMonth"}
        val dealList = dbHelper.getDeals(date)
        val adapter = DealAdapter(dealList)
        adapter.notifyDataSetChanged()
        spentListView.adapter = adapter
        adapter.itemClickListener=object:DealAdapter.OnItemClickListener{
            override fun onItemClick(holder: DealAdapter.ViewHolder, view: View, data: deal, position: Int) {
                val intent=Intent(requireContext(), ExpenseActivity::class.java)
                intent.putExtra(ExpenseActivity.UPDATE_DEAL_TAG,true)
                intent.putExtra(ExpenseActivity.UPDATE_DEAL_DATA_TAG,data)
                startActivity(intent)
            }
        }
    }


    fun initMonth() {
        val c = Calendar.getInstance()
        currentMonth = c.get(Calendar.MONTH)+1
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

        Log.i("setPrevNextBtn","click$currentYear/$currentMonth")

    }
}