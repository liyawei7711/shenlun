package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface FileLocalNameDao {

    //返回Long数据表示，插入条目的主键值
    @Insert(onConflict = REPLACE)
    Long insert(FileLocalNameBean bean);

    @Insert(onConflict = REPLACE)
    void insertAll(FileLocalNameBean... beans);

    @Insert(onConflict = REPLACE)
    void insertAll(List<FileLocalNameBean> beans);

    @Query("select * from tb_chat_file_name")
    List<FileLocalNameBean> getFileLocalList();

    @Query("select * from tb_chat_file_name where httpUrl=:httpUrl")
    FileLocalNameBean getFileLocalInfo(String httpUrl);

    @Update
    void updateFileLocal(FileLocalNameBean sendMsgUserBean);

    @Delete
    void deleteFromDao(FileLocalNameBean sendMsgUserBean);


    @Query("delete from tb_chat_file_name")
    void clearData();
    
}
