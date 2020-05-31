package huaiye.com.vim.models.download;

import android.content.Context;

import com.google.gson.Gson;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.EncyptJsonDao;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPCallback;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.jinnetwork.core.work.method_post.PostContentType;

/**
 * author: admin
 * date: 2018/02/22
 * version: 0
 * mail: secret
 * desc: DownloadApi
 */

public class DownloadApi {
    public static boolean isLoad = false;
    String URL;

    public static DownloadApi get() {
        return new DownloadApi();
    }

    private DownloadApi() {
        URL = AppDatas.Constants().getAddressBaseURL() + "download/load.action";
    }

    public void download(final Context context, String downLoadUrl) {
        Https.get(downLoadUrl)
                .setDownloadMode(new File(context.getExternalCacheDir().getPath(), "newapk.apk"))
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {

                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {

                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                });
    }

    public void upload(final ModelCallback<Upload> callback, File file) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "busidataexchange/uploadEcsFile.action";
        Https.post(URL, PostContentType.MultipartFormdata)
                .addParam("ecsFiles", file)
                .setHttpCallback(new ModelCallback<Upload>() {
                    @Override
                    public void onSuccess(final Upload versionData) {
                        callback.onSuccess(versionData);
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        callback.onFailure(httpResponse);
                    }

                })
                .build()
                .requestAsync();
    }

    public void downloadKey(final Context context, String downLoadUrl) {
        File fC1 = new File(context.getExternalFilesDir(null) + File.separator + "cmf/key.json");
        File fC = new File(context.getExternalFilesDir(null) + File.separator + "cmf/" + System.currentTimeMillis() + ".json");
        if (fC.exists()) {
            fC.delete();
        }
        Https.get(downLoadUrl)
                .setDownloadMode(fC)
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {

                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {
                        String strOld = readTextFromSDcard(fC1);
                        String strNew = readTextFromSDcard(fC);
                        try {
                            EncyptJsonDao oldBean = new Gson().fromJson(strOld, EncyptJsonDao.class);
                            EncyptJsonDao newBean = new Gson().fromJson(strNew, EncyptJsonDao.class);
                            if (oldBean == null) {
                                fC.renameTo(fC1);
                                downloadKeyDat(context, newBean);
                            } else if (oldBean.getPackage().getFileName().equals(newBean.getPackage().getFileName())) {
                                fC.delete();
                            } else {
                                fC1.delete();
                                fC.renameTo(fC1);
                                downloadKeyDat(context, newBean);
                            }
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                    }
                }).build().requestNowAsync();
    }

    public void downloadKeyDat(final Context context, EncyptJsonDao newBean) {
        File fC1 = new File(context.getExternalFilesDir(null) + File.separator + "cmf/keydata.dat");
        File fC = new File(context.getExternalFilesDir(null) + File.separator + "cmf/" + System.currentTimeMillis() + ".dat");
        if (fC.exists()) {
            fC.delete();
        }
        Https.get(AppDatas.Constants().getCommonUri(newBean.getPackage().getFileName()))
                .setDownloadMode(fC)
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {

                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {
                        try {
                            FileInputStream fileInputStream = new FileInputStream(fC);
                            byte[] hex = DigestUtils.sha256(fileInputStream);

                            StringBuffer stringBuffer = new StringBuffer();
                            String temp = null;
                            for (int i = 0; i < hex.length; i++) {
                                temp = Integer.toHexString(hex[i] & 0xFF);
                                if (temp.length() == 1) {
                                    //1得到一位的进行补0操作
                                    stringBuffer.append("0");
                                }
                                stringBuffer.append(temp);
                            }

                            if (stringBuffer.toString().equals(newBean.getPackage().getSHA256())) {
                                if (fC1.exists()) {
                                    fC1.delete();
                                }
                                fC.renameTo(fC1);
                            } else {
                                fC.delete();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                }).build().requestNowAsync();
    }

    /**
     * 上传图片等文件
     *
     * @param file
     */
    public void uploadFile(ModelCallback<Upload> callback, final File file, String end) {
        String URL = AppDatas.Constants().getFileServerURL() + end;

        try {
            httppost(callback, URL, file.getPath(), file.getName());
        } catch (Exception e) {
            callback.onFailure(null);
            e.printStackTrace();
        }
    }

    private void httppost(final ModelCallback<Upload> callback, String url, String filePath, String fileName) throws Exception {
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
                callback.onFailure(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResp = response.body().string();
                try {
                    final Upload upload = new Gson().fromJson(strResp, Upload.class);
                    callback.onSuccess(upload);
                } catch (Exception e) {
                    callback.onFailure(null);
                }

            }
        });
    }

    private String readTextFromSDcard(File file) {
        String resultString;
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            inputStreamReader.close();
            bufferedReader.close();
            resultString = stringBuilder.toString();
            return resultString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
