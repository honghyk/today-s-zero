package com.example.todayzero.findstore

import android.graphics.ColorSpace
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.todayzero.R

class GuAdapter(var items:Array<String>):RecyclerView.Adapter<GuAdapter.ViewHolder>() {
    var itemClickListener:OnItemClickListener?=null
    interface OnItemClickListener  {
        fun onItemClick(holder: ViewHolder, view: View, data: String, position: Int)
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.gu_item, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.gu.text=items[p1]
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
                itemClickListener?.onItemClick(this,it,items[position],position)
            }
        }

    }
}