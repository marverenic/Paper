package com.marverenic.reader.ui.article

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class PicassoImageGetter(
        private val context: Context,
        private val onChangeCallback: () -> Unit
) : Html.ImageGetter {

    override fun getDrawable(source: String): Drawable {
        return PlaceholderDrawable(context, onChangeCallback).also {
            Picasso.with(context)
                    .load(source)
                    .into(it)
        }
    }

}

private class PlaceholderDrawable(
        private val context: Context,
        private val onChangeCallback: () -> Unit
) : Drawable(), Target {

    private var _alpha: Int = 255
    private var _colorFilter: ColorFilter? = null

    private var drawable: Drawable? = null
        set(value) {
            field = value
            field?.alpha = _alpha
            field?.colorFilter = _colorFilter

            val width = value?.intrinsicWidth ?: 0
            val height = value?.intrinsicHeight ?: 0
            drawable?.setBounds(0, 0, width, height)
            setBounds(0, 0, width, height)

            onChangeCallback()
        }

    override fun setAlpha(alpha: Int) {
        _alpha = alpha
        drawable?.alpha = alpha
    }

    override fun getOpacity() = _alpha

    override fun setColorFilter(colorFilter: ColorFilter?) {
        _colorFilter = colorFilter
    }

    override fun draw(canvas: Canvas) {
        drawable?.draw(canvas)
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        drawable = placeHolderDrawable
    }

    override fun onBitmapFailed(errorDrawable: Drawable?) {
        drawable = errorDrawable
    }

    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
        bitmap?.let {
            drawable = BitmapDrawable(context.resources, it)
        }
    }

}