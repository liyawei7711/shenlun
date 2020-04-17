package huaiye.com.vim.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.RadioButton;

import huaiye.com.vim.R;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppRadioButton
 */

public class AppRadioButton extends RadioButton {

    int nUnReadNumber = 0;
    int nUnReadBackColor = Color.RED;
    int nUnReadTextColor = Color.WHITE;
    int nUnReadTextSize = 10;
    int nUnReadCircleRadius = 20;

    int nCircleRightOffset = 0;
    int nCircleTopOffset = 0;

    Paint mPaint;
    TextPaint mTextPaint;

    public AppRadioButton(Context context) {
        this(context, null);
    }

    public AppRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AppRadioButton);

        nUnReadNumber = ta.getColor(R.styleable.AppRadioButton_unReadNumber, 0);
        nUnReadBackColor = ta.getColor(R.styleable.AppRadioButton_unReadBackColor, Color.RED);
        nUnReadTextColor = ta.getColor(R.styleable.AppRadioButton_unReadTextColor, Color.WHITE);
        nUnReadTextSize = ta.getDimensionPixelSize(R.styleable.AppRadioButton_unReadTextSize, 16);
        nUnReadCircleRadius = ta.getDimensionPixelOffset(R.styleable.AppRadioButton_unReadCircleRadius, 20);

        nCircleRightOffset = ta.getDimensionPixelOffset(R.styleable.AppRadioButton_unReadCircleRightOffset, nUnReadCircleRadius);
        nCircleTopOffset = ta.getDimensionPixelOffset(R.styleable.AppRadioButton_unReadCircleTopOffset, nUnReadCircleRadius);

        ta.recycle();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(nUnReadBackColor);
        mPaint.setAntiAlias(true);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(nUnReadTextColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(nUnReadTextSize);
    }

    public void setUnReadNumber(int number) {
        this.nUnReadNumber = number;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (nUnReadNumber > 0) {
            int x = getWidth() - nCircleRightOffset;
            int y = nCircleTopOffset + nUnReadCircleRadius;
            canvas.drawCircle(x, y, nUnReadCircleRadius, mPaint);

            int textX = x;
            int textY = y;

            if (nUnReadNumber < 100) {
                Paint.FontMetrics fm = mTextPaint.getFontMetrics();
                textY = (int) (textY - (fm.top + fm.bottom) / 2);
            }

            String text = nUnReadNumber > 99 ? "..." : "" + nUnReadNumber;
            canvas.drawText(text, textX, textY, mTextPaint);
        }
    }
}
