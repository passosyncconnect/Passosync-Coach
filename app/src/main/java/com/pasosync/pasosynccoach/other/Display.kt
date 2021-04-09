package com.pasosync.pasosynccoach.other

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.pasosync.pasosynccoach.other.Constant.colorList
import com.pasosync.pasosynccoach.other.Constant.current_brush
import com.pasosync.pasosynccoach.other.Constant.paint_brush
import com.pasosync.pasosynccoach.other.Constant.path
import com.pasosync.pasosynccoach.other.Constant.pathList


class Display :androidx.appcompat.widget.AppCompatImageView{
    constructor(context: Context?) : super(context!!)init {
        initialise(context)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)init {
        initialise(context)
    }


//    var pathList = arrayListOf<Path>()
//    var colorList = arrayListOf<Int>()
//    val current_brush = Color.BLACK





    fun initialise(context: Context) {
        paint_brush.isAntiAlias = true
        paint_brush.color = Color.BLACK
        paint_brush.style = Paint.Style.STROKE
        paint_brush.strokeCap = Paint.Cap.ROUND
        paint_brush.strokeJoin = Paint.Join.ROUND
        paint_brush.strokeWidth = 10f

        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        try {
            for (i in 0..pathList.size) {
                paint_brush.color = colorList[i]
                canvas!!.drawPath(pathList[i], paint_brush)
                invalidate()
            }
        } catch (e: Exception) {

        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
        val x = event!!.x
        val y = event!!.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                pathList.add(path)
                colorList.add(current_brush)
                invalidate()
            }

        }

    }



}