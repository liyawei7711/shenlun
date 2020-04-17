package huaiye.com.vim.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import huaiye.com.vim.R;

/**
 * Created by ywt on 2019/2/21.
 */

public class StatusbarUtil {
    /**
     * 状态栏亮色模式，设置状态栏黑色文字、图标，
     * 适配Android6.0及以上版本
     * Android6.0以下版本状态栏背景使用colorResId颜色值
     */
    public static void StatusBarLightMode(Activity activity, int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

            activity.getWindow().getDecorView().
                    setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
        } else {
            StatusBarByColor(activity, colorResId);
        }
    }

    /**
     * 状态栏暗色模式，白色文字、图标
     * 适配Android6.0以上版本
     */
    public static void StatusBarDarkMode(Activity activity, int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(activity, colorResId));
            }
        }
    }

    /**
     * 设置指定的状态栏颜色
     */
    public static void StatusBarByColor(Activity activity, int colorResId) {
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

    /**
     * 修改状态栏为全透明，需要在setContentView前调用
     * 布局会被透明状态栏覆盖，需要设置布局paddingTop（系统默认状态栏高度25dp）或fitsSystemWindows
     * @param activity
     */
    @TargetApi(19)
    public static void transparencyBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //api21新增接口
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**透明 亮色、暗色状态栏*/
    public static void transparencyDisplayModeBar(Activity activity, boolean isLight) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            if(isLight){
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //api21新增接口,状态栏全透
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
