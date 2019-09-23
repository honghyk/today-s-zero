package com.example.todayzero.findstore

import android.graphics.Color
import android.graphics.ColorSpace
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.todayzero.R
import com.google.android.libraries.places.internal.i

class GuAdapter(var items:Array<String>):RecyclerView.Adapter<GuAdapter.ViewHolder>() {
    var itemClickListener:OnItemClickListener?=null
     var mSelectedItem = 0


    interface OnItemClickListener  {
        fun onItemClick(holder: ViewHolder, view: View, data: String, position: Int)
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.gu_item, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.gu.text=items[p1]
        p0.itemView.isSelected=if(p1==mSelectedItem) true else false
       }


    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var gu: TextView
        var check:ImageView
        init{
            gu=itemView.findViewById(R.id.guText)
            check=itemView.findViewById(R.id.guSelImageView)
            itemView.setOnClickListener {
                val position=adapterPosition
                mSelectedItem=position
                notifyDataSetChanged()
                itemClickListener?.onItemClick(this,it,items[position],position)
            }
        }

    }
}