package huaiye.com.vim.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingRsp;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackReq;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactDetail;
import huaiye.com.vim.ui.talk.TalkActivity;
import huaiye.com.vim.ui.talk.TalkVoiceActivity;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: GroupContactDetailActivity
 */
@BindLayout(R.layout.activity_contact_detail)
public class ContactDetailActivity extends AppBaseActivity {

    @BindView(R.id.iv_collection)
    ImageView iv_collection;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_company)
    TextView tv_company;
    @BindView(R.id.tv_depart)
    TextView tv_depart;
    @BindView(R.id.tv_job)
    TextView tv_job;
    @BindView(R.id.tv_create_meet)
    TextView tv_create_meet;
    @BindView(R.id.tv_talking)
    TextView tv_talking;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.chat_layout)
    View chat_layout;

    @BindExtra
    long nId;
    @BindExtra
    long nContactEntCode;

    ContactDetail mContactDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        iv_collection.setVisibility(View.GONE);
        chat_layout.setVisibility(View.GONE);

        requestOnLine(false);
    }

    void requestDetail() {
        ModelApis.Contacts().requestContactDetail(nId, nContactEntCode, new ModelCallback<ContactDetail>() {
            @Override
            public void onSuccess(ContactDetail contactDetail) {
                mContactDetail = contactDetail;

                if (contactDetail.result.loginName.equals(AppDatas.Auth().getUserLoginName())) {
                    iv_collection.setVisibility(View.GONE);
                    chat_layout.setVisibility(View.GONE);
                } else {
                    iv_collection.setVisibility(View.VISIBLE);
                    chat_layout.setVisibility(View.VISIBLE);
                }

                if (contactDetail.result.isAddedCommon()) {
                    iv_collection.setImageResource(R.drawable.ic_collect_press);
                } else {
                    iv_collection.setImageResource(R.drawable.ic_collect);
                }

                tv_name.setText(contactDetail.result.name);
                if (map.containsKey(contactDetail.result.loginName)) {
                    if (map.get(contactDetail.result.loginName).isSpeaking() ||
                            map.get(contactDetail.result.loginName).isUserCapturing() ||
                            map.get(contactDetail.result.loginName).isUserTalking() ||
                            map.get(contactDetail.result.loginName).isUserMeeting()) {
                        tv_status.setText(getString(R.string.status_notice1));
                    } else {
                        tv_status.setText(getString(R.string.status_notice2));
                    }
                } else {
                    tv_status.setText(getString(R.string.status_notice3));
                }
                tv_company.setText(contactDetail.result.entName);
                tv_phone.setText(contactDetail.result.mobilePhone);
                tv_depart.setText("" + contactDetail.result.depName);
                tv_job.setText(contactDetail.result.jobName == null ? "" : contactDetail.result.jobName);
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                showToast(ErrorMsg.getMsg(ErrorMsg.get_err_code));
            }
        });
    }

    @OnClick(R.id.iv_collection)
    void addCommonContactControl() {
        if (mContactDetail == null
                || mContactDetail.result == null) {
            showToast(getString(R.string.status_notice4));
            return;
        }

        if (mContactDetail.result.isAddedCommon()) {
            // 删除常用联系人
            ModelApis.Contacts().delCommonContact(mContactDetail.result.loginName,
                    mContactDetail.result.groupId,
                    mContactDetail.result.entCode,
                    new ModelCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            mContactDetail.result.isCommonContents = 0;
                            iv_collection.setImageResource(R.drawable.ic_collect);
                        }

                        @Override
                        public void onFailure(HTTPResponse httpResponse) {
                            super.onFailure(httpResponse);
                            showToast(ErrorMsg.getMsg(ErrorMsg.delete_err_code));
                        }
                    });
        } else {
            // 添加常用联系人
            ModelApis.Contacts().addCommonContact(mContactDetail.result.loginName,
                    new ModelCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            mContactDetail.result.isCommonContents = 1;
                            iv_collection.setImageResource(R.drawable.ic_collect_press);
                        }

                        @Override
                        public void onFailure(HTTPResponse httpResponse) {
                            super.onFailure(httpResponse);
                            showToast(ErrorMsg.getMsg(ErrorMsg.update_err_code));
                        }
                    });
        }

    }

    @OnClick(R.id.tv_create_meet)
    void onMeetClicked() {
        // 发起会议
        if (mContactDetail == null) {
            showToast(getString(R.string.status_notice5));
            return;
        }

        CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();
        user.setDevTypeUser();
        user.strUserDomainCode = mContactDetail.result.domainCode;
        user.strUserID = mContactDetail.result.loginName;
        user.strUserName = mContactDetail.result.name;

        HYClient.getModule(ApiMeet.class)
                .createMeeting(SdkParamsCenter.Meet.CreateMeet()
                        .addUsers(user)
                        .setOpenRecord(false)
                        .setMeetName(getString(R.string.status_notice6)), new SdkCallback<CStartMeetingRsp>() {
                    @Override
                    public void onSuccess(CStartMeetingRsp cStartMeetingRsp) {
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            EncryptUtil.startEncrypt(true, user.strUserID, user.strUserDomainCode,
                                    cStartMeetingRsp.nMeetingID + "", cStartMeetingRsp.strMeetingDomainCode, null);
                        } else {
                            if (nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.create_meet_err_code));
                    }
                });
    }

    @OnClick(R.id.tv_talking)
    void onTalkClicked() {
        if (mContactDetail == null) {
            showToast(getString(R.string.status_notice7));
            return;
        }

        CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
        toUser.strToUserDomainCode = mContactDetail.result.domainCode;
        toUser.strToUserID = mContactDetail.result.loginName;
        toUser.strToUserName = mContactDetail.result.name;

        Intent intent = new Intent(this, TalkActivity.class);
        intent.putExtra("isTalkStarter", true);
        intent.putExtra("toUser", toUser);
        startActivity(intent);
    }


    @OnClick(R.id.tv_voice_talk)
    void onTalkVoiceClicked() {
        if (mContactDetail == null) {
            showToast(getString(R.string.status_notice7));
            return;
        }

        CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
        toUser.strToUserDomainCode = mContactDetail.result.domainCode;
        toUser.strToUserID = mContactDetail.result.loginName;
        toUser.strToUserName = mContactDetail.result.name;

        Intent intent = new Intent(this, TalkVoiceActivity.class);
        intent.putExtra("isTalkStarter", true);
        intent.putExtra("toUser", toUser);
        startActivity(intent);
    }


    @Override
    protected void afterOnLineUser(boolean value) {
        super.afterOnLineUser(value);
        requestDetail();
    }

    @OnClick(R.id.ll_back_view)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
