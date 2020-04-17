package huaiye.com.vim.ui.fenxiang;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToMuliteUserRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.CloseZhuanFa;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.utils.ChatUtil;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatGroupMsgBean;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.dao.msgs.SendMsgUserBean;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.CreateGroupContactData;
import huaiye.com.vim.models.contacts.bean.GroupInfo;
import huaiye.com.vim.ui.meet.ChatGroupActivityNew;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppBaseActivity.showToast;
import static huaiye.com.vim.common.AppUtils.getString;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

public class ShareGroupPopupWindow extends PopupWindow {
    private Context mContext;
    Bundle data;

    String strUserID; //老对话的id
    String strUserDomainCode;//老对话的domain
    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();//老对话的所有user

    boolean isGroup;
    String strGroupID;
    String strGroupDomain;

    GroupInfo groupInfo;//转发的对象
    ArrayList<SdpMessageCmProcessIMReq.UserInfo> usersNew = new ArrayList<>();//转发的对话user
    ArrayList<SendUserBean> sendUserBeans = new ArrayList<>();

    ImageView iv_head;
    TextView tv_name;
    TextView tv_title;
    TextView tv_content;
    TextView tv_from;
    ImageView iv_content;

    TextView tv_send;

    RequestOptions requestOptions;

    public ShareGroupPopupWindow(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public void initView() {

        setBackgroundDrawable(null);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        requestOptions = new RequestOptions().centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());

        Drawable drawable = new ColorDrawable(Color.parseColor("#00000000"));
        setBackgroundDrawable(drawable);// 点击外部消失
        setOutsideTouchable(true); // 点击外部消失
        setFocusable(true); // 点击back键消失

        View view = LayoutInflater.from(mContext).inflate(R.layout.share_popwindow, null);
        setContentView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        iv_head = view.findViewById(R.id.iv_head);
        tv_name = view.findViewById(R.id.tv_name);
        tv_title = view.findViewById(R.id.tv_title);
        tv_content = view.findViewById(R.id.tv_content);
        tv_from = view.findViewById(R.id.tv_from);
        iv_content = view.findViewById(R.id.iv_content);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_send = view.findViewById(R.id.tv_send);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_send.setEnabled(false);
                sendTxtMsg();
            }
        });
    }

    public void setSendUser(GroupInfo groupInfo, Bundle data) {
        this.data = data;
        this.groupInfo = groupInfo;
        initUserEncrypt();
    }

    private void sendTxtMsg() {
        if (TextUtils.isEmpty(tv_title.getHint())) {
            tv_title.setHint("");
        }
        if (TextUtils.isEmpty(tv_title.getText())) {
            tv_title.setText("");
        }
        String msgContent = ChatUtil.getChatContentJson(mContext, tv_title.getText().toString(),
                tv_content.getText().toString(),
                tv_title.getHint().toString(), 0, 0,
                false,
                tv_title.getText().toString().length(), 0, 0, 0, "");
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            encrypt(msgContent);
        } else {
            sendWetherEncrypt(false, msgContent);
        }
    }

    void encrypt(String str) {
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptTxt(str, true, true,
                    groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                    "", "", usersNew, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                            sendWetherEncrypt(true, sessionRsp);
                        }

                        @Override
                        public void onError(ErrorInfo sessionRsp) {
//                            AppBaseActivity.showToast("对方未开启加密,无法发送");
                        }
                    });
        } else {
            sendWetherEncrypt(false, str);
        }
    }

    /**
     * 加密群组发送
     *
     * @param sessionRsp
     */
    private void sendWetherEncrypt(boolean isEncrypt, SdpMessageCmProcessIMRsp sessionRsp) {
        for (SdpMessageCmProcessIMRsp.UserData temp : sessionRsp.m_lstData) {
            ChatMessageBean bean = new ChatMessageBean();
            bean.content = temp.strData;
            bean.type = AppUtils.MESSAGE_TYPE_SHARE;
            bean.sessionID = getGroupSessionId();
            bean.sessionName = getSessionName();
            bean.fromUserDomain = AppDatas.Auth().getDomainCode();
            bean.fromUserId = AppDatas.Auth().getUserID() + "";
            bean.fromUserName = AppDatas.Auth().getUserName();
            bean.groupType = 1;
            bean.groupDomainCode = groupInfo.strGroupDomainCode;
            bean.groupID = groupInfo.strGroupID;
            bean.bEncrypt = isEncrypt ? 1 : 0;
            bean.time = System.currentTimeMillis() / 1000;
            bean.sessionUserList = new ArrayList<>();
            bean.sessionUserList.addAll(sendUserBeans);

            ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
            sessionUserList.add(new SendUserBean(temp.strUserID, temp.strUserDomainCode, temp.strUserID));

            Gson gson = new Gson();
            HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                            .setIsImportant(true)
                            .setMessage(gson.toJson(bean))
                            .setUser(sessionUserList), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                        @Override
                        public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                            EncryptUtil.converEncryptText(bean.content, true,
                                    groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                                    temp.strUserID, temp.strUserDomainCode,
                                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                        @Override
                                        public void onSuccess(SdpMessageCmProcessIMRsp rsp) {
                                            if (rsp.m_nResultCode == 0) {
                                                bean.content = rsp.m_lstData.get(0).strData;
                                            }
                                            dealSaveMessageAndLoad(sessionRsp, temp, bean);
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                            dealSaveMessageAndLoad(sessionRsp, temp, bean);
                                        }
                                    });
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            if (sessionRsp.m_lstData.indexOf(temp) == sessionRsp.m_lstData.size() - 1) {
                                ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                                showToast(AppUtils.getString(R.string.share_txt_1) + errorInfo.getMessage());
                                dismiss();
                            }

                        }
                    }
            );
        }
    }

    private void dealSaveMessageAndLoad(SdpMessageCmProcessIMRsp sessionRsp, SdpMessageCmProcessIMRsp.UserData temp, ChatMessageBean bean) {
        if (sessionRsp.m_lstData.indexOf(temp) == sessionRsp.m_lstData.size() - 1) {
            ChatGroupMsgBean groupMsgBean = ChatGroupMsgBean.from(bean);
            groupMsgBean.read = 1;
            AppDatas.MsgDB()
                    .chatGroupMsgDao()
                    .insert(groupMsgBean);

            SendMsgUserBean sendUserInfo = AppDatas.MsgDB().getSendUserListDao().getSendUserInfo(groupMsgBean.sessionID);
            if (sendUserInfo != null) {
                sendUserInfo.strUserID = temp.strUserID;
                sendUserInfo.strUserDomainCode = temp.strUserDomainCode;
                AppDatas.MsgDB().getSendUserListDao().update(sendUserInfo);
            } else {
                AppDatas.MsgDB().getSendUserListDao().insert(new SendMsgUserBean(groupMsgBean.sessionID, temp.strUserID, temp.strUserDomainCode));
            }

            VimMessageBean vimMessageBean = VimMessageBean.from(bean);
            huaiye.com.vim.dao.msgs.ChatUtil.get().saveChangeMsg(vimMessageBean, true);

            ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
            showToast(getString(R.string.share_txt_2));
            dismiss();
            jumpToChat();
            EventBus.getDefault().post(new CloseZhuanFa());
        }
    }

    private void sendWetherEncrypt(boolean isEncrypt, String msgContent) {
        final ChatMessageBean bean = new ChatMessageBean();
        bean.content = msgContent;
        bean.type = AppUtils.MESSAGE_TYPE_SHARE;
        bean.sessionID = getGroupSessionId();
        bean.sessionName = getSessionName();
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 1;
        bean.bEncrypt = isEncrypt ? 1 : 0;
        bean.groupDomainCode = groupInfo.strGroupDomainCode;
        bean.groupID = groupInfo.strGroupID;
        bean.time = System.currentTimeMillis() / 1000;
        bean.sessionUserList = new ArrayList<>();
        bean.sessionUserList.addAll(sendUserBeans);
        Gson gson = new Gson();
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(true)
                        .setMessage(gson.toJson(bean))
                        .setUser(sendUserBeans), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                        ChatGroupMsgBean groupMsgBean = ChatGroupMsgBean.from(bean);
                        groupMsgBean.read = 1;
                        AppDatas.MsgDB()
                                .chatGroupMsgDao()
                                .insert(groupMsgBean);
                        VimMessageBean vimMessageBean = VimMessageBean.from(bean);
                        huaiye.com.vim.dao.msgs.ChatUtil.get().saveChangeMsg(vimMessageBean, true);

                        for (SendUserBean temp : bean.sessionUserList) {
                            SendMsgUserBean sendUserInfo = AppDatas.MsgDB().getSendUserListDao().getSendUserInfo(groupMsgBean.sessionID);
                            if (sendUserInfo != null) {
                                sendUserInfo.strUserID = temp.strUserID;
                                sendUserInfo.strUserDomainCode = temp.strUserDomainCode;
                                AppDatas.MsgDB().getSendUserListDao().update(sendUserInfo);
                            } else {
                                AppDatas.MsgDB().getSendUserListDao().insert(new SendMsgUserBean(groupMsgBean.sessionID, temp.strUserID, temp.strUserDomainCode));
                            }
                        }

                        ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                        showToast(getString(R.string.share_txt_2));
                        dismiss();
                        jumpToChat();
                        EventBus.getDefault().post(new CloseZhuanFa());
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                        showToast(AppUtils.getString(R.string.share_txt_1) + errorInfo.getMessage());
                        dismiss();
                    }
                }
        );
    }

    private void jumpToChat() {
        Intent intent = new Intent(mContext, ChatGroupActivityNew.class);
        CreateGroupContactData contactsBean = new CreateGroupContactData();
        contactsBean.strGroupDomainCode = groupInfo.strGroupDomainCode;
        contactsBean.strGroupID = groupInfo.strGroupID;
        contactsBean.sessionName = getSessionName();
        intent.putExtra("mContactsBean", contactsBean);
        intent.putExtra("from", "share");
        mContext.startActivity(intent);
    }

    public void showData() {

        Glide.with(mContext)
                .load(AppDatas.Constants().getFileServerURL() + groupInfo.strHeadUrl)
                .apply(requestOptions)
                .into(iv_head);
        tv_name.setText(groupInfo.strGroupName);
        //url   //title  //content  //share_source_from  //file
        //android.intent.extra.SUBJECT
        //android.intent.extra.TEXT
        if (TextUtils.isEmpty(data.getString("title"))) {
            if (data.containsKey("android.intent.extra.SUBJECT")) {
                tv_title.setText(data.getString("android.intent.extra.SUBJECT"));
            }
        } else {
            tv_title.setText(data.getString("title"));
        }

        if (TextUtils.isEmpty(data.getString("url"))) {
            if (TextUtils.isEmpty(data.getString("android.intent.extra.TEXT"))) {
                tv_title.setHint("url is empty");
            } else {
                try {
                    tv_title.setHint(data.getString("android.intent.extra.TEXT").substring(data.getString("android.intent.extra.TEXT").indexOf("http")));
                } catch (Exception e) {
                    tv_title.setHint("url is empty");
                }
            }
        } else {
            tv_title.setHint(data.getString("url"));
        }
        if (TextUtils.isEmpty(data.getString("content"))) {
            tv_content.setText(tv_title.getText());
        } else {
            tv_content.setText(data.getString("content"));
        }
        tv_from.setText(TextUtils.isEmpty(data.getString("share_source_from")) ? "" : data.getString("share_source_from"));
        Glide.with(mContext)
                .load(data.getString("file"))
                .apply(requestOptions)
                .into(iv_content);
    }

    private String getGroupSessionId() {
        return groupInfo.strGroupDomainCode + groupInfo.strGroupID;
    }

    private String getSessionName() {
        return groupInfo.strGroupName;
    }

    void initUserEncrypt() {
        ModelApis.Contacts().requestqueryGroupChatInfo(groupInfo.strGroupDomainCode, groupInfo.strGroupID,
                new ModelCallback<ContactsGroupUserListBean>() {
                    @Override
                    public void onSuccess(final ContactsGroupUserListBean contactsBean) {
                        sendUserBeans.clear();
                        usersNew.clear();
                        if (contactsBean != null && contactsBean.lstGroupUser != null) {
                            for (ContactsGroupUserListBean.LstGroupUser temp : contactsBean.lstGroupUser) {
                                if (!AppAuth.get().getUserID().equals(temp.strUserID)) {
                                    SendUserBean sendUserBean = new SendUserBean();
                                    sendUserBean.strUserID = temp.strUserID;
                                    sendUserBean.strUserDomainCode = temp.strUserDomainCode;
                                    sendUserBean.strUserName = temp.strUserName;
                                    sendUserBeans.add(sendUserBean);

                                    SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                                    info.strUserDomainCode = temp.strUserDomainCode;
                                    info.strUserID = temp.strUserID;
                                    usersNew.add(info);
                                }
                            }
                        }
                        showData();
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        showToast("onFailure");
                    }
                });
    }

}
