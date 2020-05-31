package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import huaiye.com.vim.models.contacts.bean.OrganizationContacts;

/**
 * Created by LENOVO on 2019/3/28.
 */
@Dao
public interface ContactDao {
    @Query("select * from tb_contact")
    List<OrganizationContacts.Data> queryAll();
}
