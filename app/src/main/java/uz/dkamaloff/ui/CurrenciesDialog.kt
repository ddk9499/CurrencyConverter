package uz.dkamaloff.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_currency.view.*
import uz.dkamaloff.R
import uz.dkamaloff.entities.SupportedCurrencies
import uz.dkamaloff.entities.SupportedCurrency
import uz.dkamaloff.utils.loadRoundedImage

/**
 * Created at March 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

class CurrenciesDialog(
    private val currencies: SupportedCurrencies,
    private val listener: (SupportedCurrency) -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ctx = requireContext()
        val rv = RecyclerView(ctx).also {
            it.layoutManager = LinearLayoutManager(ctx)
            it.adapter = CurrenciesAdapter()
            it.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        return FrameLayout(ctx).also {
            it.addView(rv)
        }
    }

    private inner class CurrenciesAdapter : RecyclerView.Adapter<CurrencyHolder>() {
        override fun getItemCount(): Int = currencies.size
        override fun onBindViewHolder(holder: CurrencyHolder, position: Int) =
            holder.bind(currencies[position])

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder =
            CurrencyHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_currency,
                    parent,
                    false
                )
            )
    }

    private inner class CurrencyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(currency: SupportedCurrency) = with(itemView) {
            item_currency_flag.loadRoundedImage(currency.icon)
            item_currency_name.text = "${currency.symbol} - ${currency.fullName}"
            setOnClickListener {
                listener(currency)
                dismiss()
            }
        }
    }

}