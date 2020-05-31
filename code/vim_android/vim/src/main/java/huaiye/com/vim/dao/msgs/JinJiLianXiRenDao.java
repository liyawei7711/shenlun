package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by LENOVO on 2019/4/2.
 */
@Dao
public interface JinJiLianXiRenDao {
    @Insert(onConflict = REPLACE)
    void insertAll(JinJiLianXiRenBean... msg);


    @Insert(onConflict = REPLACE)
    void insertAll(List<JinJiLianXiRenBean> msg);

    @Query("select * from tb_user_jinji_lianxi where (ownerUserId=:ownerUserId AND ownerUserDomain=:ownerUserDomain)")
    JinJiLianXiRenBean queryOneItem(String ownerUserId, String ownerUserDomain);

    @Query("select * from tb_user_jinji_lianxi where extend4=:extend4")
    JinJiLianXiRenBean queryOneItem(String extend4);

    @Query("update tb_user_jinji_lianxi set users= :users,extend4=:extend4 where (ownerUserId=:ownerUserId  AND ownerUserDomain=:ownerUserDomain )")
    void updateData(String ownerUserId, String ownerUserDomain, String users, String extend4);

    @Query("update tb_user_jinji_lianxi set isOpen= :isOpen where (ownerUserId=:ownerUserId  AND ownerUserDomain=:ownerUserDomain )")
    void updateData(String ownerUserId, String ownerUserDomain, boolean isOpen);

    @Query("delete from tb_user_jinji_lianxi where ownerUserId=:ownerUserId AND ownerUserDomain=:ownerUserDomain")
    void deleteByUser(String ownerUserId, String ownerUserDomain);

    @Query("delete from tb_user_jinji_lianxi")
    void chlearData();

}
