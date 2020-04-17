package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Ignore;

import com.google.gson.Gson;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.io.Serializable;
import java.util.ArrayList;

import huaiye.com.vim.common.utils.ChatUtil;
import ttyy.com.datasdao.annos.Column;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: VimMessageBean
 */

public class VimMessageBean implements Serializable {

    @Column
    public int type;
    @Column
    public int groupType;
    @Column
    public String groupDomainCode;
    @Column
    public String groupID;
    @Column
    public String sessionID;
    @Column
    public String sessionName;
    @Column
    public String fromUserId;
    @Column
    public String fromUserTokenId;
    @Column
    public String fromUserDomain;
    @Column
    public String fromUserName;
    @Column
    public String msgID;
    @Column
    public String msgTxt;
    @Column
    public String fileUrl;
    @Column
    public int nDuration;
    @Column
    public int fileSize;
    @Column
    public int bFire;
    @Column
    public int bEncrypt;

    @Column
    public double latitude;
    @Column
    public double longitude;

    @Column
    public int nCallState;
    @Column
    public int fireTime;
    @Column
    public ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
    @Column
    public long time;

    @Ignore
    public boolean isSend;
    @Ignore
    public long nMsgToptime;
    @Ignore
    public int nMsgTop;
    @Ignore
    public int nNoDisturb;

    public static VimMessageBean from(ChatMessageBean chatMessageBean){
        VimMessageBean vimMessageBean = new VimMessageBean();
        vimMessageBean.type = chatMessageBean.type;
        vimMessageBean.groupType = chatMessageBean.groupType;
        vimMessageBean.bEncrypt = chatMessageBean.bEncrypt;
        ContentBean content = ChatUtil.analysisChatContentJson(chatMessageBean.content);
        vimMessageBean.msgID = content.msgID;
        vimMessageBean.msgTxt = content.msgTxt;
        vimMessageBean.fileUrl = content.fileUrl;
        vimMessageBean.nDuration = content.nDuration;
        vimMessageBean.fileSize = content.fileSize;
        vimMessageBean.bFire = content.bFire;

        vimMessageBean.latitude = content.latitude;
        vimMessageBean.longitude = content.longitude;

        vimMessageBean.nCallState = content.nCallState;
        vimMessageBean.fireTime = content.fireTime;
        vimMessageBean.fromUserDomain = chatMessageBean.fromUserDomain;
        vimMessageBean.fromUserId = chatMessageBean.fromUserId;
        vimMessageBean.fromUserName = chatMessageBean.fromUserName;
        vimMessageBean.groupDomainCode = chatMessageBean.groupDomainCode;
        vimMessageBean.groupID = chatMessageBean.groupID;
        vimMessageBean.sessionID = chatMessageBean.sessionID;
        vimMessageBean.sessionName = chatMessageBean.sessionName;
        vimMessageBean.time = System.currentTimeMillis();
        vimMessageBean.sessionUserList=chatMessageBean.sessionUserList;
        return vimMessageBean;
    }
    public static VimMessageBean from(ChatMessageBase chatMessageBean){
        VimMessageBean vimMessageBean = new VimMessageBean();
        vimMessageBean.type = chatMessageBean.type;
        vimMessageBean.groupType = chatMessageBean.groupType;
        vimMessageBean.fromUserDomain = chatMessageBean.fromUserDomain;
        vimMessageBean.fromUserId = chatMessageBean.fromUserId;
        vimMessageBean.fromUserName = chatMessageBean.fromUserName;
        vimMessageBean.msgID = chatMessageBean.msgID;
        vimMessageBean.msgTxt = chatMessageBean.msgTxt;
        vimMessageBean.fileUrl = chatMessageBean.fileUrl;
        vimMessageBean.nDuration = chatMessageBean.nDuration;
        vimMessageBean.fileSize = chatMessageBean.fileSize;
        vimMessageBean.bFire = chatMessageBean.bFire;
        vimMessageBean.bEncrypt = chatMessageBean.bEncrypt;

        vimMessageBean.latitude = chatMessageBean.latitude;
        vimMessageBean.longitude = chatMessageBean.longitude;

        vimMessageBean.nCallState = chatMessageBean.nCallState;
        vimMessageBean.fireTime = chatMessageBean.fireTime;
        vimMessageBean.sessionID = chatMessageBean.sessionID;
        vimMessageBean.sessionName = chatMessageBean.sessionName;
        vimMessageBean.groupDomainCode = chatMessageBean.groupDomainCode;
        vimMessageBean.groupID = chatMessageBean.groupID;
        vimMessageBean.time = chatMessageBean.time;
        vimMessageBean.sessionUserList=chatMessageBean.sessionUserList;
        return vimMessageBean;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
