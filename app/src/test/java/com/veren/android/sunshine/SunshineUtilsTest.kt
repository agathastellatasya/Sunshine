package com.veren.android.sunshine

import org.junit.Assert
import org.junit.Test

/**
 * Created by Veren on 6/20/2018.
 */

class SunshineUtilsTest {
    val utils = SunshineUtils()

    @Test
    fun convertion_isCorrect() {
        Assert.assertEquals(212.0, utils.celsiusToFahrenheit(100.0),0.0)
    }
}