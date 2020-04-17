package huaiye.com.vim.ui.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.dao.AppDatas;

/**
 * @author zhangzhen
 */
@BindLayout(R.layout.activity_chat_setting)
public class SettingChatActivity extends AppBaseActivity {

    @BindView(R.id.activity_chat_setting_notification_checkbox)
    CheckBox activityChatSettingNotificationCheckbox;
    @BindView(R.id.activity_chat_voice_setting_checkbox)
    CheckBox activityChatVoiceSettingCheckbox;

    @BindExtra
    boolean isSOS;
    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.activity_setting_chat))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        activityChatSettingNotificationCheckbox.setChecked(SP.getBoolean(AppUtils.SP_CHAT_SETTING_NOTIFICATION, true));
        activityChatSettingNotificationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SP.putBoolean(AppUtils.SP_CHAT_SETTING_NOTIFICATION, isChecked);

            }
        });

        activityChatVoiceSettingCheckbox.setChecked(SP.getBoolean(AppUtils.SP_SETTING_VOICE, false));
        activityChatVoiceSettingCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SP.putBoolean(AppUtils.SP_SETTING_VOICE, isChecked);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @OnClick(R.id.activity_chat_setting_del)
    void clearData() {
        if(isSOS) {
            return;
        }
        getLogicDialog()
                .setTitleText(AppUtils.getString(R.string.notice))
                .setMessageText(AppUtils.getString(R.string.activity_chat_setting_del_tip))
                .setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppDatas.MsgDB()
                                .chatSingleMsgDao().clearData();
                        AppDatas.MsgDB().chatGroupMsgDao().clearData();
                    }
                })
                .show();

    }

}
