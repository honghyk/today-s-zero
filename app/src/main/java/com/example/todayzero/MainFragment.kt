package com.example.todayzero


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todayzero.db.DBHelper
import kotlinx.android.synthetic.main.main_frag.*


class MainFragment : Fragment() {

    lateinit var dbHelper: DBHelper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_frag, container, false)
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
    }
}
