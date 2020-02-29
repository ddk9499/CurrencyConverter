package uz.dkamaloff.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
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

class MainViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    val supportedCurrencies: LiveData<SupportedCurrencies> =
        MutableLiveData(currencyRepository.supportedCurrencies)

    private val userInputChannel = ConflatedBroadcastChannel<Boolean>()
    private val _ratio = MutableLiveData<BigDecimal>()
    private val _baseCurrency = MutableLiveData<SupportedCurrency>(supportedCurrencies.value!![1])
    private val _resultCurrency = MutableLiveData<SupportedCurrency>(supportedCurrencies.value!![0])

    val ratio: LiveData<BigDecimal> get() = _ratio
    val baseCurrency: LiveData<SupportedCurrency> get() = _baseCurrency
    val resultCurrency: LiveData<SupportedCurrency> get() = _resultCurrency

    init {
        viewModelScope.launch {
            userInputChannel
                .asFlow()
                .debounce(1000L) // to prevent making a туецщкл request for each user input
                .collect { updateRatio() }
        }
    }

    fun needUpdateRatio() {
        viewModelScope.launch {
            userInputChannel.send(true)
        }
    }

    private suspend fun updateRatio() {
        _ratio.value =
            currencyRepository.refreshRatio(_baseCurrency.value!!, _resultCurrency.value!!)
    }

}