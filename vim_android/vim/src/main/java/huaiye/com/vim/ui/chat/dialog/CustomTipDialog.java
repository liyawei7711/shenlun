package huaiye.com.vim.ui.chat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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

public class CustomTipDialog extends Dialog implements View.OnClickListener {
    TextView custom_dialog_content;

    TextView custom_dialog_left_btn;
    TextView custom_dialog_right_btn;

    private IFunctionClickedListener iFunctionClickedListener;

    public interface IFunctionClickedListener {
        void onClickedLeftFunction();

        void onClickedRightFunction();

    }

    public void setOnFunctionClickedListener(IFunctionClickedListener iFunctionClickedListener) {
        this.iFunctionClickedListener = iFunctionClickedListener;
    }

    public CustomTipDialog(@NonNull Context context) {
        this(context,"");
    }

    public CustomTipDialog(@NonNull Context context,String content) {
        super(context,R.style.customDialog);

        setCancelable(true);
        setCanceledOnTouchOutside(true);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable());
        getWindow().setGravity(Gravity.CENTER);
        setContentView(R.layout.dialog_custom_tip);
        init();
        setContent(content);
    }

    public void setContent(String content) {
        if(null!=custom_dialog_content&&!TextUtils.isEmpty(content)){
            custom_dialog_content.setText(content);
        }
    }

    private void init() {
        custom_dialog_left_btn = (TextView) findViewById(R.id.custom_dialog_left_btn);
        custom_dialog_right_btn = (TextView) findViewById(R.id.custom_dialog_right_btn);
        custom_dialog_content = (TextView) findViewById(R.id.custom_dialog_content);
        custom_dialog_left_btn.setOnClickListener(this);
        custom_dialog_right_btn.setOnClickListener(this);
    }

    public void setLeftFunctionText(String content){
        custom_dialog_left_btn.setText(content);
    }

    public void setRightFunctionText(String content){
        custom_dialog_right_btn.setText(content);

    }

    public void hideLeftFunctionText() {
        custom_dialog_left_btn.setVisibility(View.GONE);
    }

    public void hideRightFunctionText() {
        custom_dialog_right_btn.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.custom_dialog_left_btn) {
            iFunctionClickedListener.onClickedLeftFunction();
        } else if (v.getId() == R.id.custom_dialog_right_btn){
            iFunctionClickedListener.onClickedRightFunction();
        }
        dismiss();
    }

}
