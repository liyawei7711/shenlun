package huaiye.com.vim.models.contacts.bean;

import java.util.ArrayList;

public class GroupChatContacts {

    public ArrayList<LetterStructure> letterStructures;

    public  static class LetterStructure{
        public ArrayList<GroupInfo> users;
    }

}
