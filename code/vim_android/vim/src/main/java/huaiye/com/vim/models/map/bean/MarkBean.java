package huaiye.com.vim.models.map.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: MarkBean
 */

public class MarkBean implements Serializable {
    public int nResultCode;
    public int nTotalSize;
    public String strResultDescribe;
    public ArrayList<MarkModelBean> markInfoList = new ArrayList<>();
}
