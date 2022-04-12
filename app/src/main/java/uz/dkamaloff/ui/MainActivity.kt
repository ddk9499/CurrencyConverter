package uz.dkamaloff.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.dkamaloff.databinding.ActivityMainBinding
import uz.dkamaloff.entities.SupportedCurrency
import uz.dkamaloff.utils.AmountFormatter
import uz.dkamaloff.utils.loadRoundedImage
import uz.dkamaloff.utils.scaled
import uz.dkamaloff.utils.viewModel
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

private val LIMIT = BigDecimal(999_999_999_999.99)

@FlowPreview
@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private val vm by viewModel<MainViewModel>()
    private var currentAmount = BigDecimal.ZERO
    private lateinit var binding: ActivityMainBinding
    private val originFormatter by lazy {
        AmountFormatter(binding.originCurrencyInput, listener = ::onTextChanged, limit = LIMIT)
    }
    private val resultFormatter by lazy {
        AmountFormatter(binding.resultCurrencyOutput,
            limit = LIMIT.multiply(BigDecimal.TEN).scaled())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.originCurrencyInput.also {
            it.showSoftInputOnFocus = false
            it.addTextChangedListener(originFormatter)
            val inputConn = it.onCreateInputConnection(EditorInfo())
            binding.keyboardView.onClick { keyEvent -> inputConn?.sendKeyEvent(keyEvent) }
            it.setText("0")
            it.requestFocus()
        }

        binding.resultCurrencyOutput.addTextChangedListener(resultFormatter)
        binding.swapCurrenciesFab.setOnClickListener { vm.swapCurrencies() }
        binding.changeOriginCurrency.setOnClickListener { showCurrenciesDialog(true) }
        binding.changeResultCurrency.setOnClickListener { showCurrenciesDialog(false) }

        observeChanges()
    }

    private fun onTextChanged(amount: BigDecimal) {
        currentAmount = amount
        vm.needUpdateRatio(amount)
    }

    private fun showCurrenciesDialog(isOrigin: Boolean) {
        val dialog =
            CurrenciesDialog(vm.supportedCurrencies) { vm.changeCurrency(isOrigin, it) }
        dialog.show(supportFragmentManager, "dialog")
    }

    private fun observeChanges() = lifecycleScope.launchWhenResumed {
        vm.originCurrency.onEach { updateOriginCurrency(it) }.launchIn(this)
        vm.resultCurrency.onEach { updateResultCurrency(it) }.launchIn(this)
        vm.ratio.onEach { updateResultAmount(it) }.launchIn(this)
    }


    private fun updateOriginCurrency(currency: SupportedCurrency) {
        originFormatter.setCurrency(Currency.getInstance(currency.symbol))
        binding.originCurrencyFlag.loadRoundedImage(currency.icon)
        binding.originCurrencyFullName.text = "${currency.symbol} - ${currency.fullName}"
    }

    private fun updateResultCurrency(currency: SupportedCurrency) {
        resultFormatter.setCurrency(Currency.getInstance(currency.symbol))
        binding.resultCurrencyFlag.loadRoundedImage(currency.icon)
        binding.resultCurrencyFullName.text = "${currency.symbol} - ${currency.fullName}"
    }

    private fun updateResultAmount(ratio: MainViewModel.Ratio) {
        val result = BigDecimal.ONE.divide(ratio.value, MathContext.DECIMAL32)
        binding.originCurrencyRatio.text =
            "1 ${vm.originCurrency.value.symbol} - ${ratio.value} ${vm.resultCurrency.value.symbol}"
        binding.resultCurrencyRatio.text =
            "1 ${vm.resultCurrency.value.symbol} - ${result.scaled()} ${vm.originCurrency.value.symbol}"
        binding.resultCurrencyOutput.text = currentAmount.multiply(ratio.value).scaled().toString()
    }
}
