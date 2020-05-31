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
public interface GroupListDao {

    //返回Long数据表示，插入条目的主键值
    @Insert(onConflict = REPLACE)
    Long insert(GroupInfo groupInfo);

    @Insert(onConflict = REPLACE)
    void insertAll(GroupInfo... groupInfos);

    @Insert(onConflict = REPLACE)
    void insertAll(List<GroupInfo> groupInfos);

    @Query("select * from tb_group_list")
    List<GroupInfo> getGroupList();

    @Query("select strHeadUrl from tb_group_list where strGroupID=:strGroupID And strGroupDomainCode=:strGroupDomainCode")
    String getGroupHeadPic(String strGroupID, String strGroupDomainCode);

    @Query("select * from tb_group_list where strGroupID=:strGroupID And strGroupDomainCode=:strGroupDomainCode")
    GroupInfo getGroupInfo(String strGroupID, String strGroupDomainCode);

    @Update
    void updateGroup(GroupInfo groupInfo);

    @Delete
    void deleteGroup(GroupInfo groupInfo);



    @Query("delete from tb_group_list")
    void clearData();
    
}
