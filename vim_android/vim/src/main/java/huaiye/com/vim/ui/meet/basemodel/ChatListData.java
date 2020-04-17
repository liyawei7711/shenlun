package huaiye.com.vim.ui.meet.basemodel;

import java.io.Serializable;

/**
 * Created by ywt on 2019/4/2.
 */

public class ChatListData implements Serializable {
    public String name;
    public String message;
    public long time;
    public int unReadNum;
    /**0--私聊  1--群聊*/
    public int groupType;
    public String strUserDomainCode;
    public String strUserID;
    public String strUserName;
}
