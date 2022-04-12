package uz.dkamaloff.currencyconverter.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View.OnClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import uz.dkamaloff.currencyconverter.databinding.ViewKeyboardBinding

/**
 * Created at February 2020
 *
 * @project CurrencyConverter
 * @author Dostonbek Kamalov (aka @ddk9499)
 */

class KeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onClick: (KeyEvent) -> Unit = {}

    init {
        val binding = ViewKeyboardBinding.inflate(LayoutInflater.from(context), this)

        val onClickListener = OnClickListener {
            val tag = it.tag as KeyEvent
            onClick(tag)
        }

        binding.keyboardNumber0.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0)
        binding.keyboardNumber1.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1)
        binding.keyboardNumber2.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_2)
        binding.keyboardNumber3.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_3)
        binding.keyboardNumber4.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_4)
        binding.keyboardNumber5.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_5)
        binding.keyboardNumber6.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_6)
        binding.keyboardNumber7.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_7)
        binding.keyboardNumber8.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_8)
        binding.keyboardNumber9.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_9)
        binding.keyboardDot.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PERIOD)
        binding.keyboardBackspace.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)

        binding.keyboardNumber0.setOnClickListener(onClickListener)
        binding.keyboardNumber1.setOnClickListener(onClickListener)
        binding.keyboardNumber2.setOnClickListener(onClickListener)
        binding.keyboardNumber3.setOnClickListener(onClickListener)
        binding.keyboardNumber4.setOnClickListener(onClickListener)
        binding.keyboardNumber5.setOnClickListener(onClickListener)
        binding.keyboardNumber6.setOnClickListener(onClickListener)
        binding.keyboardNumber7.setOnClickListener(onClickListener)
        binding.keyboardNumber8.setOnClickListener(onClickListener)
        binding.keyboardNumber9.setOnClickListener(onClickListener)
        binding.keyboardDot.setOnClickListener(onClickListener)
        binding.keyboardBackspace.setOnClickListener(onClickListener)
    }

    fun onClick(listener: (KeyEvent) -> Unit) {
        onClick = listener
    }
}
