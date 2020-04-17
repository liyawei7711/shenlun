package huaiye.com.vim.models.contacts.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: ContactGroups
 */

public class OrganizationContacts implements Serializable {

    public int code;
    public String desc;
    public ArrayList<Data> result;

    public static class Data implements Serializable {
        public String createTime;//": "2017-06-30 14:03:39",
        public long createUserId;//": 1,
        public int nofuzzy;//: 0,
        public long id;//": 100000,
        public int isdel;//": 0,
        public String updateTime;//": "2017-09-21 11:30:35",
        public long updateUserId;//": 100000,
        public String name;//": "系统管理员",
        public String loginName;//": "admin",
        public String password;//": null,
        public String address;//": "",
        public String mobilePhone;//": "13913988888",
        public String email;//": "",
        public int sex;//": 1,
        public long entCode;//": "10000",
        public long jobId;//": 100000,
        public String imgUrl;//": "/upload/user/man.png",
        public int userType;//": 0,
        public String extendIndexa;//": "200000",
        public String entName;//": "上级公安厅",
        public long contactGroupId;//": 100000,
        public int isCommonContents;//": "0",
        public long groupId;//": 0,
        public int isOnline;//": "0",
        public String domainCode;//": "300ed5c53e2b",
        public String sieUserTokenId;//": "0",
        public String lng;//": 0.0,
        public String lat;//": 0.0

        public boolean isOnline() {
            return isOnline >= 1;
        }
    }

}
