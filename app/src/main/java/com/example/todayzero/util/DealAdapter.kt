package com.example.todayzero.util

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.todayzero.R
import com.example.todayzero.data.Notice
import com.example.todayzero.db.deal
import com.example.todayzero.notice.NoticeAdapter

class DealAdapter(var items: ArrayList<deal>) :
    RecyclerView.Adapter<DealAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(holder:DealAdapter.ViewHolder, view: View, data: deal, position: Int)
    }
    var itemClickListener:OnItemClickListener?=null

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(p0.context).inflate(R.layout.deal_list_item, p0, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: DealAdapter.ViewHolder, p1: Int) {
            p0.deal_list_store.text=items[p1].store
            p0.deal_list_payment.text=items[p1].date
        if(items[p1].isZero==1){
                 //p0.deal_list_payment.text="제로 페이 결제"
                 p0.deal_list_img.setImageResource(R.drawable.ic_exposure_zero_blue_24dp)
             }else{
                 //p0.deal_list_payment.text="일반 결제"
                 p0.deal_list_img.setImageResource(R.drawable.ic_exposure_zero_black_24dp)
             }
          p0.deal_list_price.text=items[p1].price +"원"

    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        var deal_list_img:ImageView
        var deal_list_store:TextView
        var deal_list_payment:TextView
        var deal_list_price:TextView

        init {
            deal_list_img = itemView.findViewById(R.id.deal_list_img)
            deal_list_store = itemView.findViewById(R.id.deal_list_store)
            deal_list_payment = itemView.findViewById(R.id.deal_list_pay)
            deal_list_price = itemView.findViewById(R.id.deal_list_price)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.onItemClick(this, it, items[position], position)
            }
        }


    }
}