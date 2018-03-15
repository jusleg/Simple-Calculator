package com.simpletools.calculator.commons.helpers

import com.simpletools.calculator.commons.R
import com.simpletools.calculator.commons.operations.NegativeOperation
import com.simpletools.calculator.commons.operations.OperationFactory
import com.simpletools.calculator.commons.operations.base.BinaryOperation
import com.simpletools.calculator.commons.operations.base.UnaryOperation

class CalculatorImpl(calculator: Calculator) {
    private var mCallback: Calculator? = calculator

    private var firstNumber: Double = 0.0
    private var secondNumber: Double = 0.0
    private var operator: String? = ""
    private var decimalClicked: Boolean = false
    private var decimalCounter = 0
    private var secondNumberSet: Boolean = false
    private var digits = 0
    private var lastIsOperation: Boolean = false

    init {
        resetValues()
        setValue("0")
        setFormula("")
    }

    fun numpadClicked(id: Int) {
        when (id) {
            R.id.btn_decimal -> decimalClicked = true
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

    fun handleOperation(operation: String) {
        if (OperationFactory.forId(operation, firstNumber, secondNumber) is NegativeOperation) return negateNumber()

        handleEquals()
        operator = operation
        if (OperationFactory.forId(operator!!, firstNumber, secondNumber) is UnaryOperation) {
            if (lastIsOperation == true) swapRegisters()
            handleEquals()
        } else if(OperationFactory.forId(operator!!, firstNumber, secondNumber) is BinaryOperation && lastIsOperation == false) {
            swapRegisters()
        }
    }

    fun decimalClick(){
        decimalClicked = true
    }

    fun handleEquals() {
        val operation = OperationFactory.forId(operator!!, firstNumber, secondNumber)

        if (operation != null && (digits > 0 || operation is UnaryOperation)) {
            setAllClear()
            resetValues()
            firstNumber = operation.getResult()
            setValue(Formatter.doubleToString(firstNumber))
            setFormula(operation.getFormula())
            lastIsOperation = false
        }
    }

    fun handleReset() {
        setAllClear()
        resetValues()
    }

    fun handleClear() {
        if(!secondNumberSet){
            handleReset()
            setAllClear()
        }
        else {
            firstNumber = 0.0
            setAllClear()
            setValue("0")
            secondNumberSet = false
        }
    }

    private fun setValue(value: String) {
        mCallback!!.setValue(value)
    }

    private fun setFormula(value: String) {
        mCallback!!.setFormula(value)
    }

    private fun resetValues() {
        firstNumber = 0.0
        secondNumber = 0.0
        decimalCounter = 0
        decimalClicked = false
        operator = ""
        secondNumberSet = false
        setValue("0")
        setFormula("")
        digits = 0
    }

    fun addDigit(i: Int) {
        if (decimalClicked) decimalCounter--
        if (operator != "") {
            secondNumberSet = true
            setClear()
        } else if (digits == 0) {
            resetValues()
        }

        firstNumber = if (!decimalClicked) signumMultiply(firstNumber) * (Math.abs(firstNumber)  * 10 + i)
        else signumMultiply(firstNumber) * (Math.abs(firstNumber) + i * Math.pow(10.0, decimalCounter.toDouble()))
        setValue(Formatter.doubleToStringWithGivenDigits(firstNumber, Math.abs(decimalCounter)))

        digits++
    }

    private fun signumMultiply(i: Double): Double {
        return if (Math.signum(i) == -1.0) -1.0 else 1.0 // slight modification of signum to use in multiplication
    }

    private fun swapRegisters() {
        firstNumber += secondNumber
        secondNumber = firstNumber - secondNumber
        firstNumber -= secondNumber
        decimalClicked = false
        decimalCounter = 0
        digits = 0
        lastIsOperation = true
    }

    private fun setClear(){
       mCallback!!.setClear("CE")
    }

    private fun setAllClear(){
        mCallback!!.setClear("AC")
    }

    private fun negateNumber(){
        firstNumber *= -1
        setValue(Formatter.doubleToString(firstNumber))
    }
}