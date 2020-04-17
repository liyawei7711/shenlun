package huaiye.com.vim.models.config;

import java.util.ArrayList;

import huaiye.com.vim.VIMApp;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.config.bean.GetConfigResponse;
import huaiye.com.vim.models.download.DownloadApi;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * Created by LENOVO on 2019/3/29.
 */

public class ConfigApi {
    private ConfigApi(){

    }

    public static ConfigApi get(){
        return new ConfigApi();
    }

    public void getAllConfig(final ModelCallback<GetConfigResponse> callback){
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_vss_config_para";
        ArrayList<String> list = new ArrayList<>();
        list.add("FILE_SERVICE_IP");
        list.add("FILE_SERVICE_PORT");
        list.add("FILE_SERVICE_UPLOAD_URI");
        list.add("KEY_FILE_DOWNLOAD_URL");
        list.add("APP_VERSION_UPDATE_URL");
        list.add("HEADER_IMAGE_URL");
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("lstVssConfigParaName", list)
                .setHttpCallback(new ModelCallback<GetConfigResponse>() {

                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                    }

                    @Override
                    public void onSuccess(final GetConfigResponse response) {
                        /* 先更新全局变量 */
                        if (response.nResultCode == 0) {
                            if (response.lstVssConfigParaInfo != null && response.lstVssConfigParaInfo.size() > 1) {
                                for (GetConfigResponse.Data data : response.lstVssConfigParaInfo) {
                                    if (data.strVssConfigParaName.equals("FILE_SERVICE_IP")) {
                                        AppDatas.Constants().setFileServerIp(data.strVssConfigParaValue);
                                    }
                                    if (data.strVssConfigParaName.equals("FILE_SERVICE_PORT")) {
                                        AppDatas.Constants().setFileServerPort(Integer.parseInt(data.strVssConfigParaValue));
                                    }
                                    if(data.strVssConfigParaName.equals("FILE_SERVICE_UPLOAD_URI")){
                                        AppDatas.Constants().setFileUploadUri(data.strVssConfigParaValue);
                                    }
                                    if(data.strVssConfigParaName.equals("APP_VERSION_UPDATE_URL")){
                                        AppDatas.Constants().setAppUploadUri(data.strVssConfigParaValue);
                                    }
                                    if(data.strVssConfigParaName.equals("HEADER_IMAGE_URL")){
                                        AppDatas.Constants().setHeaderUri(data.strVssConfigParaValue);
                                    }
                                    if(data.strVssConfigParaName.equals("KEY_FILE_DOWNLOAD_URL")) {
                                        AppDatas.Constants().setKeyFileUploadUri(data.strVssConfigParaValue);
                                        DownloadApi.get().downloadKey(VIMApp.getInstance(), AppDatas.Constants().getKeyFileUploadUri());
                                    }
                                }
                            }
                        }
                        if (callback != null) {
                            callback.onSuccess(response);
                        }
                    }
                })
                .build()
                .requestAsync();
    }
}
