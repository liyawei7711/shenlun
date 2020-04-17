package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * 群组聊天记录
 * Created by LENOVO on 2019/3/28.
 */

@Entity(tableName = "tb_chat_send_to_user")
public class SendMsgUserBean implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int key;
    @ColumnInfo
    public String sessionID;
    @ColumnInfo
    public String strUserID;
    @ColumnInfo
    public String strUserDomainCode;

    public SendMsgUserBean(String sessionID, String strUserID, String strUserDomainCode) {
        this.sessionID = sessionID;
        this.strUserID = strUserID;
        this.strUserDomainCode = strUserDomainCode;
    }
}
