package huaiye.com.vim.common.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.ui.contacts.ContactsAddOrDelActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.home.MainSettingsActivity;
import huaiye.com.vim.ui.meet.MeetCreateByAllFriendActivity;

import static com.huaiye.sdk.HYClient.getContext;

public class ChatMoreStylePopupWindow extends PopupWindow {
    private Context mContext;
    boolean isSOS;

    TextView home_popwindow_bt1;
    TextView home_popwindow_bt2;
    TextView home_popwindow_bt3;
    TextView home_popwindow_bt4;
    TextView home_popwindow_null;

    public ChatMoreStylePopupWindow(Context context){
        super(context);
        mContext = context;
    }

    public void setSOS(boolean SOS) {
        isSOS = SOS;
    }

    public void initView(){
        setBackgroundDrawable(null);setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(AppUtils.dip2Px(mContext,171));

        View view= LayoutInflater.from(mContext).inflate(R.layout.home_popwindow,null);
        setContentView(view);

        home_popwindow_bt1 = view.findViewById(R.id.home_popwindow_bt1);
        home_popwindow_bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2CreateGroupChat();
                dismiss();
            }
        });

        home_popwindow_bt2 = view.findViewById(R.id.home_popwindow_bt2);
        home_popwindow_bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2CreatePushVideo();
                dismiss();
            }
        });

        home_popwindow_bt3 = view.findViewById(R.id.home_popwindow_bt3);
        home_popwindow_bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2CreateVideoChat();
                dismiss();
            }
        });

        home_popwindow_bt4 = view.findViewById(R.id.home_popwindow_bt4);
        home_popwindow_bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2MySetting();
                dismiss();
            }
        });

        home_popwindow_null = view.findViewById(R.id.home_popwindow_null);

    }

    public void changeMenu(boolean isStatus) {
        if(isStatus) {
            home_popwindow_bt1.setVisibility(View.GONE);
            home_popwindow_bt2.setVisibility(View.GONE);
            home_popwindow_bt3.setVisibility(View.GONE);
            home_popwindow_null.setVisibility(View.VISIBLE);
        } else {
            home_popwindow_bt1.setVisibility(View.VISIBLE);
            home_popwindow_bt2.setVisibility(View.VISIBLE);
            home_popwindow_bt3.setVisibility(View.VISIBLE);
            home_popwindow_null.setVisibility(View.GONE);

            if(AppAuth.get().getCreateGroupChatRole()){//只有管理员以及高级用户才可以创建群
                home_popwindow_bt1.setVisibility(View.VISIBLE);
            }else{
                home_popwindow_bt1.setVisibility(View.GONE);
            }
            if(AppAuth.get().getCreateMeetRole()){
                home_popwindow_bt3.setVisibility(View.VISIBLE);
            }else{
                home_popwindow_bt3.setVisibility(View.GONE);
            }
        }

    }

    private void go2CreateGroupChat() {
        Intent intent = new Intent(mContext, ContactsAddOrDelActivity.class);
        intent.putExtra("titleName", AppUtils.getString(R.string.user_detail_add_user_title));
        intent.putExtra("isSelectUser", true);
        intent.putExtra("isCreateGroup", true);
        intent.putExtra("isAddMore", false);
        mContext.startActivity(intent);
    }

    private void go2MySetting() {
        Intent intent = new Intent(mContext, MainSettingsActivity.class);
        intent.putExtra("isSOS", isSOS);
        mContext.startActivity(intent);
    }

    private void go2CreatePushVideo() {
        Intent intent = new Intent(mContext, ContactsAddOrDelActivity.class);
        intent.putExtra("titleName", AppUtils.getString(R.string.main_chat_push_video));
        intent.putExtra("isSelectUser", true);
        intent.putExtra("isCreateGroup", false);
        intent.putExtra("isCreateVideoPish",true);
        intent.putExtra("isAddMore", false);
        mContext.startActivity(intent);
    }

    private void go2CreateVideoChat() {
        ChoosedContacts.get().clear();
        Intent intent = new Intent(getContext(), MeetCreateByAllFriendActivity.class);
        intent.putExtra("nMeetType", 1);
        mContext.startActivity(intent);
    }

}
