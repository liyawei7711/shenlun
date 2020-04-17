package huaiye.com.vim.models.auth.bean;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AuthUser
 */

public class AuthUser implements Serializable {
    public int nResultCode;
    public String strResultDescribe;
    /**域code*/
    public String strDomainCode;
    public String strUserID;
    public String strUserName;
    /**优先级，越小越高*/
    public int nPriority;
    /**角色编号*/
    public int nRoleID;
    /**角色类型 1:超级管理员 2：自定义*/
    public int nRoleType;
    /**媒体注册ip*/
    public String strSieIP;
    /**媒体注册port*/
    public int nSiePort;
    /**媒体http服务端口*/
    public int nSieHttpPort;
    /**Token,后续消息需要放置在http头域的X-Token*/
    public String strToken;
    public String password;
    public String loginName;
    /*public int code;
    public String  desc;
    public Data result;

    public static class Data {
        public String createTime;// "2017-06-30 14:03:39",
        public long createUserId;// 1,
        public int nofuzzy;//  0,
        public long id;// 100000,
        public int isdel;// 0,
        public String updateTime;//  "2017-06-30 14:04:41",
        public long updateUserId;// 1,
        public String name;// "系统管理员",
        public String loginName;// "admin",
        public String password;//:null,
        public String address;//":"",
        public String mobilePhone;//":"",
        public String email;//":"",
        public int sex;//":1,
        public long entCode;//":"10000",
        public String imgUrl;// /upload/user/man.png",
        public int userType;// :0,
        public String entName; //":"某市人民检察院",
        public String tcpIp;// :"192.168.3.122",
        public int webTcpPort;// :8282,
        public String webIp; //:"192.168.3.122",
        public int webPort; //:8280,
        public String sieHttpUrl;//:"http://192.168.3.21:9200/sie/httpjson/",
        public String sieIp;//:"192.168.3.21",
        public int sieMobilePort; //:9012,
        public int sieTcpPort;//:9000,
        public String lng;// :"0",
        public String lat;//:"0",
        public int androidTcpPort;//:8281,
        public int iosTcpPort;//:8283,
        public String gatekeeperIp;//:"",
        public int isLocal;//:"0",
        public String schedulingServerCode;//:"3401000000"--域编码
    }*/

}
