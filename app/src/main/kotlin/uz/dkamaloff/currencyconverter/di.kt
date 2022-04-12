package uz.dkamaloff.currencyconverter

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import okhttp3.OkHttpClient
import toothpick.ktp.binding.bind
import toothpick.ktp.binding.module

/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

fun appModule(context: Context) = module {
    bind<Context>().toInstance(context)
    bind<AssetManager>().toInstance(context.assets)
    bind<Gson>().toInstance(Gson())
    bind<OkHttpClient>().toInstance(OkHttpClient())
}
