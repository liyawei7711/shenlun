package huaiye.com.vim.common.views.flowlayout.impl;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import huaiye.com.vim.R;
import huaiye.com.vim.common.views.flowlayout.base.FlowBaseLayout;


/**
 * ******************************
 *
 * @文件名称:FlowLayout.java
 * @文件作者:Administrator
 * @创建时间:2015年9月28日
 * @文件描述:标签布局 行高相等
 * *****************************
 */
public class LabelLayout extends FlowBaseLayout {

    /**
     * 最大行数
     */
    protected int mMaxLines;

    /**
     * 最大列数
     */
    protected int mMaxColumns;

    /**
     * 行间距
     */
    protected int mLineMargin;

    /**
     * 列间距
     */
    protected int mColumnMargin;

    protected int mTagsViewHeight;

    /**
     * @param context
     * @param attrs
     */
    public LabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.FlowBaseLayout);
        mMaxColumns = ta.getInt(R.styleable.FlowBaseLayout_maxColumns, -1);
        mMaxLines = ta.getInt(R.styleable.FlowBaseLayout_maxLines, -1);
        mLineMargin = (int) ta.getDimension(R.styleable.FlowBaseLayout_linemargin,
                0);
        mColumnMargin = (int) ta.getDimension(
                R.styleable.FlowBaseLayout_columnmargin, 0);
        mTagsViewHeight = (int) ta.getDimension(R.styleable.FlowBaseLayout_tagHeight, 0);
        ta.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {

            int height = measureHeight();
            setMeasuredDimension(getMeasuredWidth(), height);
        }
    }

    /**
     * 计算ViewGroup大小
     *
     * @return view高度
     */
    protected int measureHeight() {

        int childCount = getChildCount();

        int lines = 1;

        int lineMaxWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int lineMaxHeight = 0;

        int lineCurrHeight = 0;
        int lineCurrWidth = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            if (i > 0) {
                lineCurrWidth += childWidth + mColumnMargin;
            } else {
                lineCurrWidth += childWidth;
            }

            if (mMaxColumns > 0) {
                // 列数有限制的情况下 优先当前列Index判断
                // 换行处理

                int currColumnIndex = i % mMaxColumns;
                if (i > 0 && currColumnIndex == 0) {

                    lines++;
                    // 行数限制判断
                    if (mMaxLines > 0 && lines > mMaxLines) {
                        break;
                    }
                    lineMaxHeight += lineCurrHeight + mLineMargin;
                    lineCurrWidth = childWidth;

                }

            } else if (lineCurrWidth > lineMaxWidth) {
                // 没有列数限制
                // 换行处理

                lines++;
                // 行数限制判断
                if (mMaxLines > 0 && lines > mMaxLines) {
                    break;
                }
                lineCurrWidth = childWidth;
                lineMaxHeight += lineCurrHeight + mLineMargin;
            }

            // 每一行的最大高度
            lineCurrHeight = lineCurrHeight > childHeight ? lineCurrHeight : childHeight;
        }

        lineMaxHeight += lineCurrHeight;

        lineMaxHeight += getPaddingTop() + getPaddingBottom();

        return lineMaxHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount <= 0)
            return;

        int lines = 1;

        int lineMaxWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        int lineCurrHeight = 0;
        int lineCurrWidth = 0;

        int startX = getPaddingLeft();
        int startY = getPaddingTop();

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            if (i > 0) {
                lineCurrWidth += childWidth + mColumnMargin;
            } else {
                lineCurrWidth += childWidth;
            }

            if (mMaxColumns > 0) {
                // 列数有限制的情况下 优先当前列Index判断
                // 换行处理

                int currColumnIndex = i % mMaxColumns;
                if (i > 0 && currColumnIndex == 0) {

                    lines++;
                    // 行数限制判断
                    if (mMaxLines > 0 && lines > mMaxLines) {
                        break;
                    }
                    lineCurrWidth = childWidth;

                    startX = getPaddingLeft();
                    startY += lineCurrHeight + mLineMargin;
                }

            } else if (lineCurrWidth > lineMaxWidth) {
                // 没有列数限制
                // 换行处理

                lines++;
                // 行数限制判断
                if (mMaxLines > 0 && lines > mMaxLines) {
                    break;
                }
                lineCurrWidth = childWidth;

                startX = getPaddingLeft();
                startY += lineCurrHeight + mLineMargin;
            }

            // 每一行的最大高度
            lineCurrHeight = lineCurrHeight > childHeight ? lineCurrHeight : childHeight;

            childView.layout(startX, startY, startX + childWidth, startY + childHeight);
            startX += childWidth + mColumnMargin;
        }
    }

}
