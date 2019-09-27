package com.example.todayzero.data


import android.app.Activity
import android.os.AsyncTask
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.db.DBHelper
import com.example.todayzero.findstore.StoreActivity
import com.example.todayzero.findstore.StoreRepository
import com.example.todayzero.util.NetworkTask
import java.io.*
import kotlin.collections.ArrayList


data class Store(
    var sid:Int,
    val name: String,
    val addr: String,
    var locality: String,
    var dong: String,
    val type: String
) {
    init {
        var startIdx = addr.indexOf("(")
        if (startIdx != -1) {
            var endIdx = addr.indexOf(")")
            dong = addr.slice(IntRange(startIdx + 1, endIdx - 1))
            if (dong.length > 1) {
                endIdx = dong.indexOf(",")
                if (endIdx != -1) dong = dong.substring(IntRange(0, endIdx - 1))
                endIdx = dong.indexOf(" ")
                if (endIdx != -1) dong = dong.substring(IntRange(0, endIdx - 1))
            }
        }
    }

    companion object {
        private const val baseUrl =
            "https://www.zeropay.or.kr/intro/frncSrchList_json.do?tryCode=01&firstIndex="
        private const val originStoreNum = 121788 //나중엔 db 객체 갯수로 바꾸기

        fun loadStore(
            storeList: ArrayList<ArrayList<Store>>,
            callback: DataSource.ApiListener,
            scanArr: Array<InputStream>
        ) {
            val storeRawTask=NetworkTask(storeList, DataFilterType.STORE_RAW, scanArr,callback)
            storeRawTask.execute()
        }

        fun updateStore(storeList: ArrayList<Store>, callback: DataSource.ApiListener) {
            lateinit var storeNumCallBack: DataSource.ApiListener

            class UpdateStoreList(var url: String) : StoreRepository.StoreNumApiListener {

                override fun onDataLoaded(dataFilterType: DataFilterType) {

                }

                override fun onFailure(dataFilterType: DataFilterType) {
                    callback.onFailure(dataFilterType)
                }

                override fun onDataLoaded(storeNum: Int) {

                    var addNum = storeNum - originStoreNum
                    if (addNum == 0) {
                        callback.onDataLoaded(DataFilterType.STORE)
                    } else {
                        for (i in 1 until (addNum / 10) + 2) {
                            val startIdx =
                                (((i - 1) * 10) + 1) + originStoreNum  // 1 - 10 , 11 - 20
                            val endIdx = i * 10 + originStoreNum
                            val url = baseUrl + startIdx + "&" + "lastIndex=" + endIdx
                            val storeUpdateTask =
                                NetworkTask(storeList, DataFilterType.STORE, url, callback, -1)
                            storeUpdateTask.execute() //execteOnExecutor() 는 순차적으로 호출함 , 쓰레드 풀 -> 병렬 처리
                            callback.onDataLoaded(DataFilterType.STORE_NUM)
                        }
                    }
                }
            }

            val url = baseUrl + "1&lastIndex=10"
            storeNumCallBack = UpdateStoreList(url)
            val storeNumTask = NetworkTask(DataFilterType.STORE_NUM, url, storeNumCallBack)
            storeNumTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }
}