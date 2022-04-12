package uz.dkamaloff.currencyconverter.entities

import com.google.gson.annotations.SerializedName

/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

typealias SupportedCurrencies = List<SupportedCurrency>

data class SupportedCurrency(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("iso") val symbol: String,
    @SerializedName("icon_url") val icon: String
)
