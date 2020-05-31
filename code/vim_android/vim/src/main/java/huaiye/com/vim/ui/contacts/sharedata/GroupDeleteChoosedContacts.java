package huaiye.com.vim.ui.contacts.sharedata;


import java.util.ArrayList;
import huaiye.com.vim.models.contacts.bean.ContactData;

/**
 * author: admin
 * date: 2018/01/16
 * version: 0
 * mail: secret
 * desc: ChoosedContacts
 */

public class GroupDeleteChoosedContacts {

    ArrayList<ContactData> contacts = new ArrayList<>();

    private GroupDeleteChoosedContacts() {
    }

    static class Holder {
        static final GroupDeleteChoosedContacts SINGLETON = new GroupDeleteChoosedContacts();
    }

    public static GroupDeleteChoosedContacts get() {
        return Holder.SINGLETON;
    }

    public void setContacts(ArrayList<ContactData> contacts) {
        this.contacts = contacts;
    }

    public void del(ContactData contact) {
        contacts.remove(contact);
    }

    public ArrayList<ContactData> getContacts() {
        return contacts;
    }

}
