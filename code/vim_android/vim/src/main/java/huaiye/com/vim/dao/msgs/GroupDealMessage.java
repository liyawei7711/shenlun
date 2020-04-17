package huaiye.com.vim.dao.msgs;

import java.io.Serializable;
import java.util.List;

public class GroupDealMessage implements Serializable {

    /**
     * strKickerDomainCode : f079595d296b
     * strKickerID : f079595d296b100939834
     * strGroupDomainCode : f079595d296b
     * strGroupID : 2019120616352200004
     * lstOutUser : [{"strUserDomainCode":"f079595d296b","strUserID":"f079595d296b611552662","strUserName":"jy"}]
     */

    private String strKickerDomainCode;
    private String strKickerID;
    private String strGroupDomainCode;
    private String strGroupID;
    private List<LstOutUserBean> lstOutUser;

    public String getStrKickerDomainCode() {
        return strKickerDomainCode;
    }

    public void setStrKickerDomainCode(String strKickerDomainCode) {
        this.strKickerDomainCode = strKickerDomainCode;
    }

    public String getStrKickerID() {
        return strKickerID;
    }

    public void setStrKickerID(String strKickerID) {
        this.strKickerID = strKickerID;
    }

    public String getStrGroupDomainCode() {
        return strGroupDomainCode;
    }

    public void setStrGroupDomainCode(String strGroupDomainCode) {
        this.strGroupDomainCode = strGroupDomainCode;
    }

    public String getStrGroupID() {
        return strGroupID;
    }

    public void setStrGroupID(String strGroupID) {
        this.strGroupID = strGroupID;
    }

    public List<LstOutUserBean> getLstOutUser() {
        return lstOutUser;
    }

    public void setLstOutUser(List<LstOutUserBean> lstOutUser) {
        this.lstOutUser = lstOutUser;
    }

    public static class LstOutUserBean implements Serializable{
        /**
         * strUserDomainCode : f079595d296b
         * strUserID : f079595d296b611552662
         * strUserName : jy
         */

        private String strUserDomainCode;
        private String strUserID;
        public String strUserName;

        public String getStrUserDomainCode() {
            return strUserDomainCode;
        }

        public void setStrUserDomainCode(String strUserDomainCode) {
            this.strUserDomainCode = strUserDomainCode;
        }

        public String getStrUserID() {
            return strUserID;
        }

        public void setStrUserID(String strUserID) {
            this.strUserID = strUserID;
        }

        public String getStrUserName() {
            return strUserName;
        }

        public void setStrUserName(String strUserName) {
            this.strUserName = strUserName;
        }
    }
}
