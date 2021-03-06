package com.simplemobiletools.calculator.helpers

import android.content.Context
import com.simpletools.calculator.commons.R
import com.simpletools.calculator.commons.helpers.Calculator
import com.simpletools.calculator.commons.helpers.Formatter

open class BaseCalculatorImpl(calculator: Calculator, open val context: Context) {
    var mCallback: Calculator? = calculator
    var number: String = ""
    var decimalClicked: Boolean = false
    var decimalCounter: Int = 0

    init {
        handleClear()
    }

    open fun numpadClicked(id: Int) {
        when (id) {
            R.id.btn_decimal -> decimalClick()
            R.id.btn_0 -> addDigit(0)
            R.id.btn_1 -> addDigit(1)
            R.id.btn_2 -> addDigit(2)
            R.id.btn_3 -> addDigit(3)
            R.id.btn_4 -> addDigit(4)
            R.id.btn_5 -> addDigit(5)
            R.id.btn_6 -> addDigit(6)
            R.id.btn_7 -> addDigit(7)
            R.id.btn_8 -> addDigit(8)
            R.id.btn_9 -> addDigit(9)
        }
    }

    open fun addDigit(i: Int) {
        if ((number != "" || i != 0) && decimalCounter < 2) {
            number = number + i
            if (decimalClicked) {
                decimalCounter++
            }
            setValue(String.format("%,.2f", number.toDouble()))
        }
    }

    fun decimalClick() {
        if (number == "") {
            number = "0."
        } else if (!decimalClicked) {
            number += "."
        }
        decimalClicked = true
    }

    open fun handleDelete() {
        if (number.length <= 1) {
            return handleClear()
        } else if (decimalClicked && decimalCounter<2) {
            number = number.dropLast(1 + decimalCounter)
            decimalClicked = false
            decimalCounter = 0
        } else if (decimalClicked) {
            decimalCounter--
            number = number.dropLast(1)
        } else {
            number = number.dropLast(1)
        }
        setValue(String.format("%,.2f", number.toDouble()))
    }

    open fun handleClear() {
        number = ""
        decimalClicked = false
        decimalCounter = 0
        setValue("0.00")
    }

    // will be used by the 3 money action buttons to display the result
    open fun overwriteNumber(newNumber: Double) {
        number = ""
        decimalClicked = false
        decimalCounter = 0
        setValue(String.format("%,.2f", newNumber))
    }

    fun setValue(value: String) {
        mCallback!!.setValue(value)
    }

    fun getResult() = Formatter.stringToDouble(mCallback!!.getResult())
    open fun addDigit(i: Long) {}
}