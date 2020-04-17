package huaiye.com.vim.push;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.huaiye.cmf.sdp.SdpMessageCmCtrlRsp;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.cmf.sdp.SdpMsgFRAlarmNotify;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._api.ApiFace;
import com.huaiye.sdk.sdkabi._api.ApiIO;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.encrypt.ParamsEncryptSecretKeyDestroy;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyUserKickout;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;
import com.huaiye.sdk.sdpmsgs.io.CNotifyReconnectStatus;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.social.CNotifyMsgToUser;
import com.huaiye.sdk.sdpmsgs.social.COfflineMsgToUserReq;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListReq;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToMuliteUserRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.bus.NewChatMessage;
import huaiye.com.vim.common.AlarmMediaPlayer;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.ScreenNotify;
import huaiye.com.vim.common.helper.ChatContactsGroupUserListHelper;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.WeiXinDateFormat;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.AppMessages;
import huaiye.com.vim.dao.msgs.CaptureMessage;
import huaiye.com.vim.dao.msgs.ChatGroupMsgBean;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.dao.msgs.ChatSingleMsgBean;
import huaiye.com.vim.dao.msgs.ChatUtil;
import huaiye.com.vim.dao.msgs.ContentBean;
import huaiye.com.vim.dao.msgs.GroupDealAddMessage;
import huaiye.com.vim.dao.msgs.GroupDealMessage;
import huaiye.com.vim.dao.msgs.MessageData;
import huaiye.com.vim.dao.msgs.StopCaptureMessage;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.dao.msgs.VimMessageListBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.map.baidu.GPSLocation;
import huaiye.com.vim.map.baidu.LocationStrategy;
import huaiye.com.vim.models.EncyptUserBean;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.GroupInfo;
import huaiye.com.vim.models.contacts.bean.NotificationDelGroup;
import huaiye.com.vim.models.contacts.bean.NotificationLeaveGroup;
import huaiye.com.vim.ui.auth.StartActivity;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static com.huaiye.sdk.sdkabi._params.SdkBaseParams.ConnectionStatus.Disconnected;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;
import static huaiye.com.vim.ui.meet.adapter.ChatContentAdapter.CHAT_CONTENT_CUSTOM_NOTICE_ITEM;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: PushMessageReceiver
 */

public class MessageReceiver {

    LinkedList<MessageObserver> observers = new LinkedList<>();
    LinkedList<MessageNotify> observersNotify = new LinkedList<>();
    Gson gson = new Gson();

    static class Holder {
        static MessageReceiver SINGLETON = new MessageReceiver();
    }

    public static MessageReceiver get() {
        return Holder.SINGLETON;
    }

