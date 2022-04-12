package uz.dkamaloff.currencyconverter.ui

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
import uz.dkamaloff.currencyconverter.databinding.ItemCurrencyBinding
import uz.dkamaloff.currencyconverter.entities.SupportedCurrencies
import uz.dkamaloff.currencyconverter.entities.SupportedCurrency
import uz.dkamaloff.currencyconverter.utils.loadRoundedImage

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
    ): View {
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
                ItemCurrencyBinding.inflate(layoutInflater)
//                LayoutInflater.from(parent.context).inflate(
//                    R.layout.item_currency,
//                    parent,
//                    false
//                )
            )
    }

    private inner class CurrencyHolder(private val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: SupportedCurrency) = with(binding) {
            binding.itemCurrencyFlag.loadRoundedImage(currency.icon)
            binding.itemCurrencyName.text = "${currency.symbol} - ${currency.fullName}"
            binding.root.setOnClickListener {
                listener(currency)
                dismiss()
            }
        }
    }

}
