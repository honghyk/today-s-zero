package com.example.todayzero.findstore


import android.annotation.SuppressLint
import android.content.Context
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
import com.github.windsekirun.koreanindexer.KoreanIndexerListView
import java.util.*
import kotlin.collections.ArrayList


class StoreListFragment : Fragment() {
    lateinit var zeroList:ArrayList<String>
    lateinit var filterList:ArrayList<String>
    private var listView: KoreanIndexerListView? = null
    lateinit var search_adapter:ArrayAdapter<String>
    lateinit var dongName:String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.store_list_frag, container, false)
        var searchinput=root.findViewById<EditText>(R.id.search_store);
        listView = root.findViewById(R.id.store_list_view)
        searchinput.setOnFocusChangeListener { v, hasFocus ->
            if (v == searchinput)
                listView!!.setIndexerWidth(0)
            else
                listView!!.setIndexerWidth(20)
        }
        indexer(root)
        filterList = arrayListOf()

        searchinput.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                var filterText = s.toString()
                if (filterText.length > 0) {
                    filterList.clear()
                    for (i in zeroList) {
                        if (i.toLowerCase().contains(filterText.toLowerCase()))
                            addFilterList(i)
                    }
                    listView!!.adapter = AlphabetAdapter(filterList)
                    listView!!.setIndexerWidth(0)

                    Toast.makeText(context, ""+ filterList, Toast.LENGTH_LONG).show()
                } else {
                    listView!!.adapter = AlphabetAdapter(zeroList)
                    listView!!.setIndexerWidth(20)
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        return root
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(arguments!=null)
            dongName = arguments!!.getString("dongName")
    }

    override fun onResume() {
        super.onResume()
        (activity as StoreActivity).supportActionBar?.title=dongName
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
        for(store in StoreActivity.storeList){
            if(store.dong==dongName)
                zeroList.add(store.name)
        }

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

    inner class AlphabetAdapter internal constructor(var list:ArrayList<String>) :
        KoreanIndexerListView.KoreanIndexerAdapter<String>(context!!, list), Filterable {

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