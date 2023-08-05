package com.andresuryana.aptasari.ui.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.andresuryana.aptasari.R

class WaveformView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val waveformPaint = Paint()
    private var waveformData: ShortArray? = null

    init {
        waveformPaint.color = ContextCompat.getColor(context, R.color.primary)
        waveformPaint.strokeWidth = 3f
        waveformPaint.isAntiAlias = true
    }

    fun setWaveformData(data: ShortArray) {
        waveformData = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        waveformData?.let {
            drawWaveform(canvas, it)
        }
    }

    private fun drawWaveform(canvas: Canvas, data: ShortArray) {
        // Get canvas size
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()
        val centerY = height / 2

        // Calculate the number of samples to be drawn
        val samplePerBucket = data.size / width
        val bucketSize = data.size / width

        for (i in 0 until width.toInt()) {
            var sum = 0L
            for (j in 0 until bucketSize.toInt()) {
                val index = (i * bucketSize + j).toInt()
                if (index < data.size) {
                    sum += data[index].toInt()
                }
            }
            val amplitude = centerY - sum / bucketSize
            canvas.drawLine(i.toFloat(), centerY, i.toFloat(), amplitude, waveformPaint)
        }
    }
}