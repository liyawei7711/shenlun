package huaiye.com.vim.dao.msgs;

import java.io.Serializable;

public class UserInfo implements Serializable {
    public String strUserDomainCode;
    public String strUserID;

    public UserInfo(String strUserID, String strUserDomainCode) {
        this.strUserID = strUserID;
        this.strUserDomainCode = strUserDomainCode;
    }
}
