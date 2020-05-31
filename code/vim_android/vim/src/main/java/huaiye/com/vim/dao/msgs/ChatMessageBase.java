package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.io.Serializable;
import java.util.ArrayList;

import huaiye.com.vim.common.AppUtils;

import static huaiye.com.vim.ui.meet.adapter.ChatContentAdapter.CHAT_CONTENT_CUSTOM_NOTICE_ITEM;

/**
 * @Describe 聊天内容的基类
 * @Author lxf
 * @date 2019-04-24
 */
public class ChatMessageBase implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo
    public int type;
    @ColumnInfo
    public int groupType;

    @ColumnInfo
    public String fromUserDomain;

    @ColumnInfo
    public String fromUserId;

    @ColumnInfo
    public String fromUserName;

    @ColumnInfo
    public String msgID;
    @ColumnInfo
    public String msgTxt;
    @ColumnInfo
    public String summary;
    @ColumnInfo
    public String fileUrl;
    @ColumnInfo
    public String fileName;
    @ColumnInfo
    public int nDuration;
    @ColumnInfo
    public int fileSize;
    @ColumnInfo
    public int bFire;
    @ColumnInfo
    public int bEncrypt;
    @ColumnInfo
    public int nCallState;
    @ColumnInfo
    public int fireTime;
    @ColumnInfo
    public double latitude;
    @ColumnInfo
    public double longitude;

    @ColumnInfo
    public String sessionID;
    @ColumnInfo
    public String sessionName;//会话名称,发起方将会议名填写到sessionName字段

    @ColumnInfo
    public String groupDomainCode;
    @ColumnInfo
    public String groupID;

    @ColumnInfo
    public long time;

    @ColumnInfo
    public int read;

    @ColumnInfo
    public String localFilePath;

    @ColumnInfo
    public String extend1;

    @ColumnInfo
    public String extend2;

    @ColumnInfo
    public String extend3;//暂存的是content内容

    @Ignore
    public boolean isUnEncrypt;
    @Ignore
    public String mStrEncrypt;

    @Ignore
    public int downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;//0未下载 1下载中 2已下载

    @Ignore
    public boolean isPlaying = false;//当前类型是否正在播放

    @Ignore
    public boolean needShowTime = false;//当前项是否需要显示时间

    @Ignore
    public String headPic;//头像

    @Ignore
    public ArrayList<SendUserBean> sessionUserList;

    public boolean isShow() {
        return type != CHAT_CONTENT_CUSTOM_NOTICE_ITEM;
    }

}
