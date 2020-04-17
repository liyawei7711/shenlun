package huaiye.com.vim.common.views;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Method;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;

import static huaiye.com.vim.common.AppUtils.xuanfuDevice;


/**
 * Created by liyawei on 2017-01-06.
 * phone 18952280597
 * QQ    751804582
 */

public class PermissionUtils {
    /**
     * 检测权限
     *
     * @param mContext
     * @return
     */
    static boolean checkAlertPermission(Context mContext) {
        boolean permission = false;
        try {
            @SuppressLint("WrongConstant") Object object = mContext.getSystemService("appops");
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = Integer.valueOf(24);
            arrayOfObject1[1] = Integer.valueOf(Binder.getCallingUid());
            arrayOfObject1[2] = mContext.getPackageName();
            int m = ((Integer) method.invoke(object, arrayOfObject1)).intValue();
            permission = (m == AppOpsManager.MODE_ALLOWED);
        } catch (Exception ex) {

        }
        return permission;
    }

    /**
     * 小米权限问题
     *
     * @param mContext
     * @return
     */
    public static boolean XiaoMiMobilePermission(Context mContext) {
        if (xuanfuDevice.contains(Build.MODEL) || xuanfuDevice.contains(Build.MANUFACTURER)) {
            if (!PermissionUtils.checkAlertPermission(mContext)) {
                AppBaseActivity.showToast(AppUtils.getString(R.string.has_power_float));

                Uri packageURI = Uri.parse("package:" + mContext.getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppUtils.ctx.startActivity(intent);
                return true;
            }
        }
        return false;
    }

}
