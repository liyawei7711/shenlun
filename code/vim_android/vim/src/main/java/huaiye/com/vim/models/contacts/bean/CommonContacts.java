package huaiye.com.vim.models.contacts.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/01/09
 * version: 0
 * mail: secret
 * desc: CommonContact
 */

public class CommonContacts implements Serializable {

    public int code;
    public String desc;
    public ArrayList<Data> result;

    public static class Data {
        public int createUserId;//: 0,
        public int nofuzzy;//: 0,
        public String id;//100000,
        public long contactId;//:100101779,
        public long groupId;//:100100222,
        public long contactEntCode;//:"10000",
        public long entCode;//:"10000",
        public String contactLoginName;//:"admin",
        public String groupLoginName;//:"hjq",
        public String name;//:"系统管理员",
        public String loginName;//:"admin",
        public String imgUrl;//:"/upload/user/man.png",
        public String sexName;//:"男",
        public String entName;//:"上级公安厅",
        public String entDomainCode;//:"300ed5c53e2b",
        public String dataRoleName;//:"",
        public String depName;//:"",
        public String roleName;//:"系统管理角色",
        public int isOnline;//:0,
        public String domainCode;//:"300ed5c53e2b",
        public int devType;//:0

        public boolean isOnline() {
            return isOnline >= 1;
        }
    }
}
