package com.example.todayzero.findstore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ProgressBar
import com.example.todayzero.R
import com.example.todayzero.data.Store
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.db.DBHelper
import com.example.todayzero.util.NetworkTask
import com.example.todayzero.util.replaceFragInActNotAddToBackStack
import kotlinx.android.synthetic.main.store_act.*
import java.io.InputStream

class StoreActivity : AppCompatActivity() {

    companion object{
        lateinit var storeList: ArrayList<ArrayList<Store>>
    }
    lateinit var dbHelper: DBHelper
    private lateinit var fisList: Array<InputStream>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_act)

        setSupportActionBar(store_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initData()
        //구/동 선택 -> 가맹점 리스트 -> 선택한 가맹점 위치
        //GuDongFragment -> StoreListFragment -> StoreMapFragment 순서로 호출

    }


    fun initData() {
        //속도에 따라서 들어갈 때 마다 파싱을 해야할지 검사해봐야할듓 ㅎㅂㅎ ,, ,, ,  storeList=ArrayList()부분 위치를 초기화 지점으로
        //db에 데이터가 있는지 없는지 검사
        /*
        * 있다 -> 가맹점 url 받아와서, 총 갯수 확인
        * ->갯수 일치함 -> 바로 화면 보여주기
        * -> 갯수 일치 안함 -> 해당 갯수만큼 파싱한 이후에 보여주기
        * 없다 -> (어플 처음깐걸 의미) storelist에서 파싱해서 디비에 저장 -> storelistt삭제 ->
        * */
        dbHelper = DBHelper(this)
        storeList = ArrayList<ArrayList<Store>>()
        progress_circular.visibility = ProgressBar.VISIBLE
        if (dbHelper.getStores("중랑구").isEmpty()) {
            for (i in 0..24) storeList.add(ArrayList())
            val assetManager = applicationContext.assets
            fisList = arrayOf(
                assetManager.open("storelist1.json"),
                assetManager.open("storelist2.json"),
                assetManager.open("storelist3.json"),
                assetManager.open("storelist4.json"),
                assetManager.open("storelist5.json"),
                assetManager.open("storelist6.json"),
                assetManager.open("storelist7.json"),
                assetManager.open("storelist8.json"),
                assetManager.open("storelist9.json"),
                assetManager.open("storelist10.json"),
                assetManager.open("storelist11.json")
            )
            val storeDataRepository = StoreRepository(storeList)
            storeDataRepository.initStore(fisList, object :
                DataSource.LoadDataCallback {
                override fun onDataLoaded() {

                    for (storeArr in storeList) {
                        dbHelper.insertStores(storeArr)
                    }
                    progress_circular.visibility = ProgressBar.GONE
                    replaceFragInActNotAddToBackStack(GuDongFragment(), R.id.store_contentFrame)
                }

                override fun onNetworkNotAvailable() {
                }
            })
        } else {
            val storeDBTask= NetworkTask(storeList, dbHelper,DataFilterType.STORE_DB,object:DataSource.ApiListener{
                override fun onDataLoaded(dataFilterType: DataFilterType) {
                    progress_circular.visibility = ProgressBar.GONE
                    replaceFragInActNotAddToBackStack(GuDongFragment(), R.id.store_contentFrame)
                }

                override fun onFailure(dataFilterType: DataFilterType) {}
            })
            storeDBTask.execute()
        }

    }
}
