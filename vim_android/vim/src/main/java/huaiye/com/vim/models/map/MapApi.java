package huaiye.com.vim.models.map;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.huaiye.sdk.logger.Logger;

import java.util.ArrayList;

import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.MapMarkBean;
import huaiye.com.vim.map.baidu.GPSLocation;
import huaiye.com.vim.models.CommonResult;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.map.bean.GPSMapBean;
import huaiye.com.vim.models.map.bean.MarkBean;
import huaiye.com.vim.models.map.bean.MarkModelBean;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: MapApi
 */

public class MapApi {
    String URL;
    GeoCoder geoCoder = GeoCoder.newInstance();
    /**
     * 是否是登录后第一次开始上传GPS
     */
    public static boolean isNewBegin = true;

    private MapApi() {
        URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_mark_list";
    }


    public static MapApi get() {
        return new MapApi();
    }

    public void getCover(final ModelCallback<MarkBean> callback) {
        URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_mark_list";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("nType", 0)
                .addParam("nOrderByID", -1)
                .addParam("strKeywords", "")
                .addParam("nAscOrDesc", 0)
                .addParam("strUserID", AppDatas.Auth().getUserID() + "")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .setHttpCallback(new ModelCallback<MarkBean>() {
                    @Override
                    public void onSuccess(MarkBean markBean) {
                        if (markBean != null && callback != null) {
                            callback.onSuccess(markBean);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestNowAsync();
    }

    public void getMarket(MarkModelBean bean, final ModelCallback<MapMarkBean> callback) {
        URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_mark_info";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("nMarkID", bean.nMarkID)
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .setHttpCallback(new ModelCallback<MapMarkBean>() {

                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                        if (callback != null) {
                            callback.onPreStart(httpRequest);
                        }
                    }

                    @Override
                    public void onSuccess(MapMarkBean markBean) {
                        if (markBean != null && callback != null) {
                            callback.onSuccess(markBean);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                        super.onFinish(httpResponse);
                        if (callback != null) {
                            callback.onFinish(httpResponse);
                        }
                    }
                })
                .build()
                .requestNowAsync();

    }


    public void deleteMark(Observer<CommonResult> observer, final ArrayList<Integer> markIDs) {
        final String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/del_mark";

        io.reactivex.Observable.create(new ObservableOnSubscribe<CommonResult>() {
            @Override
            public void subscribe(ObservableEmitter<CommonResult> emitter) throws Exception {
                for (Integer markID : markIDs){
                    HTTPResponse httpResponse = Https.post(URL)
                            .addHeader("Connection", "close")
                            .addHeader("X-Token", AppDatas.Auth().getToken())
                            .addParam("nMarkID", markID)
                            .addParam("strUserID", AppDatas.Auth().getUserID() + "")
                            .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                            .build()
                            .request();
                    if (!httpResponse.isStatusCodeSuccessful()){
                        emitter.onError(new Exception(httpResponse.getErrorMessage()));
                        break;
                    }
                    Logger.debug(new String(httpResponse.getContent()));

                }
                emitter.onNext(new CommonResult());
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(observer);

    }


    public void pushGps(GPSMapBean bean, final ModelCallback<Object> callback) {
        URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/push_gps_info";
        bean.nIsBegin = isNewBegin ? 1 : 0;
        Https.post(URL)
                .addHeader("Connection", "close")
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addParam("strObjID", AppDatas.Auth().getUserID() + "")
                .addParam("strObjDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("nObjType", 1)
                .addParam("rGPSInfo", bean)
                .setHttpCallback(new ModelCallback<Object>() {
                    @Override
                    public void onSuccess(Object markBean) {
                        if (markBean != null && callback != null) {
                            callback.onSuccess(markBean);
                        }
                        isNewBegin = false;
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestNowAsync();

    }

    public void getUserAddress(LatLng lat, OnGetGeoCoderResultListener listener) {
        LatLng latLng = GPSLocation.convertGPSToBaidu(lat);
        // 设置反地理经纬度坐标,请求位置时,需要一个经纬度
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
        //设置地址或经纬度反编译后的监听,这里有两个回调方法,
        geoCoder.setOnGetGeoCodeResultListener(listener);
    }

}
