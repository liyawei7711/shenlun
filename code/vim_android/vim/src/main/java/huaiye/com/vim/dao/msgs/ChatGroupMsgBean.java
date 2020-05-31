package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Entity;

import huaiye.com.vim.common.helper.ChatLocalPathHelper;
import huaiye.com.vim.common.utils.ChatUtil;

/**
 * 群组聊天记录
 * Created by LENOVO on 2019/3/28.
 */

@Entity(tableName = "tb_chat_group_msg")
public class ChatGroupMsgBean extends ChatMessageBase  {

    public static ChatGroupMsgBean from(ChatMessageBean chatMessageBean){
        ChatGroupMsgBean groupMsgBean = new ChatGroupMsgBean();
        groupMsgBean.type = chatMessageBean.type;
        groupMsgBean.bEncrypt = chatMessageBean.bEncrypt;
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
        groupMsgBean.longitude = content.longitude;
        groupMsgBean.latitude = content.latitude;
        groupMsgBean.nCallState = content.nCallState;
        groupMsgBean.fireTime = content.fireTime;
        groupMsgBean.fromUserDomain = chatMessageBean.fromUserDomain;
        groupMsgBean.fromUserId = chatMessageBean.fromUserId;
        groupMsgBean.fromUserName = chatMessageBean.fromUserName;
        groupMsgBean.groupDomainCode = chatMessageBean.groupDomainCode;
        groupMsgBean.groupID = chatMessageBean.groupID;
        groupMsgBean.sessionID = chatMessageBean.sessionID;
        groupMsgBean.sessionName = chatMessageBean.sessionName;
        groupMsgBean.sessionUserList = chatMessageBean.sessionUserList;
        groupMsgBean.time = System.currentTimeMillis();
        groupMsgBean.read = 0;
        groupMsgBean.localFilePath = ChatLocalPathHelper.getInstance().getChatLoaclPath(groupMsgBean.fileUrl);
        return groupMsgBean;
    }

}
