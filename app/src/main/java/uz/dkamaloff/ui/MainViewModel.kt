package uz.dkamaloff.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.dkamaloff.entities.SupportedCurrencies
import uz.dkamaloff.entities.SupportedCurrency
import uz.dkamaloff.model.CurrencyRepository
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

@FlowPreview
class MainViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    val supportedCurrencies: SupportedCurrencies = currencyRepository.supportedCurrencies

    private val userInputChannel = MutableStateFlow(BigDecimal.ZERO)
    private val _originCurrency = MutableStateFlow(supportedCurrencies[0])
    private val _resultCurrency = MutableStateFlow(supportedCurrencies[1])
    private val _ratio = MutableStateFlow(Ratio(BigDecimal.ONE))

    val originCurrency: StateFlow<SupportedCurrency> = _originCurrency
    val resultCurrency: StateFlow<SupportedCurrency> = _resultCurrency
    val ratio: StateFlow<Ratio> = _ratio

    init {
        userInputChannel
            .debounce(1000L) // prevent make a network request for each user input
            .distinctUntilChanged() // prevent make network request if input is not different from previous
            .onEach { updateRatio() }
            .launchIn(viewModelScope)
    }

    fun needUpdateRatio(amount: BigDecimal) = viewModelScope.launch {
        userInputChannel.value = amount
    }

    fun swapCurrencies() {
        val tmp = _originCurrency.value
        _originCurrency.value = resultCurrency.value
        _resultCurrency.value = tmp

        updateRatio()
    }

    fun changeCurrency(isOrigin: Boolean, currency: SupportedCurrency) {
        if (isOrigin) _originCurrency.value = currency
        else _resultCurrency.value = currency

        updateRatio()
    }

    private fun updateRatio() = viewModelScope.launch {
        _ratio.value =
            Ratio(currencyRepository.refreshRatio(_originCurrency.value, _resultCurrency.value))
    }

    class Ratio(val value: BigDecimal)

}