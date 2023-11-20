package farkhat.myrzabekov.shabyttan.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewConfiguration

@SuppressLint("ClickableViewAccessibility")
class ZoomableImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private var scaleGestureDetector: ScaleGestureDetector
    private var matrix: Matrix = Matrix()
    private var currentScale = 1f
    private val minScale = 1f
    private val maxScale = 5f
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f

    private var lastTapTime = 0L
    private val tapTimeout = ViewConfiguration.getTapTimeout().toLong()
    var onTapListener: (() -> Unit)? = null
    init {
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        scaleType = ScaleType.MATRIX
        setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                    lastTapTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {
                    if (System.currentTimeMillis() - lastTapTime <= tapTimeout) {
                        onTapListener?.invoke()
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY

                    if (currentScale > minScale) {
                        matrix.postTranslate(dx, dy)
                        imageMatrix = matrix
                    }

                    lastTouchX = event.x
                    lastTouchY = event.y
                }

            }

            true
        }

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) centerImage()
    }

    private fun isMatrixWithinBounds(matrix: Matrix): Boolean {
        val drawable = drawable ?: return false
        val imageRect = RectF(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
        matrix.mapRect(imageRect)

        return (imageRect.left >= 0 && imageRect.top >= 0 && imageRect.right <= width && imageRect.bottom <= height)
    }
    private fun adjustMatrix(dx: Float, dy: Float) {
        val testMatrix = Matrix(matrix)
        testMatrix.postTranslate(dx, dy)

        if (isMatrixWithinBounds(testMatrix)) {
            matrix.postTranslate(dx, dy)
            imageMatrix = matrix
        }
    }
    private fun centerImage() {
        val drawable = drawable ?: return
        val dWidth = drawable.intrinsicWidth
        val dHeight = drawable.intrinsicHeight
        val vWidth = width - paddingLeft - paddingRight
        val vHeight = height - paddingTop - paddingBottom

        val scale = (vWidth / dWidth.toFloat()).coerceAtMost(vHeight / dHeight.toFloat())
        matrix.setScale(scale, scale)

        val dx = (vWidth - dWidth * scale) / 2f
        val dy = (vHeight - dHeight * scale) / 2f
        matrix.postTranslate(dx, dy)

        imageMatrix = matrix
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scaleFactor = detector.scaleFactor
            val newScale = currentScale * scaleFactor
            if (newScale in minScale..maxScale) {
                matrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                imageMatrix = matrix
                currentScale = newScale
            }
            return true
        }
    }
}
