package com.example.todayzero.data

import android.os.AsyncTask
import android.util.Log
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.findstore.StoreRepository
import com.example.todayzero.notice.NoticeRepository
import com.example.todayzero.util.NetworkTask
import org.json.JSONArray
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import com.example.todayzero.R

data class Store(val name:String, val addr:String, var locality:String, val type:String) {
    init {
        locality=addr.split(" ").get(1)
    }
    companion object{
        private const val baseUrl="https://www.zeropay.or.kr/intro/frncSrchList_json.do?tryCode=01&firstIndex="
        private const val originStoreNum=121788 //나중엔 db 객체 갯수로 바꾸기
        fun loadStore(storeList:ArrayList<Store>, callback: DataSource.ApiListener, scanArr:Array<Scanner>){
            for(scan in scanArr) {
                var result = "" //비동기처리로 변경하기
                while (scan.hasNextLine()) {
                    val line = scan.nextLine()
                    result += line
                }

                val storeRawTask =  NetworkTask(storeList,DataFilterType.STORE_RAW,result,callback,-1)
                storeRawTask.execute()
            }
            callback.onDataLoaded(DataFilterType.STORE_RAW)// callback 호출한 곳 가서 storelist 삭제하기 !
        }

        fun updateStore(storeList:ArrayList<Store>, callback: DataSource.ApiListener){
            lateinit var storeNumCallBack:DataSource.ApiListener

            class UpdateStoreList(var url:String): StoreRepository.StoreNumApiListener{

                override fun onDataLoaded(dataFilterType: DataFilterType) {

                }

                override fun onFailure(dataFilterType: DataFilterType) {
                    callback.onFailure(dataFilterType)
                }

                override fun onDataLoaded(storeNum: Int) {

                    var addNum=storeNum- originStoreNum
                    if(addNum==0){
                        callback.onDataLoaded(DataFilterType.STORE)
                    }
                    else{
                            for(i in 1 until (addNum/10)+2){
                                val startIdx = (((i-1)*10)+1)+originStoreNum  // 1 - 10 , 11 - 20
                                val endIdx = i*10+originStoreNum
                                val url = baseUrl + startIdx + "&" + "lastIndex="+endIdx
                                val storeUpdateTask =  NetworkTask(storeList,DataFilterType.STORE,url,callback,-1)
                                storeUpdateTask.execute() //execteOnExecutor() 는 순차적으로 호출함 , 쓰레드 풀 -> 병렬 처리
                                callback.onDataLoaded(DataFilterType.STORE_NUM)
                            }
                        }

                    }

            }
            val url=baseUrl+"1&lastIndex=10"
            storeNumCallBack=UpdateStoreList(url)
            val storeNumTask= NetworkTask(DataFilterType.STORE_NUM,url,storeNumCallBack)
            storeNumTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }
}