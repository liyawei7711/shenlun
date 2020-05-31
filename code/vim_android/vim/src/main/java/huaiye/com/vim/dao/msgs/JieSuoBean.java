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

/**
 * 紧急联系人
 * Created by LENOVO on 2019/3/28.
 */

@Entity(tableName = "tb_user_jiesuo")
public class JieSuoBean implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo
    public String ownerUserId;
    @ColumnInfo
    public String ownerUserDomain;

    @ColumnInfo
    public String jiesuo;


    @ColumnInfo
    public boolean isJieSuo;

    @ColumnInfo
    public String extend1;
    @ColumnInfo
    public String extend2;
    @ColumnInfo
    public String extend3;
    @ColumnInfo
    public String extend4;

    public JieSuoBean(String ownerUserId, String ownerUserDomain, String jiesuo, boolean isJieSuo, String extend1, String extend2, String extend3, String extend4) {
        this.id = id;
        this.ownerUserId = ownerUserId;
        this.ownerUserDomain = ownerUserDomain;
        this.jiesuo = jiesuo;
        this.isJieSuo = isJieSuo;
        this.extend1 = extend1;
        this.extend2 = extend2;
        this.extend3 = extend3;
        this.extend4 = extend4;
    }

}
