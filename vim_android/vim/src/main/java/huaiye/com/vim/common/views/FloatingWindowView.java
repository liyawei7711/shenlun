package huaiye.com.vim.common.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

public class FloatingWindowView extends FrameLayout {
    OnKeyListener mOnKeyListener = null;
    public FloatingWindowView(@NonNull Context context) {
        super(context);
    }

    public FloatingWindowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingWindowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                || event.getKeyCode() == KeyEvent.KEYCODE_SETTINGS) {
            if(getChildCount()==2&&getChildAt(1).getVisibility()== View.VISIBLE){
                if (mOnKeyListener != null) {
                    mOnKeyListener.onKey(this, KeyEvent.KEYCODE_BACK, event);
                }
                return true;
            }else{
                return super.dispatchKeyEvent(event);
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setOnKeyListener(OnKeyListener onKeyListener) {
        mOnKeyListener = onKeyListener;
        super.setOnKeyListener(onKeyListener);
    }

}
