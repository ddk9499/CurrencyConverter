package uz.dkamaloff.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_keyboard.view.*
import uz.dkamaloff.R

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
        View.inflate(context, R.layout.view_keyboard, this)

        val onClickListener = OnClickListener {
            val tag = it.tag as KeyEvent
            onClick(tag)
        }

        keyboard_number0.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_0)
        keyboard_number1.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_1)
        keyboard_number2.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_2)
        keyboard_number3.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_3)
        keyboard_number4.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_4)
        keyboard_number5.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_5)
        keyboard_number6.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_6)
        keyboard_number7.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_7)
        keyboard_number8.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_8)
        keyboard_number9.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_9)
        keyboard_dot.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PERIOD)
        keyboard_backspace.tag = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)

        keyboard_number0.setOnClickListener(onClickListener)
        keyboard_number1.setOnClickListener(onClickListener)
        keyboard_number2.setOnClickListener(onClickListener)
        keyboard_number3.setOnClickListener(onClickListener)
        keyboard_number4.setOnClickListener(onClickListener)
        keyboard_number5.setOnClickListener(onClickListener)
        keyboard_number6.setOnClickListener(onClickListener)
        keyboard_number7.setOnClickListener(onClickListener)
        keyboard_number8.setOnClickListener(onClickListener)
        keyboard_number9.setOnClickListener(onClickListener)
        keyboard_dot.setOnClickListener(onClickListener)
        keyboard_backspace.setOnClickListener(onClickListener)
    }

    fun onClick(listener: (KeyEvent) -> Unit) {
        onClick = listener
    }
}