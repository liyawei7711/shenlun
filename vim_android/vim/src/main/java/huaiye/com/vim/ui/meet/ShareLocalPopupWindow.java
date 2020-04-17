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
import huaiye.com.vim.common.AppUtils;

import static huaiye.com.vim.common.AppUtils.getString;

/**
 * author: admin
 * date: 2018/02/23
 * version: 0
 * mail: secret
 * desc: AlertPopupWindow
 */

public class ShareLocalPopupWindow extends PopupWindow implements View.OnClickListener {

    ConfirmClickListener confirmClickListener;
    TextView tv_open_white_board;
    View contentView;
    public ShareLocalPopupWindow(Context context) {
        super(context);

        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        Drawable drawable = new ColorDrawable(Color.parseColor("#00000000"));
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

        contentView = LayoutInflater.from(context).inflate(R.layout.popu_share_local, null);
        setContentView(contentView);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        contentView.findViewById(R.id.tv_open_white_board);

        contentView.findViewById(R.id.tv_share_img).setOnClickListener(this);
        tv_open_white_board = (TextView) contentView.findViewById(R.id.tv_open_white_board);
        tv_open_white_board.setOnClickListener(this);
        contentView.findViewById(R.id.tv_share_file).setOnClickListener(this);
    }

    public void setConfirmClickListener(ConfirmClickListener listener) {
        this.confirmClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_share_img:
                confirmClickListener.onShareImg();
                break;
            case R.id.tv_open_white_board:
                confirmClickListener.onOpenWhiteBoard();
                break;
            case R.id.tv_share_file:
                confirmClickListener.onShareFile();
                break;

        }
        dismiss();
    }

    public void showView(View view) {
        if (Build.VERSION.SDK_INT < 24) {
        } else {

        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int y = location[1];
        this.showAtLocation(view,
                Gravity.TOP | Gravity.START, location[0], y-AppUtils.getSize(165));
    }

    public void init() {
        tv_open_white_board.setText(getString(R.string.meet_open_whiteboard));
    }

    public void changeStatue(String str) {
        tv_open_white_board.setText(str);
    }


    public interface ConfirmClickListener {
        void onShareImg();

        void onOpenWhiteBoard();

        void onShareFile();

        void onCancel();
    }

}
