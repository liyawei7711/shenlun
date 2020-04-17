package huaiye.com.vim.models;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;

/**
 * @Describe 界面上的主讲人,服务器没有主讲人的时候,就用的列表第一个
 * @Author lxf
 * @date 2019-04-11
 */
public class MeetSpeakMain {
    private String strUserTokenID;
    private String strUserDomainCode;
    private String strUserID;
    private String strUserName;
    /**
     *  true 服务器设置的主讲人
     *  false 没有主讲人,按顺序找到主讲人
     */
    private  boolean mainSpeakOnServer;

    private String desc;

    private boolean needShowDesc;


    public MeetSpeakMain(CGetMeetingInfoRsp.UserInfo userInfo, boolean mainSpeakOnServer) {
        this.strUserTokenID = userInfo.strUserTokenID;
        this.strUserDomainCode = userInfo.strUserDomainCode;
        this.strUserID = userInfo.strUserID;
        this.strUserName = userInfo.strUserName;
        this.mainSpeakOnServer = mainSpeakOnServer;
        this.needShowDesc = false;
        if (mainSpeakOnServer){
            desc="主讲人";
            this.needShowDesc = true;
        }else {
            if (userInfo.nUserRole == 1){
                desc = "主持人";
                this.needShowDesc = true;
            }
        }
    }


    public MeetSpeakMain(CNotifyMeetingStatusInfo.User userInfo, boolean mainSpeakOnServer) {
        this.strUserTokenID = userInfo.strUserTokenID;
        this.strUserDomainCode = userInfo.strUserDomainCode;
        this.strUserID = userInfo.strUserID;
        this.strUserName = userInfo.strUserName;
        this.mainSpeakOnServer = mainSpeakOnServer;
        this.needShowDesc = false;
        if (mainSpeakOnServer){
            desc="主讲人";
            this.needShowDesc = true;
        }else {
            if (userInfo.nUserRole == 1){
                desc = "主持人";
                this.needShowDesc = true;
            }
        }
    }


    public boolean isMainSpeakOnServer() {
        return mainSpeakOnServer;
    }

    public String getStrUserTokenID() {
        return strUserTokenID;
    }

    public String getStrUserDomainCode() {
        return strUserDomainCode;
    }

    public String getStrUserID() {
        return strUserID;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isNeedShowDesc() {
        return needShowDesc;
    }
}
