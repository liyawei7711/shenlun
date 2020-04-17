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
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.dao.msgs.ChatSingleMsgBean;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.UserInfo;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.ui.meet.ChatSingleActivity;

import static huaiye.com.vim.common.AppUtils.getString;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

public class SharePopupWindow extends PopupWindow {
    private Context mContext;
    Bundle data;

    User user;//分享的对象
    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();//分享的对话user

    ImageView iv_head;
    TextView tv_name;
    TextView tv_title;
    TextView tv_content;
    TextView tv_from;
    ImageView iv_content;

    TextView tv_send;

    RequestOptions requestOptions;

    public SharePopupWindow(Context context, ArrayList<UserInfo> users, User user) {
        super(context);
        mContext = context;
        this.user = user;
        for (UserInfo temp : users) {
            SdpMessageCmProcessIMReq.UserInfo userInfo = new SdpMessageCmProcessIMReq.UserInfo();
            userInfo.strUserID = temp.strUserID;
            userInfo.strUserDomainCode = temp.strUserDomainCode;
            this.users.add(userInfo);
        }
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
            sendWetherEncrypt(msgContent, false);
        }
    }

    void encrypt(String str) {

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptTxt(str, true, false, "", "",
                    user.strUserID, user.strDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                            sendWetherEncrypt(sessionRsp.m_lstData.get(0).strData, true);
                        }

                        @Override
                        public void onError(ErrorInfo sessionRsp) {
                            ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                            AppBaseActivity.showToast(getString(R.string.notice_txt_9));
                            dismiss();
                        }
                    });
        } else {
            sendWetherEncrypt(str, false);
        }
    }

    private void sendWetherEncrypt(String msgContent, boolean isEncrypt) {
        ChatMessageBean bean = new ChatMessageBean();
        bean.content = msgContent;
        bean.type = AppUtils.MESSAGE_TYPE_SHARE;
        bean.sessionID = getSessionId();
        bean.sessionName = getSessionName();
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 0;
        bean.bEncrypt = isEncrypt ? 1 : 0;
        bean.groupDomainCode = user.strUserDomainCode;
        bean.groupID = user.strUserID;
        bean.time = System.currentTimeMillis() / 1000;

        SendUserBean mySelf = new SendUserBean(AppAuth.get().getUserID() + "", AppAuth.get().getDomainCode(), AppAuth.get().getUserName());
        SendUserBean otherUser = new SendUserBean(user.strUserID, TextUtils.isEmpty(user.strUserDomainCode) ? user.strDomainCode : user.strUserDomainCode, user.strUserName);

        bean.sessionUserList = new ArrayList<>();
        bean.sessionUserList.add(mySelf);
        bean.sessionUserList.add(otherUser);

        ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
//        sessionUserList.add(new SendUserBean(mySelf.strUserID, mySelf.strDomainCode, mySelf.strUserName));
        sessionUserList.add(new SendUserBean(otherUser.strUserID, otherUser.strUserDomainCode, otherUser.strUserName));
        Gson gson = new Gson();
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(true)
                        .setMessage(gson.toJson(bean))
                        .setUser(sessionUserList), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {

                        if (bean.bEncrypt == 1) {
                            EncryptUtil.converEncryptText(bean.content, false,
                                    "", "",
                                    otherUser.strUserID, otherUser.strUserDomainCode,
                                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                        @Override
                                        public void onSuccess(SdpMessageCmProcessIMRsp rsp) {
                                            if (rsp.m_nResultCode == 0) {
                                                bean.content = rsp.m_lstData.get(0).strData;
                                            }
                                            dealSaveMessageAndLoad(bean, otherUser);
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                            dealSaveMessageAndLoad(bean, otherUser);
                                        }
                                    });
                            return;
                        } else {
                            dealSaveMessageAndLoad(bean, otherUser);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                        AppBaseActivity.showToast(AppUtils.getString(R.string.share_txt_1) + errorInfo.getMessage());
                        dismiss();
                    }
                }
        );
    }

    private void dealSaveMessageAndLoad(ChatMessageBean bean, SendUserBean otherUser) {

        ChatSingleMsgBean singleMsgBean = ChatSingleMsgBean.from(bean, otherUser);
        singleMsgBean.read = 1;
        AppDatas.MsgDB()
                .chatSingleMsgDao()
                .insertAll(singleMsgBean);
        VimMessageBean vimMessageBean = VimMessageBean.from(bean);
        vimMessageBean.sessionID = singleMsgBean.sessionID;
        huaiye.com.vim.dao.msgs.ChatUtil.get().saveChangeMsg(vimMessageBean, true);

        ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
        AppBaseActivity.showToast(getString(R.string.share_txt_2));
        dismiss();

        jumpToChat();

        EventBus.getDefault().post(new CloseZhuanFa());

    }

    private void jumpToChat() {
        SendUserBean mySelf = new SendUserBean(AppAuth.get().getUserID() + "", AppAuth.get().getDomainCode(), AppAuth.get().getUserName());
        SendUserBean otherUser = new SendUserBean(user.strUserID, TextUtils.isEmpty(user.strUserDomainCode) ? user.strDomainCode : user.strUserDomainCode, user.strUserName);
        ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
        sessionUserList.add(mySelf);
        sessionUserList.add(otherUser);

        Intent intent = new Intent(mContext, ChatSingleActivity.class);
        intent.putExtra("mOtherUserName", otherUser.strUserName);
        intent.putExtra("mOtherUserId", otherUser.strUserID);
        intent.putExtra("nUser", user);
        intent.putExtra("sessionUserList", sessionUserList);
        intent.putExtra("mOtherUserDomainCode", otherUser.strUserDomainCode);
        intent.putExtra("from", "share");
        mContext.startActivity(intent);
    }

    public void showData(Bundle data) {
        this.data = data;

        Glide.with(mContext)
                .load(AppDatas.Constants().getFileServerURL() + user.strHeadUrl)
                .apply(requestOptions)
                .into(iv_head);
        tv_name.setText(user.strUserName);
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

    private String getSessionId() {
        if (null == user) {
            return null;
        }
        return user.strDomainCode + user.strUserID;
    }

    private String getSessionName() {
        return user.strUserName;
    }

}
