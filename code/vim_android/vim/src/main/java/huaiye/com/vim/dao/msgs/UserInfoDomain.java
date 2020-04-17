package huaiye.com.vim.dao.msgs;

import java.io.Serializable;

public class UserInfoDomain implements Serializable {
    public String strDomainCode;
    public String strUserID;

    public UserInfoDomain(String strUserID, String strDomainCode) {
        this.strUserID = strUserID;
        this.strDomainCode = strDomainCode;
    }
}
