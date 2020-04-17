package huaiye.com.vim.models.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.auth.CUploadLogInfoRsp;
import com.huaiye.sdk.sdpmsgs.auth.CUserRegisterRsp;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import huaiye.com.vim.BuildConfig;
import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.bus.UploadFile;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.MD5Util;
import huaiye.com.vim.common.utils.MacUtil;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatSingleMsgBean;
import huaiye.com.vim.dao.msgs.JinJiLianXiRenBean;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.UserInfoDomain;
import huaiye.com.vim.map.baidu.LocationStrategy;
import huaiye.com.vim.models.CommonResult;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.ModelSDKErrorResp;
import huaiye.com.vim.models.auth.bean.AuthUser;
import huaiye.com.vim.models.auth.bean.ChangePwd;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.models.auth.bean.VersionData;
import huaiye.com.vim.models.contacts.bean.MenuListRole;
import huaiye.com.vim.models.download.DownloadApi;
import huaiye.com.vim.models.download.DownloadService;
import huaiye.com.vim.models.download.ErrorDialogActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPUIThreadCallbackAdapter;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppBaseActivity.showToast;
import static huaiye.com.vim.common.AppUtils.mDeviceIM;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AuthModel
 */

public class AuthApi {

    final String TAG = AuthApi.class.getSimpleName();
    String URL;
    File tag = new File(Environment.getExternalStorageDirectory() + "/huaiye_mc_android.zip");
    File tagZip = new File(Environment.getExternalStorageDirectory() + "/Android/data/huaiye.com.vim/files");
    File CrashFile = new File(Environment.getExternalStorageDirectory() + "/Android/data/huaiye.com.vim/files/mbe/log/Crash/mbe.log");

    private AuthApi() {
//        URL = AppDatas.Constants().getAddressBaseURL() + "login/appLogin.action";
        URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/user_login";
    }

    public static AuthApi get() {
        return new AuthApi();
    }

    public void chengpwd(String old, String news, final ModelCallback<ChangePwd> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/mod_user_pwd";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", String.valueOf(AppDatas.Auth().getUserID()))
                .addParam("strOldPassword", MD5Util.md5(old))
                .addParam("strNewPassword", MD5Util.md5(news))
                .setHttpCallback(new HTTPUIThreadCallbackAdapter() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                    }

                    @Override
                    public void onSuccess(HTTPResponse response) {
                        super.onSuccess(response);
                        String str = response.getContentToString();
                        ChangePwd authUser = null;
                        try {
                            authUser = new Gson().fromJson(str, ChangePwd.class);
                        } catch (Exception e) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage("HTTP ERROR AuthApi"));
                            }
                            return;
                        }
                        if (callback != null) {
                            callback.onSuccess(authUser);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        if (callback != null) {
                            callback.onFailure(response);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    public void login(final Context context,
                      final boolean notice,
                      final String account,
                      final String password,
                      final ModelCallback<AuthUser> callback) {
        Https.post(URL)
                /*.addParam("loginName", account)
                .addParam("password", password)*/
                .addHeader("Connection", "close")
                .addParam("strLoginName", account)
                .addParam("strPassword", MD5Util.md5(password))
                .addParam("strMAC", MacUtil.getMac(context))
                .addParam("nClientType", 1)
                .addParam("strIP", AppDatas.Constants().getAddressIP())
                .setHttpCallback(new HTTPUIThreadCallbackAdapter() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                        Log.i("VIMApp", "Auth Login PreStart");
                        if (callback != null) {
                            callback.onPreStart(request);
                        }
                    }

                    @Override
                    public void onSuccess(HTTPResponse response) {
                        super.onSuccess(response);
                        String str = response.getContentToString();
                        AuthUser authUser = null;
                        try {
                            authUser = new Gson().fromJson(str, AuthUser.class);
                        } catch (Exception e) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage("用户名密码错误,服务器返回解析失败"));
                            }
                            return;
                        }
                        if (authUser.nResultCode == 1010100003) {
                            callback.onFailure(new ModelSDKErrorResp().setErrorMessage("用户名错误"));
                            return;
                        } else if (authUser.nResultCode == 1010100005) {
                            callback.onFailure(new ModelSDKErrorResp().setErrorMessage("密码错误"));
                            return;
                        } else if (authUser.nResultCode != 0) {
                            if (!TextUtils.isEmpty(authUser.strResultDescribe)) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(authUser.strResultDescribe));
                            } else {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage("错误码:" + authUser.nResultCode + ",用户名密码错误"));
                            }
                            return;
                        }

                        authUser.password = password;
                        authUser.loginName = account;
