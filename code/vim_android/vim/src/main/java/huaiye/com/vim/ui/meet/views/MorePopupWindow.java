package huaiye.com.vim.ui.meet.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;

/**
 * Created by ywt on 2019/3/2.
 */

public class MorePopupWindow extends PopupWindow implements View.OnClickListener {
    private View contentView;
    private MoreItemClickListener mMoreItemClickListener;

    public interface MoreItemClickListener {
        void onAddPerson();
        void onControl();
        void onShare();
        void onChat();
        void onCancel();
    }

    public MorePopupWindow(Context context) {
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
                if (mMoreItemClickListener != null) {
                    mMoreItemClickListener.onCancel();
                }
            }
        });

        contentView = LayoutInflater.from(context).inflate(R.layout.popu_more, null);
        setContentView(contentView);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        contentView.findViewById(R.id.tv_more_add_persoon).setOnClickListener(this);
//        contentView.findViewById(R.id.tv_more_control).setOnClickListener(this);
        contentView.findViewById(R.id.tv_more_share).setOnClickListener(this);
        contentView.findViewById(R.id.tv_more_chat).setOnClickListener(this);
    }

    public void showView(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int y = location[1];
        this.showAtLocation(view,
                Gravity.TOP | Gravity.START, location[0], y- AppUtils.getSize(110));
    }

    public void setMoreItemClickListener(MoreItemClickListener listener) {
        mMoreItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more_add_persoon:
                if(mMoreItemClickListener != null){
                    mMoreItemClickListener.onAddPerson();
                }
                break;
            case R.id.tv_more_control:
                if(mMoreItemClickListener != null){
                    mMoreItemClickListener.onControl();
                }
                break;
            case R.id.tv_more_share:
                if(mMoreItemClickListener != null){
                    mMoreItemClickListener.onShare();
                }
                break;
            case R.id.tv_more_chat:
                if(mMoreItemClickListener != null){
                    mMoreItemClickListener.onChat();
                }
                break;
            default:
                break;
        }
    }
}
