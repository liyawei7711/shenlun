package huaiye.com.vim.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import huaiye.com.vim.dao.msgs.ChatGroupMsgBean;
import huaiye.com.vim.dao.msgs.ChatGroupMsgDao;
import huaiye.com.vim.dao.msgs.ChatSingleMsgBean;
import huaiye.com.vim.dao.msgs.ChatSingleMsgDao;
import huaiye.com.vim.dao.msgs.FileLocalNameBean;
import huaiye.com.vim.dao.msgs.FileLocalNameDao;
import huaiye.com.vim.dao.msgs.FriendListDao;
import huaiye.com.vim.dao.msgs.GroupListDao;
import huaiye.com.vim.dao.msgs.JieSuoBean;
import huaiye.com.vim.dao.msgs.JieSuoDao;
import huaiye.com.vim.dao.msgs.JinJiLianXiRenBean;
import huaiye.com.vim.dao.msgs.JinJiLianXiRenDao;
import huaiye.com.vim.dao.msgs.SendMsgUserBean;
import huaiye.com.vim.dao.msgs.SendMsgUserDao;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.contacts.bean.GroupInfo;

import static huaiye.com.vim.dao.AppDatas.VERSION;

/**
 * Created by LENOVO on 2019/3/28.
 */
@Database(entities = {ChatGroupMsgBean.class, ChatSingleMsgBean.class, JieSuoBean.class,
        User.class, GroupInfo.class, SendMsgUserBean.class, JinJiLianXiRenBean.class,
        FileLocalNameBean.class}, version = VERSION)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChatGroupMsgDao chatGroupMsgDao();

    public abstract ChatSingleMsgDao chatSingleMsgDao();

    public abstract FriendListDao getFriendListDao();

    public abstract GroupListDao getGroupListDao();

    public abstract SendMsgUserDao getSendUserListDao();

    public abstract FileLocalNameDao getFileLocalListDao();

    public abstract JinJiLianXiRenDao getJinJiLianXiRenDao();

    public abstract JieSuoDao getJieSuoDao();

}
