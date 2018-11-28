package kt.anko.ranguel.ankokotlin.view

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewConfiguration
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.OverScroller
import android.graphics.RectF




class PosterView : ImageView {

    private var mViewMode = 0
    private var mHaveFrame = false
    private var mSkipScaling = false
    private var mTranslateRightEdge = false
    private var mOuterTouchListener: OnTouchListener? = null
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mDragGestureDetector: GestureDetector? = null
    private var mScroller: OverScroller? = null
    private var mMinScale: Float = 0.toFloat()
    private var mMaxScale: Float = 0.toFloat()
    private var mOriginalScale: Float = 0.toFloat()
    private val m = FloatArray(9)
    private var mMatrix: Matrix? = null

    private val currentScale: Float
        get() {
            mMatrix!!.getValues(m)
            return m[Matrix.MSCALE_X]
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    fun setViewMode() {
        mViewMode = 0
        mSkipScaling = false
        requestLayout()
        invalidate()
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        mHaveFrame = true
        scale()
        return changed
    }

    override fun setImageDrawable(drawable: Drawable) {
        super.setImageDrawable(drawable)
        mSkipScaling = false
        scale()
    }

    private fun init() {
        mMatrix = Matrix()
        scaleType = ScaleType.MATRIX
        imageMatrix = mMatrix

        mScaleGestureDetector = ScaleGestureDetector(context, PrivateScaleDetector())
        mDragGestureDetector = GestureDetector(context, PrivateDragListener())
        super.setOnTouchListener { v, event ->
            mScaleGestureDetector!!.onTouchEvent(event)
            mDragGestureDetector!!.onTouchEvent(event)
            if (mOuterTouchListener != null) mOuterTouchListener!!.onTouch(v, event)
            true
        }

        mScroller = OverScroller(context)
        mScroller!!.setFriction(ViewConfiguration.getScrollFriction() * 2)
        mViewMode = 0
    }

    override fun setOnTouchListener(l: OnTouchListener) {
        mOuterTouchListener = l
    }

    fun setTranslateToRightEdge(translate: Boolean) {
        mTranslateRightEdge = translate
    }

    private fun scale() {
        val drawable = drawable
        if (drawable == null || !mHaveFrame || mSkipScaling) return

        val dwidth = drawable.intrinsicWidth
        val dheight = drawable.intrinsicHeight

        val vwidth = width
        val vheight = height

        val scale: Float
        var dx = 0f

        val mTempSrc = RectF(0f, 0f, dwidth.toFloat(), dheight.toFloat())
        val mTempDst = RectF(0f, 0f, vwidth.toFloat(), vheight.toFloat())

        mMatrix!!.setRectToRect(mTempSrc, mTempDst, Matrix.ScaleToFit.CENTER)

        // calculate min/max scale
        val heightRatio = vheight.toFloat() / dheight
        val w = dwidth * heightRatio
        if (w < vwidth) {
            mMinScale = vheight * 0.75f / dheight
            mMaxScale = Math.max(dwidth, vwidth) * 1.5f / dwidth
        } else {
            mMinScale = vwidth * 0.75f / dwidth
            mMaxScale = Math.max(dheight, vheight) * 1.5f / dheight
        }
        imageMatrix = mMatrix
        mOriginalScale = currentScale
        mSkipScaling = true
    }

    private inner class PrivateScaleDetector : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mMatrix!!.getValues(m)

            val scale = m[Matrix.MSCALE_X]
            var scaleFactor = detector.scaleFactor
            val scaleNew = scale * scaleFactor
            var scalable = true

            if (scaleFactor > 1 && mMaxScale - scaleNew < 0) {
                scaleFactor = mMaxScale / scale
                scalable = false
            } else if (scaleFactor < 1 && mMinScale - scaleNew > 0) {
                scaleFactor = mMinScale / scale
                scalable = false
            }

            mMatrix!!.postScale(
                scaleFactor, scaleFactor,
                detector.focusX, detector.focusY
            )
            imageMatrix = mMatrix

            return scalable
        }
    }

    private inner class PrivateDragListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            mScroller!!.forceFinished(true)
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            mMatrix!!.postTranslate(-distanceX, -distanceY)
            imageMatrix = mMatrix
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            val imageSize = computeCurrentImageSize()
            val offset = computeCurrentOffset()

            var minX = -imageSize.x - this@PosterView.width
            var minY = -imageSize.y - this@PosterView.height
            var maxX = 0
            var maxY = 0

            if (offset.x > 0) {
                minX = offset.x
                maxX = offset.x
            }
            if (offset.y > 0) {
                minY = offset.y
                maxY = offset.y
            }

            mScroller!!.fling(
                offset.x, offset.y,
                velocityX.toInt(), velocityY.toInt(),
                minX, maxX, minY, maxY
            )
            ViewCompat.postInvalidateOnAnimation(this@PosterView)
            return true
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            if (e.action == MotionEvent.ACTION_UP) {
                val scale = if (mOriginalScale == currentScale) mMaxScale else mOriginalScale
                zoomAnimated(e, scale)
            }
            return true
        }
    }

    private fun zoomAnimated(e: MotionEvent, scale: Float) {
        post(ZoomAnimation(e.x, e.y, scale))
    }

    override fun computeScroll() {
        if (!mScroller!!.isFinished && mScroller!!.computeScrollOffset()) {
            val curX = mScroller!!.currX
            val curY = mScroller!!.currY

            mMatrix!!.getValues(m)
            m[Matrix.MTRANS_X] = curX.toFloat()
            m[Matrix.MTRANS_Y] = curY.toFloat()
            mMatrix!!.setValues(m)
            imageMatrix = mMatrix
            ViewCompat.postInvalidateOnAnimation(this)
        }
        super.computeScroll()
    }

    private fun computeCurrentImageSize(): Point {
        val size = Point()
        val d = drawable
        if (d != null) {
            mMatrix!!.getValues(m)

            val scale = m[Matrix.MSCALE_X]
            val width = d.intrinsicWidth * scale
            val height = d.intrinsicHeight * scale

            size.set(width.toInt(), height.toInt())

            return size
        }

        size.set(0, 0)
        return size
    }

    private fun computeCurrentOffset(): Point {
        val offset = Point()

        mMatrix!!.getValues(m)
        val transX = m[Matrix.MTRANS_X]
        val transY = m[Matrix.MTRANS_Y]

        offset.set(transX.toInt(), transY.toInt())

        return offset
    }

    override fun setImageMatrix(matrix: Matrix?) {
        super.setImageMatrix(fixMatrix(matrix))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postInvalidate()
        }
    }

    private fun fixMatrix(matrix: Matrix?): Matrix? {
        if (drawable == null)
            return matrix

        matrix!!.getValues(m)

        val imageSize = computeCurrentImageSize()

        val imageWidth = imageSize.x
        val imageHeight = imageSize.y
        val maxTransX = imageWidth - width
        val maxTransY = imageHeight - height

        if (imageWidth > width)
            m[Matrix.MTRANS_X] = Math.min(0f, Math.max(m[Matrix.MTRANS_X], (-maxTransX).toFloat()))
        else
            m[Matrix.MTRANS_X] = (width / 2 - imageWidth / 2).toFloat()
        if (imageHeight > height)
            m[Matrix.MTRANS_Y] = Math.min(0f, Math.max(m[Matrix.MTRANS_Y], (-maxTransY).toFloat()))
        else
            m[Matrix.MTRANS_Y] = (height / 2 - imageHeight / 2).toFloat()

        matrix.setValues(m)
        return matrix
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        if (drawable == null)
            return false

        val imageWidth = computeCurrentImageSize().x
        val offsetX = computeCurrentOffset().x

        if (offsetX >= 0 && direction < 0) {
            return false
        } else if (Math.abs(offsetX) + width >= imageWidth && direction > 0) {
            return false
        }
        return true
    }

    private inner class ZoomAnimation internal constructor(
        internal var mX: Float,
        internal var mY: Float,
        internal var mScale: Float
    ) : Runnable {
        internal var mInterpolator: Interpolator
        internal var mStartScale: Float = 0.toFloat()
        internal var mStartTime: Long = 0

        init {
            mMatrix!!.getValues(m)

            mInterpolator = AccelerateDecelerateInterpolator()
            mStartScale = currentScale
            mStartTime = System.currentTimeMillis()
        }

        override fun run() {
            var t = (System.currentTimeMillis() - mStartTime).toFloat() / ZOOM_DURATION
            val interpolateRatio = mInterpolator.getInterpolation(t)
            t = if (t > 1f) 1f else t

            mMatrix!!.getValues(m)
            val newScale = mStartScale + interpolateRatio * (mScale - mStartScale)
            val newScaleFactor = newScale / m[Matrix.MSCALE_X]

            mMatrix!!.postScale(newScaleFactor, newScaleFactor, mX, mY)
            imageMatrix = mMatrix

            if (t < 1f) {
                post(this)
            } else {
                // set exact scale
                mMatrix!!.getValues(m)
                mMatrix!!.setScale(mScale, mScale)
                mMatrix!!.postTranslate(m[Matrix.MTRANS_X], m[Matrix.MTRANS_Y])
                setImageMatrix(mMatrix)
            }
        }

    }

    companion object {
        const val ZOOM_DURATION = 200
    }
}