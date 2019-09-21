package com.example.todayzero.findstore


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.todayzero.R
import com.github.windsekirun.koreanindexer.KoreanIndexerListView
import org.apache.commons.lang3.ArrayUtils.add
import java.util.*
import kotlin.collections.ArrayList


class StoreListFragment : Fragment() {
    lateinit var zeroList:ArrayList<String>
    private var listView: KoreanIndexerListView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.store_list_frag, container, false)
        listView = root.findViewById(R.id.store_list_view)
        indexer(root)

        //선택한 동 이름으로
        //requireActivity().actionBar!!.title = ""

        return root
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("StringConcatenationInLoop")
    fun addList(vararg text: String) {
        Collections.addAll(zeroList, *text)
    }
    fun indexer(root:View){

        zeroList = arrayListOf()
        //일단 임의로 넣어놓음..
        addList(
            "가나다라", "가", "나", "다람쥐", "호랑이", "리스트", "마바", "바보바보바보바보바보바보바보바보바보바보바보바보바보바보",
            "사아", "아랑", "타타타타타타타타", "스크롤스크롤스크롤",
            "파파파파파파", "자차", "하하", "ABC", "BC", "C", "D", "F", "G", "I", "J", "K", "L",
            "사자", "개구리", "노랑이", "초록이", "하양이", "차", "자동차", "M", "N", "O", "P",
            "Q", "R", "S", "?", "!", "1", "2", "5","4"
        )
        addList("가나다라", "4")
        //Toast.makeText(context,"" + zeroList, Toast.LENGTH_LONG).show()
        
        //var adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, zeroList)
        //listView.adapter = adapter


        val adapter = AlphabetAdapter()
        listView!!.setKeywordList(zeroList)
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(
                context,
                "clicked -> $i",
                Toast.LENGTH_SHORT
            ).show()
        }
        listView!!.setAdapter(adapter)
    }

    inner class AlphabetAdapter internal constructor() :
        KoreanIndexerListView.KoreanIndexerAdapter<String>(context!!, zeroList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
                holder = ViewHolder()
                holder.txtName = convertView!!.findViewById(R.id.txtName)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }

            holder.txtName!!.setText(zeroList.get(position))

            return convertView
        }

        override fun getCount(): Int {
            return zeroList.size
        }

        internal inner class ViewHolder {
            var txtName: TextView? = null
        }
    }



}