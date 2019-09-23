package com.example.todayzero.findstore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ProgressBar
import com.example.todayzero.R
import com.example.todayzero.data.Store
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.util.replaceFragInActNotAddToBackStack
import kotlinx.android.synthetic.main.store_act.*
import java.io.InputStream

class StoreActivity : AppCompatActivity() {

    companion object{
        lateinit var storeList: ArrayList<Store>
    }
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
        storeList = ArrayList()
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
        progress_circular.visibility=ProgressBar.VISIBLE
        storeDataRepository.initStore(fisList, object :
            DataSource.LoadDataCallback {
            override fun onDataLoaded() {
                progress_circular.visibility=ProgressBar.GONE
                replaceFragInActNotAddToBackStack(GuDongFragment(), R.id.store_contentFrame)
            }

            override fun onNetworkNotAvailable() {
                //showViews(false,true) -> 넘어가되, 가맹점 정보 업데이트 실패했습니다. 업데이트를 위해서는, 네트워크 환경을 확인하시고,
                //다시 실행해달라는 토스트 메시지 띄우깅
            }
        })
    }

}
