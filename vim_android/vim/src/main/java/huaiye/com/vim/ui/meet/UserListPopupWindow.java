package huaiye.com.vim.ui.meet;

import android.app.Activity;
import android.content.Intent;
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
import huaiye.com.vim.ui.contacts.ContactsSearchActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;

import static huaiye.com.vim.common.AppUtils.getString;

/**
 * author: admin
 * date: 2018/02/23
 * version: 0
 * mail: secret
 * desc: AlertPopupWindow
 */

public class UserListPopupWindow extends PopupWindow implements View.OnClickListener {

    ConfirmClickListener confirmClickListener;
    Activity context;
    TextView tv_online;
    boolean isSelectUser;

    public UserListPopupWindow(Activity context) {
        super(context);
        this.context = context;
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        Drawable drawable = new ColorDrawable(Color.parseColor("#44000000"));
        setBackgroundDrawable(drawable);// 点击外部消失
        setOutsideTouchable(true); // 点击外部消失
        setFocusable(true); // 点击back键消失

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        View contentView = LayoutInflater.from(context).inflate(R.layout.popu_user_list, null);
        setContentView(contentView);
        /*contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/

        tv_online = (TextView) contentView.findViewById(R.id.tv_online);

        contentView.findViewById(R.id.tv_search).setOnClickListener(this);
        tv_online.setOnClickListener(this);
    }

    public void setConfirmClickListener(ConfirmClickListener listener, boolean isSelectUser) {
        this.confirmClickListener = listener;
        this.isSelectUser = isSelectUser;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                Intent intent = new Intent(context, ContactsSearchActivity.class);
                intent.putExtra("isSelectUser", isSelectUser);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivityForResult(intent,11);
                break;
            case R.id.tv_online:
                ChoosedContacts.get().isOnLine = !ChoosedContacts.get().isOnLine;
                tv_online.setText(ChoosedContacts.get().isOnLine ? getString(R.string.show_all) : getString(R.string.show_online2));
                confirmClickListener.onClickShow();
                break;

        }
        dismiss();
    }

    public void showView(View view) {
        ref();
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

    public void ref() {
        tv_online.setText(ChoosedContacts.get().isOnLine ? getString(R.string.show_all) : getString(R.string.show_online2));
    }


    public interface ConfirmClickListener {
        void onClickShow();
    }

}
