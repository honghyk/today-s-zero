package com.example.todayzero.notice

import com.example.todayzero.data.Notice
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.data.source.DataSource

class NoticeRepository(val noticeList:ArrayList<Notice> ):
    DataSource {


    fun initNotice(callback: DataSource.LoadDataCallback){
        class ApiListener: DataSource.ApiListener {
            private var count=0
            private var networkState=true
            override fun onDataLoaded(dataFilterType: DataFilterType) {
                if(dataFilterType== DataFilterType.NOTICE) count++
                if(count==5) callback.onDataLoaded()
            }

            override fun onFailure(dataFilterType: DataFilterType) {
                if(networkState){
                    networkState=false
                    callback.onNetworkNotAvailable()
                }
            }
        }
        val apiListener=ApiListener()
        Notice.loadNotice(noticeList,apiListener)
    }
    interface NoticeNumApiListener: DataSource.ApiListener {
        override fun onDataLoaded(dataFilterType: DataFilterType) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onFailure(dataFilterType: DataFilterType) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        fun onDataLoaded(fixedNoticeNum:Int)
    }

}