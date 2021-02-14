package com.test_ecwid.interfaces

import android.graphics.Bitmap
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

interface BitmapProvider {
    fun provideBitmap(@DrawableRes id: Int) : Bitmap
}