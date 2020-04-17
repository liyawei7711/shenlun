package huaiye.com.vim.models.contacts.bean;

import java.util.ArrayList;

/**
 * Created by ywt on 2019/2/26.
 */

public class ContacsTerminal {
    public int nResultCode;
    public String strResultDescribe;
    public ArrayList<Data> clientList;

    public static class Data{
        public String strClientID;
        public String strClientName;
        public int nStatus;
    }
}
