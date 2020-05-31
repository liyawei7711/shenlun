package huaiye.com.vim.ui.contacts.sharedata;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.contacts.bean.ContactData;

/**
 * author: admin
 * date: 2018/01/16
 * version: 0
 * mail: secret
 * desc: ChoosedContacts
 */

public class ChoosedContacts {

    /**
     * 已在会议中
     */
    ArrayList<ContactData> userList = new ArrayList<>();
    public boolean isOnLine = false;

    ArrayList<ContactData> contacts = new ArrayList<>();
    ArrayList<ContactData> contactsTemp = new ArrayList<>();
    ContactData self;

    private ChoosedContacts() {
        changeSelf();
    }

    public void changeSelf() {
        self = new ContactData();
        self.naviKey = "";
        self.domainCode = AppDatas.Auth().getDomainCode();
        self.loginName = AppDatas.Auth().getUserLoginName();
        self.name = AppDatas.Auth().getUserName();
        self.entCode = AppDatas.Auth().getEnterpriseCode();
        self.userId = AppDatas.Auth().getUserID();
    }

    /**
     * 去重
     *
     * @param chooseds
     * @param contactsTemp
     */
    public void addTempToChoose(ArrayList<ContactData> chooseds, ArrayList<ContactData> contactsTemp) {
        if (!contactsTemp.isEmpty()) {
            chooseds.clear();
        }
        for (ContactData temp : contactsTemp) {
            if (!chooseds.contains(temp)) {
                chooseds.add(temp);
            }
        }
    }

    static class Holder {
        static final ChoosedContacts SINGLETON = new ChoosedContacts();
    }

    public static ChoosedContacts get() {
        return Holder.SINGLETON;
    }

    public void setContacts(ArrayList<ContactData> contacts) {
        if (contacts == null) {
            this.contacts.clear();
            return;
        }
        this.contacts = contacts;
    }

    /**
     * 是否在会议中
     *
     * @param
     * @return
     */
    public boolean contact(String loginName) {
        if (userList.isEmpty()) return false;
        for (ContactData temp : userList) {
            if (temp.loginName.equals(loginName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取开会人员，创建群组的人员。可能包括自己
     *
     * @param withMe
     * @return
     */
    public ArrayList<ContactData> getContacts( boolean withMe) {
        if (withMe && !contacts.contains(self)) {
            self.naviKey = "common";
            contacts.add(0, self);
        }
        
        return new ArrayList<>(contacts);
    }



    /**
     * 删除选中
     *
     * @param data
     */
    public void deleteSelected(ContactData data) {
        if (data.loginName.equals(self.loginName)) {
            deleteSelf();
        } else {
            contacts.remove(data);
        }
    }

    /**
     * 删除自己
     */
    public void deleteSelf() {
        contacts.remove(self);
    }

    public void initDelete() {
    }

    /**
     * 获取删除标识
     *
     * @return
     */
    public int isDeleteSelf() {
        if (contacts.contains(self)) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 获取临时选中数据
     *
     * @return
     */
    public ArrayList<ContactData> getContactsTemp() {
        return new ArrayList<>(contactsTemp);
    }

    /**
     * 获取开会人员，创建群组的人员。不可能包括自己
     * @return
     */
    public ArrayList<ContactData> getContactsCreate() {
        contacts.remove(self);
        return new ArrayList<>(contacts);
    }

    /**
     * 添加所有数据
     *
     * @param datas
     */
    public void add(List<ContactData> datas) {
        contacts.clear();
        contacts.addAll(datas);
    }

    /**
     * 复制到选中数据
     *
     * @param datas
     */
    public void addTempAll(List<ContactData> datas) {
        if (datas == null
                || datas.isEmpty()) {
            return;
        }
        contactsTemp.clear();
        for (ContactData tmp : datas) {
            if (!contactsTemp.contains(tmp)) {
                contactsTemp.add(tmp);
            }
        }
    }

    /**
     * 添加选中
     *
     * @param datas
     */
    public void addTemp(ContactData datas) {
        if (datas == null) {
            return;
        }
        if (datas.loginName.equals(self.loginName)) {
            if (!contactsTemp.contains(self)) {
                self.naviKey = "common";
                contactsTemp.add(0, self);
            }
            return;
        }
        if (!contactsTemp.contains(datas)) {
            contactsTemp.add(datas);
        }

    }

    /**
     * 设置参会人员
     *
     * @param all
     */
    public void setOnMeetUsers(ArrayList<ContactData> all) {
        clearMeetUsers();
        self.naviKey = "common";
        userList.addAll(all);
    }

    public void setOnMeetUsersInfo(ArrayList<CGetMeetingInfoRsp.UserInfo> data) {
        clearMeetUsers();
        self.naviKey = "none";
        for (CGetMeetingInfoRsp.UserInfo temp : data) {
            ContactData contactData = new ContactData();
            contactData.loginName = temp.strUserID;
            contactData.domainCode = temp.strUserDomainCode;
            contactData.name = temp.strUserName;
            userList.add(contactData);
        }
    }
    public void setOnMeetUsersInfoFromGroup(ArrayList<ContactData> data) {
        clearMeetUsers();
        self.naviKey = "none";
        userList.addAll(data);
    }


    /**
     * 清空会议人员
     */
    public void clearMeetUsers() {
        userList.clear();
        self.naviKey = "common";
    }

    public void del(ContactData contact) {
        contacts.remove(contact);
    }

    public void delTemp(ContactData contact) {
        contactsTemp.remove(contact);
    }

    public void clear() {
        contacts.clear();
        clearTemp();
    }

    public void clearTemp() {
        contactsTemp.clear();
    }


    public ArrayList<CStartMeetingReq.UserInfo> convertContacts(ArrayList<ContactData> contacts) {
        ArrayList<CStartMeetingReq.UserInfo> users = new ArrayList<>();

        for (ContactData tmp : contacts) {
            CStartMeetingReq.UserInfo user = new CStartMeetingReq.UserInfo();

            user.setDevTypeUser();
            user.strUserDomainCode = tmp.domainCode;
            user.strUserID = tmp.loginName;
            user.strUserName = tmp.name;

            users.add(user);
        }

        return users;
    }

}
