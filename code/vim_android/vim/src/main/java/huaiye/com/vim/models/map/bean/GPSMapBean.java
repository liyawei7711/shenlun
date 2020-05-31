package huaiye.com.vim.models.map.bean;

import java.io.Serializable;

/**
 * author: admin
 * date: 2018/09/10
 * version: 0
 * mail: secret
 * desc: GPSMapBean
 */

public class GPSMapBean implements Serializable {
    public String strCollectTime = "";
    public double fLongitude;
    public double fLatitude;
    public double fAltitude;
    public float fSpeed;
    public int nSignalGrades;
    public int nDataSourceType;
    public int nIsBegin;
}
