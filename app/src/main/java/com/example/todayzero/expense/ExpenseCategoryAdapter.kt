package com.example.todayzero.expense

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.content.Context


class ExpenseCategoryAdapter(var items:ArrayList<Category>):RecyclerView.Adapter<ExpenseCategoryAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun OnItemClick(holder: ViewHolder, view: View, data: Category, position: Int)
    }
    var itemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ExpenseCategoryAdapter.ViewHolder {
        var v = LayoutInflater.from(p0.context).inflate(com.example.todayzero.R.layout.category_layout,p0,false)
        return ViewHolder(v)
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun getImageId(context: Context, imageName: String): Int {
        return context.resources.getIdentifier("drawable/$imageName", null, context.packageName)
    }
    override fun getItemCount(): Int {
        return items.size
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: ExpenseCategoryAdapter.ViewHolder, p1: Int) {
        p0.category_name.text = items.get(p1).category_string
        var image_name = items.get(p1).category_image
        p0.category_image.setImageResource(image_name)
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var category_name:TextView
        var category_image:ImageView
        init {
            category_name = itemView.findViewById(com.example.todayzero.R.id.expense_text)
            category_image = itemView.findViewById(com.example.todayzero.R.id.expense_image)
            itemView.setOnClickListener {
                val position = adapterPosition
                itemClickListener?.OnItemClick(this, it, items[position], position)
            }
        }

    }
}