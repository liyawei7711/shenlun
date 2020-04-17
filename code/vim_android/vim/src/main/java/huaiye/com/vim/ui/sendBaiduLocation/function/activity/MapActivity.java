package huaiye.com.vim.ui.sendBaiduLocation.function.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.helper.ChatLocalPathHelper;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.BitmapResizeUtil;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.ui.sendBaiduLocation.app.MapPositioning;
import huaiye.com.vim.ui.sendBaiduLocation.function.adapter.MapAdapter;
import huaiye.com.vim.ui.sendBaiduLocation.util.AppStaticVariable;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;


/**
 * Created by xz on 2017/8/8 0008.
 * 关于地图的activity
 *
 * @author xz
 */
@BindLayout(R.layout.activity_map)
public class MapActivity extends AppBaseActivity {

    @BindView(R.id.am_rv)
    public RecyclerView mRecyclerView;

    @BindView(R.id.am_map)
    public MapView mMapView;

    /**
     * 定位按钮
     */
    @BindView(R.id.am_location)
    public ImageView mLocationButton;

    /**
     * 搜索按钮
     */
    @BindView(R.id.am_search)
    public ImageView mSearchButton;

    @BindExtra
    public String nMeetID;
    @BindExtra
    public String nMeetDomain;
    @BindExtra
    User nUser;
    @BindExtra
    boolean isGroup;
    @BindExtra
    ArrayList<SendUserBean> mMessageUsersDate;

    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();

