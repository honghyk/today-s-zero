package com.example.todayzero


import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.todayzero.db.DBHelper
import com.example.todayzero.db.deal
import com.example.todayzero.expense.ExpenseActivity
import com.example.todayzero.util.DealAdapter
import com.example.todayzero.util.NumberFormatter
import com.example.todayzero.util.replaceFragmentInActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainFragment : Fragment() {

    companion object {
        const val EXPENSE_TITLE = "월 사용 금액"
    }

    lateinit var benefitTextView:TextView
    lateinit var spentListView:RecyclerView
    lateinit var expenseTxt:TextView
    lateinit var expenseTxtTitle: TextView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var dealAdapter: DealAdapter
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
        dbHelper = DBHelper(requireContext())

        with(root) {
            benefitTextView=findViewById(R.id.benefit_text_view)
            spentListView = findViewById(R.id.spent_list_view)
            expenseTxt = findViewById(R.id.userExpenseTxt)
            expenseTxtTitle = findViewById(R.id.expenseTxtTitle)

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            spentListView.layoutManager = layoutManager
            dealAdapter = DealAdapter(ArrayList(), requireContext())
            spentListView.adapter = dealAdapter
            val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
            spentListView.addItemDecoration(dividerItemDecoration)

            dealAdapter.itemClickListener = object : DealAdapter.OnItemClickListener {
                override fun onItemClick(holder: DealAdapter.ViewHolder, view: View, data: deal, position: Int) {
                    val intent = Intent(requireContext(), ExpenseActivity::class.java)
                    intent.putExtra(ExpenseActivity.UPDATE_DEAL_TAG, true)
                    intent.putExtra(ExpenseActivity.UPDATE_DEAL_DATA_TAG, data)
                    startActivity(intent)
                }
            }

            currentMonthTextView = findViewById(R.id.current_month_text_view)
            prevMonthTextView = findViewById<TextView>(R.id.prev_month_text_button).apply {
                setOnClickListener {
                    backwardMonth()
                    loadMonthlyDealList()
                }
            }
            prevMonthBtn = findViewById<ImageButton>(R.id.prev_month_button).apply {
                setOnClickListener {
                    backwardMonth()
                    loadMonthlyDealList()
                }
            }
            nextMonthTextView = findViewById<TextView>(R.id.next_month_text_button).apply {
                setOnClickListener {
                    forwardMonth()
                    loadMonthlyDealList()
                }
            }
            nextMonthBtn = findViewById<ImageButton>(R.id.next_month_button).apply {
                setOnClickListener {
                    forwardMonth()
                    loadMonthlyDealList()
                }
            }

        }

        initMonth()

        return root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().actionBar?.title = resources.getString(R.string.app_name)

        loadMonthlyDealList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val userName = dbHelper.getUser().uname
        val navView = requireActivity().findViewById<NavigationView>(R.id.nav_view)
        val navHeaderView = navView.getHeaderView(0)
        val navUserNameTextView = navHeaderView.findViewById<TextView>(R.id.nav_header_user_text)
        navUserNameTextView.text = String.format("%s님 안녕하세요", userName)
        val navModifyUserTextView = navHeaderView.findViewById<TextView>(R.id.nav_modify_text)
        navModifyUserTextView.setOnClickListener {
            requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            loadUserDataAndShowWelcomeFrag()
        }
    }

    private fun loadUserDataAndShowWelcomeFrag() {
        //사용자 이름, 총 급여 정보를  데이터베이스에서 불러옴
        val name: String = dbHelper.getUser().uname
        val income: Long = dbHelper.getUser().income.toLong()

        showWelcomeFragToModifyUserData(name, income)
    }

    private fun showWelcomeFragToModifyUserData(name: String, income: Long) {
        val welcomeFragment = WelcomeFragment.newInstance(name, income).also {
            (requireActivity() as AppCompatActivity).replaceFragmentInActivity(it, R.id.main_contentFrame)
        }
    }

    fun loadMonthlyDealList() {
        var date = if (currentMonth < 10) "$currentYear.0$currentMonth" else "$currentYear.$currentMonth"
        var dealList = dbHelper.getDeals(date)

        dealAdapter.items = dealList
        dealAdapter.notifyDataSetChanged()

        expenseTxtTitle.text = currentMonth.toString() + EXPENSE_TITLE
        var expense = dbHelper.getExpense(date)
        expenseTxt.text = NumberFormatter.format(expense.toString()) + "원"


        var total_expense=dbHelper.getTotalExpense(currentYear)
        var benefit_expense = 0L
        var over_expense = 0L
        if (total_expense < ((dbHelper.getUser().income.toLong()) / 4))
            benefitTextView.text = "약 0원"
        else {
            over_expense=dbHelper.getZeroPayExpense(currentYear)
            if(over_expense > dbHelper.getUser().income.toLong() / 4)
                over_expense = over_expense - dbHelper.getUser().income.toLong() / 4
            if((over_expense  * 4) / 10 > 4000000)
                over_expense = 4000000
            else
                over_expense = (over_expense  * 4) / 10

            benefit_expense =
                dbHelper.getUser().income.toLong() - over_expense

            var final_benefit = calculate_tax(benefit_expense)

            var normal_tax = calculate_tax(dbHelper.getUser().income.toLong())

            benefitTextView.text = "약 " + NumberFormatter.format((normal_tax - final_benefit).toString()) + "원"
       }
    }
//300000
    fun calculate_tax(benefit: Long): Long {
        var tax = 0L
        if (benefit <= 12000000) {
            tax = (benefit * 6) / 100
        } else if (benefit > 12000000 && benefit <= 46000000) {
            tax = 720000 + (((benefit - 12000000) * 15) / 100)
        }else if(benefit > 46000000&&benefit<=88000000){
            tax = 720000 + 5100000 + (((benefit - 46000000) * 24) / 100)
        }else if(benefit > 88000000&&benefit<=150000000){
            tax = 720000 + 5100000 + 10080000 + (((benefit - 88000000) * 35) / 100)
        }else{
            tax = 720000 + 5100000 + 10080000 + 21700000 + (((benefit - 150000000) * 38) / 100)
        }
        return tax
    }


    fun initMonth() {
        val c = Calendar.getInstance()
        currentMonth = c.get(Calendar.MONTH) + 1
        currentYear = c.get(Calendar.YEAR)

        currentMonthTextView.text = String.format("%d월", currentMonth)
        val prevMonth = currentMonth - 1
        val nextMonth = currentMonth + 1

        setPrevNextMonthTextBtn(prevMonth, nextMonth)
    }

    fun forwardMonth() {
        currentMonth++
        if (currentMonth == 13) {
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
        if (currentMonth == 0) {
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

        if (currentMonth == 1) {
            prevMonthTextView.text = "12월"
        }
        if (currentMonth == 12) {
            nextMonthTextView.text = "1월"
        }


    }
}