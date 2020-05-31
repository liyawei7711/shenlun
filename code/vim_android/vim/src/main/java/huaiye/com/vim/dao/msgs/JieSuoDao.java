package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 *
 * Created by LENOVO on 2019/4/2.
 */
@Dao
public interface JieSuoDao {
    @Insert(onConflict = REPLACE)
    void insertAll(JieSuoBean... msg);


    @Insert(onConflict = REPLACE)
    void insertAll(List<JieSuoBean> msg);

    @Query("select * from tb_user_jiesuo where (ownerUserId=:ownerUserId AND ownerUserDomain=:ownerUserDomain)")
    JieSuoBean queryOneItem(String ownerUserId, String ownerUserDomain);

    @Query("update tb_user_jiesuo set jiesuo= :jiesuo where (ownerUserId=:ownerUserId  AND ownerUserDomain=:ownerUserDomain )")
    void updateData(String ownerUserId, String ownerUserDomain, String jiesuo);
    @Query("update tb_user_jiesuo set isJieSuo= :isJieSuo where (ownerUserId=:ownerUserId  AND ownerUserDomain=:ownerUserDomain )")
    void updateData(String ownerUserId, String ownerUserDomain, boolean isJieSuo);

    @Query("delete from tb_user_jiesuo where ownerUserId=:ownerUserId AND ownerUserDomain=:ownerUserDomain")
    void deleteByUser(String ownerUserId, String ownerUserDomain);

    @Query("delete from tb_user_jiesuo")
    void chlearData();

}
