package com.veren.android.sunshine

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateUtils.DAY_IN_MILLIS
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.temporal.TemporalQueries.localDate
import java.util.*
import android.text.format.DateUtils.HOUR_IN_MILLIS
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.activity_main.*

var list : ArrayList<MainActivity.Weather> = ArrayList()

class MainActivity : AppCompatActivity() {

    data class Weather(val day: String, val description: String, val max: Double, val min: Double)
    val TAG = "MainActivity"
    val url = "https://andfun-weather.udacity.com/staticweather"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.layoutManager = LinearLayoutManager(this)

        AsyncTaskHandleJson().execute(url)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.refresh -> {
                AsyncTaskHandleJson().execute(url)
            }
            R.id.map_location -> {
                val address = "Jakarta"
                val geoLocation = Uri.parse("geo:0,0?q=" + address)

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(geoLocation)

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent)
                } else {
                    Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
            }
            R.id.settings -> {
                startActivity(Intent())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun runAdapter() {
        recyclerView.adapter = RecyclerAdapter(this, list)
    }

    inner class AsyncTaskHandleJson : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            Log.d(TAG, "masuk doInBackground")
            var text : String
            var conn = URL(params[0]).openConnection() as HttpURLConnection
            try {
                Log.d(TAG, "before connect")
                conn.connect()
                Log.d(TAG, "after connect")
                text = conn.inputStream.use { it.reader().use { reader -> reader.readText() } }
                Log.d(TAG,"text connection: "+ text)
            } finally {
                Log.d(TAG, "before disconnect")
                conn.disconnect()
                Log.d(TAG, "after disconnect")
            }
            Log.d(TAG, "after try")
            handleJson(text)
            return text
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            runAdapter()
        }
    }

    private fun handleJson(jsonStr: String?) {
        Log.d(TAG, "masuk handleJson")
        val jsonObject = JSONObject(jsonStr)

        val weatherArray = jsonObject.getJSONArray("list")

        val sunshineDateUtils = SunshineDateUtils()
        val localDate: Long = System.currentTimeMillis()
        val utcDate: Long = sunshineDateUtils.getUTCDateFromLocal(localDate)
        val startDay: Long = sunshineDateUtils.normalizeDate(utcDate)

        for (i in 0..weatherArray.length()-1) {
            val dayForcast = weatherArray.getJSONObject(i)
            val dateTimeMillis = startDay + sunshineDateUtils.DAY_IN_MILLIS * i
            val date = sunshineDateUtils.getFriendlyDateString(this, dateTimeMillis, false)

            val weatherObject = dayForcast.getJSONArray("weather").getJSONObject(0)
            val description = weatherObject.getString("description")

            val temperatureObject = dayForcast.getJSONObject("temp")
            val high = temperatureObject.getDouble("max")
            val low = temperatureObject.getDouble("min")

            Log.d(TAG,"high: "+ high.toString())
            list.add(Weather(date, description, high, low))
        }


    }
}
