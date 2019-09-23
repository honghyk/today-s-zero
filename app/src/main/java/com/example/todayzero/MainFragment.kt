package com.example.todayzero


import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todayzero.expense.ExpenseActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.example.todayzero.db.DBHelper
import com.example.todayzero.util.DealAdapter
import kotlinx.android.synthetic.main.main_frag.*


class MainFragment : Fragment() {

    lateinit var dbHelper: DBHelper
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

        return root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().actionBar?.title = resources.getString(R.string.app_name)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbHelper= DBHelper(this.requireContext())
        init()
    }

    fun init(){
        // user정보 등록 후
        //userBalanceTxt.text=dbHelper.getUser().balance.toString()
        val layoutManager=LinearLayoutManager(this.requireContext(),RecyclerView.VERTICAL,false)
        val dealList=dbHelper.getDeals()
        val adapter=DealAdapter(dealList)
        spent_list_view.layoutManager=layoutManager
        spent_list_view.adapter=adapter
        adapter.notifyDataSetChanged()

    }
}