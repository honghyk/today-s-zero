package com.example.todayzero.findstore

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.todayzero.R
import com.example.todayzero.data.Store
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.util.replaceFragmentInActivity
import kotlinx.android.synthetic.main.store_act.*
import org.json.JSONArray
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class StoreActivity : AppCompatActivity() {

    private lateinit var storeList: ArrayList<Store>
    private lateinit var scannerList : Array<Scanner>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_act)

        setSupportActionBar(store_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initData()
        //구/동 선택 -> 가맹점 리스트 -> 선택한 가맹점 위치
        //GuDongFragment -> StoreListFragment -> StoreMapFragment 순서로 호출
        replaceFragmentInActivity(GuDongFragment(), R.id.store_contentFrame)
    }


    fun initData(){
        //속도에 따라서 들어갈 때 마다 파싱을 해야할지 검사해봐야할듓 ㅎㅂㅎ ,, ,, ,  storeList=ArrayList()부분 위치를 초기화 지점으로
        //db에 데이터가 있는지 없는지 검사
        /*
        * 있다 -> 가맹점 url 받아와서, 총 갯수 확인
        * ->갯수 일치함 -> 바로 화면 보여주기
        * -> 갯수 일치 안함 -> 해당 갯수만큼 파싱한 이후에 보여주기
        * 없다 -> (어플 처음깐걸 의미) storelist에서 파싱해서 디비에 저장 -> storelistt삭제 ->
        * */
        storeList= ArrayList()
        scannerList=arrayOf(Scanner(resources.openRawResource(R.raw.storelist1)),Scanner(resources.openRawResource(R.raw.storelist2)),Scanner(resources.openRawResource(R.raw.storelist3))
            ,Scanner(resources.openRawResource(R.raw.storelist4)),Scanner(resources.openRawResource(R.raw.storelist5)),Scanner(resources.openRawResource(R.raw.storelist6))
            ,Scanner(resources.openRawResource(R.raw.storelist7)),Scanner(resources.openRawResource(R.raw.storelist8)), Scanner(resources.openRawResource(R.raw.storelist9))
            ,Scanner(resources.openRawResource(R.raw.storelist10)),Scanner(resources.openRawResource(R.raw.storelist11)))

        val storeDataRepository= StoreRepository(storeList)
        storeDataRepository.initStore(scannerList,object:
            DataSource.LoadDataCallback{
            override fun onDataLoaded() {
                Log.i("test","데이터파싱 잘끝남  "+storeList.size)
                //창넘기기
            }
            override fun onNetworkNotAvailable() {
                //showViews(false,true) -> 넘어가되, 가맹점 정보 업데이트 실패했습니다. 업데이트를 위해서는, 네트워크 환경을 확인하시고,
                //다시 실행해달라는 토스트 메시지 띄우깅
            }
        })
    }

}