    /**
     * 地图放大级别
     */
    private float mapZoom = 19;
    private BaiduMap mBaiduMap;
    private MapAdapter mMapAdapter;
    private PoiSearch mPoiSearch;
    private MapPositioning mMapPositioning;
    private GeoCoder mGeoCoder;
    /**
     * 是否是点击列表导致的移动
     */
    private boolean isRvClick = false;
    private ProgressDialog mProgressDialog;
    private BDLocation mLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isNeedSecureSnap = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            if (isGroup && mMessageUsersDate != null) {
                for (SendUserBean temp : mMessageUsersDate) {
                    if (!AppAuth.get().getUserID().equals(temp.strUserID)) {
                        SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                        info.strUserDomainCode = temp.strUserDomainCode;
                        info.strUserID = temp.strUserID;
                        users.add(info);
                    }
                }
            } else {
                SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                info.strUserDomainCode = nUser.strDomainCode;
                info.strUserID = nUser.strUserID;
                users.add(info);
            }
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
        }

        initView();
        initSetting();
        initListener();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mBaiduMap = mMapView.getMap();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mMapAdapter = new MapAdapter(this);
        //条目点击移动界面
        mMapAdapter.setOnItemClickListener(new MapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                isRvClick = true;
                PoiInfo poiInfo = (PoiInfo) mMapAdapter.getItem(position);
                setNewLatLngZoom(poiInfo.location);
                mMapAdapter.setmIndexTag(position);
            }
        });
        mRecyclerView.setAdapter(mMapAdapter);
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
     * 设置xy
     */
    private LatLng setLatLng(double lat, double lon) {
        LatLng latLng = new LatLng(lat, lon);
        return latLng;
    }


    /**
     * 设置标记点的放大级别
     */
    private void setNewLatLngZoom(LatLng latLng) {
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, mapZoom));
    }

    /**
     * 定位用户位置用户位置
     */
    public void initUserLocation() {

        mProgressDialog = ProgressDialog.show(this, null, "正在定位,请稍后");
        mProgressDialog.setCancelable(true);
        //开启定位
        mMapPositioning = MapPositioning.getInstance();
        mMapPositioning.setmLocation(new MapPositioning.XbdLocation() {

            @Override
            public void locSuccess(BDLocation location) {
                mProgressDialog.dismiss();
                if (null != location) {
                    mLocation = location;
                }
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        //设置精确度
                        .accuracy(0)
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(0)
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();

                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                        .fromResource(R.color.transparent);
                //保存配置，定位图层显示方式，是否允许显示方向信息，用户自定义定位图标
                MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurrentMarker);
                mBaiduMap.setMyLocationConfiguration(config);
                //移动到屏幕中心
                LatLng latLng = setLatLng(location.getLatitude(), location.getLongitude());
                setNewLatLngZoom(latLng);

                //设置用户地址
                PoiInfo userPoi = new PoiInfo();
                userPoi.location = latLng;
                userPoi.address = location.getAddrStr() + location.getLocationDescribe();
                userPoi.name = "[位置]";
                mMapAdapter.setmUserPoiInfo(userPoi);

                mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }

            @Override
            public void locFailure(int errorType, String errorString) {
                mProgressDialog.dismiss();
                showToast(errorString);
            }
        });
        mMapPositioning.start();
    }


    /**
     * 搜索返回后，需要先搜索
     */
    public void searchStr(String address, double lon, double lat) {
        if (lon > 0 && lat > 0) {
            LatLng latLng = setLatLng(lat, lon);
            //设置搜索地址
            PoiInfo userPoi = new PoiInfo();
            userPoi.location = latLng;
            userPoi.address = address;
            userPoi.name = "[位置]";
            mMapAdapter.setmUserPoiInfo(userPoi);
            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            setNewLatLngZoom(latLng);
        }
    }


    /**
     * 检索 创建
     */
    private void createSearch() {
        //兴趣点检索   没有用到
        mPoiSearch = PoiSearch.newInstance();

        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                //获取POI检索结果
                mMapAdapter.setDatas(result.getAllPoi(), true);
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult result) {
                //获取Place详情页检索结果
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                //poi 室内检索结果回调
            }
        };
        //mPoiSearch.searchInCity((new PoiCitySearchOption()).city(“北京”).keyword(“美食”).pageNum(10)).pageNum(10));
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        //地里编码
        mGeoCoder = GeoCoder.newInstance();
        OnGetGeoCoderResultListener getGeoListener = new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果
                }
                //获取地理编码结果
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                }
                //设置搜索地址
                PoiInfo userPoi = new PoiInfo();
                userPoi.location = result.getLocation();
                userPoi.address = result.getSematicDescription();
                userPoi.name = "[位置]";
                mMapAdapter.setmUserPoiInfo(userPoi);

                //获取反向地理编码结果
                List<PoiInfo> poiList = result.getPoiList();
                mMapAdapter.setDatas(poiList, true);
                mRecyclerView.scrollToPosition(0);
            }
        };
        mGeoCoder.setOnGetGeoCodeResultListener(getGeoListener);
    }

    /**
     * 地图监听
     */
    private void initListener() {
        //地图加载完成回调
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                createSearch();
                initUserLocation();
                mLocationButton.setVisibility(View.VISIBLE);
                mSearchButton.setVisibility(View.VISIBLE);
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


    /**
     * 提交位置信息
     */
    public void doSubmit() {
        mZeusLoadView.show();
        mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<File>() {
                    @Override
                    public File doOnThread() {
                        String time = System.currentTimeMillis() + "";
                        boolean saveOk = BitmapResizeUtil.saveBitmap(bitmap, time);
                        if (saveOk) {
                            File file = new File(AppUtils.ctx.getExternalFilesDir(null) + File.separator + "Vim", time + "baiduDituSnap.png");
                            return file;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mZeusLoadView.dismiss();
                                }
                            });
                            return null;
                        }

                    }

                    @Override
                    public void doOnMain(File data) {
                        if (null != data) {
                            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                EncryptUtil.encryptFile(data.getPath(), EncryptUtil.getNewFile(data.getPath()),
                                        true, isGroup, isGroup ? nMeetID + "" : "", isGroup ? nMeetDomain : "",
                                        isGroup ? "" : nUser.strUserID, isGroup ? "" : nUser.strDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                            @Override
                                            public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                upFile(new File(resp.m_strData));
                                            }

                                            @Override
                                            public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                showToast("对方未开启加密,无法发送");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        mZeusLoadView.dismiss();
                                                    }
                                                });
                                                mZeusLoadView.dismiss();
                                            }
                                        }
                                );
                            } else {
                                upFile(data);
                            }
                        }

                    }

                });

            }
        });

    }

    private void upFile(File data) {
        ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
            @Override
            public void onSuccess(final Upload upload) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChatLocalPathHelper.getInstance().cacheChatLoaclPath(upload.file1_name, data.getPath());
                        mZeusLoadView.dismiss();
                        PoiInfo item = (PoiInfo) mMapAdapter.getItem(mMapAdapter.getmIndexTag());
                        MessageEvent messageEvent = new MessageEvent(AppUtils.EVENT_MESSAGE_UPLOAD_BAIDU_SNAP_PIX, upload.file1_name, item);
                        EventBus.getDefault().post(messageEvent);
                        finish();
                    }
                });


            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mZeusLoadView.dismiss();
                        showToast(AppUtils.getString(R.string.upload_address_pic_failed));
                    }
                });
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                mZeusLoadView.dismiss();
            }
        }, data, AppDatas.Constants().getFileUploadUri());
    }

    public void onExit() {
        if (mMapPositioning != null) {
            mMapPositioning.onExit();
        }


        if (mPoiSearch != null) {
            mPoiSearch.destroy();
        }

        if (mGeoCoder != null) {
            mGeoCoder.destroy();
        }
    }

    @OnClick({R.id.send_location_send_btn, R.id.am_location, R.id.am_search, R.id.send_location_search, R.id.chat_title_bar_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_title_bar_title:
                finish();
                break;
            case R.id.send_location_send_btn:
                //提交位置
                doSubmit();
                break;
            case R.id.am_location:
                //重新定位用户位置
                initUserLocation();
                break;
            case R.id.send_location_search:
            case R.id.am_search:
                //搜索按钮
                Intent nIntent = new Intent(this, MapSearchActivity.class);
                nIntent.putExtra("mLocation", mLocation);
                startActivityForResult(nIntent, AppStaticVariable.MAP_SEARCH_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppStaticVariable.MAP_SEARCH_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String address = data.getStringExtra(AppStaticVariable.MAP_SEARCH_ADDRESS);
                double lon = data.getDoubleExtra(AppStaticVariable.MAP_SEARCH_LONGITUDE, 0.0);
                double lat = data.getDoubleExtra(AppStaticVariable.MAP_SEARCH_LATITUDE, 0.0);
                searchStr(address, lon, lat);
            }
        }
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
