package huaiye.com.vim.common.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by liyawei on 2016-12-15.
 * phone 18952280597
 * QQ    751804582
 */

public class MyViewPager extends ViewPager {
    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException ignored) {
        }

        return false;

    }

}
