package huaiye.com.vim.models.map.bean;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: MarkBean
 */

public class MarkModelBean implements Serializable {
    public int nMarkID;
    public String strMarkName;
    public int nType;
    public String strRemark;
    public String strLastModTime;
    public String strDomainCode;
    public String strCreatorID;
    public double dLongitude;
    public double dLatitude;
    public boolean isChoose;
    //我的地图编辑模式中的选中
    public boolean isEditChoose;

    @Override
    public String toString() {
        return "MarkModelBean{" +
                "nMarkID=" + nMarkID +
                ", strMarkName='" + strMarkName + '\'' +
                ", dLongitude=" + dLongitude +
                ", dLatitude=" + dLatitude +
                ", strRemark='" + strRemark + '\'' +
                ", strLastModTime='" + strLastModTime + '\'' +
                ", strDomainCode='" + strDomainCode + '\'' +
                '}';
    }
}
