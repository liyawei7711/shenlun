package huaiye.com.vim.ui.chat;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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


@BindLayout(R.layout.activity_modify_group_name)
public class ModifyGroupNameActivity extends AppBaseActivity {
    @BindView(R.id.modify_group_name)
    EditText modifyGroupName;

    @BindExtra
    String strGroupDomainCode;
    @BindExtra
    String strGroupID;

    @BindExtra
    String groupName;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.group_business_cards))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != modifyGroupName && !(TextUtils.isEmpty(modifyGroupName.getText()))) {
                            groupName = modifyGroupName.getText().toString().trim();
                            ModelApis.Contacts().requestModGroupChat(strGroupDomainCode, strGroupID, groupName, "", "", new ModelCallback<CustomResponse>() {
                                @Override
                                public void onSuccess(final CustomResponse contactsBean) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast(AppUtils.getString(R.string.modify_group_name_success));
                                            MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_MODIFY_GROUPNAME_SUCCESS, groupName);
                                            nMessageEvent.argStr0 = strGroupID;
                                            nMessageEvent.msgContent = modifyGroupName.getText().toString().trim();
                                            EventBus.getDefault().post(nMessageEvent);
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
    }

    @Override
    public void doInitDelay() {
        if (TextUtils.isEmpty(strGroupID) || TextUtils.isEmpty(strGroupDomainCode)) {
            finish();
        }

        if (!TextUtils.isEmpty(groupName)) {
            modifyGroupName.setText(groupName);
            if (groupName.length() <= 15) {
                modifyGroupName.setSelection(groupName.length());
            } else {
                modifyGroupName.setSelection(15);

            }
        }

    }
}
