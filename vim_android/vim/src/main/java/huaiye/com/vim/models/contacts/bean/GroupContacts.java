package huaiye.com.vim.models.contacts.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/01/19
 * version: 0
 * mail: secret
 * desc: GroupContacts
 */

public class GroupContacts implements Serializable {

    public int code;
    public String desc;
    public ArrayList<Data> result;

    public static class Data implements Serializable {
        public long createUserId;//": 0,
        public int nofuzzy;//": 0,
        public String id;//": 103000072,
        public long updateUserId;//": 0,
        public long contactId;//": 100101912,
        public long groupId;//": 100100265,
        public long contactEntCode;//": "10000",
        public String contactLoginName;//": "hjq",
        public String name;//": "hjq",
        public String loginName;//": "hjq",
        public String imgUrl;//": "/upload/user/man.png",
        public String sexName;//": "男",
        public String entName;//": "上级公安厅",
        public String entDomainCode;//": "300ed5c53e2b",
        public String dataRoleName;//": "",
        public String depName;//": "办公室",
        public String roleName;//": "系统管理角色,执行人员",
        public int isOnline;//": 0,
        public String domainCode;//": "300ed5c53e2b",
        public int devType;//": 0
    }

}
