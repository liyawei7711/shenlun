package huaiye.com.vim.ui.showfile.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class SwanTextView extends TextView {

    private int mPreBottom = -1;
    private OnTextChangedListener textChangedListener = null;

    public SwanTextView(Context context) {
        super(context);
    }

    public SwanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwanTextView(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mPreBottom != getBottom()) {
            mPreBottom = getBottom();

            if (textChangedListener != null)
                textChangedListener.onPreOnDraw(mPreBottom);
        }

        super.onDraw(canvas);
    }

    public static interface OnTextChangedListener {
        public void onPreOnDraw(int bottom);
    }

    public void setOnTextChangedListener(OnTextChangedListener listener) {
        textChangedListener = listener;
    }

}
