package com.test_ecwid

import com.test_ecwid.interfaces.BitmapProvider
import com.test_ecwid.model.Item

class Spawner(bitmapProvider: BitmapProvider) {
    //Все объекты будут получать созданные Bitmap-ы, а не просить провайдера создать еще
    private val hero1 = bitmapProvider.provideBitmap(R.drawable.hero1)
    private val hero2 = bitmapProvider.provideBitmap(R.drawable.hero2)

    private val item1 = bitmapProvider.provideBitmap(R.drawable.item1)
    private val item2 = bitmapProvider.provideBitmap(R.drawable.item2)
    private val item3 = bitmapProvider.provideBitmap(R.drawable.item3)

    //Массив функций создания сотрудников
    private val collectorsViewOptions = arrayOf(
        fun(): Item { return Item(hero1, 1, 3) },
        fun(): Item { return Item(hero2, 3, 3) })

    fun spawnCollector(): Item {
        return collectorsViewOptions.random().invoke()
    }

    //Массив функций создания заказов
    private val ordersViewOptions = arrayOf(
        fun(): Item { return Item(item1, 1, 6) },
        fun(): Item { return Item(item2, 2, 3) },
        fun(): Item { return Item(item3, 3, 2) })

    fun spawnOrder(): Item {
        return ordersViewOptions.random().invoke()
    }
}