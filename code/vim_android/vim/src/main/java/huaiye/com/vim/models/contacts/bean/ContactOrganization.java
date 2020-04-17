package huaiye.com.vim.models.contacts.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: ContactOrganization
 */

public class ContactOrganization implements Serializable {

    public int code;
    public String desc;
    public Data result;

    public static class Data implements Serializable {

        public ArrayList<Enterprise> entList;
        public ArrayList<Department> depList;

    }

    public static class Enterprise implements Serializable {
        public int createUserId;// 0,
        public int nofuzzy;//: 0,
        public int id;//": 1,
        public int isdel;//: 0,
        public String updateTime;//: "2018-01-02 10:09:16",
        public String updateUserId;//: 100000,
        public int refCount;//: 0,
        public long entCode;// "10000",
        public String entName;// "上级公安厅",
        public String lng;//: "118.75517",
        public String lat;//: "31.974891",
        public int level;//: 1,
        public String fydz;//: "A20",
        public String manufacturercode;//: "HY2013",

        public ArrayList<Department> depList;
        public ArrayList<Enterprise> entList;
    }

    public static class Department implements Serializable {
        public boolean isEnterprise;
        public String createTime;//": "2017-06-30 13:54:23",
        public long createUserId;//": 1,
        public int nofuzzy;//": 0,
        public long id;//": 200000,
        public int isdel;//": 0,
        public String updateTime;//": "2017-07-05 13:36:15",
        public long updateUserId;//": 1,
        public int refCount;//": 0,
        public String name;//": "办公室",
        public long parentId;//": 0,
        public int depType;//": 0,
        public int judgementFlag;//": 0,
        public int priority;//": 0,
        public long entCode;//": "10000",
        public long userId;//": 1,
        public int level;//": 0,
        public ArrayList<Department> depList;
        public Enterprise entList;
    }
}
