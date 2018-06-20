package com.veren.android.sunshine

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val intent = this.intent

        val day = intent.getStringExtra("day")
        val description = intent.getStringExtra("desc")
        val high = intent.getDoubleExtra("high", 0.0)
        val low = intent.getDoubleExtra("low", 0.0)

        date_detail.text = day
        description_detail.text = description
        high_detail.text = String.format(this.getString(R.string.format_temperature), high)
        low_detail.text = String.format(this.getString(R.string.format_temperature), low)
    }
}
