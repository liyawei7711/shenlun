package huaiye.com.vim.ui.meet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import huaiye.com.vim.R;

/**
 * author: admin
 * date: 2018/02/23
 * version: 0
 * mail: secret
 * desc: AlertPopupWindow
 */

public class LayoutPopupWindow extends PopupWindow implements View.OnClickListener {

    ConfirmClickListener confirmClickListener;
    Context context;
    TextView tv_online;

    public LayoutPopupWindow(Context context) {
        super(context);
        this.context = context;
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        Drawable drawable = new ColorDrawable(Color.parseColor("#00000000"));
        setBackgroundDrawable(drawable);// 点击外部消失
        setOutsideTouchable(true); // 点击外部消失
        setFocusable(true); // 点击back键消失

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        View contentView = LayoutInflater.from(context).inflate(R.layout.popu_layout_chang, null);
        setContentView(contentView);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        tv_online = (TextView) contentView.findViewById(R.id.tv_online);

        contentView.findViewById(R.id.tv_big_small).setOnClickListener(this);
        contentView.findViewById(R.id.tv_avg).setOnClickListener(this);

    }

    public void setConfirmClickListener(ConfirmClickListener listener) {
        this.confirmClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_big_small:
                if(confirmClickListener != null)
                    confirmClickListener.onClickShow(true);
                break;
            case R.id.tv_avg:
                if(confirmClickListener != null)
                    confirmClickListener.onClickShow(false);
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
        void onClickShow(boolean isBigSmall);
    }

}
