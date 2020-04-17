package huaiye.com.vim.ui.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.CustomResponse;
import ttyy.com.jinnetwork.core.work.HTTPResponse;


@BindLayout(R.layout.activity_modify_group_announcement)
public class ModifyGroupAnnouncementActivity extends AppBaseActivity {

    @BindView(R.id.modify_group_announcement)
    EditText modifyGroupAnnouncement;

    @BindView(R.id.modify_group_announcement_group_owner_lin)
    LinearLayout modify_group_announcement_group_owner_lin;
    @BindExtra
    String strGroupDomainCode;
    @BindExtra
    String strGroupID;
    @BindExtra
    boolean isGroupOwner;
    @BindExtra
    String strAnnouncement;


    @Override
    protected void initActionBar() {
        if (isGroupOwner) {
            getNavigate().setTitlText(AppUtils.getString(R.string.group_announcement))
                    .setLeftClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    })
                    .setRightClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != modifyGroupAnnouncement && !(TextUtils.isEmpty(modifyGroupAnnouncement.getText()))) {
                                strAnnouncement = modifyGroupAnnouncement.getText().toString().trim();
                                ModelApis.Contacts().requestModGroupChat(strGroupDomainCode, strGroupID, "", strAnnouncement, "", new ModelCallback<CustomResponse>() {
                                    @Override
                                    public void onSuccess(final CustomResponse contactsBean) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showToast(AppUtils.getString(R.string.modify_group_announce_success));
                                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_MODIFY_GROUP_ANNOUNCEMENT_SUCCESS, strAnnouncement));
                                                finish();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(HTTPResponse httpResponse) {
                                        super.onFailure(httpResponse);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showToast(AppUtils.getString(R.string.custom_tip_network_error));
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });

            getNavigate().getRightTextView().setPadding(AppUtils.dp2px(this, 8f), AppUtils.dp2px(this, 4f), AppUtils.dp2px(this, 8f), AppUtils.dp2px(this, 4f));
            getNavigate().getRightTextView().setBackgroundResource(R.drawable.shape_choosed_confirm);
            getNavigate().setRightText(AppUtils.getString(R.string.save));
        } else {
            getNavigate().setTitlText(AppUtils.getString(R.string.group_announcement))
                    .setLeftClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
        }

    }

    @Override
    public void doInitDelay() {
        if (TextUtils.isEmpty(strGroupID) || TextUtils.isEmpty(strGroupDomainCode)) {
            finish();
        }
        if (TextUtils.isEmpty(strAnnouncement)) {
            modifyGroupAnnouncement.setText("");
        } else {
            modifyGroupAnnouncement.setText(strAnnouncement + "");
            try {
                modifyGroupAnnouncement.setSelection(strAnnouncement.length());
            } catch (Exception e) {
            }


        }
        if (isGroupOwner) {
            modifyGroupAnnouncement.setEnabled(true);
            modify_group_announcement_group_owner_lin.setVisibility(View.GONE);

        } else {
            modifyGroupAnnouncement.setEnabled(false);
            modify_group_announcement_group_owner_lin.setVisibility(View.VISIBLE);


        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
