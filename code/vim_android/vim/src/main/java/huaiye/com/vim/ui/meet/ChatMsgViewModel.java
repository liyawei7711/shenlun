package huaiye.com.vim.ui.meet;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.ChatGroupMsgBean;
import huaiye.com.vim.dao.msgs.ChatSingleMsgBean;

/**
 * Created by LENOVO on 2019/3/28.
 */

public class ChatMsgViewModel extends ViewModel {
    private LiveData<List<ChatGroupMsgBean>> chatGroupMsg;
    private LiveData<List<ChatSingleMsgBean>> chatSingleMsg;

    public  LiveData<List<ChatGroupMsgBean>> getChatGroupMsg(String meetID){
        if(chatGroupMsg == null){
            chatGroupMsg = AppDatas.MsgDB()
                    .chatGroupMsgDao()
                    .queryAll(meetID);
        }
        return chatGroupMsg;
    }


    public  LiveData<List<ChatSingleMsgBean>> getChatSingleMsg(String firstUser,String secondUser){
        if(chatSingleMsg == null){
            chatSingleMsg = AppDatas.MsgDB()
                    .chatSingleMsgDao()
                    .queryAll(firstUser,secondUser);
        }
        return chatSingleMsg;
    }

    public  LiveData<List<ChatSingleMsgBean>> getFirstPageChatSingleMsg(String firstUser,String secondUser,int index,int limit){
        if(chatSingleMsg == null){
            chatSingleMsg = AppDatas.MsgDB()
                    .chatSingleMsgDao()
                    .queryPagingItem(firstUser,secondUser,index,limit);
        }
        return chatSingleMsg;
    }


}
