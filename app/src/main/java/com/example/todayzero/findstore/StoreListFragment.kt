package com.example.todayzero.findstore


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.todayzero.R
import com.example.todayzero.db.DBHelper
import com.github.windsekirun.koreanindexer.KoreanIndexerListView
import java.util.*
import kotlin.collections.ArrayList
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.store_list_frag.*
import com.example.todayzero.data.Store
import com.example.todayzero.util.replaceFragmentInActivity


class StoreListFragment : Fragment() {
    lateinit var zeroList: ArrayList<String>
    lateinit var filterList: ArrayList<String>
    private var listView: KoreanIndexerListView? = null
    lateinit var search_adapter:ArrayAdapter<String>
    lateinit var dongName:String
     var guNum:Int?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(com.example.todayzero.R.layout.store_list_frag, container, false)

        var searchinput = root.findViewById<EditText>(com.example.todayzero.R.id.search_store);
        listView = root.findViewById(com.example.todayzero.R.id.store_list_view)
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

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        searchinput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus == true)
                listView!!.setIndexerWidth(0)
            else
                listView!!.setIndexerWidth(20)
        }
        return root
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(arguments!=null) {
            dongName = arguments!!.getString("dongName")
            guNum=arguments!!.getInt("guNum")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as StoreActivity).supportActionBar?.title = dongName
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("StringConcatenationInLoop")
    fun addList(vararg text: String) {
        Collections.addAll(zeroList, *text)
    }

    fun addFilterList(vararg text: String) {
        Collections.addAll(filterList, *text)
    }


    fun indexer(root:View){
        zeroList = arrayListOf()

        for(store in StoreActivity.storeList[guNum!!]){
            if(store.dong==dongName)
                addList(store.name)

        }
        val adapter = AlphabetAdapter(zeroList)
        listView!!.setKeywordList(zeroList)
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            (requireActivity() as AppCompatActivity).replaceFragmentInActivity(StoreMapFragment.newInstance(
                Store(0, "서울시 은평구 갈현로23길 5 지하1층", "은평구", "ㅇㅇ동", "dong","type")),
                R.id.store_contentFrame)
        }
        listView!!.adapter = adapter
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