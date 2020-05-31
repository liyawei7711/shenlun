package huaiye.com.vim.common.views.flowlayout.impl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: hujinqi
 * Date  : 2016-06-04
 * Description: 豆腐块 宽高相等 已宽为准
 */
public class SimpleTagsLabelLayout extends LabelLayout {
    /**
     * @param context
     * @param attrs
     */
    public SimpleTagsLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mMaxColumns == -1)
            return;

        int width = getMeasuredWidth();

        int childWidth = width / mMaxColumns;
        int childHeight = mTagsViewHeight;
        if (childHeight == 0) {
            childHeight = childWidth;
        }

        int childCount = getChildCount();
        int visibleCount = 0;
        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            visibleCount++;
            childView.measure(childWidth +
                    View.MeasureSpec.EXACTLY, childHeight + View.MeasureSpec.EXACTLY);
        }

        int totalLinesNum = visibleCount / mMaxColumns;
        if (visibleCount % mMaxColumns != 0) {
            totalLinesNum++;
        }

        int rootHeight = totalLinesNum * childHeight;
        this.setMeasuredDimension(width + View.MeasureSpec.EXACTLY, rootHeight + View.MeasureSpec.EXACTLY);
    }
}
