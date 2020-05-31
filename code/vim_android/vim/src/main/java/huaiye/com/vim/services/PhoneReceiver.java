package huaiye.com.vim.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.greenrobot.eventbus.EventBus;

import huaiye.com.vim.bus.PhoneStatus;

/**
 * Created by Administrator on 2018\3\26 0026.
 */

public class PhoneReceiver extends BroadcastReceiver {
    PhoneStatus BUSY = new PhoneStatus(true);
    PhoneStatus EMPTY = new PhoneStatus(false);
    MyPhoneListener myPhoneCallListener = new MyPhoneListener();

    TelephonyManager tm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (tm == null)
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneCallListener.onCallStateChanged(tm.getCallState(), "");
//        tm.listen(myPhoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyPhoneListener extends PhoneStateListener {
        /**
         * 当电话状态改变了将会执行该方法
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    EventBus.getDefault().post(EMPTY);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    EventBus.getDefault().post(BUSY);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
            }

        }

    }
}
