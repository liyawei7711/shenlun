package huaiye.com.vim.ui.meet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import huaiye.com.vim.R;

/**
 * author: admin
 * date: 2018/02/23
 * version: 0
 * mail: secret
 * desc: AlertPopupWindow
 */

public class CreateMeetPopupWindow extends PopupWindow implements View.OnClickListener {

    ConfirmClickListener confirmClickListener;

    public CreateMeetPopupWindow(Context context) {
        super(context);

        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        Drawable drawable = new ColorDrawable(Color.parseColor("#44000000"));
        setBackgroundDrawable(drawable);// 点击外部消失
        setOutsideTouchable(true); // 点击外部消失
        setFocusable(true); // 点击back键消失

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (confirmClickListener != null) {
                    confirmClickListener.onCancel();
                }
            }
        });

        View contentView = LayoutInflater.from(context).inflate(R.layout.popu_create_meet, null);
        setContentView(contentView);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        contentView.findViewById(R.id.tv_create_jishi).setOnClickListener(this);
        contentView.findViewById(R.id.tv_create_yuyue).setOnClickListener(this);
        contentView.findViewById(R.id.tv_joine).setOnClickListener(this);
    }

    public void setConfirmClickListener(ConfirmClickListener listener) {
        this.confirmClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_jishi:
                confirmClickListener.onCreateJiShi();
                break;
            case R.id.tv_create_yuyue:
                confirmClickListener.onCreateYuYue();
                break;
            case R.id.tv_joine:
                confirmClickListener.onJoine();
                break;

        }
        dismiss();
    }

    public void showView(View view) {
        if (Build.VERSION.SDK_INT < 24) {
            this.showAsDropDown(view);
        } else {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int y = location[1];
            this.showAtLocation(view,
                    Gravity.NO_GRAVITY, 0,
                    y + view.getHeight());
        }
    }


    public interface ConfirmClickListener {
        void onCreateJiShi();

        void onCreateYuYue();

        void onJoine();

        void onCancel();
    }

}
