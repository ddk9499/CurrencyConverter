package uz.dkamaloff.currencyconverter.model

import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import uz.dkamaloff.currencyconverter.entities.SupportedCurrencies
import uz.dkamaloff.currencyconverter.entities.SupportedCurrency
import uz.dkamaloff.currencyconverter.utils.scaled
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

class CurrencyRepository @Inject constructor(
    private val assets: AssetManager,
    private val gson: Gson,
    private val client: OkHttpClient,
) {

    val supportedCurrencies: SupportedCurrencies by lazy { fromAsset<SupportedCurrencies>("currencies.json") }

    suspend fun refreshRatio(
        baseCurrency: SupportedCurrency,
        resultCurrency: SupportedCurrency,
    ): BigDecimal = withContext(Dispatchers.IO) {
        client.fetchCurrencyRatio(baseCurrency, resultCurrency)
    }

    private fun OkHttpClient.fetchCurrencyRatio(
        baseCurrency: SupportedCurrency,
        resultCurrency: SupportedCurrency,
    ): BigDecimal {
        val fromCurrency = baseCurrency.symbol.lowercase()
        val toCurrency = resultCurrency.symbol.lowercase()
        val url = "https://cdn.jsdelivr.net/gh/fawazahmed0/".toHttpUrl()
            .newBuilder()
            .addPathSegment("currency-api@1/latest/currencies/")
            .addPathSegment("$fromCurrency.json")
            .build()

        val json = newCall(Request.Builder().url(url).get().build())
            .execute()
            .unwrap()
            .string()

        // Why JSONObject instead of Gson? Because Rest API returned response contains
        // dynamic keys for currency. For shorthand I have used JSONObject and manually convert
        // response. In future it will be replaced with TypeAdapter.
        return JSONObject(json)
            .getJSONObject(fromCurrency)
            .getDouble(toCurrency)
            .toBigDecimal()
            .scaled()
    }

    private fun Response.unwrap(): ResponseBody =
        if (isSuccessful) body!!
        else throw IOException("HTTP $code")

    private inline fun <reified T> fromAsset(assetsPath: String): T =
        assets.open(assetsPath).use { stream ->
            gson.fromJson(InputStreamReader(stream), object : TypeToken<T>() {}.type)
        }

}
