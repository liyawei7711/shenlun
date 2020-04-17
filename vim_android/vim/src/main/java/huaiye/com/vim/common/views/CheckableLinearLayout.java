package huaiye.com.vim.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * ******************************
 *
 * @文件名称:CheckableLinearLayout.java
 * @文件作者:Administrator
 * @创建时间:2015年9月25日
 * @文件描述: *****************************
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private boolean isChecked = false;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    private OnCheckedChangedListener listener;

    /**
     * @param context
     * @param attrs
     */
    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            if (listener != null) {
                listener.onCheckedChanged(this, checked);
            }
        }
        isChecked = checked;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public void setOnCheckedListener(OnCheckedChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void toggle() {
        isChecked = !isChecked;
        if (listener != null) {
            listener.onCheckedChanged(this, isChecked);
        }
    }

    public interface OnCheckedChangedListener {
        void onCheckedChanged(View parent, boolean isChecked);
    }

}
