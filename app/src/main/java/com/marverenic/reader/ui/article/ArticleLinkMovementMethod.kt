package com.marverenic.reader.ui.article

import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

/**
 * Shamelessly stolen from https://stackoverflow.com/a/30572151
 *
 * This includes a fix where initiating a scroll from a link would not cause the link to be
 * deselected
 */
object ArticleLinkMovementMethod : LinkMovementMethod() {

    private var handlingTouchEvent = false

    override fun canSelectArbitrarily() = true

    override fun initialize(widget: TextView, text: Spannable) {
        Selection.setSelection(text, text.length)
    }

    override fun onTakeFocus(view: TextView, text: Spannable, dir: Int) {
        if (dir and (View.FOCUS_FORWARD or View.FOCUS_DOWN) != 0) {
            if (view.layout == null) {
                // This shouldn't be null, but do something sensible if it is.
                Selection.setSelection(text, text.length)
            }
        } else {
            Selection.setSelection(text, text.length)
        }
    }

    override fun onTouchEvent(widget: TextView, buffer: Spannable,
                     event: MotionEvent): Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())

            val link = buffer.getSpans(off, off, ClickableSpan::class.java)

            if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget)
                    handlingTouchEvent = false
                } else if (action == MotionEvent.ACTION_DOWN) {
                    handlingTouchEvent = true
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]))
                }
                return true
            }
        } else if (action == MotionEvent.ACTION_CANCEL && handlingTouchEvent) {
            handlingTouchEvent = false
            Selection.setSelection(buffer, 0, 0)
            return true
        }

        return Touch.onTouchEvent(widget, buffer, event)
    }

}