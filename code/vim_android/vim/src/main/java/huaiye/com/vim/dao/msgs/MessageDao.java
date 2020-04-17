package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by LENOVO on 2019/3/28.
 */
@Dao
public interface MessageDao {
    @Insert(onConflict = REPLACE)
    void insertAll(MessageData... msg);

    @Delete
    void delete(MessageData user);

    //删除全部
    @Query("delete from tb_message")
    void deleteAll();

    @Query("delete from tb_message where nMillions=:id")
    void updateById(long id);

    @Update
    int update(MessageData user);

    //更新某个字段
    @Query("update tb_message set isRead=:isRead where nMillions=:id")
    void updateRead(int isRead,long id);

    //更新某个字段
    @Query("update tb_message set isRead= 1 where  userId=:userId and domainCode=:domainCode ")
    void updateAllRead(String userId, String domainCode);




    @Query("select * from tb_message where userId=:userId and domainCode=:domainCode order by nMillions desc ")
    List<MessageData> queryAll(String userId, String domainCode);

    @Query("select * from tb_message where userId=:userId and domainCode=:domainCode and isRead=0 order by nMillions desc ")
    List<MessageData> queryNotReadAll(String userId, String domainCode);
}
