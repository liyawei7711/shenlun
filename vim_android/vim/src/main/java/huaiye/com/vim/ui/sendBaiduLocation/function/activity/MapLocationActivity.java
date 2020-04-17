package huaiye.com.vim.ui.sendBaiduLocation.function.activity;

import android.view.MotionEvent;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.map.baidu.LocationStrategy;
import huaiye.com.vim.ui.sendBaiduLocation.app.MapPositioning;


/**
 * Created by xz on 2017/8/8 0008.
 * 关于地图的activity
 *
 * @author xz
 */
@BindLayout(R.layout.activity_map_location)
public class MapLocationActivity extends AppBaseActivity {

    @BindView(R.id.am_map)
    public MapView mMapView;
    @BindView(R.id.am_location)
    public View am_location;

    @BindExtra
    double longitude;
    @BindExtra
    double latitude;

    private MarkerOptions markerOptions;
    private Marker marker;

    /**
     * 地图放大级别
     */
    private float mapZoom = 19;
    /**
     * 是否是点击列表导致的移动
     */
    private boolean isRvClick = false;
    private BaiduMap mBaiduMap;
    private MapPositioning mMapPositioning;
    private BDLocation mLocation;
    private GeoCoder mGeoCoder;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(getString(R.string.common_notice45))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {

        mBaiduMap = mMapView.getMap();
        mGeoCoder = GeoCoder.newInstance();
        am_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initUserLocation();
            }
        });
        initSetting();
        initListener();
//        initUserLocation();

        LatLng latLng = LocationStrategy.convertGPSToBaidu(new LatLng(latitude, longitude));

        dingWei(latLng);

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);
        markerOptions = new MarkerOptions().icon(bitmap).position(new LatLng(longitude, latitude));
        marker = (Marker) mBaiduMap.addOverlay(markerOptions);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(latLng).zoom(mapZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 初始化地图的设置
     */
    private void initSetting() {
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        //是否允许旋转手势
        uiSettings.setRotateGesturesEnabled(false);
        //是否允许指南针
        uiSettings.setCompassEnabled(false);
        //是否允许俯视手势
        uiSettings.setOverlookingGesturesEnabled(false);
        //是否显示缩放控件
        mMapView.showZoomControls(false);
        //是否显示比例尺
        mMapView.showScaleControl(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //楼快效果
        mBaiduMap.setBuildingsEnabled(true);
        //设置放大缩小级别
        mBaiduMap.setMaxAndMinZoomLevel(21, 4);
    }


    /**
     * 定位用户位置用户位置
     */
    public void initUserLocation() {

        //开启定位
        mMapPositioning = MapPositioning.getInstance();
        mMapPositioning.setmLocation(new MapPositioning.XbdLocation() {

            @Override
            public void locSuccess(BDLocation location) {
                if (null != location) {
                    mLocation = location;
                }
                dingWei(new LatLng(location.getLatitude(), location.getLongitude()));
            }

            @Override
            public void locFailure(int errorType, String errorString) {
                showToast(errorString);
            }
        });
        mMapPositioning.start();
    }

    private void dingWei(LatLng location) {
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                //设置精确度
                .accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(0)
                .latitude(location.latitude)
                .longitude(location.longitude).build();

        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.color.transparent);
        //保存配置，定位图层显示方式，是否允许显示方向信息，用户自定义定位图标
        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurrentMarker);
        mBaiduMap.setMyLocationConfiguration(config);
        //移动到屏幕中心
        setNewLatLngZoom(location);

        //设置用户地址
        PoiInfo userPoi = new PoiInfo();
        userPoi.location = location;
//        userPoi.address = location.getAddrStr() + location.getLocationDescribe();
        userPoi.name = getString(R.string.common_notice44);
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(location));
    }

    /**
     * 设置标记点的放大级别
     */
    private void setNewLatLngZoom(LatLng latLng) {
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, mapZoom));
    }

    /**
     * 地图监听
     */
    private void initListener() {
        //地图加载完成回调
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                initUserLocation();
            }
        });

        //单击事件监听
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        //监听地图状态
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (!isRvClick) {
                    mapStatus.toString();

                    //得到中心点坐标，开始反地理编码
                    LatLng centerLatLng = mapStatus.target;
                    mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(centerLatLng));
                }
            }
        });
        //监听地图的按下事件
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                //如果用户触碰了地图，那么把 isRvClick 还原
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    isRvClick = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

}
