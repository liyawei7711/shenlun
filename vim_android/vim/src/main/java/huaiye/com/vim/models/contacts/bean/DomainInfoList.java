package huaiye.com.vim.models.contacts.bean;

import java.io.Serializable;
import java.util.List;

public class DomainInfoList implements Serializable {

    public List<DomainInfo> domainInfoList;

    public static class DomainInfo implements Serializable{
        public String strDomainCode;
        public String strDomainName;
        public String strVssIP;
        public int nVssPort;
        public String strSieIP;
        public int nSiePort;
        public int nSieHttpPort;
        public String strSieNatIP;
        public int nSieNatPort;
        public int nSieNatHttpPort;
        public int nIsLocal;
        public String strParentDomainCode;
        public double dLongitude;
        public double dLatitude;
        public double dHeight;
    }

}
