package huaiye.com.vim.ui.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToMuliteUserRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackReq;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.utils.ChatUtil;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.constants.AppFrequentlyConstants;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.ui.meet.ChatSingleActivity;
import huaiye.com.vim.ui.talk.TalkActivity;
import huaiye.com.vim.ui.talk.TalkVoiceActivity;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;


/**
 * Created by ywt on 2019/3/21.
 */
@BindLayout(R.layout.activity_contact_detail_new)
public class ContactDetailNewActivity extends AppBaseActivity {

    @BindView(R.id.contact_video)
    TextView contactVideo;
    @BindView(R.id.contact_voice)
    TextView contactVoice;
    @BindView(R.id.contact_message)
    TextView contactMessage;

    @BindView(R.id.contact_detail_head_img)
    ImageView contactDetailHeadImg;
    @BindView(R.id.contact_detail_name)
    TextView contactDetailName;
    @BindView(R.id.contact_detail_Str_name)
    TextView contactDetailStrName;
    @BindView(R.id.contact_detail_buttom)
    LinearLayout contactDetailButtom;

    private RequestOptions requestOptions;
    public String mState;
    public String mName;
    public String mSex;
    public String mStrDepName;


    @BindExtra
    User nUser;

    @Override
    protected void initActionBar() {
        if (null == nUser) {
            return;
        }
        initNavigateView();
        requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());
    }

    private void initNavigateView() {
        getNavigate().setVisibility(View.VISIBLE);
        getNavigate().setTitlText("个人详情")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        initView();
    }

    private void initView() {
        mName = nUser.strUserName;

        if (nUser.nStatus == -1) {
            mState = "(未登录)";
        } else if (nUser.nStatus == 0) {
            mState = "(离线)";
        } else if (nUser.nStatus == 1) {
            mState = "(空闲)";
        } else if (nUser.nStatus == 2) {
            mState = "(采集中)";
        } else if (nUser.nStatus == 3) {
            mState = "(对讲中)";
        } else if (nUser.nStatus == 4) {
            mState = "(会议中)";
        }

        /* 根据性别类型，显示对应的内容，如果为0，则不显示(比如会议终端) */
        if (nUser.nSex == 1) {
            mSex = "性别 男";
        } else if (nUser.nSex == 2) {
            mSex = "性别 女";
        }
        mStrDepName = nUser.strDepName;

        /* 将变量值更新到视图上 */
//        mBindingImpl.setVariable(com.android.databinding.library.baseAdapters.BR.contactDetailNewActivity, this);

        bindDtae();
        /* 如果联系人为自己，则隐藏相关的功能按钮 */
        if (nUser.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
            findViewById(R.id.contact_video).setVisibility(View.GONE);
            findViewById(R.id.contact_voice).setVisibility(View.GONE);
            findViewById(R.id.contact_message).setVisibility(View.GONE);
            contactDetailButtom.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(AppDatas.Constants().getAddressWithoutPort() + nUser.strHeadUrl)
                .apply(requestOptions)
                .into(contactDetailHeadImg);
    }

    private void bindDtae() {
        contactDetailName.setText(mName);
        contactDetailStrName.setText(mState);
    }


    /* 视频通话 */
    @OnClick(R.id.contact_video)
    public void onVideoClicked(View view) {
        if (nUser == null) {
            showToast("获取人员详情失败，无法通话");
            return;
        }
        if (nUser.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
            showToast("不能与自己通话");
            return;
        }
        CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
        toUser.strToUserDomainCode = nUser.strDomainCode;
        toUser.strToUserID = nUser.strUserID;
        toUser.strToUserName = nUser.strUserName;

        /* 将用户加入常用联系人 */
        AppFrequentlyConstants.get().AddContacts(new ArrayList<User>() {{
            add(nUser);
        }});

        Intent intent = new Intent(this, TalkActivity.class);
        intent.putExtra("isTalkStarter", true);
        intent.putExtra("toUser", toUser);
        startActivity(intent);
    }


    /* 音频通话 */
    @OnClick(R.id.contact_voice)
    public void onVoiceClicked(View view) {
        if (nUser == null) {
            showToast("获取人员详情失败，无法对讲");
            return;
        }
        if (nUser.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
            showToast("不能与自己通话");
            return;
        }
        CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
        toUser.strToUserDomainCode = nUser.strDomainCode;
        toUser.strToUserID = nUser.strUserID;
        toUser.strToUserName = nUser.strUserName;

        /* 将用户加入常用联系人 */
        AppFrequentlyConstants.get().AddContacts(new ArrayList<User>() {{
            add(nUser);
        }});

        Intent intent = new Intent(this, TalkVoiceActivity.class);
        intent.putExtra("isTalkStarter", true);
        intent.putExtra("toUser", toUser);
        startActivity(intent);
    }


    @OnClick(R.id.contact_message)
    public void onMessageClicked(View view) {
        if (nUser == null) {
            showToast("获取人员详情失败，无法对讲");
            return;
        }
        if (nUser.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
            showToast("不能与自己通话");
            return;
        }
        CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
        toUser.strToUserDomainCode = nUser.strDomainCode;
        toUser.strToUserID = nUser.strUserID;
        toUser.strToUserName = nUser.strUserName;

        /* 将用户加入常用联系人 */
        AppFrequentlyConstants.get().AddContacts(new ArrayList<User>() {{
            add(nUser);
        }});

        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INTENT_CHATSINGLEACTIVITY));
        Intent intent = new Intent(this, ChatSingleActivity.class);
        intent.putExtra("mOtherUserName", nUser.strUserName);
        intent.putExtra("mOtherUserId", nUser.strUserID);
        intent.putExtra("nUser", nUser);
        intent.putExtra("mOtherUserDomainCode", nUser.strUserDomainCode);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.contact_video, R.id.contact_voice, R.id.contact_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.contact_video:
                break;
            case R.id.contact_voice:
                break;
            case R.id.contact_message:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        switch (messageEvent.what) {
            case AppUtils.EVENT_VOICE_CANCLE:
                String msgContentVoiceCancle = ChatUtil.getChatContentJson(ContactDetailNewActivity.this, messageEvent.msgContent, "","", 0, 0, false, 0, 0, 0, 0, "");
                sendRealMsg(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, msgContentVoiceCancle);
                break;
            case AppUtils.EVENT_VOICE_REFUSE:
                String msgContentVoiceRefuse = ChatUtil.getChatContentJson(ContactDetailNewActivity.this, messageEvent.msgContent, "","", 0, 0, false, 0, 1, 0, 0, "");
                sendRealMsg(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, msgContentVoiceRefuse);
                break;
            case AppUtils.EVENT_VOICE_SUCCESS:
                String msgContentVoiceSuccess = ChatUtil.getChatContentJson(ContactDetailNewActivity.this, messageEvent.msgContent, "","", 0, 0, false, 0, 2, 0, 0, "");
                sendRealMsg(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, msgContentVoiceSuccess);
                break;
            case AppUtils.EVENT_VIDEO_CANCLE:
                String msgContentVideoCancle = ChatUtil.getChatContentJson(ContactDetailNewActivity.this, messageEvent.msgContent, "","", 0, 0, false, 0, 0, 0, 0, "");
                sendRealMsg(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, msgContentVideoCancle);
                break;
            case AppUtils.EVENT_VIDEO_REFUSE:
                String msgContentVideoRefuse = ChatUtil.getChatContentJson(ContactDetailNewActivity.this, messageEvent.msgContent, "","", 0, 0, false, 0, 1, 0, 0, "");
                sendRealMsg(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, msgContentVideoRefuse);
                break;
            case AppUtils.EVENT_VIDEO_SUCCESS:
                String msgContentVideoSuccess = ChatUtil.getChatContentJson(ContactDetailNewActivity.this, messageEvent.msgContent, "","", 0, 0, false, 0, 2, 0, 0, "");
                sendRealMsg(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, msgContentVideoSuccess);
                break;
            default:
                break;
        }
    }

    private void sendRealMsg(int msgType, String msgContent) {
        if (null == msgContent || TextUtils.isEmpty(msgContent)) {
            Logger.debug("MessageReceiver 未拿到有效的用户GPS数据");
            return;
        }
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();
            SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
            info.strUserDomainCode = nUser.strDomainCode;
            info.strUserID = nUser.strUserID;
            users.add(info);
            EncryptUtil.encryptTxt(msgContent, true, false, "", "",
                    nUser.strUserID, nUser.strDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                            sendWetherEncrypt(msgType, sessionRsp.m_lstData.get(0).strData);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("信息加密失败");
                        }
                    });
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            sendWetherEncrypt(msgType, msgContent);
        }
    }

    private void sendWetherEncrypt(int msgType, String msgContent) {
        ChatMessageBean bean = new ChatMessageBean();
        bean.content = msgContent;

        bean.type = msgType;
        bean.sessionID = nUser.strDomainCode + nUser.strUserID;
        bean.sessionName = nUser.strUserName;
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 0;
        bean.groupDomainCode = "";
        bean.groupID = "";
        bean.time = System.currentTimeMillis() / 1000;

        SendUserBean mySelf = new SendUserBean(AppAuth.get().getUserID() + "", AppAuth.get().getDomainCode(), AppAuth.get().getUserName());
        SendUserBean otherUser = new SendUserBean(nUser.strUserID, nUser.strDomainCode, nUser.strUserName);

        bean.sessionUserList = new ArrayList<>();
        bean.sessionUserList.add(mySelf);
        bean.sessionUserList.add(otherUser);

        ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
        sessionUserList.add(new SendUserBean(mySelf.strUserID, mySelf.strUserDomainCode, mySelf.strUserName));
        sessionUserList.add(new SendUserBean(otherUser.strUserID, otherUser.strUserDomainCode, otherUser.strUserName));
        Gson gson = new Gson();
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(true)
                        .setMessage(gson.toJson(bean))
                        .setIsImportant(false)
                        .setUser(sessionUserList), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                        Logger.debug("singleMsg 发送成功");
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast("发送失败" + errorInfo.getMessage());

                    }
                }
        );
    }

}
