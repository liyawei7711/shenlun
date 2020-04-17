package huaiye.com.vim.map.baidu;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.huaiye.sdk.logger.Logger;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.map.LocationStrategyHighImp;
import huaiye.com.vim.map.LocationStrategyLowImp;
import huaiye.com.vim.map.LocationStrategyMiddleImp;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_LOCATION_FREQUENCY;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_LOCATION_FREQUENCY_HIGH;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_LOCATION_FREQUENCY_LOW;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_LOCATION_FREQUENCY_MIDDLE;


/**
 * @author baidu
 */
public class LocationService {
    public static final String TAG = LocationService.class.getSimpleName();
    public static final int DEFAULT_LOCATION_TIME_INTERVAL = 5;
    public static final int DEFAULT_LOCATION_TIME_INTERVAL_HIGH = 2;
    private LocationClient client = null;
    private LocationClientOption mOption, DIYoption;
    private Object objLock = new Object();
    private LocationStrategy mLocationStrategy;

    private boolean isStart = false;
    private boolean isPause = false;
    private int timeInterval = DEFAULT_LOCATION_TIME_INTERVAL;
    

    public BDLocation getLastCity() {
        return mLocationStrategy.getLastUploadLocations();
    }

    public BDLocation getCurrentBDLocation() {
        return mLocationStrategy.getCurrentBDLocation();
    }


    /***
     *
     * @param locationContext
     */
    public LocationService(Context locationContext) {
        synchronized (objLock) {
            mLocationStrategy = getDefaultStrategy();
            if (client == null) {
                client = new LocationClient(locationContext);
                //默认定位一次
                client.setLocOption(getLocationClientOption(timeInterval));
                client.registerLocationListener(mLocationStrategy);
//                client.registerLocationListener(new BDAbstractLocationListener() {
//
//                    @Override
//                    public void onReceiveLocation(BDLocation bdLocation) {
//                        handleLocation(bdLocation);
//                        //高速的情况一直开着
//                        if (!(mLocationStrategy instanceof LocationStrategyHighImp)){
//                            pause();
//                        }
//                    }
//
//                    @Override
//                    public void onConnectHotSpotMessage(String connectWifiMac, int hotSpotState) {
//                        //在这个回调中，可获取当前设备所链接网络的类型、状态信息
//                        //connectWifiMac：表示连接WI-FI的MAC地址，无连接或者异常时返回NULL
//                        //hotSpotState有以下三种情况
//                        //LocationClient.CONNECT_HPT_SPOT_TRUE：连接的是移动热点
//                        //LocationClient.CONNECT_HPT_SPOT_FALSE：连接的非移动热点
//                        //LocationClient.CONNECT_HPT_SPOT_UNKNOWN：连接状态未知
//                        mLocationStrategy.setWifiStatus(connectWifiMac,hotSpotState);
//                    }
//                });
            }
        }
    }

    private LocationStrategy getDefaultStrategy() {
        String locationFrequency = (String) SP.getParam(STRING_KEY_LOCATION_FREQUENCY, STRING_KEY_LOCATION_FREQUENCY_HIGH);
        LocationStrategy locationStrategy;
        switch (locationFrequency) {
            case STRING_KEY_LOCATION_FREQUENCY_LOW:
                locationStrategy = new LocationStrategyLowImp();
                break;
            case STRING_KEY_LOCATION_FREQUENCY_MIDDLE:
                locationStrategy = new LocationStrategyMiddleImp();

                break;
            case STRING_KEY_LOCATION_FREQUENCY_HIGH:
                locationStrategy = new LocationStrategyHighImp();
                break;
            default:
                locationStrategy = new LocationStrategyMiddleImp();
        }
        return locationStrategy;
    }

    public void updateStrategy(String type) {
        Logger.debug(TAG, " LocationService updateStrategy " + type);
        mLocationStrategy.stop();
        client.unRegisterLocationListener(mLocationStrategy);
        switch (type) {
            case STRING_KEY_LOCATION_FREQUENCY_LOW:
                mLocationStrategy = new LocationStrategyLowImp();
                break;
            case STRING_KEY_LOCATION_FREQUENCY_MIDDLE:
                mLocationStrategy = new LocationStrategyMiddleImp();

                break;
            case STRING_KEY_LOCATION_FREQUENCY_HIGH:
                mLocationStrategy = new LocationStrategyHighImp();
                break;
        }
        client.registerLocationListener(mLocationStrategy);
        if (isPause) {
            resume();
        }
    }


    public int getTimeInterval() {
        return timeInterval;
    }

    public void updateTimeInterval(int currentTimeInterval) {
        if (timeInterval == currentTimeInterval) {
            return;
        }
        this.timeInterval = currentTimeInterval;
        client.setLocOption(getLocationClientOption(timeInterval));
    }

    /**
     * 发送位置给LocationService,APP使用GPS定位后拿到gps信息调用此方法
     * @param bdLocation
     */
    public void sendLocation(BDLocation bdLocation){
        if (isStart && !isPause && mLocationStrategy != null){
            mLocationStrategy.onReceiveGPSLocation(bdLocation);
        }
    }

    /***
     *
     * @return DefaultLocationClientOption  默认O设置
     */
    public LocationClientOption getLocationClientOption(int second) {
        if (mOption == null) {
            mOption = new LocationClientOption();
            mOption.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(second * 1000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
            mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            mOption.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
            mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        } else {
            mOption.setScanSpan(second * 1000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        }
        return mOption;
    }

    public void start() {
        Logger.debug(TAG, "  location service start");
        synchronized (objLock) {
            if (!isStart) {
                isStart = true;
                isPause = false;
                if (client != null && !client.isStarted()) {
                    mLocationStrategy.reset();
                    client.start();
                    client.requestHotSpotState();
                }
                GPSLocation.get().startLocation();
            }
        }
    }

    public void stop() {
        Logger.debug(TAG, "  location service stop");
        synchronized (objLock) {
            isStart = false;
            isPause = false;
            if (client != null && client.isStarted()) {
                client.stop();

            }
            GPSLocation.get().stopLocation();

            mLocationStrategy.stop();
        }

    }


    public void pause() {
        Logger.debug(TAG, "location service pause");
        synchronized (objLock) {
            if (isStart) {
                isPause = true;
                if (client != null && client.isStarted()) {
                    client.stop();
                }
                GPSLocation.get().stopLocation();
            }

        }
    }

    public void requestHotSpotState(){
        if (client != null){
            client.requestHotSpotState();
        }
    }

    public void resume() {
        Logger.debug(TAG, "  location service resume");
        synchronized (objLock) {
            if (isStart) {
                isPause = false;
                if (client != null && !client.isStarted()) {
                    client.start();
                    client.requestHotSpotState();
                }
                GPSLocation.get().startLocation();

            }

        }
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isPause() {
        return isPause;
    }


}
