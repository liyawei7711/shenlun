package huaiye.com.vim.models.contacts.bean;

import java.util.ArrayList;

import huaiye.com.vim.dao.msgs.User;

/**
 * Created by ywt on 2019/2/25.
 */

public class CustomContacts {
    public ArrayList<LetterStructure> letterStructures;

    public static class LetterStructure{
        public char letter;
        public ArrayList<User> users;
    }
}
