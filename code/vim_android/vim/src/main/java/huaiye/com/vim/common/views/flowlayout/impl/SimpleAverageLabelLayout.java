package huaiye.com.vim.common.views.flowlayout.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: hujinqi
 * Date  : 2016-06-04
 * Description: 豆腐块 宽高均分
 */
public class SimpleAverageLabelLayout extends LabelLayout {
    /**
     * @param context
     * @param attrs
     */
    public SimpleAverageLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mMaxColumns == -1
                || mMaxLines == -1)
            return;

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int childWidth = width / mMaxColumns;
        int childHeight = height / mMaxLines;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            childView.measure(childWidth +
                    View.MeasureSpec.EXACTLY, childHeight + View.MeasureSpec.EXACTLY);
        }
    }
}
