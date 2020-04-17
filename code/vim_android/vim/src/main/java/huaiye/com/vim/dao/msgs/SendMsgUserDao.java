package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import huaiye.com.vim.models.contacts.bean.GroupInfo;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface SendMsgUserDao {

    //返回Long数据表示，插入条目的主键值
    @Insert(onConflict = REPLACE)
    Long insert(SendMsgUserBean sendMsgUserBean);

    @Insert(onConflict = REPLACE)
    void insertAll(SendMsgUserBean... sendMsgUserBeans);

    @Insert(onConflict = REPLACE)
    void insertAll(List<SendMsgUserBean> sendMsgUserBeans);

    @Query("select * from tb_chat_send_to_user")
    List<SendMsgUserBean> getSendUserList();

    @Query("select * from tb_chat_send_to_user where sessionID=:sessionID")
    SendMsgUserBean getSendUserInfo(String sessionID);

    @Update
    int update(SendMsgUserBean sendMsgUserBean);

    @Query("delete from tb_chat_send_to_user")
    void clearData();
    
}
