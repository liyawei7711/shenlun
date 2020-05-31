package huaiye.com.vim.dao.msgs;

import java.io.Serializable;
import java.util.List;

public class GroupDealAddMessage implements Serializable {


    /**
     * strInviterDomainCode : f079595d296b
     * strInviterID : f079595d296b100939834
     * strInviterName : china
     * strGroupDomainCode : f079595d296b
     * strGroupID : 2019120616352200004
     * strGroupName : china、jy
     * nBeinviteMode : 0
     * lstGroupUser : [{"strUserDomainCode":"f079595d296b","strUserID":"f079595d296b96922865","strUserName":"zll"}]
     */

    private String strInviterDomainCode;
    private String strInviterID;
    private String strInviterName;
    private String strGroupDomainCode;
    private String strGroupID;
    private String strGroupName;
    private int nBeinviteMode;
    private List<LstGroupUserBean> lstGroupUser;

    public String getStrInviterDomainCode() {
        return strInviterDomainCode;
    }

    public void setStrInviterDomainCode(String strInviterDomainCode) {
        this.strInviterDomainCode = strInviterDomainCode;
    }

    public String getStrInviterID() {
        return strInviterID;
    }

    public void setStrInviterID(String strInviterID) {
        this.strInviterID = strInviterID;
    }

    public String getStrInviterName() {
        return strInviterName;
    }

    public void setStrInviterName(String strInviterName) {
        this.strInviterName = strInviterName;
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

    public String getStrGroupName() {
        return strGroupName;
    }

    public void setStrGroupName(String strGroupName) {
        this.strGroupName = strGroupName;
    }

    public int getNBeinviteMode() {
        return nBeinviteMode;
    }

    public void setNBeinviteMode(int nBeinviteMode) {
        this.nBeinviteMode = nBeinviteMode;
    }

    public List<LstGroupUserBean> getLstGroupUser() {
        return lstGroupUser;
    }

    public void setLstGroupUser(List<LstGroupUserBean> lstGroupUser) {
        this.lstGroupUser = lstGroupUser;
    }

    public static class LstGroupUserBean implements Serializable {
        /**
         * strUserDomainCode : f079595d296b
         * strUserID : f079595d296b96922865
         * strUserName : zll
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
