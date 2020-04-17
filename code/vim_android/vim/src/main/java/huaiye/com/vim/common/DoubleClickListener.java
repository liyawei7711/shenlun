package huaiye.com.vim.common;

import android.view.View;

/**
 * Author: justin
 * Date: 16-4-16
 * Description:
 **/
public abstract class DoubleClickListener implements View.OnClickListener {

    // 每次响应点击事件 间隔
    long mDuration = 500;

    long mLastClickMillions;

    @Override
    public void onClick(View v) {
        if (doubleClickCheck()) {
            // 2s后再响应 避免频繁点击
            mLastClickMillions += 2000;
            onDoubleClick(v);
            return;
        }
    }

    /**
     * 本次点击是否与上一次点击间隔>=mDuration
     */
    boolean doubleClickCheck() {
        long currentMillions = System.currentTimeMillis();
        if (currentMillions < mLastClickMillions) {
            return false;
        } else {
            try {
                if (currentMillions - mLastClickMillions > mDuration) {
                    return false;
                }
            } finally {
                mLastClickMillions = currentMillions;
            }
        }
        return true;
    }

    public abstract void onDoubleClick(View v);
}
