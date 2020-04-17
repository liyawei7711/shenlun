package huaiye.com.vim.push;

import com.huaiye.cmf.sdp.SdpMsgFRAlarmNotify;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyUserKickout;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

import huaiye.com.vim.dao.msgs.ChatMessageBean;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: MessageReceiver
 */

public interface MessageObserver {

    /**
     * 订阅 被踢出消息
     *
     * @param data
     */
    void onKickedOut(CNotifyUserKickout data);

    void onNetworkStatusChanged(SdkBaseParams.ConnectionStatus data, CQueryUserListRsp.UserInfo usrInfo);

    /**
     * 订阅对讲邀请
     *
     * @param data
     */
    void onTalkInvite(CNotifyUserJoinTalkback data, long millis);

    /**
     * 邀请查看视频推送
     */
    void onVideoPushInvite(ChatMessageBean chatMessageBean);

    /**
     * 订阅会议邀请
     *
     * @param data
     */
    void onMeetInvite(CNotifyInviteUserJoinMeeting data, long millis);
    void onMeetInviteCancel(CNotifyInviteUserCancelJoinMeeting data, long millis);

    /**
     * 人脸识别告警
     *
     * @param data
     */
    void onServerFaceAlarm(CServerNotifyAlarmInfo data);

    void onLocalFaceAlarm(SdpMsgFRAlarmNotify data);

}
