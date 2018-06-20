package com.veren.android.sunshine

import android.content.Context
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager


/**
 * Created by Veren on 6/7/2018.
 */

class SunshineUtils {

    val SECOND_IN_MILLIS: Long = 1000
    val MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60
    val HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60
    val DAY_IN_MILLIS = HOUR_IN_MILLIS * 24

    /**
     * This method returns the number of days since the epoch (January 01, 1970, 12:00 Midnight UTC)
     * in UTC time from the current date.
     *
     * @param date A date in milliseconds in local time.
     *
     * @return The number of days in UTC time from the epoch.
     */
    fun getDayNumber(date: Long): Long {
        val tz = TimeZone.getDefault()
        val gmtOffset = tz.getOffset(date)
        return (date + gmtOffset) / DAY_IN_MILLIS
    }

    /**
     * Since all dates from the database are in UTC, we must convert the given date
     * (in UTC timezone) to the date in the local timezone. Ths function performs that conversion
     * using the TimeZone offset.
     *
     * @param utcDate The UTC datetime to convert to a local datetime, in milliseconds.
     * @return The local date (the UTC datetime - the TimeZone offset) in milliseconds.
     */
    fun getLocalDateFromUTC(utcDate: Long): Long {
        val tz = TimeZone.getDefault()
        val gmtOffset = tz.getOffset(utcDate)
        return utcDate - gmtOffset
    }

    /**
     * Since all dates from the database are in UTC, we must convert the local date to the date in
     * UTC time. This function performs that conversion using the TimeZone offset.
     *
     * @param localDate The local datetime to convert to a UTC datetime, in milliseconds.
     * @return The UTC date (the local datetime + the TimeZone offset) in milliseconds.
     */
    fun getUTCDateFromLocal(localDate: Long): Long {
        val tz = TimeZone.getDefault()
        val gmtOffset = tz.getOffset(localDate)
        return localDate + gmtOffset
    }

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     *
     * The day string for forecast uses the following logic:
     * For today: "Today, June 8"
     * For tomorrow:  "Tomorrow"
     * For the next 5 days: "Wednesday" (just the day name)
     * For all days after that: "Mon, Jun 8" (Mon, 8 Jun in UK, for example)
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds (UTC)
     * @param showFullDate Used to show a fuller-version of the date, which always contains either
     * the day of the week, today, or tomorrow, in addition to the date.
     *
     * @return A user-friendly representation of the date such as "Today, June 8", "Tomorrow",
     * or "Friday"
     */
    fun getFriendlyDateString(context: Context, dateInMillis: Long, showFullDate: Boolean): String {

        val localDate = getLocalDateFromUTC(dateInMillis)
        val dayNumber = getDayNumber(localDate)
        val currentDayNumber = getDayNumber(System.currentTimeMillis())

        if (dayNumber == currentDayNumber || showFullDate) {
            /*
             * If the date we're building the String for is today's date, the format
             * is "Today, June 24"
             */
            val dayName = getDayName(context, localDate)
            val readableDate = getReadableDateString(context, localDate)
            if (dayNumber - currentDayNumber < 2) {
                /*
                 * Since there is no localized format that returns "Today" or "Tomorrow" in the API
                 * levels we have to support, we take the name of the day (from SimpleDateFormat)
                 * and use it to replace the date from DateUtils. This isn't guaranteed to work,
                 * but our testing so far has been conclusively positive.
                 *
                 * For information on a simpler API to use (on API > 18), please check out the
                 * documentation on DateFormat#getBestDateTimePattern(Locale, String)
                 * https://developer.android.com/reference/android/text/format/DateFormat.html#getBestDateTimePattern
                 */
                val localizedDayName = SimpleDateFormat("EEEE").format(localDate)
                return readableDate.replace(localizedDayName, dayName)
            } else {
                return readableDate
            }
        } else if (dayNumber < currentDayNumber + 7) {
            /* If the input date is less than a week in the future, just return the day name. */
            return getDayName(context, localDate)
        } else {
            val flags = (DateUtils.FORMAT_SHOW_DATE
                    or DateUtils.FORMAT_NO_YEAR
                    or DateUtils.FORMAT_ABBREV_ALL
                    or DateUtils.FORMAT_SHOW_WEEKDAY)

            return DateUtils.formatDateTime(context, localDate, flags)
        }
    }

    /**
     * Returns a date string in the format specified, which shows a date, without a year,
     * abbreviated, showing the full weekday.
     *
     * @param context      Used by DateUtils to formate the date in the current locale
     * @param timeInMillis Time in milliseconds since the epoch (local time)
     *
     * @return The formatted date string
     */
    private fun getReadableDateString(context: Context, timeInMillis: Long): String {
        val flags = (DateUtils.FORMAT_SHOW_DATE
                or DateUtils.FORMAT_NO_YEAR
                or DateUtils.FORMAT_SHOW_WEEKDAY)

        return DateUtils.formatDateTime(context, timeInMillis, flags)
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "Wednesday".
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds (local time)
     *
     * @return the string day of the week
     */
    private fun getDayName(context: Context, dateInMillis: Long): String {
        /*
         * If the date is today, return the localized version of "Today" instead of the actual
         * day name.
         */
        val dayNumber = getDayNumber(dateInMillis)
        val currentDayNumber = getDayNumber(System.currentTimeMillis())
        if (dayNumber == currentDayNumber) {
            return context.getString(R.string.today)
        } else if (dayNumber == currentDayNumber + 1) {
            return context.getString(R.string.tomorrow)
        } else {
            /*
             * Otherwise, if the day is not today, the format is just the day of the week
             * (e.g "Wednesday")
             */
            val dayFormat = SimpleDateFormat("EEEE")
            return dayFormat.format(dateInMillis)
        }
    }

    /**
     * This method will convert a temperature from Celsius to Fahrenheit.
     *
     * @param temperatureInCelsius Temperature in degrees Celsius(°C)
     *
     * @return Temperature in degrees Fahrenheit (°F)
     */
    private fun celsiusToFahrenheit(temperatureInCelsius: Double): Double {
        val temperatureInFahrenheit = temperatureInCelsius * 1.8 + 32
        return temperatureInCelsius * 1.8 + 32
    }

    /**
     * Returns true if the user has selected metric temperature display.
     *
     * @param context Context used to get the SharedPreferences
     *
     * @return true If metric display should be used
     */
    private fun isCelcius(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val keyForUnits = context.getString(R.string.pref_key_units)
        val defaultUnits = context.getString(R.string.pref_def_units)
        val preferredUnits = prefs.getString(keyForUnits, defaultUnits)
        val metric = context.getString(R.string.pref_def_units)
        val userPrefersMetric: Boolean = metric == preferredUnits
        return userPrefersMetric
    }

    /**
     * Temperature data is stored in Celsius by our app. Depending on the user's preference,
     * the app may need to display the temperature in Fahrenheit. This method will perform that
     * temperature conversion if necessary. It will also format the temperature so that no
     * decimal points show. Temperatures will be formatted to the following form: "21°C"
     *
     * @param context     Android Context to access preferences and resources
     * @param temperature Temperature in degrees Celsius (°C)
     *
     * @return Formatted temperature String in the following form:
     * "21°C"
     */
    fun formatTemperature(context: Context, temperature: Double): String {
        var temperature = temperature
        var temperatureFormatResourceId = R.string.format_temperature

        if (!isCelcius(context)) {
            temperature = celsiusToFahrenheit(temperature)
        }

        /* For presentation, assume the user doesn't care about tenths of a degree. */
        return String.format(context.getString(temperatureFormatResourceId), temperature)
    }
}