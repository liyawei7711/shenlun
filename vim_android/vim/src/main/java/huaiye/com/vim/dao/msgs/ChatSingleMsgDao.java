package huaiye.com.vim.dao.msgs;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * 单聊dao
 * Created by LENOVO on 2019/4/2.
 */
@Dao
public interface ChatSingleMsgDao {
    @Insert(onConflict = REPLACE)
    void insertAll(ChatSingleMsgBean... msg);


    @Insert(onConflict = REPLACE)
    void insertAll(List<ChatSingleMsgBean> msg);

    @Query("select * from tb_chat_single_msg where ((fromUserId=:firstUserID  AND toUserId=:secondUserId ) or (fromUserId=:secondUserId AND toUserId=:firstUserID)) limit 1")
    ChatSingleMsgBean queryOneItem(String firstUserID, String secondUserId);


    @Query("select * from tb_chat_single_msg where ((fromUserId=:firstUserID  AND toUserId=:secondUserId ) or (fromUserId=:secondUserId AND toUserId=:firstUserID)) order by time desc limit 1")
    ChatSingleMsgBean queryLastItem(String firstUserID, String secondUserId);

    @Query("select * from tb_chat_single_msg where ((fromUserId=:firstUserID  AND toUserId=:secondUserId ) or (fromUserId=:secondUserId AND toUserId=:firstUserID)) order by time")
    LiveData<List<ChatSingleMsgBean>> queryAll(String firstUserID, String secondUserId);

    @Query("select *from (select * from tb_chat_single_msg where ((fromUserId=:firstUserID  AND toUserId=:secondUserId ) or (fromUserId=:secondUserId AND toUserId=:firstUserID)) order by time desc limit :index,:limit) order by time")
    LiveData<List<ChatSingleMsgBean>> queryPagingItem(String firstUserID, String secondUserId, int index, int limit);

    @Query("select *from (select * from tb_chat_single_msg where ((fromUserId=:firstUserID  AND toUserId=:secondUserId ) or (fromUserId=:secondUserId AND toUserId=:firstUserID)) order by time desc limit :index,:limit) order by time")
    List<ChatSingleMsgBean> queryPagingItemWithoutLive(String firstUserID, String secondUserId, int index, int limit);

    @Query("select count(*) from tb_chat_single_msg where ((fromUserId=:firstUserID  AND toUserId=:secondUserId ) or (fromUserId=:secondUserId AND toUserId=:firstUserID)) AND read=0")
    int getUnreadNum(String firstUserID, String secondUserId);

    @Query("select count(*) from tb_chat_single_msg where (sessionID=:sessionID AND read=0)")
    int getUnreadNum(String sessionID);

    @Query("update tb_chat_single_msg set read= 1 where ((fromUserId=:firstUserID  AND toUserId=:secondUserId ) or (fromUserId=:secondUserId AND toUserId=:firstUserID)) AND type!=9996 AND bFire!=1")
    void updateAllRead(String firstUserID, String secondUserId);

    @Query("update tb_chat_single_msg set read= 1 where ((fromUserId=:firstUserID  AND toUserId=:secondUserId ) or (fromUserId=:secondUserId AND toUserId=:firstUserID)) AND msgID=:msgID")
    void updateReadWithMsgID(String firstUserID, String secondUserId, String msgID);

    @Query("update tb_chat_single_msg set read= 1 where sessionID=:sessionID")
    void updateReadMsgID(String sessionID);

    @Query("update tb_chat_single_msg set localFilePath=:localFilePath where id=:messageId")
    void updateDownloadState(String localFilePath, long messageId);

    @Query("delete from tb_chat_single_msg where sessionID=:sessionID")
    void deleteBySessionID(String sessionID);

    @Query("delete from tb_chat_single_msg where sessionID=:sessionID AND id=:id")
    void deleteBySessionIDAndId(String sessionID, long id);

    @Query("delete from tb_chat_single_msg where sessionID=:sessionID AND msgID=:msgID")
    void deletByMsgID(String sessionID, String msgID);


    @Query("delete from tb_chat_single_msg")
    void clearData();

}
