package huaiye.com.vim.push;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import huaiye.com.vim.common.SP;

import static android.content.ContentValues.TAG;

/**
 * author: admin
 * date: 2018/01/22
 * version: 0
 * mail: secret
 * desc: MobileRingingReceiver
 */

public class MobileRingingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //如果是拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {

        } else {
            //如果是来电
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // 来电
                    Log.d("PhoneState", "来电");
                    boolean isSettedAnswer = SP.getBoolean("MOBILE_RING_ANSWER", true);
                    if (isSettedAnswer) {

                    } else {
                        endCall(context);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // 电话接通/接通挂断都会触发
                    Log.d("PhoneState", "CALL_STATE_OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // 空闲状态
                    Log.d("PhoneState", "空闲");
                    break;
            }
        }
    }

    // 挂断
    void endCall(Context context) {
        Class<TelephonyManager> c = TelephonyManager.class;
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);
            iTelephony.endCall();
        } catch (Exception e) {
            Log.e(TAG, "Fail to answer ring call.", e);
        }
    }
}
