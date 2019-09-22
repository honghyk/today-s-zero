package com.example.todayzero.findstore

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.todayzero.R

class DongAdapter(var items:ArrayList<String>): RecyclerView.Adapter<DongAdapter.ViewHolder>() {
    var itemClickListener:OnItemClickListener?=null
    interface OnItemClickListener  {
        fun onItemClick(holder: ViewHolder, view: View, data: String, position: Int)
    }

    override fun onBindViewHolder(p0: DongAdapter.ViewHolder, p1: Int) {
        p0.dong.text=items[p1]
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DongAdapter.ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.dong_item, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var dong: TextView
        init{
            dong=itemView.findViewById(R.id.dongText)
            itemView.setOnClickListener {
                val position=adapterPosition
                itemClickListener?.onItemClick(this,it,items[position],position)
            }
        }

    }
}