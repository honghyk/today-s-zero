package com.example.todayzero.findstore


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.todayzero.R


class StoreListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        //선택한 동 이름으로
        requireActivity().actionBar!!.title = ""

        return inflater.inflate(R.layout.store_list_frag, container, false)
    }


}
