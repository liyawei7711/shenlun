package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Ignore;

import com.google.gson.Gson;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.io.Serializable;
import java.util.ArrayList;

import huaiye.com.vim.common.AppUtils;
import ttyy.com.datasdao.annos.Column;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: vimMessageBean
 */

public class VimMessageListBean implements Serializable {

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
    public String lastUserId;
    @Column
    public String lastUserDomain;
    @Column
    public String lastUserName;
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
    public int nCallState;
    @Column
    public int fireTime;
    @Column
    public ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
    @Column
    public long time;
    @Column
    public String ownerId;
    @Column
    public String ownerDomain;
    @Column
    public int isRead;
    @Column
    public long nMsgToptime;
    @Column
    public int nMsgTop;
    @Column
    public int nNoDisturb;

    @Ignore
    public String strHeadUrl;

    @Ignore
    public boolean isUnEncrypt;
    @Ignore
    public String mStrEncrypt = "";

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getTime() {
        return AppUtils.getTimeHour(time);
    }
}
