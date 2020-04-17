package huaiye.com.vim.map;

import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.huaiye.sdk.logger.Logger;

import huaiye.com.vim.map.baidu.LocationService;
import huaiye.com.vim.map.baidu.LocationStrategy;

/**
 * 无效的定位,15秒定位一次
 * 速度大于8,2秒后再次定位
 * 如果是wifi,60秒定位一次
 * 长时间不动,60秒定位一次
 * @return
 */

public class LocationStrategyLowImp extends LocationStrategy {
    private BDLocation lastLocation;
    private BDLocation currentLocation;
    private String connectWifiMac;
    private int hotSpotState;
    private int sameLocationCount = 0;


    @Override
    protected boolean checkNeedSleep() {
        if (currentLocation == null){
            return false;
        }
        //返回的定位信息是错的
        if (currentLocation.getLatitude() == 0 || currentLocation.getLongitude() == 0
                || currentLocation.getLatitude() == 4.9e-324 || currentLocation.getLongitude() == 4.9e-324) {
            return false;
        }


        //在wifi下,说明在室内,就5秒定位一次
        if (!TextUtils.isEmpty(connectWifiMac) && hotSpotState == LocationClient.CONNECT_HOT_SPOT_FALSE){
            sameLocationCount = 0;
            Logger.debug(LocationService.TAG,"LocationStrategyMiddleImp connectWifiMac  " + connectWifiMac);
            return true;
        }

        //目前只有一次定位,15秒后继续
        if (lastLocation == null){
            return false;
        }

        double dis = DistanceUtil.getDistance(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
        Logger.debug(LocationService.TAG,"LocationStrategyLowImp dis  " + dis);
        //小于5米认为没有移动
        if (dis < 5){
            sameLocationCount++;
        }else {
            sameLocationCount = 0;
        }

        if (sameLocationCount >= 10){
            sameLocationCount = 0;
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected int getSleepTime() {
        return 60;
    }

    @Override
    public void setNewLocation(BDLocation location) {
        lastLocation = currentLocation;
        currentLocation = location;
    }

    @Override
    public void setWifiStatus(String connectWifiMac, int hotSpotState) {
        this.connectWifiMac = connectWifiMac;
        this.hotSpotState = hotSpotState;
    }

    @Override
    public void reset() {
        this.connectWifiMac = null;
        this.hotSpotState = 0;
        this.sameLocationCount = 0;
        this.lastLocation = null;
        this.currentLocation = null;
    }

}
