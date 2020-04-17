package huaiye.com.vim.dao.msgs;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * 群组聊天dao
 * Created by LENOVO on 2019/3/28.
 */
@Dao
public interface ChatGroupMsgDao {

    //返回Long数据表示，插入条目的主键值（uid）
    @Insert(onConflict = REPLACE)
    Long insert(ChatGroupMsgBean msg);

    @Insert(onConflict = REPLACE)
    void insertAll(ChatGroupMsgBean... msg);

    @Query("select * from tb_chat_group_msg where groupID=:strGroupID order by time desc limit 1")
    ChatGroupMsgBean queryLastItem(String strGroupID);

    @Query("select * from tb_chat_group_msg where groupID=:strGroupID order by time")
    LiveData<List<ChatGroupMsgBean>> queryAll(String strGroupID);

    @Query("select *from (select * from tb_chat_group_msg where groupID=:strGroupID order by time desc limit :index,:limit) order by time")
    List<ChatGroupMsgBean> queryPagingItemWithoutLive(String strGroupID,int index,int limit);

    @Query("select * from tb_chat_group_msg where groupID=:strGroupID  order by time")
    List<ChatGroupMsgBean> queryAllGroupChat(String strGroupID);

    @Query("select count(*) from tb_chat_group_msg where groupID=:strGroupID  AND read=0")
    int getGroupUnreadNum(String strGroupID);

    @Query("select count(*) from tb_chat_group_msg where (read=0)")
    int getUnreadNum();

    @Query("select * from tb_chat_group_msg where groupID=:strGroupID  order by time desc limit 1")
    ChatGroupMsgBean getGroupNewestMsg(String strGroupID);

    @Query("update tb_chat_group_msg set read= 1 where groupID=:strGroupID AND type!=9996 AND bFire!=1")
    void updateAllRead(String strGroupID);
    @Query("update tb_chat_group_msg set read= 1 where sessionID=:sessionID AND type!=9996 AND bFire!=1")
    void updateSessionIDRead(String sessionID);

    @Query("update tb_chat_group_msg set read= 1 where groupID=:strGroupID AND msgID=:msgID")
    void updateReadWithMsgID( String strGroupID,String msgID);

    @Query("update tb_chat_group_msg set localFilePath=:localFilePath where groupID=:strGroupID AND id=:messageId")
    void updateDownloadState(String strGroupID,String localFilePath,long messageId);

    @Query("delete from tb_chat_group_msg where groupID=:strGroupID")
    void deleteGroup(String strGroupID);

    @Query("delete from tb_chat_group_msg where sessionID=:sessionID")
    void deleteBySessionID(String sessionID);

    @Query("delete from tb_chat_group_msg where sessionID=:sessionID AND id=:id")
    void deleteBySessionIDAndId(String sessionID,long id);

    @Query("delete from tb_chat_group_msg where sessionID=:sessionID AND msgID=:msgID")
    void deletByMsgID(String sessionID,String msgID);


    @Query("delete from tb_chat_group_msg")
    void clearData();
}
