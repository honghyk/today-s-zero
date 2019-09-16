package com.example.todayzero.notice

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.example.todayzero.R
import com.example.todayzero.data.Notice
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.data.source.NoticeRepository
import com.example.todayzero.util.NetworkTask
import kotlinx.android.synthetic.main.notice_act.*

class NoticeActivity : AppCompatActivity() {

    private lateinit var noticeList: ArrayList<Notice>
    private lateinit var noticeAdapter: NoticeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_act)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initData()

    }

    fun initData() {
        noticeList = ArrayList()
        val noticeDataRepository=NoticeRepository(noticeList)
        noticeDataRepository.initNotice(object:DataSource.LoadDataCallback{
            override fun onDataLoaded() {
                initLayout()
            }
            override fun onNetworkNotAvailable() {

            }
        })
    }

    fun initLayout() {
        noticeAdapter = NoticeAdapter(noticeList)
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        notice_list_view.layoutManager = layoutManager as RecyclerView.LayoutManager?
        notice_list_view.adapter = noticeAdapter

        noticeAdapter.itemClickListener = object : NoticeAdapter.OnItemClickListener {
            override fun onItemClick(holder: NoticeAdapter.ViewHolder, view: View, data: Notice, position: Int)
            {

                val url= "https://www.zeropay.or.kr/custCntr/notiMtrDetail.do?id="+noticeList[position].num
                val noticeDetailTask= NetworkTask(noticeList, DataFilterType.NOTICE_DETAIL,url,object :DataSource.ApiListener{

                    override fun onDataLoaded(dataFilterType: DataFilterType) {
                        noticeList[position].selected= !noticeList[position].selected
                        noticeAdapter.notifyDataSetChanged()

                    }
                    override fun onFailure(dataFilterType: DataFilterType) {
                       //네트워크 오류
                    }
                },position)
                noticeDetailTask.execute()
            }
        }
    }

}
