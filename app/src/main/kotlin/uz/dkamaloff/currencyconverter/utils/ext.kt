package uz.dkamaloff.currencyconverter.utils

import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import uz.dkamaloff.currencyconverter.glide.GlideApp
import uz.dkamaloff.currencyconverter.glide.SvgSoftwareLayerSetter
import java.math.BigDecimal
import java.math.RoundingMode


/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

fun BigDecimal.scaled(scale: Int = 2): BigDecimal = this.setScale(scale, RoundingMode.HALF_UP)

fun ImageView.loadRoundedImage(url: String) {
    GlideApp.with(context)
        .`as`(PictureDrawable::class.java)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .listener(SvgSoftwareLayerSetter())
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}
