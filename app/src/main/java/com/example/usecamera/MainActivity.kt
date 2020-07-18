package com.example.usecamera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Camera
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import java.io.IOException
import android.util.Log
import android.view.*
import android.view.Window.FEATURE_NO_TITLE


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 全螢幕顯示
        this.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // 以程式建立 UI，為了層層相疊，最底層用的是 FrameLayout
        val frmLayout = FrameLayout(this)

        // 往上一層是攝影機預覽畫面
        val sv = SurfaceView(this)
        val sh = sv.holder
        // 加入攝影機功能
        sh.addCallback(CameraCallback())
        sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        frmLayout.addView(sv)

        // 最上層是方框
        val sqv = SquareView(this)
        frmLayout.addView(sqv)

        setContentView(frmLayout)
    }
    inner class CameraCallback : SurfaceHolder.Callback {
        private var c: Camera? = null
        private val TAG = "CameraCallback"
        override fun surfaceCreated(holder: SurfaceHolder) {
            Log.d(TAG, "啟動相機")
            this.c = Camera.open()
            try {
                Log.d(TAG, "設定預覽視窗")
                this.c!!.setPreviewDisplay(holder)
                this.c!!.setDisplayOrientation(90)
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
            }
        }

        override fun surfaceChanged(
            holder: SurfaceHolder, format: Int, width: Int,
            height: Int
        ) {
            Log.d(TAG, "開始預覽")
            this.c!!.startPreview()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.d(TAG, "停止預覽")
            this.c!!.stopPreview()
            Log.d(TAG, "釋放相機資源")
            this.c!!.release()
            this.c = null
        }
    }
    inner class SquareView(context: Context) : View(context) {
        private val TAG = "SquareView"
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            Log.d(TAG, "畫框")
            // 黃色的線
            val p = Paint()
            p.setColor(Color.YELLOW)
            // 方框
            val w = canvas.getWidth()
            val h = canvas.getHeight()
            // 上下左右四邊
            val margin = 90
            canvas.drawLine(margin.toFloat(), margin.toFloat(), (w - margin).toFloat(), margin.toFloat(), p)
            canvas.drawLine(margin.toFloat(), h - margin.toFloat(), w - margin.toFloat(), h - margin.toFloat(), p)
            canvas.drawLine(margin.toFloat(), margin.toFloat(), margin.toFloat(), h - margin.toFloat(), p)
            canvas.drawLine(w - margin.toFloat(), margin.toFloat(), w - margin.toFloat(), h - margin.toFloat(), p)
        }
    }
}
