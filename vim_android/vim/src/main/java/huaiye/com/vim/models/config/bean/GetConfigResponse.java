package huaiye.com.vim.models.config.bean;

import java.util.ArrayList;

/**
 * Created by LENOVO on 2019/3/29.
 */

public class GetConfigResponse {
    public int nResultCode;
    public String strResultDescribe;
    public ArrayList<Data> lstVssConfigParaInfo;

    public static class Data{
        public String strVssConfigParaName;
        public String strVssConfigParaValue;
    }
}
