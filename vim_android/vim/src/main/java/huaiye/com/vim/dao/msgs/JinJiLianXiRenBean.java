package huaiye.com.vim.dao.msgs;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;

import huaiye.com.vim.dao.auth.AppAuth;

/**
 * 紧急联系人
 * Created by LENOVO on 2019/3/28.
 */

@Entity(tableName = "tb_user_jinji_lianxi")
public class JinJiLianXiRenBean implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo
    public String ownerUserId;
    @ColumnInfo
    public String ownerUserDomain;

    @ColumnInfo
    public String users;
    @ColumnInfo
    public boolean isOpen;
    @ColumnInfo
    public String extend1;
    @ColumnInfo
    public String extend2;
    @ColumnInfo
    public String extend3;
    @ColumnInfo
    public String extend4;
    @Ignore
    public ArrayList<User> usersRel;

    public JinJiLianXiRenBean(String ownerUserId, String ownerUserDomain, ArrayList<User> users) {
        this.ownerUserId = ownerUserId;
        this.ownerUserDomain = ownerUserDomain;
        this.users = new Gson().toJson(users);
    }
    public JinJiLianXiRenBean(String ownerUserId, String ownerUserDomain, String users) {
        this.ownerUserId = ownerUserId;
        this.ownerUserDomain = ownerUserDomain;
        this.users = users;
    }

    public JinJiLianXiRenBean(long id, String ownerUserId, String ownerUserDomain, String users, boolean isOpen, String extend1, String extend2, String extend3, String extend4, ArrayList<User> usersRel) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.ownerUserDomain = ownerUserDomain;
        this.users = users;
        this.isOpen = isOpen;
        this.extend1 = extend1;
        this.extend2 = extend2;
        this.extend3 = extend3;
        this.extend4 = extend4;
        this.usersRel = usersRel;
    }

    public JinJiLianXiRenBean setExtend4(String extend4) {
        this.extend4 = extend4;
        return this;
    }
    public ArrayList<User> getUserRel() {
        if (TextUtils.isEmpty(users)) {
            return new ArrayList<>();
        }
        usersRel = new Gson().fromJson(users, new TypeToken<ArrayList<User>>(){}.getType());
        return usersRel;
    }


}
