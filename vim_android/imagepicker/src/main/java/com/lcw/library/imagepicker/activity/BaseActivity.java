package com.lcw.library.imagepicker.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lcw.library.imagepicker.R;

/**
 * BaseActivity基类
 * Create by: chenWei.li
 * Date: 2018/10/9
 * Time: 下午11:34
 * Email: lichenwei.me@foxmail.com
 */
public abstract class BaseActivity extends AppCompatActivity {

    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        transparencyDisplayModeBar(this,false);
        setStatusBarByColor(this, R.color.titlebar_30313C);
        super.onCreate(savedInstanceState);
        if (mView == null) {
            mView = View.inflate(this, bindLayout(), null);
        }
        setContentView(mView);

        initConfig();
        initView();
        initListener();
        getData();
    }


    /**
     * 设置指定的状态栏颜色
     */
    public void setStatusBarByColor(Activity activity, int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, colorResId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用系统的状态栏
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            StatusBarCompat.setStatusBarColor(activity, colorResId);
            /*SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorResId);*/
        }
    }


    protected abstract int bindLayout();

    protected void initConfig() {
    }

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void getData();


}
