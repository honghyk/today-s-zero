package com.example.todayzero


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup



class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_frag, container, false)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().actionBar?.title = resources.getString(R.string.app_name)
    }


}
