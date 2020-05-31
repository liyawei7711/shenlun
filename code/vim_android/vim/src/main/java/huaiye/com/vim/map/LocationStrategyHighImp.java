package huaiye.com.vim.map;

import com.baidu.location.BDLocation;

import huaiye.com.vim.map.baidu.LocationStrategy;

/**
 * 5秒定位一次,速度大于8的情况下2秒一次
 */
public class LocationStrategyHighImp extends LocationStrategy {
    private BDLocation lastLocation;
    private BDLocation currentLocation;
    private String connectWifiMac;
    private int hotSpotState;
    private int sameLocationCount = 0;
    /**
     * 无效的定位,15秒定位一次
     * 速度大于8,2秒后再次定位
     * 如果是wifi,60秒定位一次
     *
     * @return
     */
    @Override
    public boolean checkNeedSleep() {
        return false;
    }

    @Override
    protected int getSleepTime() {
        return 0;
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
