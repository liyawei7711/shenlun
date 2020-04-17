package huaiye.com.vim.map.baidu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.huaiye.sdk.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import huaiye.com.vim.VIMApp;
import huaiye.com.vim.bus.GPSStatus;
import huaiye.com.vim.common.AppUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;

/**
 * author: admin
 * date: 2018/05/12
 * version: 0
 * mail: secret
 * desc: GPSLocation
 */

public class GPSLocation {

    private static GPSLocation GPS;


    private LocationManager lm;
    private int currentGpsNum = 0;
    private boolean currentGPSStatus;
    private GpsStatusInterface gpsStatusInterface;
    private GpsStatusReceiver receiver;
    private int lastCount;
    private GpsStatus.Listener listener;
    private GnssStatus.Callback gnssListener;
    private LocationListener locationListener;
    private Disposable sleepDisposable;

    public static GPSLocation get() {
        if (GPS == null) {
            synchronized (GPSLocation.class) {
                if (GPS == null) {
                    GPS = new GPSLocation(VIMApp.getInstance());
                }
            }
        }
        return GPS;
    }


    @SuppressLint("MissingPermission")
    private GPSLocation(Context context) {


        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!isGpsOpen()) {
            return;
        }
        startListener(lm);


    }

    public void setGpsStatusInterface(GpsStatusInterface gpsStatusInterface) {
        this.gpsStatusInterface = gpsStatusInterface;
        if (gpsStatusInterface != null){
            gpsStatusInterface.gpsSwitchState(isGpsOpen());
        }
    }

    public boolean isPermissionGranted(){
        if (ActivityCompat.checkSelfPermission(VIMApp.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(VIMApp.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Logger.debug("GPSLocation","GPSLocation isPermissionGranted false" );
            return false;
        }

        if(lm == null || !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return false;
        }
        return true;
    }

    /**
     * 注册gps状态监听,主要是为了获取gps卫星数量
     * @param lm
     */
    @SuppressLint("MissingPermission")
    private void startListener(LocationManager lm) {
        if (!isPermissionGranted()){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gnssListener = new GnssStatus.Callback() {
                long lastStatusTime ;
                @Override
                public void onStarted() {
                    super.onStarted();
                    Logger.debug("GPSLocation","GnssStatus  onStarted");
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    Logger.debug("GPSLocation","GnssStatus  onStopped");
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    super.onFirstFix(ttffMillis);
                    Logger.debug("GPSLocation","GnssStatus  onFirstFix");
                }

                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    Logger.debug("GPSLocation","GnssStatus  onSatelliteStatusChanged " + status.getSatelliteCount());
                    currentGpsNum = status.getSatelliteCount();
                    //1秒一次
                    if (System.currentTimeMillis() - lastStatusTime < 1 *1000){
                        return;
                    }
                    postGps();
                    lastStatusTime = System.currentTimeMillis();


                }
            };
            lm.registerGnssStatusCallback(gnssListener);
        } else {
            listener = new GpsStatus.Listener() {
                long lastStatusTime ;
                @Override
                public void onGpsStatusChanged(int event) {
                    //1秒一次
                    if (System.currentTimeMillis() - lastStatusTime < 1 *1000){
                        return;
                    }
                    lastStatusTime = System.currentTimeMillis();
                    switch (event) {             //第一次定位
                        case GpsStatus.GPS_EVENT_FIRST_FIX:
                            Logger.debug("GPSLocation","gps count 第一次定位 ");
                            getGPSCount();
                            break;
                        //卫星状态改变
                        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                            getGPSCount();
                            break;//定位启动
                        case GpsStatus.GPS_EVENT_STARTED:
                            Logger.debug("GPSLocation","gps count 定位启动 ");
                            getGPSCount();
                            break;
                        //定位结束
                        case GpsStatus.GPS_EVENT_STOPPED:
                            Logger.debug("GPSLocation","gps count 定位结束 ");
                            break;
                    }
                }
            };
            lm.addGpsStatusListener(listener);

        }
    }

    private void observeGPSSwitch() {

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.location.PROVIDERS_CHANGED");
        receiver = new GpsStatusReceiver();
        AppUtils.ctx.registerReceiver(receiver, filter);
    }


    class GpsStatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                if (gpsStatusInterface != null) {
                    if (!isGpsOpen()){
                        gpsStatusInterface.gpsSwitchState(false);
                        stopGpsObserver();
                    }else {
                        startListener(lm);
                        gpsStatusInterface.gpsSwitchState(true);
                    }
                }
            }
        }
    }


    private void getGPSCount() {
        //获取当前状态
        @SuppressLint("MissingPermission")
        GpsStatus gpsStatus = lm.getGpsStatus(null);
        //获取卫星颗数的默认最大值
        int maxSatellites = gpsStatus.getMaxSatellites();
        //创建一个迭代器保存所有卫星
        Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
        int count = 0;
        while (iters.hasNext() && count <= maxSatellites) {
            GpsSatellite gpsSatellite = iters.next();
            if (gpsSatellite.getSnr() > 0) {
                count++;
            }
        }
        currentGpsNum = count;
        postGps();
    }

    public int getIndex() {
        if (currentGpsNum <= 0) {
            return 0;
        } else if (currentGpsNum <= 4) {
            return 1;
        } else if (currentGpsNum <= 8) {
            return 2;
        } else {
            return 3;
        }
    }


    private void postGps() {
        boolean temp;
        if (currentGpsNum <= 4) {
            temp = true;
        } else {
            temp = false;
        }
        if (currentGPSStatus == temp) {
            return;
        }
        currentGPSStatus = temp;
        if (lastCount != currentGpsNum) {
            lastCount = currentGpsNum;

            EventBus.getDefault().post(new GPSStatus(currentGPSStatus, getIndex()));
        }
    }



    @SuppressLint("MissingPermission")
    public void startGpsObserver() {
        if (!isPermissionGranted()){
            return;
        }
        lm.addGpsStatusListener(listener);
        observeGPSSwitch();
        if (gpsStatusInterface != null) {
            gpsStatusInterface.gpsSwitchState(isGpsOpen());
        }

    }

    public void stopGpsObserver() {
        if (receiver != null) {
            AppUtils.ctx.unregisterReceiver(receiver);
        }
        if (lm != null && listener != null) {
            lm.removeGpsStatusListener(listener);
            listener = null;
        }

        if (lm != null && gnssListener != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lm.unregisterGnssStatusCallback(gnssListener);
        }
    }

    public boolean isGpsOpen() {
        boolean isOpen = false;
        if (lm != null && lm.isProviderEnabled(GPS_PROVIDER)) {
            isOpen = true;
        }
        return isOpen;
    }


    @SuppressLint("MissingPermission")
    public void startLocation(){
        //需要定位可以运行,才启动GPS定位
        if (!VIMApp.getInstance().locationService.isStart() || VIMApp.getInstance().locationService.isPause()){
            return;
        }
        if (!isPermissionGranted()){
            return;
        }
        Logger.debug("GPSLocation","GPSLocation startLocation");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location == null ){
                    Logger.debug("GPSLocation","GPSLocation locationListener onLocationChanged null");
                    return;
                }

                Logger.debug("GPSLocation","GPSLocation locationListener onLocationChanged" + location.getLatitude() + " " + location.getLongitude() + " " + location.getSpeed()
                        + " " + location.getProvider());
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                LatLng bdLatLng = convertGPSToBaidu(latLng);
                BDLocation bdLocation = new BDLocation();
                bdLocation.setLatitude(bdLatLng.latitude);
                bdLocation.setLongitude(bdLatLng.longitude);
                bdLocation.setAltitude(location.getAltitude());
                bdLocation.setSpeed(location.getSpeed());
                bdLocation.setLocType(BDLocation.TypeGpsLocation);
                VIMApp.getInstance().locationService.sendLocation(bdLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Logger.debug("GPSLocation","GPSLocation locationListener onStatusChanged" + status
                        + "" + provider);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Logger.debug("GPSLocation","GPSLocation locationListener onProviderEnabled"
                        + "" + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Logger.debug("GPSLocation","GPSLocation locationListener onProviderDisabled"
                        + "" + provider);
            }
        };
        lm.requestLocationUpdates(GPS_PROVIDER, 5*1000, 5, locationListener);

    }

    public void stopLocation(){
        Logger.debug("GPSLocation","GPSLocation stopLocation");

        if (lm != null && locationListener != null){
            lm.removeUpdates(locationListener);
            locationListener = null;
        }

        if (sleepDisposable != null){
            sleepDisposable.dispose();
        }
    }


    @SuppressLint("MissingPermission")
    public Location getLastLocation() {
        if (!isPermissionGranted()){
            return null;
        }
        Location location = null;
        if (lm != null) {
            location = lm.getLastKnownLocation(GPS_PROVIDER);
            if (location == null) {
                location = lm.getLastKnownLocation(NETWORK_PROVIDER);
            }
        }
        if (location == null) {
            return null;
        }
        LatLng latLng = convertGPSToBaidu(new LatLng(location.getLatitude(), location.getLongitude()));
        Location location1 = new Location("baidu");
        location1.setLatitude(latLng.latitude);
        location1.setLongitude(latLng.longitude);
        return location1;
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(true);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(true);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    public static LatLng convertGPSToBaidu(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    public interface GpsStatusInterface {
        void gpsSwitchState(boolean gpsOpen);
    }


    public void doSleep(){
        Logger.debug("GPSLocation","GPSLocation doSleep");
        if (sleepDisposable != null){
            sleepDisposable.dispose();
            sleepDisposable = null;
        }

        //取消定位返回
        if (lm != null && locationListener != null){
            lm.removeUpdates(locationListener);
            locationListener = null;
        }


        //等待30s后启动
        sleepDisposable = Observable.timer(60, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Logger.debug("GPSLocation","GPSLocation doSleep startLocation");
                        startLocation();
                    }
                });
    }

}
