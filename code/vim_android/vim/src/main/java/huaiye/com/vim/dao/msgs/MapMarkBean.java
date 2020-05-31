package huaiye.com.vim.dao.msgs;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: VssMessageBean
 */

public class MapMarkBean implements Serializable {
    public static final int POINT = 1;
    public static final int LINE = 2;
    public static final int BOARD = 3;
    public static final int CIRCLE = 4;
    public static final int MANYBOARD = 5;

    public int nResultCode;
    public int nMsgType; // 1:添加，2:删除，3:修改
    public boolean isAdd(){
        return nMsgType == 1;
    }
    public int nMarkID;
    public int nType;//1：点 2：线 3：框 4：圆 5：多边形
    public int nRadius;//半径（画圆时使用）
    public String strResultDescribe;
    public String strColorValue;
    public String strLastModTime;
    public String strRemark;
    public String strMarkName;
    public String strDomainCode;
    public String strCreatorID;

    public ArrayList<PointBean> lstSite;

}
