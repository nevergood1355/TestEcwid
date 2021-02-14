package com.test_ecwid.model

import android.graphics.Bitmap
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class Item(val bitmap: Bitmap, val lvl: Int, private val speed: Int) {
    var x: Float = 0F;
    var y: Float = 0F;
    private val angleStep = Random.nextInt(45)
    private var angle = Random.nextDouble(-360.0, 360.0)

    //Метод привязки к полю отрисовки
    fun bind(x: Int, y: Int) {
        this.x = x.toFloat()
        this.y = y.toFloat()
    }

    //Поворот объекта, вызывается если объект касается края экрана
    fun turn() {
        angle += angleStep
    }

    //Перемещение объекта
    fun tick() {
        x += (speed * cos(Math.toRadians(angle))).toFloat()
        y += (speed * sin(Math.toRadians(angle))).toFloat()
    }
}