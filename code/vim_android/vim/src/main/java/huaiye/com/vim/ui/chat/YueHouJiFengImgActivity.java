package huaiye.com.vim.ui.chat;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import huaiye.com.vim.dao.msgs.UserInfo;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;


@BindLayout(R.layout.activity_chat_yuehoujifeng_img)
public class YueHouJiFengImgActivity extends AppBaseActivity {
    @BindView(R.id.img_yuehoujifeng_content)
    PhotoView imgYuehoujifeng;
    @BindView(R.id.img_yuehoujifeng_time)
    TextView imgYuehoujifengTime;

    @BindView(R.id.img_loading)
    ProgressBar img_loading;


    @BindExtra
    ChatMessageBase chatMessage;
    @BindExtra
    boolean isGroup;
    @BindExtra
    String strGroupID;
    @BindExtra
    String strUserID;
    @BindExtra
    String strUserDomainCode;
    @BindExtra
    String strGroupDomain;
    @BindExtra
    ArrayList<UserInfo> usersTrans;
    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();
    File fC;

    private boolean isLoadOK = false;

    int time = 10;

    private RequestOptions mOptions = new RequestOptions()
            .fitCenter()
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(R.color.transparent)
            .error(R.drawable.icon_image_error);

    private Disposable mDisposable;
    private File img;
    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        fC = new File(getExternalFilesDir(null) + File.separator + "Vim/chat/");
        if (!fC.exists()) {
            fC.mkdirs();
        }

        if (isGroup) {
            for (UserInfo temp : usersTrans) {
                if (!temp.strUserID.equals(AppAuth.get().getUserID()) && users.isEmpty()) {
                    SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                    info.strUserDomainCode = temp.strUserDomainCode;
                    info.strUserID = temp.strUserID;
                    users.add(info);
                }
            }
        } else {
            SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
            info.strUserDomainCode = strUserDomainCode;
            info.strUserID = strUserID;
            users.add(info);
        }

