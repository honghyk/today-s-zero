package com.example.todayzero.findstore


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.todayzero.R
import com.example.todayzero.db.DBHelper
import com.github.windsekirun.koreanindexer.KoreanIndexerListView
import com.google.android.libraries.places.internal.i
import kotlinx.android.synthetic.main.list_item.*
import kotlinx.android.synthetic.main.notice_act.*
import kotlinx.android.synthetic.main.store_list_frag.*
import kotlinx.android.synthetic.main.store_list_frag.view.*
import org.apache.commons.lang3.ArrayUtils.add
import java.util.*
import kotlin.collections.ArrayList
import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.support.v4.content.ContextCompat.getSystemService
import android.app.Activity


class StoreListFragment : Fragment() {
    lateinit var zeroList: ArrayList<String>
    lateinit var filterList: ArrayList<String>
    private var listView: KoreanIndexerListView? = null
    lateinit var search_adapter: ArrayAdapter<String>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(com.example.todayzero.R.layout.store_list_frag, container, false)

        var searchinput = root.findViewById<EditText>(com.example.todayzero.R.id.search_store);
        listView = root.findViewById(com.example.todayzero.R.id.store_list_view)
        val DB: DBHelper

        indexer(root)
        filterList = arrayListOf()

        searchinput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var filterText = s.toString()
                if (filterText.length > 0) {
                    filterList.clear()
                    for (i in zeroList) {
                        if (i.toLowerCase().contains(filterText.toLowerCase()))
                            addFilterList(i)
                    }
                    listView!!.adapter = AlphabetAdapter(filterList)
                    Toast.makeText(context, "" + filterList, Toast.LENGTH_LONG).show()
                } else {
                    listView!!.adapter = AlphabetAdapter(zeroList)
                    search_layout.setFocusableInTouchMode(true)
                    search_layout.requestFocus()
                    val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm!!.hideSoftInputFromWindow(searchinput.getWindowToken(), 0) // 키보드 숨기기
                }
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        searchinput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus == true)
                listView!!.setIndexerWidth(0)
            else
                listView!!.setIndexerWidth(20)
        }
        //선택한 동 이름으로
        //requireActivity().actionBar!!.title = ""
        return root
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("StringConcatenationInLoop")
    fun addList(vararg text: String) {
        Collections.addAll(zeroList, *text)
    }

    fun addFilterList(vararg text: String) {
        Collections.addAll(filterList, *text)
    }

    fun indexer(root: View) {

        zeroList = arrayListOf()
        //일단 임의로 넣어놓음..
        addList(
            "가나다라", "가", "나", "다람쥐", "호랑이", "리스트", "마바", "바보바보바보바보바보바보바보바보바보바보바보바보바보바보",
            "사아", "아랑", "타타타타타타타타", "스크롤스크롤스크롤",
            "파파파파파파", "자차", "하하", "ABC", "BC", "C", "D", "F", "G", "I", "J", "K", "L",
            "사자", "개구리", "노랑이", "초록이", "하양이", "차", "자동차", "M", "N", "O", "P",
            "Q", "R", "S", "?", "!", "1", "2", "5", "4"
        )
        addList("가나다라", "4")
        //Toast.makeText(context,"" + zeroList, Toast.LENGTH_LONG).show()

        //var adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, zeroList)
        //listView.adapter = adapter


        val adapter = AlphabetAdapter(zeroList)
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

    inner class AlphabetAdapter internal constructor(var list: ArrayList<String>) :
        KoreanIndexerListView.KoreanIndexerAdapter<String>(context!!, list), Filterable {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(com.example.todayzero.R.layout.list_item, parent, false)
                holder = ViewHolder()
                holder.txtName = convertView!!.findViewById(com.example.todayzero.R.id.txtName)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }

            holder.txtName!!.setText(list.get(position))

            return convertView
        }

        override fun getCount(): Int {
            return list.size
        }

        internal inner class ViewHolder {
            var txtName: TextView? = null
        }
    }


}