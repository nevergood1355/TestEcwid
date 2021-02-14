package com.test_ecwid.main

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.view.SurfaceHolder
import com.test_ecwid.Spawner
import com.test_ecwid.interfaces.BitmapProvider
import com.test_ecwid.model.Item
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.math.abs
import kotlin.random.Random

const val MSG_ADD_ORDER = 100
const val MSG_ADD_COLLECTOR = 101
const val MSG_TICK = 102

/*
* Локальные функции используются для переиспользования кода
* */

class DrawingThread(private val mDrawingSurface: SurfaceHolder, private val provider: BitmapProvider) : HandlerThread("DrawingThread"), Handler.Callback {
    //Ширина, высота экрана
    private var width = 0
    private var height = 0
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mReceiver: Handler? = null

    /*
     * CopyOnWriteArraySet - Специальное множество, которое можно  изменять во время цикла
     * collectors - множество всех сотрудников
     * orders - множество всех заказов
     */
    private val orders: CopyOnWriteArraySet<Item> = CopyOnWriteArraySet()
    private val collectors: CopyOnWriteArraySet<Item> = CopyOnWriteArraySet()

    //Обертка над моделью, для переиспользования Bitmap-ов
    private val spawner = Spawner(provider)

    override fun onLooperPrepared() {
        mReceiver = Handler(looper, this)
        mReceiver?.sendEmptyMessage(MSG_TICK)

        //Начальная инициализация объектов
        repeat(10) { addItem(MSG_ADD_ORDER) }
        repeat(3) { addItem(MSG_ADD_COLLECTOR) }

        //Каждые 10 секунд на складе появляется 10 новый заказов
        fun spawnOrders() {
            val delay = 10_000L
            mReceiver?.postDelayed({
                //Ограничение количества элеменов на складе
                if (orders.size <= 40) {
                    repeat(10) { addItem(MSG_ADD_ORDER) }
                }
                spawnOrders()
            }, delay)
        }
        spawnOrders()
    }

    override fun quit(): Boolean {
        mReceiver?.removeCallbacksAndMessages(null)
        mReceiver = null
        return super.quit()
    }

    override fun handleMessage(msg: Message): Boolean {
        //Привязываем элемент так, чтобы он не выходил за границы экрана
        fun bindItem(it: Item) {
            it.bind(Random.nextInt(width - it.bitmap.width), Random.nextInt(height - it.bitmap.height))
        }
        when (msg.what) {
            MSG_ADD_ORDER -> {
                val item = spawner.spawnOrder()
                bindItem(item)
                orders.add(item)
            }
            MSG_ADD_COLLECTOR -> {
                val item = spawner.spawnCollector()
                bindItem(item)
                collectors.add(item)
            }
            MSG_TICK -> {
                val c = mDrawingSurface.lockCanvas()
                with(c) {
                    //Заливка белым цветом
                    drawColor(Color.WHITE)
                    //Попытка сделать поворот
                    fun tryTurn(it: Item) {
                        val outOfBounds = it.x > (width - it.bitmap.width) ||
                                it.y > (height - it.bitmap.height) ||
                                it.x <= 0 ||
                                it.y <= 0
                        if (outOfBounds) {
                            it.turn()
                        }
                    }

                    //Перемещение объектов
                    fun tickItems(set: Set<Item>) {
                        set.forEach { it.tick();tryTurn(it) }
                    }
                    tickItems(collectors)
                    tickItems(orders)

                    //Поиск поглощения заказа сотрудником
                    collectors.forEach { collector ->
                        orders.forEach { order ->
                            //Расстояние между объектами по осям
                            val dx = abs(collector.x - order.x)
                            val dy = abs(collector.y - order.y)
                            //Проверям объекты на пересечение
                            if (dx <= collector.bitmap.width / 2 && dy <= collector.bitmap.height / 2 && collector.lvl >= order.lvl) {
                                orders.remove(order)
                            }
                        }
                    }

                    //Отрисовка объектов
                    fun drawItems(set: Set<Item>) {
                        set.forEach {
                            drawBitmap(it.bitmap, it.x, it.y, mPaint)
                        }
                    }
                    drawItems(collectors)
                    drawItems(orders)
                    mDrawingSurface.unlockCanvasAndPost(c)
                }
            }
        }
        mReceiver?.sendEmptyMessage(MSG_TICK)
        return true
    }

    fun updateSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun addItem(type: Int) {
        mReceiver?.sendEmptyMessage(type)
    }
}