package uz.dkamaloff.utils

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.IntRange
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

private const val DEFAULT_STRING = "0"
private val DECIMAL_SEPARATOR = DecimalFormatSymbols.getInstance().decimalSeparator
typealias AmountListener = (BigDecimal) -> Unit

class AmountFormatter(
    private val view: TextView,
    var currency: Currency? = null,
    private val listener: AmountListener? = null,
    private val limit: BigDecimal
) : TextWatcher {

    private var previousFormattedString: String = DEFAULT_STRING
    private var formatting: Boolean = false
    private var cursorAtStartPosition: Boolean = false
    private var forcePositionCalculation: Boolean = false
    private var forcePositionAfterFirstSymbol: Boolean = false

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (!formatting) {
            cursorAtStartPosition = isInsertingBeforeDigits(s, start, count, after)
            forcePositionCalculation = isDeletingLastSymbol(s, start, after)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // does nothing
    }

    override fun afterTextChanged(s: Editable) {
        if (formatting) {
            return
        }

        val currentString = s.toString()
        var currentPosition = view.selectionEnd

        var numericString = extractNumericString(currentString)

        val lastIndex = numericString.length - 1
        if (isNullDeletionRequired(numericString, lastIndex)) {
            // if user entered '.' we should change string to '0.' for further proper formatting
            if (numericString[lastIndex - 1] == '.') {
                numericString = "0."
                currentPosition = currentString.length
            } else {
                numericString = numericString.substring(0, lastIndex)
                forcePositionAfterFirstSymbol = true
            }
        }

        val amountFormatted = if (isSurplus(numericString)) formatAmount(numericString)
        else formatAmount(extractNumericString(previousFormattedString))

        formatting = true
        s.replace(0, s.length, amountFormatted)
        formatting = false

        val amountFormattedString = amountFormatted.toString()

        val shouldCalculatePosition =
            view is EditText && amountFormattedString != previousFormattedString || forcePositionCalculation
        if (shouldCalculatePosition) {
            calculateCursorPosition(
                s,
                currentString,
                currentPosition,
                amountFormatted,
                amountFormattedString
            )
        }
    }

    private fun formatAmount(rawString: String): CharSequence {
        try {
            val amount = BigDecimal(rawString)

            listener?.invoke(amount.setScale(2, BigDecimal.ROUND_FLOOR))

            val sumSplits = rawString.split(".")


            val fractions = if (sumSplits.size == 1 && rawString.contains(".")) 0
            else if (sumSplits.size == 1) -1
            else sumSplits[1].length.coerceAtMost(2)

            return formatAsUserInput(amount, currency ?: Currency.getInstance("USD"), fractions)
        } catch (_: NumberFormatException) {
            return ""
        }
    }

    private fun calculateCursorPosition(
        s: Editable,
        currentString: String,
        currentPosition: Int,
        amountFormatted: CharSequence,
        amountFormattedString: String
    ) {
        forcePositionCalculation = false
        var positionToSet: Int
        if (forcePositionAfterFirstSymbol) {
            positionToSet = amountFormatted.firstDigitIndex + 1
            forcePositionAfterFirstSymbol = false
        } else {
            positionToSet =
                getCursorPositionAfterFormat(amountFormattedString, currentString, currentPosition)

            // if user was entering before separator, we should correct position by 1
            if (currentPosition < currentString.length && currentString[currentPosition] == DECIMAL_SEPARATOR) {
                positionToSet--
            }

            if (positionToSet == s.length) {
                positionToSet =
                    getPositionToSetEncounteringSeparator(amountFormattedString, positionToSet)
            }
        }
        if (positionToSet >= 0 && positionToSet <= s.length) {
            (view as? EditText)?.setSelection(positionToSet)
        }

        previousFormattedString = amountFormattedString
    }

    private fun isDeletingLastSymbol(s: CharSequence, start: Int, after: Int): Boolean {
        return start == s.length - 1 && after == 0
    }

    private fun isNullDeletionRequired(numericString: String, lastIndex: Int): Boolean {
        return lastIndex == 1 && numericString[lastIndex] == '0' && cursorAtStartPosition
    }

    private fun isInsertingBeforeDigits(
        s: CharSequence,
        start: Int,
        count: Int,
        after: Int
    ): Boolean {
        return count == 0 && after > 0 && start <= s.firstDigitIndex
    }

    private fun getPositionToSetEncounteringSeparator(
        amountFormattedString: String,
        positionToSet: Int
    ): Int {
        var position = positionToSet
        var digitFound = false
        while (!digitFound && position > 0) {
            val char = amountFormattedString[position - 1]
            if (char.isDigit() || char == DECIMAL_SEPARATOR) {
                digitFound = true
            } else {
                position--
            }
        }

        return position
    }

    private fun extractNumericString(sumString: String): String {
        val stringBuilder = StringBuilder(
            sumString
                .replace(String.format("[^\\d^%s^.]", DECIMAL_SEPARATOR).toRegex(), "")
                .replace(DECIMAL_SEPARATOR, '.')
        )

        val separatorIndex = stringBuilder.indexOf(".")
        for (i in separatorIndex + 1 until stringBuilder.length) {
            if (stringBuilder[i] == '.') {
                stringBuilder.deleteCharAt(i)
            }
        }

        val extractedString = stringBuilder.toString()

        return if (extractedString.isEmpty() || "." == extractedString) DEFAULT_STRING else extractedString
    }

    private fun isSurplus(numericString: String): Boolean {
        return BigDecimal(numericString) <= limit
    }

    private fun getCursorPositionAfterFormat(
        formattedString: String,
        originalString: String,
        originalPosition: Int
    ): Int {
        var digitsCounter = 0
        for (i in originalString.length - 1 downTo originalPosition) {
            if (Character.isDigit(originalString[i])) {
                digitsCounter++
            }
        }

        var positionToSet = formattedString.length
        var digitsCounterAfterFormat = 0
        while (digitsCounter > digitsCounterAfterFormat && positionToSet > 0) {
            if (Character.isDigit(formattedString[--positionToSet])) {
                digitsCounterAfterFormat++
            }
        }
        return positionToSet
    }

    private fun formatAsUserInput(
        value: BigDecimal,
        currency: Currency,
        @IntRange(from = -1, to = 2) fractionDigits: Int
    ): CharSequence {
        if (fractionDigits == 0) {
            val formatted = format(value, currency, 1)
            val formattedString = formatted.toString()
            val decimalSeparatorIndex =
                formattedString.indexOf(DecimalFormatSymbols.getInstance().decimalSeparator)

            val end = decimalSeparatorIndex + 2
            if (decimalSeparatorIndex == -1 || end > formatted.length) {
                return formatted
            }

            val start = decimalSeparatorIndex + 1
            return SpannableStringBuilder(formatted).replace(start, end, "")
        } else {
            return format(value, currency, 0.coerceAtLeast(fractionDigits))
        }
    }

    private fun format(
        value: BigDecimal,
        currency: Currency,
        @IntRange(from = 0) maximumFractionDigits: Int
    ): CharSequence {
        val numberFormat = getNumberFormat(currency)
        numberFormat.roundingMode = RoundingMode.FLOOR
        numberFormat.maximumFractionDigits = maximumFractionDigits

        return numberFormat.format(value)
    }

    private fun getNumberFormat(currency: Currency): NumberFormat {
        val numberFormat = NumberFormat.getCurrencyInstance()
        numberFormat.currency = currency
        return numberFormat
    }

    private val CharSequence.firstDigitIndex: Int
        get() {
            this.forEachIndexed { index, c -> if (c.isDigit()) return index }

            return -1
        }
}