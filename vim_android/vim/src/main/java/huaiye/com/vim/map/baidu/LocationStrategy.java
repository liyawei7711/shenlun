package huaiye.com.vim.map.baidu;

import android.text.TextUtils;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import huaiye.com.vim.VIMApp;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.map.bean.GPSMapBean;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class LocationStrategy extends BDAbstractLocationListener {
    /**
     * 根据id来获取用户最后上报位置,切换用户后就可以切换最后位置
     */
    private HashMap<String, BDLocation> lastUploadLocations = new HashMap<>();
    /**
     * 最后一次上传的时间
     */
    private long lastUploadTime;
    private Disposable disposable;
    private BDLocation currentBDLocation;

    public BDLocation getCurrentBDLocation(){
        return currentBDLocation;
    }

    /**
     * 最后一次上传的数据
     * @return
     */
    public BDLocation getLastUploadLocations() {
        String id = AppDatas.Auth().getUserID();
        BDLocation bdLocation = lastUploadLocations.get(id);
        return bdLocation;
    }

    /**
     * 如果未登录 啥都不干
     *
     * @param location
     */
    protected void putLastUploadLocations(BDLocation location) {
        if (TextUtils.isEmpty(HYClient.getSdkOptions().User().getUserTokenId())) {
            return;
        }
        if (location == null) {
            return;
        }
        lastUploadLocations.put(AppDatas.Auth().getUserID(), location);
    }


    private void clearLastCity() {
        lastUploadLocations.clear();
    }

    @Override
    public void onConnectHotSpotMessage(String connectWifiMac, int hotSpotState) {
        //在这个回调中，可获取当前设备所链接网络的类型、状态信息
        //connectWifiMac：表示连接WI-FI的MAC地址，无连接或者异常时返回NULL
        //hotSpotState有以下三种情况
        //LocationClient.CONNECT_HPT_SPOT_TRUE：连接的是移动热点
        //LocationClient.CONNECT_HPT_SPOT_FALSE：连接的非移动热点
        //LocationClient.CONNECT_HPT_SPOT_UNKNOWN：连接状态未知
        Logger.debug(LocationService.TAG, " onConnectHotSpotMessage connectWifiMac  " + connectWifiMac + " hotSpotState" + hotSpotState);
        setWifiStatus(connectWifiMac, hotSpotState);
    }

    protected abstract boolean checkNeedSleep();
    protected abstract int getSleepTime();

    protected abstract void setNewLocation(BDLocation location);

    public abstract void setWifiStatus(String connectWifiMac, int hotSpotState);

    public abstract void reset();


    /**
     * 速度大于8时,要变为2秒每次定位
     */
    public void checkNeedChangeTime(BDLocation currentLocation) {
        if (currentLocation == null) {
            //do noting
            return;
        }
        if (currentLocation.getSpeed() >= 8) {
            VIMApp.getInstance().locationService.updateTimeInterval(LocationService.DEFAULT_LOCATION_TIME_INTERVAL_HIGH);
        } else {
            VIMApp.getInstance().locationService.updateTimeInterval(LocationService.DEFAULT_LOCATION_TIME_INTERVAL);

        }
    }

    protected void keepLive(BDLocation bdLocation) {
        lastUploadTime = System.currentTimeMillis();
        Logger.debug(LocationService.TAG, " lastCity new  " + bdLocation.getLatitude() + " " + bdLocation.getLongitude() + " : " + bdLocation.getLocType());

//        millions = System.currentTimeMillis();
        LatLng latLng = convertBaiduToGPS(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));
        double gpsLatitude = latLng.latitude;
        double gpsLongitude = latLng.longitude;

        Logger.debug(LocationService.TAG, " local up gps" + gpsLatitude + "," + gpsLongitude);

        GPSMapBean gpsMapBean = new GPSMapBean();
        if (bdLocation.getAltitude() == 4.9e-324) {
            gpsMapBean.fAltitude = 0;
        } else if (bdLocation.getAltitude() == 5.9e-324) {
            gpsMapBean.fAltitude = 0;
        } else {
            gpsMapBean.fAltitude = bdLocation.getAltitude();
        }
        gpsMapBean.fLongitude = gpsLongitude;
        gpsMapBean.fLatitude = gpsLatitude;
        gpsMapBean.fSpeed = bdLocation.getSpeed();
        gpsMapBean.nSignalGrades = GPSLocation.get().getIndex();
        gpsMapBean.nDataSourceType = 0;
//        //暂时屏蔽位置上报
//        MapApi.get().pushGps(gpsMapBean, new ModelCallback<Object>() {
//            @Override
//            public void onSuccess(Object o) {
//
//            }
//        });
    }


    public static LatLng convertBaiduToGPS(LatLng sourceLatLng) {
        // 将百度坐标换回 GPS设备采集的原始GPS坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
        double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
        BigDecimal bdLatitude = new BigDecimal(latitude);
        bdLatitude = bdLatitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal bdLongitude = new BigDecimal(longitude);
        bdLongitude = bdLongitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        return new LatLng(bdLatitude.doubleValue(), bdLongitude.doubleValue());
    }
    public static LatLng convertGPSToBaidu(LatLng sourceLatLng) {
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    protected long getUploadTimeDuration() {
        return System.currentTimeMillis() - lastUploadTime;
    }


    protected void sleepLocation(int time) {
        Logger.debug(LocationService.TAG, " sleepLocation  time " + time);
        if (disposable == null || disposable.isDisposed()) {
            Logger.debug(LocationService.TAG, " have no location disposable ");
            disposable = io.reactivex.Observable.timer(time, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            //定位服务启动了,且正在暂停
                            if (VIMApp.getInstance().locationService.isPause() && VIMApp.getInstance().locationService.isStart()) {
                                VIMApp.getInstance().locationService.resume();
                            }
                        }
                    });
            VIMApp.getInstance().locationService.pause();
        }
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        //bdLocation.getLocType()注意返回值
        //TypeNetWorkLocation 161 网络定位
        //TypeGpsLocation = 61
        //TypeOffLineLocation 66 离线定位

        VIMApp.getInstance().locationService.requestHotSpotState();
        if (bdLocation == null) {
            Logger.debug(LocationService.TAG ," new location  null ");
            return;
        }

        //返回的定位信息是错的
        if (bdLocation.getLatitude() == 0 || bdLocation.getLongitude() == 0
                || bdLocation.getLatitude() == 4.9e-324 || bdLocation.getLongitude() == 4.9e-324) {
//            if (BuildConfig.DEBUG)
//                showToast("get gps err");
            return;
        }
        currentBDLocation=bdLocation;

        if(bdLocation.getLocType() != BDLocation.TypeOffLineLocation){
            //来个有效的百度定位,那就休眠GPS定位1分钟
            GPSLocation.get().doSleep();
        }


        if (filter(bdLocation)){
            return;
        }

        Logger.debug(LocationService.TAG , " new location receive  type " + bdLocation.getLocType() + "   " + bdLocation.getGpsCheckStatus());
        Logger.debug(LocationService.TAG , " local " + bdLocation.getLatitude() + "," + bdLocation.getLongitude());


        setNewLocation(bdLocation);

        checkNeedChangeTime(bdLocation);

        boolean needSleep = checkNeedSleep();
        if (needSleep){
            sleepLocation(getSleepTime());
        }


        //发送到界面
        EventBus.getDefault().post(bdLocation);



        BDLocation lastCity = getLastUploadLocations();
        if (lastCity == null) {
            lastCity = bdLocation;
            putLastUploadLocations(lastCity);
            //第一次来数据了上报,之后就是位置移动了上报
            keepLive(bdLocation);
        }

        double dis = DistanceUtil.getDistance(new LatLng(lastCity.getLatitude(), lastCity.getLongitude()), new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));

