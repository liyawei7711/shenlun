package huaiye.com.vim.common.views.flowlayout.base;

import android.view.View;

/**
 * ******************************
 *
 * @文件名称:FlowLayoutAdapter.java
 * @文件作者:Administrator
 * @创建时间:2015年9月28日
 * @文件描述:流式布局内容适配器 *****************************
 */
public abstract class FlowBaseAdapter {

    /**
     * 订阅者模式
     **/
    private OnDatasetChangedNotifyer notifyer;

    /**
     * Item数量
     */
    public abstract int getCount();

    /**
     * 获取当前位置上的View
     */
    public abstract View getView(int position);

    public final void notifyDatasetChanged() {
        notifyer.onDatasetChanged();
    }

    protected final void setDatasetChangedNotifyer(OnDatasetChangedNotifyer notifyer) {
        this.notifyer = notifyer;
    }

    /**
     * ******************************
     *
     * @文件名称:FlowBaseAdapter.java
     * @文件作者:Administrator
     * @创建时间:2015年9月28日
     * @文件描述:数据变化监听者 *****************************
     */
    protected interface OnDatasetChangedNotifyer {

        public void onDatasetChanged();

    }

}
