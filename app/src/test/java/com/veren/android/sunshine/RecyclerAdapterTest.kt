package com.veren.android.sunshine

import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by Veren on 6/20/2018.
 */
class RecyclerAdapterTest {
    val mainActivity = MainActivity()
    val list = ArrayList<MainActivity.Weather>()
    val adapter = RecyclerAdapter(mainActivity, list)

    @Before
    fun setUp() {
        list.add(MainActivity.Weather("Today","Light Rain",18.32,13.0))
        list.add(MainActivity.Weather("Today","Light Rain",18.32,13.0))
        list.add(MainActivity.Weather("Today","Light Rain",18.32,13.0))
        list.add(MainActivity.Weather("Today","Light Rain",18.32,13.0))
    }

    @Test
    fun itemCount_isCorrect() {
        Assert.assertEquals(4, adapter.itemCount)
    }
}