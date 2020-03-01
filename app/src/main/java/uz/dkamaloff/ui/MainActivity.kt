package uz.dkamaloff.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import libs.toothpick.viewmodel.viewModel
import uz.dkamaloff.R
import uz.dkamaloff.entities.SupportedCurrency
import uz.dkamaloff.utils.AmountFormatter
import uz.dkamaloff.utils.loadRoundedImage
import uz.dkamaloff.utils.scaled
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

private val LIMIT = BigDecimal(999_999_999_999.99)

class MainActivity : AppCompatActivity() {

    private val vm by viewModel<MainViewModel>()
    private var currentAmount = BigDecimal.ZERO
    private val originFormatter by lazy {
        AmountFormatter(origin_currency_input, listener = ::onTextChanged, limit = LIMIT)
    }
    private val resultFormatter by lazy {
        AmountFormatter(result_currency_output, limit = LIMIT.multiply(BigDecimal.TEN).scaled())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        origin_currency_input.also {
            it.showSoftInputOnFocus = false
            it.addTextChangedListener(originFormatter)
            val inputConn = it.onCreateInputConnection(EditorInfo())
            keyboard_view.onClick { keyEvent -> inputConn?.sendKeyEvent(keyEvent) }
            it.setText("0")
            it.requestFocus()
        }

        result_currency_output.addTextChangedListener(resultFormatter)
        swap_currencies_fab.setOnClickListener { vm.swapCurrencies() }
        change_origin_currency.setOnClickListener { showCurrenciesDialog(true) }
        change_result_currency.setOnClickListener { showCurrenciesDialog(false) }

        vm.ratio.observe(this, Observer { updateResultAmount(it) })
        vm.baseCurrency.observe(this, Observer { updateBaseCurrency(it) })
        vm.resultCurrency.observe(this, Observer { updateResultCurrency(it) })
    }

    private fun showCurrenciesDialog(isBase: Boolean) {
        val dialog =
            CurrenciesDialog(vm.supportedCurrencies.value!!) { vm.changeCurrency(isBase, it) }
        dialog.show(supportFragmentManager, "dialog")
    }

    private fun updateBaseCurrency(currency: SupportedCurrency) {
        originFormatter.setCurrency(Currency.getInstance(currency.symbol))
        origin_currency_flag.loadRoundedImage(currency.icon)
        origin_currency_full_name.text = "${currency.symbol} - ${currency.fullName}"
    }

    private fun updateResultCurrency(currency: SupportedCurrency) {
        resultFormatter.setCurrency(Currency.getInstance(currency.symbol))
        result_currency_flag.loadRoundedImage(currency.icon)
        result_currency_full_name.text = "${currency.symbol} - ${currency.fullName}"
    }

    private fun updateResultAmount(ratio: BigDecimal) {
        val result = BigDecimal.ONE.divide(ratio, MathContext.DECIMAL32)
        origin_currency_ratio.text =
            "1 ${vm.baseCurrency.value?.symbol} - $ratio ${vm.resultCurrency.value?.symbol}"
        result_currency_ratio.text =
            "1 ${vm.resultCurrency.value?.symbol} - ${result.scaled()} ${vm.baseCurrency.value?.symbol}"
        result_currency_output.text = currentAmount.multiply(ratio).scaled().toString()
    }

    private fun onTextChanged(amount: BigDecimal) {
        currentAmount = amount
        vm.needUpdateRatio(amount)
    }
}
