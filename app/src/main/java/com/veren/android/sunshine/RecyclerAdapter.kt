package com.veren.android.sunshine

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.BaseAdapter
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.row_layout.view.*

/**
 * Created by Veren on 6/7/2018.
 */

class RecyclerAdapter(val context: Context, val list: ArrayList<MainActivity.Weather>) : RecyclerView.Adapter<CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
        holder?.day!!.text = list.get(position).day
        holder?.desc.text = list.get(position).description
        holder?.temp.text = list.get(position).min.toString() + " - " + list.get(position).max.toString()
    }

}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val day = view.day_view
    val desc = view.desc_view
    val temp = view.temp_view
}
