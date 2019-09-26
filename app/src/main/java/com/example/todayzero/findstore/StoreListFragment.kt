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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.todayzero.R
import com.example.todayzero.data.Store
import com.example.todayzero.db.DBHelper
import com.example.todayzero.util.replaceFragmentInActivity
import com.github.windsekirun.koreanindexer.KoreanIndexerListView
import com.google.android.libraries.places.internal.i
import kotlinx.android.synthetic.main.store_list_frag.*
import java.util.*


class StoreListFragment : Fragment() {
    lateinit var zeroListName:ArrayList<String>
    lateinit var zeroList: ArrayList<Store>
    lateinit var filterList: ArrayList<String>
    lateinit var filterListInfo:ArrayList<Store>
    lateinit var dbHelper: DBHelper
    private var listView: KoreanIndexerListView? = null
    lateinit var search_adapter: ArrayAdapter<String>
    lateinit var dongName: String
    var guNum: Int? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(com.example.todayzero.R.layout.store_list_frag, container, false)
        dbHelper = DBHelper(requireContext())
        var searchinput = root.findViewById<EditText>(com.example.todayzero.R.id.search_store);
        listView = root.findViewById(com.example.todayzero.R.id.store_list_view)
        indexer(root)
        filterList = arrayListOf()
        filterListInfo = arrayListOf()

        searchinput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var filterText = s.toString()
                if (filterText.length > 0) {
                    listView!!.setIndexerWidth(0)
                    filterList.clear()
                    filterListInfo.clear()
                    var i = 0
                    while (i < zeroListName.size) {
                        if (zeroListName[i].toLowerCase().contains(filterText.toLowerCase())){
                            filterList.add(zeroListName[i])
                            filterListInfo.add((zeroList[i]))
                        }
                        i++
                    }
                    listView!!.adapter = AlphabetAdapter(filterList)
                    listView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                        var item = adapterView.adapter.getItem(i).toString()

                        var store_info = dbHelper.findStores(guNum!! + 1, item, filterListInfo[i].addr)
                        //Toast.makeText(context, store_info[0].name, Toast.LENGTH_LONG).show()

                        //Toast.makeText(context, store_info.name + store_info.addr, Toast.LENGTH_LONG).show()
                        (requireActivity() as AppCompatActivity).replaceFragmentInActivity(
                            StoreMapFragment.newInstance(
                                store_info[0]
                            ),
                            R.id.store_contentFrame
                        )
                    }
                    //Toast.makeText(context, "" + filterList, Toast.LENGTH_LONG).show()
                } else {
                    listView!!.adapter = AlphabetAdapter(zeroListName)
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
        if (arguments != null) {
            dongName = arguments!!.getString("dongName")
            guNum = arguments!!.getInt("guNum")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as StoreActivity).supportActionBar?.title = dongName
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("StringConcatenationInLoop")

    fun indexer(root: View) {
        zeroList = arrayListOf()
        zeroListName = arrayListOf()
        for (store in StoreActivity.storeList[guNum!!]) {
            if (store.dong == dongName)
                zeroList.add(store)
        }
        for(i in zeroList)
            zeroListName.add(i.name)
        val adapter = AlphabetAdapter(zeroListName)
        listView!!.setKeywordList(zeroListName)
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            var item = adapterView.adapter.getItem(i).toString()

            var store_info = dbHelper.findStores(guNum!! + 1, item, zeroList[i].addr)
            //Toast.makeText(context, store_info[0].name, Toast.LENGTH_LONG).show()

            //Toast.makeText(context, store_info.name + store_info.addr, Toast.LENGTH_LONG).show()
            (requireActivity() as AppCompatActivity).replaceFragmentInActivity(
                StoreMapFragment.newInstance(
                    store_info[0]
                ),
                R.id.store_contentFrame
            )
        }
        listView!!.adapter = adapter
    }

    inner class AlphabetAdapter internal constructor(var list: ArrayList<String>) :
        KoreanIndexerListView.KoreanIndexerAdapter<String>(context!!, list), Filterable {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder

            if (convertView == null) {
                convertView =
                    LayoutInflater.from(context).inflate(com.example.todayzero.R.layout.list_item, parent, false)
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