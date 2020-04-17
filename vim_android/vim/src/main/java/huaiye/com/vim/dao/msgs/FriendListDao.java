package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface FriendListDao {
    //返回Long数据表示，插入条目的主键值
    @Insert(onConflict = REPLACE)
    Long insert(User user);

    @Insert(onConflict = REPLACE)
    void insertAll(User... users);

    @Insert(onConflict = REPLACE)
    void insertAll(List<User> users);

    @Query("select * from tb_friend_list")
    List<User> getFriendList();

    @Query("select strHeadUrl from tb_friend_list where strUserID=:strUserID And strDomainCode=:strDomainCode")
    String getFriendHeadPic(String strUserID,String strDomainCode);

    @Query("select * from tb_friend_list where strUserID=:strUserID And strDomainCode=:strDomainCode")
    User getFriend(String strUserID,String strDomainCode);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);
    


    @Query("delete from tb_friend_list")
    void clearData();
    
}
