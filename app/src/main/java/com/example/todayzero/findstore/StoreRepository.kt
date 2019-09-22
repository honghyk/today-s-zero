package com.example.todayzero.findstore

import com.example.todayzero.data.Store
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.data.source.DataSource
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


class StoreRepository(val storeList:ArrayList<Store> ): DataSource {



    fun initStore(scanArr:Array<InputStream>, callback: DataSource.LoadDataCallback){
        class ApiListener: DataSource.ApiListener {
            private var networkState=true
            private var isInitStore=false
            private var isUpdateStore=false
            private var count=0
            override fun onDataLoaded(dataFilterType: DataFilterType) {
                if(dataFilterType== DataFilterType.STORE_NUM) count++
                if(dataFilterType== DataFilterType.STORE_RAW) {
                    count--
                    if(count==0)  isInitStore=true
                }
//                if(dataFilterType== DataFilterType.STORE) {
//                    count--
//                    if(count==0)  isUpdateStore=true
//                }
                isUpdateStore=true //임시로 해놓은 것임!
                if(isInitStore && isUpdateStore) callback.onDataLoaded()
            }

            override fun onFailure(dataFilterType: DataFilterType) {
                if(networkState){
                    networkState=false
                    callback.onNetworkNotAvailable()
                }
            }
        }
        val apiListener=ApiListener()
        //  Store.updateStore(storeList,apiListener)
        Store.loadStore(storeList,apiListener,scanArr)
    }
    interface StoreNumApiListener: DataSource.ApiListener {
        override fun onDataLoaded(dataFilterType: DataFilterType) {}
        override fun onFailure(dataFilterType: DataFilterType) {}
        fun onDataLoaded(storeNum:Int)
    }
}