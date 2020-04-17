package huaiye.com.vim.models.map.bean;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/05/12
 * version: 0
 * mail: secret
 * desc: OfflineMapBean
 */

public class LocalOfflineMapBean implements Serializable {

    public int cityID;
    public String cityName;
    public int ratio;
    public int status;
    public LatLng geoPt;
    public int size;
    public int serversize;
    public int level;
    public boolean update;
    public boolean isCurrent;

}
