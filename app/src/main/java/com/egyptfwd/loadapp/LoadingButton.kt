package com.egyptfwd.loadapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var rect = RectF()
    private var textBoundRect = Rect()

    private var buttonText = ""

    private var progress = 0.0

    private var notDownloadingColor = 0
    private var downloadingColor = 0
    private var textColor = 0

    private var valueAnimator = ValueAnimator()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 35.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = resources.getColor(R.color.colorPrimary)
    }

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->

        when (new) {
            ButtonState.Loading -> {

                buttonText = "Downloading File..."

                valueAnimator = ValueAnimator.ofFloat(0f, 100f)
                valueAnimator.duration = 2000
                valueAnimator.repeatMode = ValueAnimator.RESTART
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.interpolator = AccelerateInterpolator(1f)
                valueAnimator.addUpdateListener {
                    progress = (it.animatedValue as Float).toDouble()
                    invalidate()
                }
                valueAnimator.start()
            }

            ButtonState.Clicked -> {

            }

            ButtonState.Completed -> {

                buttonText = "Download"

                valueAnimator.end()

            }
        }
        invalidate()

    }


    init {
        isClickable = true
        buttonText = "DownLoad"

        context.withStyledAttributes(attrs, R.styleable.LoadingButton){

            notDownloadingColor = getColor(R.styleable.LoadingButton_notDownloadingColor, resources.getColor(R.color.colorPrimary))
            downloadingColor = getColor(R.styleable.LoadingButton_downloadingColor, resources.getColor(R.color.colorPrimaryDark))
            textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //draw custom button when not downloading
        paint.color = notDownloadingColor
        canvas?.drawRect(
            0.0f,
            0.0f,
            width.toFloat(),
            height.toFloat(),
            paint
        )

        //draw text when not downloading
        paint.color = textColor
        canvas?.drawText(
            buttonText,
            (width / 2).toFloat(),
            ((height + 30) / 2).toFloat(),
            paint
        )


        if (buttonState == ButtonState.Loading) {

            //draw custom button when downloading
            paint.color = downloadingColor
            canvas?.drawRect(
                0f,
                0f,
                (widthSize * (progress / 100)).toFloat(),
                height.toFloat(),
                paint
            )


            //draw text when downloading
            paint.getTextBounds(
                buttonText,
                0,
                buttonText.length,
                textBoundRect
            )

            //draw circle when downloading
            paint.color = resources.getColor(R.color.colorAccent)
            rect.set(
                measuredWidth.toFloat() / 2 + textBoundRect.right / 2 + 40.0f,
                30.0f,
                measuredWidth.toFloat() / 2 + textBoundRect.right / 2 + 80.0f,
                measuredHeight.toFloat() - 25.0f
            )
            canvas?.drawArc(
                rect,
                0f,
                (360 * (progress / 100)).toFloat(),
                true,
                paint
            )

        }


        else if (buttonState == ButtonState.Completed) {

            //draw custom button after downloading
            paint.color = notDownloadingColor
            canvas?.drawRect(
                0f,
                0f,
                (widthSize * (progress / 100)).toFloat(),
                heightSize.toFloat(),
                paint
            )

            //draw text after downloading
            paint.color = Color.WHITE
            canvas?.drawText(
                buttonText,
                (width / 2).toFloat(),
                ((height + 30) / 2).toFloat(),
                paint
            )

        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h

        setMeasuredDimension(w, h)
    }

}