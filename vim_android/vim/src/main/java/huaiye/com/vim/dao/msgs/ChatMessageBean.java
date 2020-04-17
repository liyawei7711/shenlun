package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Ignore;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 服务器返回的原始数据对象
 * Created by LENOVO on 2019/3/28.
 */

public class ChatMessageBean implements Serializable {
    public long id;
    public int type;
    /*@PrimaryKey
    public long time;*/
    public String fromUserDomain;
    public String toUserDomain;
    public String fromUserId;
    public String toUserId;
    public String fromUserName;
    public String toUserName;
    public String content;//消息内容
    public String sessionID;
    public String sessionName;//会话名称,发起方将会议名填写到sessionName字段
    public int groupType;//0：个人 1：会议
    public int bEncrypt = HYClient.getSdkOptions().encrypt().isEncryptBind() ? 1 : 0;//0：不加密 1：加密
    public String groupDomainCode;
    public String groupID;


    public String fromUserTokenId;

    public long time;

    public int read;

    @Ignore
    public ArrayList<SendUserBean> sessionUserList;
}
