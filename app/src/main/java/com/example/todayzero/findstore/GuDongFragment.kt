package com.example.todayzero.findstore


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todayzero.R
import com.example.todayzero.util.replaceFragmentInActivity


class GuDongFragment : Fragment() {

    lateinit var zone: Map<Int, Int>
    lateinit var guList: Array<String>
    lateinit var guAdapter: GuAdapter
    lateinit var guView: RecyclerView
    lateinit var dongView: RecyclerView
     var dongList =ArrayList<String>()
    lateinit var dongAdapter: DongAdapter
    var guNum=0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        zone = mapOf(
            1 to R.array.gangnam, 2 to R.array.gangdong, 3 to R.array.gangbuk, 4 to R.array.gangseo, 5 to R.array.gwanak, 6 to R.array.gwangjin
            , 7 to R.array.guro, 8 to R.array.geumcheon, 9 to R.array.nowon, 10 to R.array.dobong, 11 to R.array.dongdaemun, 12 to R.array.dongjak
            , 13 to R.array.mapo, 14 to R.array.seodaemun, 15 to R.array.seocho, 16 to R.array.seongdong, 17 to R.array.seongbuk, 18 to R.array.songpa
            , 19 to R.array.yangcheon, 20 to R.array.yeongdeungpo, 21 to R.array.yongsan, 22 to R.array.eunpyeong, 23 to R.array.jongno,
            24 to R.array.jung, 25 to R.array.jungnang
        )

        val root = inflater.inflate(R.layout.gu_dong_frag, container, false)
        with(root) {
            guView = findViewById(R.id.guView)
            guList = resources.getStringArray(R.array.gu_name)
            guAdapter = GuAdapter(guList)
            val guLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            guView.layoutManager = guLayoutManager
            val dividerItemDecoration1 = DividerItemDecoration(requireContext(), guLayoutManager.orientation)
            guView.addItemDecoration(dividerItemDecoration1)
            guView.adapter = guAdapter
            guAdapter.itemClickListener = object : GuAdapter.OnItemClickListener {
                override fun onItemClick(
                    holder: GuAdapter.ViewHolder,
                    view: View,
                    data: String,
                    position: Int
                ) {
                    guNum=position
                    dongList.clear()
                    var dongArr = resources.getStringArray(zone[position+1]!!).sortedArray()
                    for (str in dongArr) {
                        dongList.add(str)
                    }
                    dongAdapter.notifyDataSetChanged()
                }
            }
            dongView = findViewById(R.id.dongView)
            var dongArr = resources.getStringArray(R.array.gangnam).sortedArray()
            dongList.clear()
            for (str in dongArr) {
                dongList.add(str)
            }
            dongAdapter = DongAdapter(dongList)
            val dongLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            dongView.layoutManager = dongLayoutManager
            val dividerItemDecoration = DividerItemDecoration(requireContext(), dongLayoutManager.orientation)
            dongView.addItemDecoration(dividerItemDecoration)
            dongView.adapter = dongAdapter
            dongAdapter.itemClickListener = object : DongAdapter.OnItemClickListener {
                override fun onItemClick(
                    holder: DongAdapter.ViewHolder,
                    view: View,
                    data: String,
                    position: Int
                ) {
                    (activity as StoreActivity).replaceFragmentInActivity(StoreListFragment(),R.id.store_contentFrame, data,guNum)
                }
            }
            (activity as StoreActivity).supportActionBar?.title = "가맹점 찾기"
        }
        return root
    }

}
