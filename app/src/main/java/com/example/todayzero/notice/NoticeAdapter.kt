package com.example.todayzero.notice

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.todayzero.R
import com.example.todayzero.data.Notice
import org.w3c.dom.Text

class NoticeAdapter(var items: ArrayList<Notice>) :
    RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, view: View, data: Notice, position: Int)
    }
    var itemClickListener:OnItemClickListener?=null
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.notice_item, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.data.text=items[p1].data
        p0.title.text=items[p1].title

        p0.content.text=items[p1].content
        if(items[p1].selected){
            p0.moreBtn.setImageResource(R.drawable.ic_notice_open)
            p0.content.visibility  = TextView.VISIBLE
        }
        else{
            p0.moreBtn.setImageResource(R.drawable.ic_notice_close)
            p0.content.visibility  =TextView.GONE
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var data: TextView
        var title: TextView
        var moreBtn: ImageView
        var content:TextView
        init {
            data = itemView.findViewById(R.id.notice_date)
            title = itemView.findViewById(R.id.notice_title)
            moreBtn = itemView.findViewById(R.id.notice_more)
            content=itemView.findViewById(R.id.notice_content)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.onItemClick(this, it, items[position], position)
            }

        }
    }

}