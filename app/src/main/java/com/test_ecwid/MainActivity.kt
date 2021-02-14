package com.test_ecwid

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.test_ecwid.interfaces.BitmapProvider
import com.test_ecwid.main.DrawingThread
import com.test_ecwid.main.MSG_ADD_COLLECTOR


class MainActivity : AppCompatActivity(), SurfaceHolder.Callback {
    private lateinit var mSurface: SurfaceView
    private lateinit var mThread: DrawingThread
    private lateinit var provider: BitmapProvider

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSurface = findViewById<SurfaceView>(R.id.surface) as SurfaceView
        val root = findViewById<RelativeLayout>(R.id.root)
        //Можно создавать сотруников кликом по экрану
        root.setOnClickListener {
            mThread.addItem(MSG_ADD_COLLECTOR)
        }
        //Создания провайдера Bitmap-oв
        provider = object : BitmapProvider {
            override fun provideBitmap(id: Int): Bitmap {
                return BitmapFactory.decodeResource(resources, id)
            }
        }
        mSurface.holder.addCallback(this)

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mThread = DrawingThread(holder, provider)
        mThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mThread.updateSize(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mThread.quit()
    }
}