        PhotoViewAttacher attacher = new PhotoViewAttacher(imgYuehoujifeng);
        if (null != chatMessage) {
            if (chatMessage.bEncrypt == 1) {
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    String fileLocal = null;
                    try {
                        fileLocal = fC + chatMessage.fileUrl.substring(chatMessage.fileUrl.lastIndexOf("/"));
                    } catch (Exception e) {
                        fileLocal = "";
                    }
                    final File ffLocal = new File(fileLocal);
                    if (null != chatMessage && ffLocal.exists()) {
                        chatMessage.localFilePath = ffLocal.getAbsolutePath();
                        unEncryptImage(imgYuehoujifeng, chatMessage);
                    } else {
                        chatMessage.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                        chatMessage.localFilePath = "";
//                updateDownloadState(data);
                        String finalFileLocal = fileLocal;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + chatMessage.fileUrl, finalFileLocal, chatMessage.type)) {
                                    new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            chatMessage.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                            chatMessage.localFilePath = ffLocal.getAbsolutePath();
//                                    updateDownloadState(data);
                                            unEncryptImage(imgYuehoujifeng, chatMessage);
                                        }
                                    }, "loadImageSuccess");
                                }
                            }
                        }).start();
                    }
                } else {
                    if (nEncryptIMEnable) {
                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                        finish();
                        return;
                    }
                    showImage();
                }
            } else {
                showImage();
            }
        }
    }

    private void showImage() {
        img_loading.setVisibility(View.VISIBLE);
        if (chatMessage.fileUrl.endsWith(".gif")) {
            Glide.with(YueHouJiFengImgActivity.this).load(AppDatas.Constants().getAddressWithoutPort() + chatMessage.fileUrl).listener(requestListener2).into(imgYuehoujifeng);
        } else {
            Glide.with(this)
                    .load(AppDatas.Constants().getAddressWithoutPort() + chatMessage.fileUrl)
                    .apply(mOptions)
                    .listener(requestListener)
                    .into(imgYuehoujifeng);
        }

        imgYuehoujifengTime.setText(time + "''");
    }

    private void unEncryptImage(ImageView view, ChatMessageBase data) {
        File file = new File(EncryptUtil.getNewFile(data.localFilePath));
        if (file.exists()) {
            try {
                img = file;
                if (file.getAbsolutePath().endsWith(".gif")) {
                    Glide.with(YueHouJiFengImgActivity.this).load(file).listener(requestListener2).into(view);
                } else {
                    Glide.with(YueHouJiFengImgActivity.this)
                            .load(file)
                            .apply(mOptions)
                            .listener(requestListener)
                            .into(view);
                }

            } catch (Exception e) {
            }
        } else {
            EncryptUtil.encryptFile(data.localFilePath, file.getAbsolutePath(),
                    false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                    isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            try {
                                img = new File(resp.m_strData);
                                if (resp.m_strData.endsWith(".gif")) {
                                    Glide.with(YueHouJiFengImgActivity.this).load(new File(resp.m_strData)).listener(requestListener2).into(view);
                                } else {
                                    Glide.with(YueHouJiFengImgActivity.this)
                                            .load(new File(resp.m_strData))
                                            .apply(mOptions)
                                            .listener(requestListener)
                                            .into(view);
                                }

                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("文件解密失败");
                        }
                    }
            );
        }
    }

    private boolean downloadFileByUrl(final String urlLoadPath, final String fileName, int type) {
        Log.i("MCApp_tt", "urlLoadPath: " + urlLoadPath + "  fileName:" + fileName);
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection httpURLConnection = null;

        //创建 这个文件名 命名的 file 对象
        File file = new File(fileName);
        // Log.i(TAG,"file: " + file);
        if (!file.exists()) {     //倘若没有这个文件
            // Log.i(TAG,"创建文件");
            //file.createNewFile();  //创建这个文件
        } else {
            //文件已存在，不重新下载
            return true;
        }
        try {

            String nFileName = urlLoadPath.substring(urlLoadPath.lastIndexOf("/") + 1);
            String urlHost = urlLoadPath.substring(0, urlLoadPath.lastIndexOf("/") + 1);
            URL url = new URL(urlHost + URLEncoder.encode(nFileName, "utf-8"));
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                //网络连接成功
                //根据响应获取文件大小
                int fileSize = httpURLConnection.getContentLength();
                // Log.i(TAG,"文件大小： " + fileSize);
                inputStream = httpURLConnection.getInputStream();
                fileOutputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int tem = 0;
                while ((tem = inputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, tem);
                }

            } else {
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void coutTime() {
        mDisposable = Flowable.intervalRange(0, time + 1, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        imgYuehoujifengTime.setText(String.valueOf(time - aLong) + "''");
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        imgYuehoujifengTime.setText("0''");
                        finish();
                    }
                })
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        if(img != null && img.exists()) {
            img.delete();
        }
        if (isLoadOK) {
            showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_tip));
            MessageEvent nMessageEvent = new MessageEvent(AppUtils.EVENT_MESSAGE_YUEHOUJIFENG);
            nMessageEvent.obj1 = chatMessage;
            EventBus.getDefault().post(nMessageEvent);
        } else {
            showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_tip_error));

        }
        super.onDestroy();

        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private RequestListener requestListener2 = new RequestListener() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            isLoadOK = false;
            img_loading.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
            img_loading.setVisibility(View.GONE);
            isLoadOK = true;
            coutTime();
            if (resource instanceof GifDrawable) {
                //加载一次
                ((GifDrawable) resource).setLoopCount(100);
            }
            return false;
        }
    };
    RequestListener requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            isLoadOK = false;
            img_loading.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            img_loading.setVisibility(View.GONE);
            isLoadOK = true;
            coutTime();
            return false;
        }
    };
}
