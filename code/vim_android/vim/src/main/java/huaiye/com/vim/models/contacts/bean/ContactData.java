package huaiye.com.vim.models.contacts.bean;

import java.io.Serializable;


/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: ContactData
 */

public class ContactData implements Serializable {

    public String loginName;//": ld3,   --成员登录名
    public String name;
    public long entCode;//": 10000,   --成员企业号
    public String userId;//":100015;    --成员Id
    public String domainCode;

    // equals标记
    public String naviKey;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof ContactData) {
            ContactData data = (ContactData) obj;

            String s1 = loginName + domainCode;
            String s2 = data.loginName + data.domainCode;

            return s1.equals(s2);
        }

        return false;
    }


    public static ContactData from(CommonContacts.Data source) {
        ContactData data = new ContactData();

        data.naviKey = "common";
        data.domainCode = source.domainCode;
        data.loginName = source.loginName;
        data.name = source.name;
        data.entCode = source.contactEntCode;
        data.userId = source.id;

        return data;
    }

    public static ContactData from(GroupContacts.Data source) {
        ContactData data = new ContactData();

        data.naviKey = "none";
        data.domainCode = source.domainCode;
        data.loginName = source.loginName;
        data.name = source.name;
        data.entCode = source.contactEntCode;
        data.userId = source.id;

        return data;
    }
}
