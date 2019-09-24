package com.example.todayzero.findstore

import com.example.todayzero.data.Store
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.db.DBHelper
import java.io.InputStream
import kotlin.collections.ArrayList


class StoreRepository(val storeList:ArrayList<ArrayList<Store>> ): DataSource {

    fun initStore(scanArr:Array<InputStream>,callback: DataSource.LoadDataCallback){
        class ApiListener: DataSource.ApiListener {
            private var networkState=true
            override fun onDataLoaded(dataFilterType: DataFilterType) {
                callback.onDataLoaded()
            }

            override fun onFailure(dataFilterType: DataFilterType) {
                if(networkState){
                    networkState=false
                    callback.onNetworkNotAvailable()
                }
            }
        }
        val apiListener=ApiListener()
        Store.loadStore(storeList,apiListener,scanArr)
    }
    interface StoreNumApiListener: DataSource.ApiListener {
        override fun onDataLoaded(dataFilterType: DataFilterType) {}
        override fun onFailure(dataFilterType: DataFilterType) {}
        fun onDataLoaded(storeNum:Int)
    }
}