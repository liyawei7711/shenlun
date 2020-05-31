package huaiye.com.vim.common.views.flowlayout.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import java.util.ArrayList;

import huaiye.com.vim.common.views.flowlayout.callback.OnFlowItemClickedListener;
import huaiye.com.vim.common.views.flowlayout.mode.FlowChoiceMode;

/**
 * ******************************
 *
 * @文件名称:FlowBaseLayout.java
 * @文件作者:Administrator
 * @创建时间:2015年9月29日
 * @文件描述: *****************************
 */
public abstract class FlowBaseLayout extends ViewGroup implements FlowBaseAdapter.OnDatasetChangedNotifyer {

    /**
     * 选中模式
     **/
    protected FlowChoiceMode mChoiceMode = FlowChoiceMode.SingleChoice;

    protected ArrayList<View> mCheckedViews;

    protected FlowBaseAdapter mAdapter;

    // 点击事件相关
    private OnClickListener mClickListener;
    private OnFlowItemClickedListener mItemClickedListener;

    /**
     * @param context
     * @param attrs
     */
    public FlowBaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCheckedViews = new ArrayList<View>();
        mClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设置选中项
                setItemChecked(v, true);

                if (mItemClickedListener != null) {
                    int index = indexOfChild(v);
                    mItemClickedListener.onItemClicked(index, v);
                }
            }
        };
    }

    /**
     * 设置选中模式
     */
    public final void setChoiceMode(FlowChoiceMode mode) {
        this.mChoiceMode = mode;
    }

    /**
     * 设置当前Index选中状态
     */
    public final void setItemChecked(int position, boolean isChecked) {
        View checkedView = getChildAt(position);
        if (checkedView == null) {
            return;
        }

        setItemChecked(checkedView, isChecked);
    }

    /**
     * 设置当前View 选中状态
     */
    protected final void setItemChecked(View view, boolean isChecked) {
        if (mChoiceMode == FlowChoiceMode.None)
            return;

        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(isChecked);
        }

        updateCurrentCheckedViews(view, isChecked);
    }

    /**
     * 更新当前CheckedView池
     */
    protected void updateCurrentCheckedViews(View view, boolean isChecked) {

        // 非选中状态 若checkedView池含有 就移除该View
        if (!isChecked) {
            if (mCheckedViews.contains(view)) {
                mCheckedViews.remove(view);
            }
            return;
        }

        // 选中状态 根据ChoiceMode 作出相应的处理
        switch (mChoiceMode) {
            case SingleChoice:
                if (mCheckedViews.size() > 0
                        && !mCheckedViews.contains(view)) {
                    View lastCheckedView = mCheckedViews.get(0);
                    if (lastCheckedView instanceof Checkable) {
                        ((Checkable) lastCheckedView).setChecked(false);
                    }
                }

                mCheckedViews.clear();
                mCheckedViews.add(view);

                break;
            case MultieChoice:

                mCheckedViews.add(view);

                break;
            default:
                break;
        }
    }

    /**
     * 获取选中的标签Index<br>
     * 选中模式必须为 SingleChoice Mode
     */
    public final int getCheckedItem() {
        if (mChoiceMode != FlowChoiceMode.SingleChoice)
            return -1;

        if (mCheckedViews.size() > 0) {
            View childView = mCheckedViews.get(mCheckedViews.size() - 1);
            return indexOfChild(childView);
        }

        return -1;
    }

    /**
     * 获取选中的多个标签Indexs<br>
     * 选中模式必须为 MutilChoice Mode
     */
    public final ArrayList<Integer> getCheckedItems() {
        if (mChoiceMode != FlowChoiceMode.MultieChoice)
            return null;

        ArrayList<Integer> indexs = new ArrayList<Integer>();
        for (View childView : mCheckedViews) {
            indexs.add(indexOfChild(childView));
        }

        return indexs;
    }

    /**
     * 设置内容适配器
     */
    public final void setAdapter(FlowBaseAdapter adapter) {
        this.mAdapter = adapter;
        this.mAdapter.setDatasetChangedNotifyer(this);
        // 重新刷新
        this.removeAllViews();

        if (adapter == null) {

            return;
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i);

            if (view instanceof Checkable) {
                if (((Checkable) view).isChecked()) {
                    mCheckedViews.add(view);
                }
            }

            view.setOnClickListener(mClickListener);
            this.addView(view);
        }
    }

    /**
     * 设置流式布局点击事件
     */
    public final void setOnFlowItemClickedListener(OnFlowItemClickedListener listener) {
        if (listener == null)
            return;

        mItemClickedListener = listener;

//		for(int i = 0 ; i < getChildCount() ; i++){
//			getChildAt(i).setOnClickListener(mClickListener);
//		}
    }

    @Override
    public final void onDatasetChanged() {
        removeAllViews();

        if (mAdapter == null) {

            return;
        }

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View view = mAdapter.getView(i);
            view.setOnClickListener(mClickListener);
            this.addView(view);
        }

    }
}
