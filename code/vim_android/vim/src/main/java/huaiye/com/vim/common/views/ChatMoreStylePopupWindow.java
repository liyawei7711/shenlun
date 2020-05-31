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
import huaiye.com.vim.ui.meet.MeetCreateByAllFriendActivity;

import static com.huaiye.sdk.HYClient.getContext;

public class ChatMoreStylePopupWindow extends PopupWindow {
    private Context mContext;
    public ChatMoreStylePopupWindow(Context context){
        super(context);
        mContext = context;
    }

    public void initView(){
        setBackgroundDrawable(null);setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(AppUtils.dip2Px(mContext,171));

        View view= LayoutInflater.from(mContext).inflate(R.layout.home_popwindow,null);
        setContentView(view);
        TextView home_popwindow_bt1 = view.findViewById(R.id.home_popwindow_bt1);
        if(AppAuth.get().getCreateGroupChatRole()){//只有管理员以及高级用户才可以创建群
            home_popwindow_bt1.setVisibility(View.VISIBLE);
        }else{
            home_popwindow_bt1.setVisibility(View.GONE);
        }

        home_popwindow_bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2CreateGroupChat();
                dismiss();
            }
        });
        TextView home_popwindow_bt2 = view.findViewById(R.id.home_popwindow_bt2);
        home_popwindow_bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2CreatePushVideo();
                dismiss();
            }
        });
        TextView home_popwindow_bt3 = view.findViewById(R.id.home_popwindow_bt3);
        if(AppAuth.get().getCreateMeetRole()){
            home_popwindow_bt3.setVisibility(View.VISIBLE);
        }else{
            home_popwindow_bt3.setVisibility(View.GONE);
        }
        home_popwindow_bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go2CreateVideoChat();
                dismiss();
            }
        });

    }

    private void go2CreateGroupChat() {
        Intent intent = new Intent(mContext, ContactsAddOrDelActivity.class);
        intent.putExtra("titleName", AppUtils.getResourceString(R.string.user_detail_add_user_title));
        intent.putExtra("isSelectUser", true);
        intent.putExtra("isCreateGroup", true);
        intent.putExtra("isAddMore", false);
        mContext.startActivity(intent);
    }

    private void go2CreatePushVideo() {
        Intent intent = new Intent(mContext, ContactsAddOrDelActivity.class);
        intent.putExtra("titleName", AppUtils.getResourceString(R.string.main_chat_push_video));
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
