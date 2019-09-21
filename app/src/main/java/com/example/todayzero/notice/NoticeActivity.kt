package com.example.todayzero.notice

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.example.todayzero.R
import com.example.todayzero.data.Notice
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.util.NetworkTask
import kotlinx.android.synthetic.main.notice_act.*
class NoticeActivity : AppCompatActivity() {

    private lateinit var noticeList: ArrayList<Notice>
    private lateinit var noticeAdapter: NoticeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notice_act)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initLayout()
        initData()

        network_refresh_button.setOnClickListener {
            initData() }
    }

    fun initData() {
        noticeList.clear()
        noticeAdapter.notifyDataSetChanged()
        showViews(true,false)
        val noticeDataRepository= NoticeRepository(noticeList)
        noticeDataRepository.initNotice(object:DataSource.LoadDataCallback{
            override fun onDataLoaded() {
                showViews(false,false)
                noticeAdapter.notifyDataSetChanged()
            }
            override fun onNetworkNotAvailable() {
                showViews(false,true)
            }
        })
    }
     fun showViews(showProgressView:Boolean,showNetworkView: Boolean) {
         no_network_view.visibility = if(showNetworkView) View.VISIBLE else View.GONE
        notice_list_view.visibility = if(showNetworkView) View.GONE else View.VISIBLE
         progress_circular.visibility =if(showProgressView) View.VISIBLE else View.GONE
    }
    fun initLayout() {

        noticeList = ArrayList()
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
                        showViews(false,true)
                    }
                },position)
                noticeDetailTask.execute()
            }
        }
    }

}