//        if (BuildConfig.DEBUG){
//            showToast(" dis  " + dis);
//        }

        Logger.debug(LocationService.TAG ," dis  " + dis);


        //只有移动距离大于5M才用当前位置更新city
        //不能来一个定位就更新lastCity:假设第一次移动4m,第二次移动3m,总共移动7米,这种情况就会距离就会错误
        long lastUploadDuration = getUploadTimeDuration();
        if (Math.abs(dis) <= 5 && lastUploadDuration < 60 * 1000) {
            return;
        } else {
            lastCity.setLocType(bdLocation.getLocType());
            lastCity.setLatitude(bdLocation.getLatitude());
            lastCity.setLongitude(bdLocation.getLongitude());
        }
        putLastUploadLocations(lastCity);

        //位置移动了上报
        keepLive(bdLocation);
    }

    /**
     * 本地GPS的信息来了
     * @param bdLocation
     */
    public void onReceiveGPSLocation(BDLocation bdLocation) {

        if (bdLocation == null) {
            Logger.debug(LocationService.TAG ,"onReceiveGPSLocation new location  null ");
            return;
        }

        //返回的定位信息是错的
        if (bdLocation.getLatitude() == 0 || bdLocation.getLongitude() == 0
                || bdLocation.getLatitude() == 4.9e-324 || bdLocation.getLongitude() == 4.9e-324) {
            return;
        }
        currentBDLocation=bdLocation;

        if (filter(bdLocation)){
            return;
        }

        Logger.debug(LocationService.TAG , "onReceiveGPSLocation new location receive  type " + bdLocation.getLocType() + "   " + bdLocation.getGpsCheckStatus());
        Logger.debug(LocationService.TAG , "onReceiveGPSLocation local " + bdLocation.getLatitude() + "," + bdLocation.getLongitude());


        setNewLocation(bdLocation);

        //发送到界面
        EventBus.getDefault().post(bdLocation);

        BDLocation lastCity = getLastUploadLocations();
        if (lastCity == null) {
            lastCity = bdLocation;
            putLastUploadLocations(lastCity);
            //第一次来数据了上报,之后就是位置移动了上报
            keepLive(bdLocation);
            return;
        }

        double dis = DistanceUtil.getDistance(new LatLng(lastCity.getLatitude(), lastCity.getLongitude()), new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()));


        Logger.debug(LocationService.TAG ,"onReceiveGPSLocation dis  " + dis);


        //只有移动距离大于5M才用当前位置更新city
        //不能来一个定位就更新lastCity:假设第一次移动4m,第二次移动3m,总共移动7米,这种情况就会距离就会错误
        long lastUploadDuration = getUploadTimeDuration();
        if (Math.abs(dis) <= 5 && lastUploadDuration < 60 * 1000) {
            return;
        } else {
            lastCity.setLocType(bdLocation.getLocType());
            lastCity.setLatitude(bdLocation.getLatitude());
            lastCity.setLongitude(bdLocation.getLongitude());
        }
        putLastUploadLocations(lastCity);

        //位置移动了上报
        keepLive(bdLocation);
    }


    /**
     *
     * @return  false,不过滤
     *          true,过滤掉,丢弃不用
     */
    private boolean filter(BDLocation nextLocation){
        BDLocation lastUploadLocation = getLastUploadLocations();
        if (lastUploadLocation == null){
            return false;
        }
        if (nextLocation == null){
            return false;
        }
        //之前是GPS定位,之后的60秒有非GPS定位的就抛弃
        if (lastUploadLocation.getLocType() == BDLocation.TypeGpsLocation){
            long duration = System.currentTimeMillis() - lastUploadTime;
            if (duration < 5 * 60 * 1000 && nextLocation.getLocType() != BDLocation.TypeGpsLocation){
                Logger.debug(LocationService.TAG ,"filter return true duration =" + duration + " type = " + nextLocation.getLocType());
                return true;
            }
        }
        return false;
    }

    public void stop(){
        if (disposable != null && !disposable.isDisposed()){
            disposable.dispose();
        }
        clearLastCity();
    }

}
