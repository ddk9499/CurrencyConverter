package uz.dkamaloff

import android.app.Application
import toothpick.ktp.KTP

/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        KTP.openRootScope().installModules(appModule(this))
    }
}