    public void subscribe(MessageObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(0, observer);
        }
    }

    public void subscribe(MessageNotify observer) {
        if (!observersNotify.contains(observer)) {
            observersNotify.add(0, observer);
        }
    }

    public void unSubscribe(MessageObserver observer) {
        if (observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    public void unSubscribe(MessageNotify observer) {
        if (observersNotify.contains(observer)) {
            observersNotify.remove(observer);
        }
    }

    private MessageReceiver() {
        initListener();
    }

    public void initListener() {
        HYClient.getModule(ApiTalk.class).observeTalkingStatus(new SdkNotifyCallback<CNotifyTalkbackStatusInfo>() {
            @Override
            public void onEvent(CNotifyTalkbackStatusInfo info) {
                EventBus.getDefault().post(info);
            }
        });

        HYClient.getModule(ApiMeet.class).observeMeetingStatus(new SdkNotifyCallback<CNotifyMeetingStatusInfo>() {
            @Override
            public void onEvent(CNotifyMeetingStatusInfo info) {
                EventBus.getDefault().post(info);
            }
        });

        // 对讲邀请
        HYClient.getModule(ApiTalk.class).observeInviteTalking(new SdkNotifyCallback<CNotifyUserJoinTalkback>() {
            @Override
            public void onEvent(final CNotifyUserJoinTalkback cNotifyUserJoinTalkback) {
                ScreenNotify.get().wakeUpAndUnlock();
                if (AppUtils.isHide) {
                    ScreenNotify.get().openApplicationFromBackground();
                }
                final long millis = System.currentTimeMillis();
                AppMessages.get().add(MessageData.from(cNotifyUserJoinTalkback, millis));

                if (!observersNotify.isEmpty()) {
                    observersNotify.get(0).refMessage();
                }
                if (!observers.isEmpty()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            observers.get(0).onTalkInvite(cNotifyUserJoinTalkback, millis);
                        }
                    }, 1000);
                }
            }
        });

        // 会议邀请
        HYClient.getModule(ApiMeet.class).observeInviteMeeting(new SdkNotifyCallback<CNotifyInviteUserJoinMeeting>() {
            @Override
            public void onEvent(CNotifyInviteUserJoinMeeting cNotifyInviteUserJoinMeeting) {
                ScreenNotify.get().wakeUpAndUnlock();
                if (AppUtils.isHide) {
                    ScreenNotify.get().openApplicationFromBackground();
                }
                if (TextUtils.isEmpty(cNotifyInviteUserJoinMeeting.strInviteUserId)) {
                    try {
                        cNotifyInviteUserJoinMeeting.strInviteUserId = cNotifyInviteUserJoinMeeting.strInviteUserTokenID
                                .substring(0, cNotifyInviteUserJoinMeeting.strInviteUserTokenID.lastIndexOf("_"));
                    } catch (Exception e) {

                    }
                }
                long millis = System.currentTimeMillis();
                if (!cNotifyInviteUserJoinMeeting.isSelfMeetCreator()) {
                    AppMessages.get().add(MessageData.from(cNotifyInviteUserJoinMeeting, millis));
                }
                if (!observersNotify.isEmpty()) {
                    observersNotify.get(0).refMessage();
                }
                if (!observers.isEmpty()) {
                    observers.get(0).onMeetInvite(cNotifyInviteUserJoinMeeting, millis);
                }
            }
        });
        HYClient.getModule(ApiMeet.class).observeMeetingInviteCancel(new SdkNotifyCallback<CNotifyInviteUserCancelJoinMeeting>() {
            @Override
            public void onEvent(CNotifyInviteUserCancelJoinMeeting cNotifyInviteUserCancelJoinMeeting) {
                ScreenNotify.get().wakeUpAndUnlock();
                if (AppUtils.isHide) {
                    ScreenNotify.get().openApplicationFromBackground();
                }
                long millis = System.currentTimeMillis();
                if (!observers.isEmpty()) {
                    observers.get(0).onMeetInviteCancel(cNotifyInviteUserCancelJoinMeeting, millis);
                }
            }
        });

        // SDK登录被踢出
        HYClient.getModule(ApiAuth.class).observeBeKickedOut(new SdkNotifyCallback<CNotifyUserKickout>() {
            @Override
            public void onEvent(CNotifyUserKickout cNotifyUserKickout) {

                AppMessages.get().add(MessageData.from(cNotifyUserKickout));

                kitOutUser(true);
            }
        });

        // 网络状态
        HYClient.getModule(ApiIO.class).observeConnectionStatus(new SdkNotifyCallback<CNotifyReconnectStatus>() {
            @Override
            public void onEvent(final CNotifyReconnectStatus cNotifyReconnectStatus) {

                if (cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Connected) {
                    if (!HYClient.getHYCapture().isCapturing()) {
                        CQueryUserListReq.UserInfo usr = new CQueryUserListReq.UserInfo();
                        usr.strUserID = HYClient.getSdkOptions().User().getUserId();
                        HYClient.getModule(ApiSocial.class).getUsers(SdkParamsCenter.Social.GetUsers()
                                .setDomainCode(HYClient.getSdkOptions().User().getDomainCode())
                                .addUser(usr), new SdkCallback<ArrayList<CQueryUserListRsp.UserInfo>>() {
                            @Override
                            public void onSuccess(ArrayList<CQueryUserListRsp.UserInfo> resp) {
                                if (resp != null && !resp.isEmpty()) {
                                    userLogin(resp.get(0), cNotifyReconnectStatus);
                                } else {
                                    userLogin(null, cNotifyReconnectStatus);
                                }
                            }

                            @Override
                            public void onError(ErrorInfo error) {
                                if (!observers.isEmpty()) {
                                    observers.get(0).onNetworkStatusChanged(SdkBaseParams.ConnectionStatus.Disconnected, null);
                                }
                            }
                        });
                    } else {
                        userLogin(null, cNotifyReconnectStatus);
                    }

                } else {
                    if (!observers.isEmpty()) {
                        observers.get(0).onNetworkStatusChanged(cNotifyReconnectStatus.getConnectionStatus(), null);
                    }
                }
            }
        });

        // 本地告警
        HYClient.getModule(ApiFace.class).observeLocalFaceAlarm(new SdkNotifyCallback<SdpMsgFRAlarmNotify>() {
            @Override
            public void onEvent(SdpMsgFRAlarmNotify sdpMsgFRAlarmNotify) {
                if (!observers.isEmpty()) {
                    observers.get(0).onLocalFaceAlarm(sdpMsgFRAlarmNotify);
                }
            }
        });

        // 服务器告警
        HYClient.getModule(ApiFace.class).observeServerFaceAlarm(new SdkNotifyCallback<CServerNotifyAlarmInfo>() {
            @Override
            public void onEvent(CServerNotifyAlarmInfo cServerNotifyAlarmInfo) {
                if (!observers.isEmpty()) {
                    observers.get(0).onServerFaceAlarm(cServerNotifyAlarmInfo);
                }
            }
        });

        // 监听 离线消息
        Log.i("MBESdk", "监听 离线消息");
        HYClient.getModule(ApiSocial.class).observeOfflineMessages(new SdkNotifyCallback<COfflineMsgToUserReq>() {
            @Override
            public void onEvent(COfflineMsgToUserReq data) {
                if (HYClient.getSdkOptions().encrypt().isEncryptBind()) {
                    doOffineMsgBean(data);
                } else {
                    VIMApp.getInstance().getLinXianBuChang().add(data);
                }
            }
        });

        HYClient.getModule(ApiSocial.class).observeOnlineMessages(new SdkNotifyCallback<CNotifyMsgToUser>() {
            @Override
            public void onEvent(CNotifyMsgToUser data) {
                onReceiverPush(data.strMsg);
            }

        });

    }

    public void doOffinMsg() {
        try {
            for (COfflineMsgToUserReq data : VIMApp.getInstance().getLinXianBuChang()) {
                doOffineMsgBean(data);
            }
            VIMApp.getInstance().getLinXianBuChang().clear();
        } catch (Exception e0) {

        }
    }

    public void doOffineMsgBean(COfflineMsgToUserReq data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (COfflineMsgToUserReq.Message msg : data.listMsg) {
            try {
                JSONObject jsonObject = new JSONObject(msg.strMsg);
                long millions = sdf.parse(msg.strDateTime).getTime();

                JSONObject msgBody = jsonObject.getJSONObject("msgbody");

                if (msgBody.has("nMeetingID")) {
                    CNotifyInviteUserJoinMeeting meetInvite = gson.fromJson(jsonObject.getString("msgbody"), CNotifyInviteUserJoinMeeting.class);
                    try {
                        if (meetInvite != null) {
                            AppMessages.get().add(MessageData.from(meetInvite, millions));
                        }
                    } catch (Exception e) {
                    }
                } else if (msgBody.has("nTalkbackID")) {
                    CNotifyUserJoinTalkback talkInvite = gson.fromJson(jsonObject.getString("msgbody"), CNotifyUserJoinTalkback.class);
                    AppMessages.get().add(MessageData.from(talkInvite, millions));
                } else if (msgBody.has("strMsgbody")) {
                    String strMsg = msgBody.getString("strMsgbody");
                    onReceiverPush(strMsg);
//                                ChatMessageBean chatMessageBean = gson.fromJson(strMsg, ChatMessageBean.class);
//                                dealMessage(chatMessageBean, gson, strMsg, msg.strDateTime);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        if (!observersNotify.isEmpty()) {
            observersNotify.get(0).refMessage();
        }
    }

    public void onReceiverPush(String strMsg) {
        try {
            final ChatMessageBean chatMessageBean = gson.fromJson(strMsg, ChatMessageBean.class);

            switch (chatMessageBean.type) {
                case AppUtils.NOTIFICATION_TYPE_CRESTE_GROUP:
                    ContactsGroupUserListBean nContactsGroupUserListBean = gson.fromJson(chatMessageBean.content, ContactsGroupUserListBean.class);
                    ChatContactsGroupUserListHelper.getInstance().cacheContactsGroupDetail(nContactsGroupUserListBean.strGroupID + "", nContactsGroupUserListBean);
                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_CREATE_GROUP_SUCCESS_ADDGROUP_TO_LIST, nContactsGroupUserListBean.strGroupID));
                    break;
                case AppUtils.NOTIFICATION_TYPE_DEL_GROUP:
                    NotificationDelGroup nNotificationDelGroup = gson.fromJson(chatMessageBean.content, NotificationDelGroup.class);
//                            VimMessageListMessages.get().del(nNotificationDelGroup.strGroupDomainCode + nNotificationDelGroup.strGroupID);
//                            AppDatas.MsgDB()
//                                    .chatGroupMsgDao()
//                                    .deleteBySessionID(nNotificationDelGroup.strGroupDomainCode + nNotificationDelGroup.strGroupID);
                    VimMessageBean vimMessageBean = new VimMessageBean();
                    vimMessageBean.groupID = nNotificationDelGroup.strGroupID;
                    EventBus.getDefault().post(vimMessageBean);
                    break;
                case AppUtils.NOTIFICATION_TYPE_LEAVE_GROUP:
                    NotificationLeaveGroup nNotificationLeaveGroup = gson.fromJson(chatMessageBean.content, NotificationLeaveGroup.class);
                    if (nNotificationLeaveGroup.strUserID.equals(AppDatas.Auth().getUserID())) {
//                                VimMessageListMessages.get().del(nNotificationLeaveGroup.strGroupDomainCode + nNotificationLeaveGroup.strGroupID);
//                                AppDatas.MsgDB()
//                                        .chatGroupMsgDao()
//                                        .deleteBySessionID(nNotificationLeaveGroup.strGroupDomainCode + nNotificationLeaveGroup.strGroupID);
                        VimMessageBean vimMessageBean2 = new VimMessageBean();
                        vimMessageBean2.groupID = nNotificationLeaveGroup.strGroupID;
                        EventBus.getDefault().post(vimMessageBean2);
                    } else {
                        queryGroupChatInfo(nNotificationLeaveGroup.strGroupDomainCode, nNotificationLeaveGroup.strGroupID);
                    }
                    break;
                case AppUtils.CAPTURE_TYPE_INT:
                    EventBus.getDefault().post(new CaptureMessage(chatMessageBean.fromUserId, chatMessageBean.fromUserDomain, chatMessageBean.fromUserName, chatMessageBean.sessionID));
                    break;
                case AppUtils.STOP_CAPTURE_TYPE_INT:
                    EventBus.getDefault().post(new StopCaptureMessage(chatMessageBean.fromUserId, chatMessageBean.fromUserDomain, chatMessageBean.fromUserName));
                    break;
                case AppUtils.NOTIFICATION_TYPE_GET_USER_GPS:
                    sendUserGps(chatMessageBean);//, getSendMessage(chatMessageBean));
                    break;
                case AppUtils.NOTIFICATION_TYPE_MODIFY_USER_HEAD:
                    dealModifyUserHead(chatMessageBean, gson, strMsg);
                    break;
                case AppUtils.MESSAGE_TYPE_SHARE:
                case AppUtils.MESSAGE_TYPE_JINJI:
                case AppUtils.MESSAGE_TYPE_TEXT:
                case AppUtils.MESSAGE_TYPE_IMG:
                case AppUtils.MESSAGE_TYPE_FILE:
                case AppUtils.MESSAGE_TYPE_VIDEO_FILE:
                case AppUtils.MESSAGE_TYPE_AUDIO_FILE:
                case AppUtils.MESSAGE_TYPE_GROUP_MEET:
                case AppUtils.MESSAGE_TYPE_ADDRESS:
                case AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE:
                case AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO:
                    if (AppUtils.MESSAGE_TYPE_JINJI == chatMessageBean.type) {
                        ContentBean content = huaiye.com.vim.common.utils.ChatUtil.analysisChatContentJson(chatMessageBean.content);
                        chatMessageBean.fromUserDomain = content.strDomainCode;
                        chatMessageBean.fromUserId = content.strUserID;
                        chatMessageBean.bEncrypt = 0;

                        chatMessageBean.sessionUserList = new ArrayList<>();
                        chatMessageBean.sessionUserList.add(new SendUserBean(content.strUserID, content.strDomainCode, content.strUserName));
                        chatMessageBean.sessionUserList.add(new SendUserBean(AppAuth.get().getUserID(), AppAuth.get().getDomainCode(), AppAuth.get().getUserName()));
                    }
                    if (chatMessageBean.bEncrypt == 1) {
                        boolean isGroup = false;
                        String ID = "";
                        String DomainCode = "";
                        if (chatMessageBean.groupType == 1) {
                            isGroup = true;
                        } else {
                            for (SendUserBean temp : chatMessageBean.sessionUserList) {
                                if (!temp.strUserID.equalsIgnoreCase(AppAuth.get().getUserID())) {
                                    ID = temp.strUserID;
                                    DomainCode = temp.strUserDomainCode;
                                    break;
                                }
                            }
                        }

                        String unEncryptStr;
                        if (chatMessageBean.type == AppUtils.MESSAGE_TYPE_SHARE) {
                            unEncryptStr = chatMessageBean.content;
                        } else {
                            ContentBean content = huaiye.com.vim.common.utils.ChatUtil.analysisChatContentJson(chatMessageBean.content);
                            if (chatMessageBean.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                                unEncryptStr = content.msgTxt;
                            } else {
                                unEncryptStr = TextUtils.isEmpty(content.fileUrl) ? content.msgTxt : content.fileUrl;
                            }
                        }

                        EncryptUtil.converEncryptText(unEncryptStr, isGroup,
                                isGroup ? chatMessageBean.groupID : "", isGroup ? chatMessageBean.groupDomainCode : "",
                                isGroup ? chatMessageBean.fromUserId : ID, isGroup ? chatMessageBean.fromUserDomain : DomainCode,
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp rsp) {
                                        if (rsp.m_nResultCode == 0) {
                                            if (chatMessageBean.type == AppUtils.MESSAGE_TYPE_SHARE) {
                                                chatMessageBean.content = rsp.m_lstData.get(0).strData;
                                            } else {
                                                ContentBean content = huaiye.com.vim.common.utils.ChatUtil.analysisChatContentJson(chatMessageBean.content);
                                                if (chatMessageBean.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                                                    content.msgTxt = rsp.m_lstData.get(0).strData;
                                                } else {
                                                    if (TextUtils.isEmpty(content.fileUrl)) {
                                                        content.msgTxt = rsp.m_lstData.get(0).strData;
                                                    } else {
                                                        content.fileUrl = rsp.m_lstData.get(0).strData;
                                                    }
                                                }
                                                chatMessageBean.content = gson.toJson(content);
                                            }
                                            dealMessage(chatMessageBean, gson, strMsg, "");
                                        } else {
                                            dealMessage(chatMessageBean, gson, strMsg, "");
                                        }
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        dealMessage(chatMessageBean, gson, strMsg, "");
                                    }
                                });
                        return;
                    } else {
                        dealMessage(chatMessageBean, gson, strMsg, "");
                    }
                    break;
                case AppUtils.NOTIFICATION_TYPE_PERSON_PUSH:
                    if (!observers.isEmpty()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                observers.get(0).onVideoPushInvite(chatMessageBean);
                            }
                        }, 1000);
                    }
                    break;
                case AppUtils.NOTIFICATION_TYPE_MODIFY_GROUP://群信息变更
                    dealModifyGroup(chatMessageBean, gson);
                    break;
                case AppUtils.NOTIFICATION_TYPE_ADD_FRIEND:
                    addFriend();
                    break;
                case AppUtils.NOTIFICATION_TYPE_DEL_FRIEND:
                    delFriend();
                    break;
                case AppUtils.NOTIFICATION_TYPE_ADD_MEMBER:
                case AppUtils.NOTIFICATION_TYPE_GROUP_KICKOUT_MEMBER:
                    MessageEvent me = new MessageEvent(AppUtils.EVENT_REFRESH_GROUP_DETAIL);
                    String sessionID;
                    String groupID;
                    String groupDomain;
                    String sessionName;
                    if (chatMessageBean.type == AppUtils.NOTIFICATION_TYPE_ADD_MEMBER) {
                        GroupDealAddMessage groupDealAddMessage = gson.fromJson(chatMessageBean.content, GroupDealAddMessage.class);
                        sessionID = groupDealAddMessage.getStrGroupDomainCode() + groupDealAddMessage.getStrGroupID();
                        groupID = groupDealAddMessage.getStrGroupID();
                        groupDomain = groupDealAddMessage.getStrGroupDomainCode();
                        sessionName = groupDealAddMessage.getStrGroupName();
                        StringBuilder str = new StringBuilder();
                        for (GroupDealAddMessage.LstGroupUserBean temp : groupDealAddMessage.getLstGroupUser()) {
                            str.append(temp.strUserName + ",");
                        }
                        String content = str.toString().substring(0, str.length() - 1);
                        me.msgContent = content + "等人加入群组";
                    } else {
                        GroupDealMessage groupDealMessage = gson.fromJson(chatMessageBean.content, GroupDealMessage.class);
                        sessionID = groupDealMessage.getStrGroupDomainCode() + groupDealMessage.getStrGroupID();
                        groupID = groupDealMessage.getStrGroupID();
                        if (ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(groupDealMessage.getStrGroupID()) != null) {
                            sessionName = ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(groupDealMessage.getStrGroupID()).strGroupName;
                        } else {
                            sessionName = "";
                        }
                        groupDomain = groupDealMessage.getStrGroupDomainCode();
                        StringBuilder str = new StringBuilder();
                        for (GroupDealMessage.LstOutUserBean temp : groupDealMessage.getLstOutUser()) {
                            str.append(temp.strUserName + ",");
                        }
                        String content = str.toString().substring(0, str.length() - 1);
                        me.msgContent = content + "等人被踢出群组";
                    }
                    addGroupNotice(me.msgContent, sessionID, groupID, groupDomain, sessionName);
                    EventBus.getDefault().post(me);
                    break;
                case AppUtils.NOTIFICATION_TYPE_CLOSE_ENCRYPT:
                    EncyptUserBean bean = gson.fromJson(chatMessageBean.content, EncyptUserBean.class);
                    try {
                        if (bean.strDeviceID.equals(SP.getString(AppUtils.mDeviceIM)) &&
                                bean.strDomainCode.equals(AppAuth.get().getDomainCode()) &&
                                bean.strUserID.equals(AppAuth.get().getUserID())) {
                            HYClient.getModule(ApiEncrypt.class)
                                    .encryptUnbind(SdkParamsCenter.Encrypt.EncryptUnbind().setLocal(1),
                                            new SdkCallback<SdpMessageCmCtrlRsp>() {
                                                @Override
                                                public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmCtrlRsp) {
                                                    HYClient.getSdkOptions().encrypt().setEncryptBind(false);

                                                    MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_INIT_KITOUT);
                                                    nMessageEvent.arg0 = -1000;
                                                    EventBus.getDefault().post(nMessageEvent);

                                                }

                                                @Override
                                                public void onError(ErrorInfo errorInfo) {
                                                }
                                            });
                        } else {
                            destoryUserIdIm(bean);
                        }
                    } catch (Exception e) {

                    }
                    break;
                case AppUtils.NOTIFICATION_TYPE_DESTORY_ENCRYPT:
                    EncyptUserBean destory = gson.fromJson(chatMessageBean.content, EncyptUserBean.class);
                    try {
                        if (destory.strDomainCode.equals(AppAuth.get().getDomainCode()) &&
                                destory.strUserID.equals(AppAuth.get().getUserID())) {
                            destoryKey(destory, false);
                            serviceKitOutEncrypt();
                        } else {
                            destoryUserIdIm(destory);
                        }
                    } catch (Exception e) {

                    }
                    break;
                case AppUtils.NOTIFICATION_TYPE_ENCRYPT_BIND:
                    EncyptUserBean bindnotify = gson.fromJson(chatMessageBean.content, EncyptUserBean.class);
                    try {
                        if (AppAuth.get().getDomainCode().equals(bindnotify.strDomainCode) &&
                                AppAuth.get().getUserID().equals(bindnotify.strUserID)) {
                        } else {
                            destoryUserIdIm(bindnotify);
                        }
                    } catch (Exception e) {

                    }
                    break;
                default:
                    break;
            }


            /**
             * 通知下各个地方,有新的聊天消息了
             */
            EventBus.getDefault().post(new NewChatMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void kitOutUser(boolean isKitOut) {
        if (!observers.isEmpty()) {
            AppBaseActivity activity = (AppBaseActivity) observers.get(0);

            if (isKitOut) {
                activity.showToast("你已被踢出登录");
            }

            observers.get(0).onTalkInvite(null, 0);
            observers.get(0).onMeetInvite(null, 0);

            Intent intent = new Intent(activity, StartActivity.class);
            intent.putExtra("from", "");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        }
    }

    private void sendUserGps(ChatMessageBean chatMessageBean) {
        JsonObject json = new JsonObject();
        JsonObject rGPSInfo = new JsonObject();
        BDLocation nBDLocation = VIMApp.getInstance().locationService.getCurrentBDLocation();
        if (null == nBDLocation) {
            return;
        }
        LatLng latLng = LocationStrategy.convertBaiduToGPS(new LatLng(nBDLocation.getLatitude(), nBDLocation.getLongitude()));

        json.addProperty("strObjDomainCode", AppAuth.get().getDomainCode());
        json.addProperty("nObjType", 1);
        json.addProperty("strObjID", AppAuth.get().getUserID());
        if (null != nBDLocation && null != latLng) {
            rGPSInfo.addProperty("strCollectTime", WeiXinDateFormat.getTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
            rGPSInfo.addProperty("fLongitude", latLng.longitude);
            rGPSInfo.addProperty("fLatitude", latLng.latitude);
            rGPSInfo.addProperty("fAltitude", nBDLocation.getAltitude());
            rGPSInfo.addProperty("fSpeed", nBDLocation.getSpeed());
            rGPSInfo.addProperty("nSignalGrades", GPSLocation.get().getIndex());
            rGPSInfo.addProperty("nDataSourceType", 0);
        } else {
            return;
        }

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();
            SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
            info.strUserDomainCode = chatMessageBean.fromUserDomain;
            info.strUserID = chatMessageBean.fromUserId;
            users.add(info);
            EncryptUtil.encryptTxt(rGPSInfo.toString(), true, false, "", "",
                    chatMessageBean.fromUserId, chatMessageBean.fromUserDomain, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                            json.addProperty("rGPSInfo", sessionRsp.m_lstData.get(0).strData);
                            sendWetherEncrypt(chatMessageBean, json.toString());
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            AppBaseActivity.showToast("信息加密失败");
                        }
                    });
        } else {
            if (nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            json.add("rGPSInfo", rGPSInfo);
            sendWetherEncrypt(chatMessageBean, json.toString());
        }
    }

    private void serviceKitOutEncrypt() {
        HYClient.getSdkOptions().encrypt().setEncryptBind(false);

        MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_INIT_KITOUT);
        nMessageEvent.arg0 = -4;
        EventBus.getDefault().post(nMessageEvent);
    }

    public static void destoryKey(EncyptUserBean destory, boolean local) {
//        if(BuildConfig.DEBUG) {
//            return;
//        }
        ParamsEncryptSecretKeyDestroy destroy = SdkParamsCenter.Encrypt.EncryptSecretKeyDestroy().setLocal(local);

        HYClient.getModule(ApiEncrypt.class)
                .secretKeyDestroy(destory == null ? destroy : destroy.setRandom(destory.strRandom)
                        , new SdkCallback<SdpMessageCmCtrlRsp>() {
                            @Override
                            public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmCtrlRsp) {
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {

                            }
                        });
    }

    public static void destoryUserIdIm(EncyptUserBean bean) {
//        if(BuildConfig.DEBUG) {
//            return;
//        }
        HYClient.getModule(ApiEncrypt.class)
                .encryptResetIm(SdkParamsCenter.Encrypt.EncryptResetIM()
                                .setUserID(bean == null ? AppAuth.get().getUserID() : bean.strUserID),
                        new SdkCallback<SdpMessageCmCtrlRsp>() {
                            @Override
                            public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmCtrlRsp) {

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                            }
                        });
    }

    private void sendWetherEncrypt(ChatMessageBean chatMessageBean, String msgContent) {
        ChatMessageBean bean = new ChatMessageBean();
        bean.content = msgContent;

        bean.type = AppUtils.NOTIFICATION_TYPE_PUSH_USER_GPS;
        bean.sessionID = chatMessageBean.fromUserDomain + chatMessageBean.fromUserId;
        bean.sessionName = chatMessageBean.fromUserName;
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 0;
        bean.groupDomainCode = "";
        bean.groupID = "";
        bean.time = System.currentTimeMillis() / 1000;

        SendUserBean mySelf = new SendUserBean(AppAuth.get().getUserID() + "", AppAuth.get().getDomainCode(), AppAuth.get().getUserName());
        SendUserBean otherUser = new SendUserBean(chatMessageBean.fromUserId, chatMessageBean.fromUserDomain, chatMessageBean.fromUserName);

        bean.sessionUserList = new ArrayList<>();
        bean.sessionUserList.add(mySelf);
        bean.sessionUserList.add(otherUser);

        ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
//                sessionUserList.add(new SendUserBean(mySelf.strUserID, mySelf.strUserDomainCode, mySelf.strUserName));
        sessionUserList.add(new SendUserBean(otherUser.strUserID, otherUser.strUserDomainCode, otherUser.strUserName));
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
                        AppBaseActivity.showToast("发送失败" + errorInfo.getMessage());
                    }
                }
        );
    }

    /**
     * 处理头像
     *
     * @param chatMessageBean
     * @param gson
     * @param strMsg
     */
    private void dealModifyUserHead(ChatMessageBean chatMessageBean, Gson gson, String strMsg) {
        User user = gson.fromJson(chatMessageBean.content, User.class);
        if (null != user) {
            User localUser = AppDatas.MsgDB().getFriendListDao().getFriend(user.strUserID, user.strDomainCode);
            if (null != localUser) {//本地有缓存 说明是好友或者是群成员,否则不做处理
                localUser.strDomainCode = user.strDomainCode;
                localUser.strUserDomainCode = user.strDomainCode;
                localUser.strHeadUrl = user.strHeadUrl;
                AppDatas.MsgDB().getFriendListDao().insert(localUser);
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_MESSAGE_MODIFY_HEAD_PIC, localUser));
            }

        }

    }

    private void addFriend() {
        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_MESSAGE_ADD_FRIEND));

    }

    private void delFriend() {
        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_MESSAGE_DEL_FRIEND));
    }

    private void dealModifyGroup(ChatMessageBean chatMessageBean, Gson gson) {
        ContactsGroupUserListBean nContactsGroupUserListBeanNew = gson.fromJson(chatMessageBean.content, ContactsGroupUserListBean.class);
        if (null != nContactsGroupUserListBeanNew) {
            if (null != ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(nContactsGroupUserListBeanNew.strGroupID)) {

                ContactsGroupUserListBean currentContactsGroupUserListBean = ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(nContactsGroupUserListBeanNew.strGroupID);
                currentContactsGroupUserListBean.strGroupDomainCode = nContactsGroupUserListBeanNew.strGroupDomainCode;

                currentContactsGroupUserListBean.strGroupID = nContactsGroupUserListBeanNew.strGroupID;

                MessageEvent event = new MessageEvent(AppUtils.EVENT_MESSAGE_MODIFY_GROUP, currentContactsGroupUserListBean.strGroupID);

                if (!TextUtils.isEmpty(nContactsGroupUserListBeanNew.strGroupName)) {
                    if (!nContactsGroupUserListBeanNew.strGroupName.equals(currentContactsGroupUserListBean.strGroupName)) {
                        event.argStr1 = "群名称修改为:" + nContactsGroupUserListBeanNew.strGroupName;
                    }
                    currentContactsGroupUserListBean.strGroupName = nContactsGroupUserListBeanNew.strGroupName;
                }

                if (!TextUtils.isEmpty(nContactsGroupUserListBeanNew.strAnnouncement)) {
                    if (!nContactsGroupUserListBeanNew.strAnnouncement.equals(currentContactsGroupUserListBean.strAnnouncement)) {
                        event.argStr1 = "群公告修改为:" + nContactsGroupUserListBeanNew.strAnnouncement;
                    }
                    currentContactsGroupUserListBean.strAnnouncement = nContactsGroupUserListBeanNew.strAnnouncement;
                }

                currentContactsGroupUserListBean.nBeinviteMode = nContactsGroupUserListBeanNew.nBeinviteMode;
                currentContactsGroupUserListBean.nInviteMode = nContactsGroupUserListBeanNew.nInviteMode;
                currentContactsGroupUserListBean.nTeamMemberLimit = nContactsGroupUserListBeanNew.nTeamMemberLimit;
                if (!TextUtils.isEmpty(nContactsGroupUserListBeanNew.strHeadUrl)) {
                    currentContactsGroupUserListBean.strHeadUrl = nContactsGroupUserListBeanNew.strHeadUrl;
                    event.argStr1 = "群头像已修改";
                }

                GroupInfo groupInfo = AppDatas.MsgDB().getGroupListDao().getGroupInfo(nContactsGroupUserListBeanNew.strGroupID, nContactsGroupUserListBeanNew.strGroupDomainCode);
                if (null == groupInfo) {
                    groupInfo = new GroupInfo();
                }
                groupInfo.strGroupDomainCode = currentContactsGroupUserListBean.strGroupDomainCode;
                groupInfo.strGroupID = currentContactsGroupUserListBean.strGroupID;
                groupInfo.strGroupName = currentContactsGroupUserListBean.strGroupName;
                groupInfo.strHeadUrl = currentContactsGroupUserListBean.strHeadUrl;
                AppDatas.MsgDB().getGroupListDao().insert(groupInfo);
                ChatContactsGroupUserListHelper.getInstance().cacheContactsGroupDetail(currentContactsGroupUserListBean.strGroupID + "", currentContactsGroupUserListBean);
                addGroupNotice(event.argStr1, groupInfo.strGroupDomainCode + groupInfo.strGroupID, groupInfo.strGroupID, groupInfo.strGroupDomainCode, groupInfo.strGroupName);
                EventBus.getDefault().post(event);
                EventBus.getDefault().post(currentContactsGroupUserListBean);//刷新页面name
            }

            if (!TextUtils.isEmpty(nContactsGroupUserListBeanNew.strGroupName)) {
                VimMessageListMessages.get().updateGroupName(nContactsGroupUserListBeanNew.strGroupDomainCode + nContactsGroupUserListBeanNew.strGroupID, nContactsGroupUserListBeanNew.strGroupName);
            }
            EventBus.getDefault().post(new VimMessageBean());//通知消息列表页面刷新数据

        }
    }

    /**
     * 群组信息变更
     *
     * @param notice
     */
    private void addGroupNotice(String notice, String sessionID, String groupID, String groupDomain, String sessionName) {
        ChatGroupMsgBean bean = new ChatGroupMsgBean();
        bean.groupType = 1;
        bean.type = CHAT_CONTENT_CUSTOM_NOTICE_ITEM;
        bean.sessionID = sessionID;
        bean.groupID = groupID;
        bean.groupDomainCode = groupDomain;
        if (!TextUtils.isEmpty(sessionName)) {
            bean.sessionName = sessionName;
        }
        bean.msgTxt = notice;
        bean.time = System.currentTimeMillis();

        AppDatas.MsgDB()
                .chatGroupMsgDao().insert(bean);

        VimMessageBean bean2 = VimMessageBean.from(bean);
        ChatUtil.get().saveChangeMsg(bean2, true);

    }

    private void dealMessage(ChatMessageBean chatMessageBean, Gson gson, String strMsg, String time) {

        String sessionId = "";
        if (null == chatMessageBean || TextUtils.isEmpty(chatMessageBean.fromUserId) || TextUtils.isEmpty(chatMessageBean.fromUserDomain) || null == gson || TextUtils.isEmpty(strMsg)) {
            return;
        }
        long serTime = 0;
        if (!TextUtils.isEmpty(time)) {
            serTime = WeiXinDateFormat.getLongTime(time, WeiXinDateFormat.DATA_FORMATE);
        }
        if (chatMessageBean.groupType == 1) {
            //群聊
            ChatGroupMsgBean groupMsgBean = ChatGroupMsgBean.from(chatMessageBean);
            if (chatMessageBean.fromUserId.equals(AppAuth.get().getUserID())) {
                groupMsgBean.read = 1;
            } else {
                groupMsgBean.read = 0;
            }
            if (serTime > 0) {
                groupMsgBean.time = serTime;
            }
            sessionId = groupMsgBean.sessionID;
            AppDatas.MsgDB()
                    .chatGroupMsgDao()
                    .insert(groupMsgBean);
            VimMessageBean bean = VimMessageBean.from(chatMessageBean);
            ChatUtil.get().saveChangeMsg(bean, true);
        } else {
            //单聊
            ArrayList<SendUserBean> messageUsers = chatMessageBean.sessionUserList;
            if (messageUsers != null && messageUsers.size() > 0) {
                if (messageUsers.size() != 2) {
                    Logger.err("receive single chat list not 2 is " + messageUsers.size());
                    return;
                }
                SendUserBean firstUser = messageUsers.get(0);
                SendUserBean secondUser = messageUsers.get(1);
                SendUserBean receiver;
                if (firstUser.strUserID.equals(chatMessageBean.fromUserId)) {
                    //第一个是发送者
                    receiver = secondUser;
                } else {
                    //第二个是发送者
                    receiver = firstUser;
                }
                ChatSingleMsgBean singleMsgBean = ChatSingleMsgBean.from(chatMessageBean, receiver);
                if (chatMessageBean.fromUserId.equals(AppAuth.get().getUserID())) {
                    singleMsgBean.read = 1;
                } else {
                    singleMsgBean.read = 0;
                }

                if (serTime > 0) {
                    chatMessageBean.time = serTime;
                }

                if (singleMsgBean.fromUserId.equals(AppDatas.Auth().getUserID())) {
                    singleMsgBean.sessionID = singleMsgBean.toUserDomain + singleMsgBean.toUserId;

                } else {
                    singleMsgBean.sessionID = singleMsgBean.fromUserDomain + singleMsgBean.fromUserId;
                }
                sessionId = singleMsgBean.sessionID;
                AppDatas.MsgDB()
                        .chatSingleMsgDao()
                        .insertAll(singleMsgBean);
                VimMessageBean bean = VimMessageBean.from(chatMessageBean);
                bean.sessionID = singleMsgBean.sessionID;
                ChatUtil.get().saveChangeMsg(bean, true);
                //EventBus.getDefault().post(new RefMessage());
            }
        }


        sendNotice(sessionId, chatMessageBean.fromUserDomain, chatMessageBean.fromUserId);
    }

    private void sendNotice(String sessionId, String fromUserDomain, String fromUserId) {
        VimMessageListBean vimBean = VimMessageListMessages.get().getMessagesSimple(sessionId);
        if (vimBean != null && vimBean.nNoDisturb == 1) {
        } else {
            if (SP.getBoolean(AppUtils.SP_CHAT_SETTING_NOTIFICATION, true) &&
                    !(AppDatas.Auth().getDomainCode().equals(fromUserDomain) &&
                            AppDatas.Auth().getUserID().equals(fromUserId))) {
                AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_ZHILLING_VOICE);
            }
        }

        MessageEvent messageEvent = new MessageEvent(AppUtils.EVENT_COMING_NEW_MESSAGE);
        messageEvent.obj2 = sessionId;
        EventBus.getDefault().post(messageEvent);
    }

    private void userLogin(CQueryUserListRsp.UserInfo resp, CNotifyReconnectStatus
            cNotifyReconnectStatus) {
        try {

            if (!observers.isEmpty() && cNotifyReconnectStatus.getConnectionStatus() == Disconnected) {
                observers.get(0).onTalkInvite(null, 0);
                observers.get(0).onMeetInvite(null, 0);
            }
            if (!observers.isEmpty()) {
                observers.get(0).onNetworkStatusChanged(cNotifyReconnectStatus.getConnectionStatus(), resp);
            }
        } catch (Exception e) {
        }
    }

    private void queryGroupChatInfo(String strGroupDomainCode, final String strGroupID) {
        ModelApis.Contacts().requestqueryGroupChatInfo(strGroupDomainCode, strGroupID, new ModelCallback<ContactsGroupUserListBean>() {
            @Override
            public void onSuccess(final ContactsGroupUserListBean contactsBean) {
                ChatContactsGroupUserListHelper.getInstance().cacheContactsGroupDetail(strGroupID, contactsBean);
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_UPDATE_GROUP_DETAIL, strGroupID));
                getGroupUserHead(contactsBean);
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
            }
        });
    }

    private void getGroupUserHead(ContactsGroupUserListBean contactsGroupUserListBean) {
        if (null != contactsGroupUserListBean && null != contactsGroupUserListBean.lstGroupUser && contactsGroupUserListBean.lstGroupUser.size() > 0) {
            ModelApis.Contacts().requestGetUserHead(contactsGroupUserListBean.lstGroupUser, new ModelCallback<ContactsBean>() {
                @Override
                public void onSuccess(ContactsBean contactsBean) {

                    if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() > 0) {
                        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                            @Override
                            public Object doOnThread() {
                                for (User user : contactsBean.userList) {
                                    user.strDomainCode = user.strUserDomainCode;
                                }
                                AppDatas.MsgDB().getFriendListDao().insertAll(contactsBean.userList);

                                return "";
                            }

                            @Override
                            public void doOnMain(Object data) {

                            }
                        });
                    }
                }
            });
        }

    }

}
