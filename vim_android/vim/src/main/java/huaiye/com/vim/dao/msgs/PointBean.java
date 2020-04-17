package huaiye.com.vim.dao.msgs;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

import huaiye.com.vim.map.baidu.GPSLocation;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: VssMessageBean
 */

public class PointBean implements Serializable {

    public int nPointID;
    public double dLongitude;
    public double dLatitude;

    public LatLng getBaiDuPoint() {
       return GPSLocation.convertGPSToBaidu(new LatLng(dLatitude, dLongitude));
    }
}
