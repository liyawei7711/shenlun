package huaiye.com.vim.dao.msgs;

import com.google.gson.Gson;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyUserKickout;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyKickUserMeeting;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.AppDatas;
import ttyy.com.datasdao.annos.Column;

/**
 * author: admin
 * date: 2018/01/17
 * version: 0
 * mail: secret
 * desc: MessageData
 */
public class MessageData {
    static final Gson gson = new Gson();

    public static final int AUTH_KICKOUT = 8; //

    public static final int MEET_INVITE_JISHI = 1;
    public static final int MEET_INVITE_YUYUE = 3;
    public static final int MEET_INVITE_QUXIAO = 38;
    public static final int MEET_KICKOUT = 2;
    public static final int MEET_SPEAKER_CONTROL = 6;

    public static final int TALK_INVITE = 4;//对讲邀请
    public static final int TALK_INVITE_VOICE = 41;//对讲邀请
    public static final int TALK_SPEAKER_CONTROL = 7;

    @Column
    int nMessageType;
    @Column
    String strTitle;
    @Column
    String strContent;
    @Column
    public long nMillions;

    @Column
    String strMessageJson;

    @Column
    String userId;
    @Column
    String domainCode;
    @Column
    String key;
    @Column
    int isRead;

    protected MessageData() {
        userId = AppDatas.Auth().getUserID();
        domainCode = AppDatas.Auth().getDomainCode();
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public boolean getIsRead() {
        return isRead == 1;
    }

    public static MessageData from(CNotifyUserKickout message) {
        MessageData data = new MessageData();

        data.nMessageType = AUTH_KICKOUT;
        data.nMillions = System.currentTimeMillis();
        data.strTitle = AppUtils.getString(R.string.login_kitout);
        data.strContent = AppUtils.getString(R.string.login_other);

        data.strMessageJson = gson.toJson(message);
        data.key = "authkickout" + message.strUserID + "@" + message.strMacAddr;

        return data;
    }

    public static MessageData from(CNotifyKickUserMeeting message) {
        MessageData data = new MessageData();

        data.nMessageType = MEET_KICKOUT;
        data.nMillions = System.currentTimeMillis();
        data.strTitle = AppUtils.getString(R.string.meet_kitout);
        data.strContent = AppUtils.getString(R.string.meet_kitout_bei)+"[" + message.strMeetingName + "]";

        data.strMessageJson = gson.toJson(message);
        data.key = "meetkickout" + message.nMeetingID + "@" + message.strMeetingName;

        return data;
    }

    public static MessageData from(CNotifyUserJoinTalkback message, long millions) {
        MessageData data = new MessageData();
        if (message.getRequiredMediaMode() == SdkBaseParams.MediaMode.Audio){
            data.nMessageType = TALK_INVITE_VOICE;
        }else {
            data.nMessageType = TALK_INVITE;
        }
        data.nMillions = millions;
        data.strTitle = AppUtils.getString(R.string.talk_invistor);
        data.strContent = AppUtils.getString(R.string.talk_accept_error) + message.strFromUserName + AppUtils.getString(R.string.talk_accept_at_error) + message.strTalkbackStartTime + AppUtils.getString(R.string.talk_accept_from_error);
        data.strMessageJson = gson.toJson(message);
        data.key = "talkinvite" + message.nTalkbackID + "@" + message.strTalkbackDomainCode;

        return data;
    }

    public static MessageData from(CNotifyInviteUserJoinMeeting message, long millions) {
        MessageData data = new MessageData();

        data.nMillions = millions;
        if (message.nMeetingType == 0) {
            data.nMessageType = MEET_INVITE_JISHI;
            data.strTitle = message.strMeetingName;
            data.strContent = AppUtils.getString(R.string.meet_accept_error) + message.strInviteUserName + "于" + message.strMeetingStartTime + AppUtils.getString(R.string.meet_accept_join_error);
        } else {
            if (message.nMeetingStatus == 8) {
                data.nMessageType = MEET_INVITE_QUXIAO;
                data.strTitle = message.strMeetingName;
                data.strContent = message.strInviteUserName + AppUtils.getString(R.string.meet_accept_yuyue_error) + message.strMeetingStartTime + AppUtils.getString(R.string.meet_accept_yuyue_cancel_error);
            } else {
                data.nMessageType = MEET_INVITE_YUYUE;
                data.strTitle = message.strMeetingName;
                data.strContent = message.strInviteUserName + AppUtils.getString(R.string.meet_accept_yuyue_attend_error) + message.strMeetingStartTime + AppUtils.getString(R.string.meet_accept_join_error);
            }
        }

        data.strMessageJson = gson.toJson(message);
        data.key = "meetinvite" + message.nMeetingID + "@" + message.strMeetingDomainCode;

        return data;
    }

    public String getTitle() {
        return strTitle;
    }

    public String getContent() {
        return strContent;
    }

    public String getMessageJson() {
        return strMessageJson;
    }

    SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");

    public String getDate() {
        Calendar now = Calendar.getInstance();

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(nMillions);
        Date timeDate = new Date(nMillions);

        int dateOffset = now.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR);
        switch (dateOffset) {
            case 0:

                return timeSdf.format(timeDate);
            case 1:

                return AppUtils.getString(R.string.date_yesterday_yingwen) + timeSdf.format(timeDate);
            case 2:

                return AppUtils.getString(R.string.date_before_qiantian_yingwen) + timeSdf.format(timeDate);
            default:

                return dateSdf.format(timeDate);
        }
    }

    public int getMessageType() {
        return nMessageType;
    }

    public int getIconResource() {
        switch (nMessageType) {
            case MEET_INVITE_JISHI:
                return R.drawable.icon_liebiao_jishihuiyi;
            case MEET_INVITE_YUYUE:
            case MEET_INVITE_QUXIAO:
                return R.drawable.icon_liebiao_yuyuehuiyi;
            case TALK_INVITE:
            case TALK_INVITE_VOICE:
                return R.drawable.default_image_personal;
            case MEET_KICKOUT:
            case AUTH_KICKOUT:
                return R.drawable.ic_person_kickout;
        }

        return R.drawable.ic_group;
    }

}
