package com.veren.android.sunshine

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.row_layout.view.*

/**
 * Created by Veren on 6/7/2018.
 */

class RecyclerAdapter(val context: Context, val mWeatherData: ArrayList<MainActivity.Weather>) : RecyclerView.Adapter<CustomViewHolder>() {

    var list : ArrayList<MainActivity.Weather> = mWeatherData

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
        holder?.day!!.text = list.get(position).day
        holder.desc.text = list.get(position).description

        val sunshineUtils : SunshineUtils = SunshineUtils()

        holder.high.text = sunshineUtils.formatTemperature(context, list.get(position).max)
        holder.low.text = sunshineUtils.formatTemperature(context, list.get(position).min)

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("day", list.get(position).day)
            intent.putExtra("desc", list.get(position).description)
            intent.putExtra("high", list.get(position).max)
            intent.putExtra("low", list.get(position).min)
            context.startActivity(intent)
        }
    }

    public fun setWeatherData(weatherData : ArrayList<MainActivity.Weather>) {
        list = weatherData
        notifyDataSetChanged()
    }
}

class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    val day = view.date
    val desc = view.description
    val high = view.high_temperature
    val low = view.low_temperature
}
