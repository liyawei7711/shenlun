package huaiye.com.vim.common.views;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.SimpleViewBean;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.msgs.CaptureMessage;
import huaiye.com.vim.ui.Capture.CaptureGuanMoOrPushActivity;

import static android.content.Context.WINDOW_SERVICE;
import static huaiye.com.vim.common.AppUtils.ctx;
import static huaiye.com.vim.common.AppUtils.getSize;


/**
 * Created by liyawei on 2017-01-19.
 * phone 18952280597
 * QQ    751804582
 * if(WindowManagerUtils.bigShowView != null) {
 * WindowManagerUtils.closeAll();
 * } else {
 * SimpleDemoView simpleDemoView = new SimpleDemoView(MyApplication.getInstence());
 * WindowManagerUtils.createSmalls(simpleDemoView, true);
 * }
 */

public class WindowManagerUtils {

    public enum CaptureModel {
        CAPTURE_GUANMO_MODEL,
        CAPTURE_PUSH_MODEL
    }

    // 创建LayoutParams
    private static WindowManager.LayoutParams layoutParams;
    // 添加View到窗口
    private static WindowManager windowManager;// = (WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE);
    public static FloatingWindowView parentLayout;

    public static SimpleView smallShowView;
    public static DisplayMetrics displayMetrics;
    public static long curTimeStamp;
    public WindowManagerUtils() {
        initWindowManager();
    }

    /**
     * 初始话manager
     */
    private static void initWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) ctx.getApplicationContext().getSystemService(WINDOW_SERVICE);
        }
    }

    public static WindowManager getWindownManager() {
        if (windowManager == null)
            initWindowManager();
        return windowManager;
    }

    public static WindowManager.LayoutParams getLayoutParams() {
        if (layoutParams == null)
            initLayoutParams();
        return layoutParams;
    }

    private static void initLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        // 设置窗口的类型
        if (Build.VERSION.SDK_INT >= 23) {

            if (!Settings.canDrawOverlays(ctx)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
                AppBaseActivity.showToast(AppUtils.getString(R.string.has_connected_false));
                closeAll();//这个时候需要获取到授权否则无法展示悬浮框
                return;
            }

            if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                //绘ui代码, 这里说明6.0系统已经有权限了
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
        } else {
            //绘ui代码,这里android6.0以下的系统直接绘出即可
            //API level 18及以下使用TYPE_TOAST无法接收触摸事件
            if (Build.VERSION.SDK_INT <= 18) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        // 设置行为选项
        //        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;


        layoutParams.format = PixelFormat.RGBA_8888;
        // 设置透明度
        //        layoutParams.alpha = 0;
        displayMetrics = ctx.getResources().getDisplayMetrics();
        // 设置位置
        layoutParams.x = getSize(20);
        layoutParams.y = getSize(60);
        // 设置宽高
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;

    }

    public static void createOriginalView(Object data, CaptureModel captureModel) {
        if (PermissionUtils.XiaoMiMobilePermission(ctx)) {
            return;
        }

        parentLayout = getParentLayout();
        if (null != parentLayout && parentLayout.getChildCount() > 0) {
            parentLayout.removeAllViews();
        }
        smallShowView = getSmallShowView(data, captureModel);
        parentLayout.addView(smallShowView);
        if (ViewCompat.isAttachedToWindow(parentLayout)) {
            try {
                getWindownManager().removeView(parentLayout);
            } catch (Exception e) {

            }
        }

        try {
            if (!ViewCompat.isAttachedToWindow(parentLayout)) {
                getWindownManager().addView(parentLayout, getLayoutParams());
            }
        } catch (Exception e) {
            Logger.log("SIMPLEVIEW ERROR createSmall " + e.toString());
        }
    }

    private static FloatingWindowView getParentLayout() {
        if (null == parentLayout) {
            parentLayout = new FloatingWindowView(ctx);
        }
        parentLayout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                }
                return false;
            }
        });
        return parentLayout;
    }

    private static SimpleView getSmallShowView(Object data, CaptureModel captureModel) {
        if (smallShowView == null) {
            smallShowView = new SimpleView(ctx);
        }

        if (captureModel == CaptureModel.CAPTURE_GUANMO_MODEL) {
            smallShowView.setTileName(AppUtils.getString(R.string.video_diaodu_watching));
            smallShowView.setLogo(R.drawable.huiyitonghua);
        } else if (captureModel == CaptureModel.CAPTURE_PUSH_MODEL) {
            smallShowView.setTileName(AppUtils.getString(R.string.video_diaodu_ing));
            smallShowView.setLogo(R.drawable.shipingtonghua);
        }

        smallShowView.setOnClickListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(System.currentTimeMillis() - curTimeStamp < 1000) {
                    return;
                }
                curTimeStamp = System.currentTimeMillis();

                EventBus.getDefault().post(new SimpleViewBean(captureModel, data));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeAll();
                    }
                }, 800);

            }
        });
        return smallShowView;
    }

    /**
     * 关闭所有窗体
     */
    public static void closeAll() {
        if (parentLayout != null && ViewCompat.isAttachedToWindow(parentLayout)) {
            try {
                getWindownManager().removeView(parentLayout);
            } catch (Exception e) {
                Logger.log("SIMPLEVIEW ERROR closeAll " + e.toString());
            }
        }
        parentLayout = null;
        smallShowView = null;
        windowManager = null;
        layoutParams = null;
    }

    public static boolean isVisiable() {
        if (null != parentLayout) {
            return ViewCompat.isAttachedToWindow(parentLayout);
        } else {
            return false;
        }
    }

    public static void justRemove() {
        if (parentLayout != null && ViewCompat.isAttachedToWindow(parentLayout)) {
            try {
                getWindownManager().removeView(parentLayout);
            } catch (Exception e) {
                Logger.log("SIMPLEVIEW ERROR justRemove " + e.toString());
            }
        }
    }

    public static void justReShow() {
        if (parentLayout != null && !ViewCompat.isAttachedToWindow(parentLayout)) {
            try {
                getWindownManager().addView(parentLayout, getLayoutParams());
            } catch (Exception e) {
                Logger.log("SIMPLEVIEW ERROR justReShow " + e.toString());
            }
        }
    }

    public static void showTime(String s) {
        if (smallShowView != null && ViewCompat.isAttachedToWindow(smallShowView)) {
            smallShowView.setTimeText(s);
        }
    }
}
