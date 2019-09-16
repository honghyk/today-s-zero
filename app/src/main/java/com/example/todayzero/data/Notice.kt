package com.example.todayzero.data

import android.os.AsyncTask
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.notice.NoticeRepository
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.util.NetworkTask

data class Notice(val title:String, var content:String, val data:String, val num:String, var selected:Boolean=false) {

    companion object{
        private const val baseUrl="https://www.zeropay.or.kr/custCntr/notiMtrList.do?pageIndex="
        private const val detailUrl="https://www.zeropay.or.kr/custCntr/notiMtrDetail.do?id="

        fun loadNotice(noticeList:ArrayList<Notice>,callback:DataSource.ApiListener){
            lateinit var noticeNumCallBack:DataSource.ApiListener

            class LoadNoticeList(var url:String): NoticeRepository.NoticeNumApiListener{

                override fun onDataLoaded(dataFilterType: DataFilterType) {

                }

                override fun onFailure(dataFilterType: DataFilterType) {
                    callback.onFailure(DataFilterType.NOTICE_NUM)
                }

                override fun onDataLoaded(fixedNoticeNum: Int) {
                    callback.onDataLoaded(DataFilterType.NOTICE_NUM)
                    for(i in 1 until 6){
                        url=baseUrl+ i
                        val noticeTask=NetworkTask(noticeList,DataFilterType.NOTICE,url,callback,fixedNoticeNum)
                        noticeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                    }
                }
            }
            val url=baseUrl+"6"
            noticeNumCallBack=LoadNoticeList(url)
            val noticeNumTask=NetworkTask(noticeList,DataFilterType.NOTICE_NUM,url,noticeNumCallBack,-1)
            noticeNumTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        fun loadDetailNotice(noticeList:ArrayList<Notice>,callback:DataSource.ApiListener){
            var count=0
            for(notice in noticeList){
                val url= detailUrl+notice.num
                val noticeDetailTask=NetworkTask(noticeList,DataFilterType.NOTICE_DETAIL,url,callback,count)
                noticeDetailTask.execute()
                count++
            }
        }
    }
}