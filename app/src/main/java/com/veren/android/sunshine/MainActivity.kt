package com.veren.android.sunshine

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

var list : ArrayList<MainActivity.Weather> = ArrayList()

class MainActivity : AppCompatActivity(), OnSharedPreferenceChangeListener {

    data class Weather(val day: String, val description: String, val max: Double, val min: Double)
    val TAG = "MainActivity"
    val url = "https://andfun-weather.udacity.com/staticweather"

    private var PREFERENCES_HAVE_BEEN_UPDATED = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        AsyncTaskHandleJson().execute(url)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onStart() {
        super.onStart()

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "onStart: preferences were updated");
            list.clear()
            AsyncTaskHandleJson().execute(url)
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        PREFERENCES_HAVE_BEEN_UPDATED = true
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.refresh -> {
                list.clear()
                AsyncTaskHandleJson().execute(url)
                return true
            }
            R.id.map_location -> {
                val prefs = PreferenceManager
                        .getDefaultSharedPreferences(this)
                val keyForLocation = this.getString(R.string.pref_key_location)
                val defaultLocation = this.getString(R.string.pref_def_location)

                val address = prefs.getString(keyForLocation, defaultLocation);
                val geoLocation = Uri.parse("geo:0,0?q=" + address)

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(geoLocation)

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent)
                } else {
                    Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
                return true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun runAdapter() {
        recyclerView.adapter = RecyclerAdapter(this, list)
    }

    inner class AsyncTaskHandleJson : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            var text : String
            var conn = URL(params[0]).openConnection() as HttpURLConnection
            try {
                conn.connect()
                text = conn.inputStream.use { it.reader().use { reader -> reader.readText() } }
            } finally {
                conn.disconnect()
            }
            handleJson(text)
            return text
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            runAdapter()
        }
    }

    fun handleJson(jsonStr: String?) {
        Log.d(TAG, "masuk handleJson")
        val jsonObject = JSONObject(jsonStr)

        val weatherArray = jsonObject.getJSONArray("list")

        val sunshineDateUtils = SunshineUtils()
        val localDate: Long = System.currentTimeMillis()
        val utcDate: Long = sunshineDateUtils.getUTCDateFromLocal(localDate)

        for (i in 0..weatherArray.length()-1) {
            val dayForcast = weatherArray.getJSONObject(i)
            val dateTimeMillis = utcDate + sunshineDateUtils.DAY_IN_MILLIS * i
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