//                        AppDatas.Auth().setHeaderTokenID(response.getHeader("token_id"));
                        Log.d("VIMApp", "authUser.strToken=" + authUser.strToken);
                        AppDatas.Auth().setHeaderTokenID(authUser.strToken);
                        AppDatas.Auth().setAuthUser(authUser);
                        requestGetMenuList(authUser.nRoleID);

                        final AuthUser finalAuthUser = authUser;
                        HYClient.getModule(ApiAuth.class)
                                .login(SdkParamsCenter.Auth.Login()
                                        .setAddress(AppDatas.Constants().getAddressIP(), AppDatas.Constants().getSiePort())
                                        .setUserId(String.valueOf(AppDatas.Auth().getUserID()))
                                        .setUserName(AppDatas.Auth().getUserName()), new SdkCallback<CUserRegisterRsp>() {
                                    @Override
                                    public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                                        successDeal(callback, finalAuthUser, cUserRegisterRsp);
                                    }

                                    @Override
                                    public void onError(final ErrorInfo errorInfo) {
                                        if (errorInfo.getCode() == ErrorMsg.re_load_code) {
                                            if (notice) {

                                                LogicDialog dialog = new LogicDialog(context)
                                                        .setMessageText("该账号已经在线，是否强制登录？")
                                                        .setCancelClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                errorDeal(errorInfo, callback);
                                                            }
                                                        })
                                                        .setConfirmClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                HYClient.getModule(ApiAuth.class)
                                                                        .login(SdkParamsCenter.Auth.Login()
                                                                                .setAddress(AppDatas.Constants().getAddressIP(), AppDatas.Constants().getSiePort())
                                                                                .setUserId(String.valueOf(AppDatas.Auth().getUserID()))
                                                                                .setAutoKickout(true)
                                                                                .setUserName(AppDatas.Auth().getUserName()), new SdkCallback<CUserRegisterRsp>() {
                                                                            @Override
                                                                            public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                                                                                successDeal(callback, finalAuthUser, cUserRegisterRsp);
                                                                            }

                                                                            @Override
                                                                            public void onError(ErrorInfo errorInfo) {
                                                                                errorDeal(errorInfo, callback);
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                dialog.setCancelable(false);
                                                dialog.setCanceledOnTouchOutside(false);
                                                dialog.show();
                                            } else {
                                                errorDeal(errorInfo, callback);
                                            }
                                        } else {
                                            errorDeal(errorInfo, callback);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        if (callback != null) {
                            callback.onFailure(response);
                        }
                    }
                })
                .build()
                .requestAsync();
    }


    /**
     * 获取菜单权限列表(如是否建群权限)
     */
    public void requestGetMenuList(int nRoleID) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "httpjson/get_menu_list";

        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("nRoleID", nRoleID)
                .addHeader("token_id", AppDatas.Auth().getHeaderTokenID())
                .setHttpCallback(new ModelCallback<MenuListRole>() {
                    @Override
                    public void onSuccess(MenuListRole menuListRole) {
                        VIMApp.getInstance().setMenuListRole(menuListRole);
                    }
                })
                .build()
                .requestAsync();
    }

    private void errorDeal(SdkCallback.ErrorInfo errorInfo, ModelCallback<AuthUser> callback) {
        if (callback != null) {
            if (errorInfo.getCode() == ErrorMsg.re_load_code) {
                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(""));
            } else {
                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(errorInfo.getMessage()));
            }
        }
    }

    private void successDeal(ModelCallback<AuthUser> callback, AuthUser finalAuthUser, CUserRegisterRsp registerRsp) {

        if (registerRsp != null) {
            AppDatas.Auth().setData("tokenId", registerRsp.strUserTokenID);
        }

        if (callback != null) {
            callback.onSuccess(finalAuthUser);
        }

        String strOSDCommand = "drawtext=fontfile="
                + HYClient.getSdkOptions().Capture().getOSDFontFile()
                + ":fontcolor=white:x=0:y=0:fontsize=26:box=1:boxcolor=black:alpha=0.8:text=' "
                + AppDatas.Auth().getUserName()
                + "'";
        // OSD名称初始化
//        HYClient.getSdkOptions().Capture().setOSDCustomCommand(strOSDCommand);
    }

    public void requestVersion(final Context context, final ModelCallback<VersionData> callback) {
        String fileServerURL = AppDatas.Constants().getAppUpdateUri();

        if (null == fileServerURL) {
            return;
        }

        Https.get(fileServerURL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .setHttpCallback(new ModelCallback<VersionData>() {

                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                    }

                    @Override
                    public void onSuccess(final VersionData versionData) {
                        if (callback != null) {
                            callback.onSuccess(versionData);
                        }
                        if (versionData.isNeedToUpdate()) {
                            final LogicDialog logicDialog = new LogicDialog(context);
                            logicDialog.setCancelable(false);
                            logicDialog.setCanceledOnTouchOutside(false);
                            logicDialog.setMessageText(AppUtils.getString(R.string.update_title_force));
                            logicDialog.setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logicDialog.dismiss();
                                    if (DownloadApi.isLoad) {
                                        return;
                                    }
                                    int netStatus = AppUtils.getNetWorkStatus(context);
                                    if (netStatus == -1) {
                                        // 无网络
                                    } else if (netStatus == 0) {
                                        // wifi
                                        showToast(AppUtils.getString(R.string.update_download_begin));
                                        Intent intent = new Intent(context, DownloadService.class);
                                        intent.putExtra("downloadURL", AppDatas.Constants().getFileServerURL() + versionData.path);
                                        context.startService(intent);
                                    } else if (netStatus == 1) {
                                        // 4G/3G
                                        Intent intent = new Intent(context, ErrorDialogActivity.class);
                                        intent.putExtra("downloadURL", AppDatas.Constants().getFileServerURL() + versionData.path);
                                        context.startActivity(intent);
                                    }
                                }
                            }).setCancelClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logicDialog.dismiss();
                                }
                            }).show();
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    /**
     * 崩溃时处理
     */
    public void uploadLogOnCrash(String host, int port, boolean isShow, String end) {
        AppUtils.zip(tagZip, tag);
        upload(host, port, tag, isShow, end);
    }

    /**
     * 每次启动检测上传
     */
    public void uploadLog(boolean isShow, String end) {
        String strHost = AppDatas.Constants().getFileServerIp();
        int nPort = AppDatas.Constants().getFileServerPort();
        if (strHost != null && nPort != -1) {
            if (CrashFile.exists()) {
                new RxUtils<List<ChatSingleMsgBean>>()
                        .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<String>() {
                            @Override
                            public String doOnThread() {
                                Log.d("uploadLog", tag.getAbsolutePath());
                                AppUtils.zip(tagZip, tag);
                                return "str";
                            }

                            @Override
                            public void doOnMain(String dataAll) {
                                upload(strHost, nPort, tag, isShow, end);
                            }
                        });
            }
        }
    }

    /**
     * 上传
     *
     * @param tag
     */
    private void upload(String host, int port, final File tag, final boolean isShow, String end) {
//        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/uploadEcsFile.action";
        String URL = "http://" + host + ":" + port + "/" + end;
        if (tag.length() > 1028 * 1028 * 50) {
            EventBus.getDefault().post(new UploadFile(2));
            return;
        }

        try {
            httppost(URL, tag.getPath(), tag.getName(), isShow);
        } catch (Exception e) {
            Log.d(TAG, "upload error " + e.getMessage());
            EventBus.getDefault().post(new UploadFile(1));
            e.printStackTrace();
        }
    }

    private void httppost(String url, String filePath, String fileName, final boolean isShow) throws Exception {
        OkHttpClient Client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file1", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .build();

        Request request = new Request.Builder()
                .header("X-Token", AppDatas.Auth().getToken())
                .url(url)
                .post(requestBody)
                .build();

        Client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(TAG, headers.name(i) + ":" + headers.value(i));
                }
                String strResp = response.body().string();
                Log.d(TAG, "onResponse: " + strResp);
                final Upload upload = new Gson().fromJson(strResp, Upload.class);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifySIE(upload, isShow);
                    }
                });
            }
        });
    }

    private void notifySIE(Upload upload, final boolean isShow) {
        Log.d(TAG, "notifySIE start ");
        HYClient.getModule(ApiAuth.class)
                .uploadLogInfo(SdkParamsCenter.Auth.UploadLogInfo()
                        .setStrLogPath(upload.file1_name)
                        .setStrLogTime(new Date().toString()), new SdkCallback<CUploadLogInfoRsp>() {
                    @Override
                    public void onSuccess(CUploadLogInfoRsp cUploadLogInfoRsp) {
                        Log.d(TAG, "notifySIE success " + cUploadLogInfoRsp.toString());
                        if (isShow) {
                            EventBus.getDefault().post(new UploadFile(0));
                        }
                        try {
                            Log.d(TAG, CrashFile.getPath() + CrashFile.getName());
                            CrashFile.delete();
                            tag.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        Log.d(TAG, "notifySIE onError " + errorInfo);
                        if (isShow) {
                            EventBus.getDefault().post(new UploadFile(1));
                        }
                    }
                });
    }

    /**
     * 警报
     */
    public void sos(Activity activity, String strLoginName, ISosListener listener) {
        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/user_emergency_alarm";
        JinJiLianXiRenBean bean;
        if (TextUtils.isEmpty(strLoginName)) {
            bean = AppDatas.MsgDB()
                    .getJinJiLianXiRenDao().queryOneItem(AppAuth.get().getUserID(), AppAuth.get().getDomainCode());
        } else {
            bean = AppDatas.MsgDB()
                    .getJinJiLianXiRenDao().queryOneItem(strLoginName);
        }
        if (bean == null) {
            showToast("尚未设置紧急联系人");
            return;
        }
        if (bean.getUserRel() == null) {
            showToast("尚未设置紧急联系人");
            return;
        }
        if (bean.getUserRel().isEmpty()) {
            showToast("尚未设置紧急联系人");
            return;
        }

        if(BuildConfig.DEBUG) {
            return;
        }
        ArrayList<UserInfoDomain> userList = new ArrayList<>();
        for (User temp : bean.getUserRel()) {
            userList.add(new UserInfoDomain(temp.strUserID, TextUtils.isEmpty(temp.strDomainCode) ? temp.strUserDomainCode : temp.strDomainCode));
        }

        BDLocation nBDLocation = VIMApp.getInstance().locationService.getCurrentBDLocation();
        double latitude = 0;
        double longitude = 0;
        if (nBDLocation != null) {
            latitude = nBDLocation.getLatitude();
            longitude = nBDLocation.getLongitude();
        }
        LatLng latLng = LocationStrategy.convertBaiduToGPS(new LatLng(latitude, longitude));
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strLoginName", TextUtils.isEmpty(strLoginName) ? AppAuth.get().getAccount() : strLoginName)
                .addParam("strIP", AppDatas.Constants().getAddressIP())
                .addParam("nClientType", 1)
                .addParam("strDevID", SP.getString(mDeviceIM, AppUtils.nEncryptDevice))
                .addParam("fLongitude", latLng.longitude)
                .addParam("fLatitude", latLng.latitude)
                .addParam("userList", userList)
                .setHttpCallback(new ModelCallback<CommonResult>() {
                    @Override
                    public void onSuccess(CommonResult response) {
                        if (response != null && response.nResultCode == 0) {
                            showToast("报警成功");
                            if (listener != null) {
                                listener.onSuccess(response);
                            }

                            if(!BuildConfig.DEBUG) {
                                AppAuth.get().clearData(activity);
                            }
                        } else {
                            showToast("报警失败");
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        showToast("报警失败");
                        if (listener != null) {
                            listener.onFailure(httpResponse);
                        }
                    }
                })
                .build()
                .requestAsync();
    }

    public void userBindNotify() {
        String URL = AppDatas.Constants().getVimAddressBaseURL() + "httpjson/user_bind_notify";
        Https.post(URL)
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppAuth.get().getDomainCode())
                .addParam("strUserID", AppAuth.get().getUserID())
                .setHttpCallback(new ModelCallback<CommonResult>() {
                    @Override
                    public void onSuccess(CommonResult response) {
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestAsync();
    }

    public interface ISosListener {
        void onSuccess(CommonResult response);

        void onFailure(HTTPResponse response);
    }

}
