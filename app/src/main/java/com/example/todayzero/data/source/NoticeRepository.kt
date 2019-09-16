package com.example.todayzero.data.source

import com.example.todayzero.data.Notice
import com.example.todayzero.data.Store

class NoticeRepository(val noticeList:ArrayList<Notice> ):DataSource {


    fun initNotice(callback: DataSource.LoadDataCallback){
        class ApiListener:DataSource.ApiListener{
            private var count=0
            private var networkState=true
            override fun onDataLoaded(dataFilterType: DataFilterType) {
                if(dataFilterType==DataFilterType.NOTICE) count++
//                if(count==5) {
//                    Notice.loadDetailNotice(noticeList, this)
//                    count=0
//                }
//                if(dataFilterType==DataFilterType.NOTICE_DETAIL) dCount++
//                if(dCount==noticeList.size)
//                    callback.onDataLoaded()
                if(count==5) callback.onDataLoaded()
            }

            override fun onFailure(dataFilterType: DataFilterType) {
                if(true){
                    networkState=false
                    callback.onNetworkNotAvailable()
                }
            }
        }
        val apiListener=ApiListener()
        Notice.loadNotice(noticeList,apiListener)
    }
    interface NoticeNumApiListener:DataSource.ApiListener{
        override fun onDataLoaded(dataFilterType: DataFilterType) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onFailure(dataFilterType: DataFilterType) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        fun onDataLoaded(fixedNoticeNum:Int)
    }

}