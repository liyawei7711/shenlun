package huaiye.com.vim.ui.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import huaiye.com.vim.R;

/**
 * author: zhangzhen
 * date: 2019/07/27
 * version: 0
 * mail: secret
 * desc: ChatSendLocationDialog
 */

public class ChatSendLocationDialog extends Dialog implements View.OnClickListener {
    TextView dialog_chat_send_location_current;
    TextView dialog_chat_send_location_always;

    private IFunctionClickedListener iFunctionClickedListener;

    public interface IFunctionClickedListener {
        void onClickedSendCustomLocation();

        void onClickedAlwaysSendCustomLocation();

    }

    public void setOnFunctionClickedListener(IFunctionClickedListener iFunctionClickedListener) {
        this.iFunctionClickedListener = iFunctionClickedListener;
    }

    public ChatSendLocationDialog(@NonNull Context context) {
        super(context);

        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_chat_send_location);
        init();
    }

    private void init() {
        dialog_chat_send_location_current = (TextView) findViewById(R.id.dialog_chat_send_location_current);
        dialog_chat_send_location_always = (TextView) findViewById(R.id.dialog_chat_send_location_always);
        dialog_chat_send_location_current.setOnClickListener(this);
        dialog_chat_send_location_always.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dialog_chat_send_location_current) {
            iFunctionClickedListener.onClickedSendCustomLocation();

        } else {
            iFunctionClickedListener.onClickedAlwaysSendCustomLocation();
        }
        dismiss();
    }

}
