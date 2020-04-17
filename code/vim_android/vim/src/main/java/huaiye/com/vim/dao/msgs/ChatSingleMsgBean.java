package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import huaiye.com.vim.common.helper.ChatLocalPathHelper;
import huaiye.com.vim.common.utils.ChatUtil;

/**
 * 点对点聊天记录
 * Created by LENOVO on 2019/3/28.
 */

@Entity(tableName = "tb_chat_single_msg")
public class ChatSingleMsgBean extends ChatMessageBase {
    @ColumnInfo
    public String toUserDomain;
    @ColumnInfo
    public String toUserName;
    @ColumnInfo
    public String toUserId;

    public static ChatSingleMsgBean from(ChatMessageBean chatMessageBean, SendUserBean receiver){
        ChatSingleMsgBean groupMsgBean = new ChatSingleMsgBean();
        groupMsgBean.type = chatMessageBean.type;
        groupMsgBean.groupType = chatMessageBean.groupType;
        ContentBean content = ChatUtil.analysisChatContentJson(chatMessageBean.content);
        groupMsgBean.msgID = content.msgID;
        groupMsgBean.msgTxt = content.msgTxt;
        groupMsgBean.summary = content.summary;
        groupMsgBean.fileUrl = content.fileUrl;
        groupMsgBean.fileName = content.fileName;
        groupMsgBean.nDuration = content.nDuration;
        groupMsgBean.fileSize = content.fileSize;
        groupMsgBean.bFire = content.bFire;

        groupMsgBean.latitude = content.latitude;
        groupMsgBean.longitude = content.longitude;


        groupMsgBean.nCallState = content.nCallState;
        groupMsgBean.fireTime = content.fireTime;
        groupMsgBean.fromUserDomain = chatMessageBean.fromUserDomain;
        groupMsgBean.fromUserId = chatMessageBean.fromUserId;
        groupMsgBean.fromUserName = chatMessageBean.fromUserName;
        groupMsgBean.groupDomainCode = chatMessageBean.groupDomainCode;
        groupMsgBean.bEncrypt = chatMessageBean.bEncrypt;
        groupMsgBean.groupID = chatMessageBean.groupID;
        groupMsgBean.sessionID = chatMessageBean.sessionID;
        groupMsgBean.sessionName = chatMessageBean.sessionName;
        groupMsgBean.time = System.currentTimeMillis();
        groupMsgBean.read = 0;
        groupMsgBean.toUserDomain = receiver.strUserDomainCode;
        groupMsgBean.toUserId = receiver.strUserID;
        groupMsgBean.toUserName = receiver.strUserName;
        groupMsgBean.localFilePath = ChatLocalPathHelper.getInstance().getChatLoaclPath(groupMsgBean.fileUrl);
        return groupMsgBean;
    }


}
