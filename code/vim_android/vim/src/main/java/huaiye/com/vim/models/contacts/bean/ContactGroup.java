package huaiye.com.vim.models.contacts.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/01/16
 * version: 0
 * mail: secret
 * desc: ContactGroup
 */

public class ContactGroup implements Serializable {

    public int code;
    public String desc;
    public ArrayList<Data> result;

    public static class Data implements Serializable {
        public String createTime;//": "2018-01-16 16:20:59",
        public long createUserId;//": -1,
        public int nofuzzy;//": 0,
        public long id;//": 100100225,
        public int isdel;//": 0,
        public long userId;//": 103000073,
        public String groupName;//": "",
        public int groupType;//": 1,
        public long entCode;//": "10000",
        public long memberId;//": 103000073,
        public long contactEntCode;//": "10000"
    }

}
