package huaiye.com.vim.ui.meet.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;

/**
 * author: admin
 * date: 2018/02/23
 * version: 0
 * mail: secret
 * desc: AlertPopupWindow
 */

public class ShareLocalView extends FrameLayout implements View.OnClickListener {

    ConfirmClickListener confirmClickListener;
    TextView tv_open_white_board;
    View contentView;

    public ShareLocalView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }
    public ShareLocalView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        contentView = LayoutInflater.from(context).inflate(R.layout.popu_share_local, null);
        addView(contentView);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
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
        setVisibility(GONE);
    }

    public void showView(View view) {
        setVisibility(VISIBLE);
    }

    public void init() {
        tv_open_white_board.setText("打开白板");
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
