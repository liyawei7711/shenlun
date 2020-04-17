package huaiye.com.vim.ui.meet.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.view.TextureView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.capture.Capture;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.sdpmsgs.video.CStartMobileCaptureRsp;
import com.huaiye.sdk.sdpmsgs.video.CStopMobileCaptureRsp;

import java.io.File;
import java.io.IOException;

import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import ttyy.com.jinnetwork.core.work.HTTPResponse;


public class VideoRecordPresenterImpl implements VideoRecordPresenterHelper.Presenter {

    private VideoRecordPresenterHelper.View view;
    private Context context;
    private boolean isForeground =false;

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    private String mFilePath = null;


    public VideoRecordPresenterImpl(VideoRecordPresenterHelper.View view, Context context) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void startRecordVideo(final TextureView textureView, final boolean localRecord) {
        final Capture.Params params;
        HYClient.getSdkOptions().Capture().setCaptureOfflineMode(true);

        if(localRecord){
            setDefaultFilePath();
            params = Capture.Params.get()
                    .setEnableServerRecord(true)
                    .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                    .setRecordPath(mFilePath)
                    .setCameraIndex(isForeground?HYCapture.Camera.Foreground:HYCapture.Camera.Background)
                    .setPreview(textureView);
        }else{
            params = Capture.Params.get()
                    .setEnableServerRecord(true)
                    .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                    .setCameraIndex(isForeground?HYCapture.Camera.Foreground:HYCapture.Camera.Background)
                    .setPreview(textureView);
        }

        if(HYClient.getHYCapture().isCapturing()){
            HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                @Override
                public void onSuccess(CStopMobileCaptureRsp resp) {
                    // 停止采集成功
                    startCapture(params,textureView,localRecord);
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    // 停止采集失败
                }

            });
        }else{
            startCapture(params,textureView,localRecord);
        }
    }

    private void startCapture(Capture.Params params, TextureView textureView, boolean localRecord) {
        HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
        HYClient.getHYCapture().startCapture(params,
                new Capture.Callback() {
                    @Override
                    public void onRepeatCapture() {
                        Logger.debug("onRepeatCapture");
                    }

                    @Override
                    public void onSuccess(CStartMobileCaptureRsp resp) {
                        Logger.debug("onSuccess");
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        Logger.debug("onError");
                    }

                    @Override
                    public void onCaptureStatusChanged(SdpMessageBase msg) {
                        Logger.debug("onCaptureStatusChanged");
                    }
                });
    }

    private void setDefaultFilePath() {
        File fileDir = new File(context.getExternalFilesDir(null) + File.separator + "Vim");
        File file = new File(context.getExternalFilesDir(null) + File.separator + "Vim", System.currentTimeMillis() + ".rmvbsf");

        try {
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFilePath = file.getAbsolutePath();
    }

    @Override
    public void stopRecordVideo() {

        if(!HYClient.getHYCapture().isCapturing()){
            view.recordOver(mFilePath);
            return;
        }
        HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
            @Override
            public void onSuccess(CStopMobileCaptureRsp resp) {
                view.recordOver(mFilePath);
                // 停止采集成功
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                // 停止采集失败
            }

        });

        HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
    }

    @Override
    public void playLocaVideoRepeate(final TextureView ttvCapture){
        if(!TextUtils.isEmpty(mFilePath)){
            HYClient.getHYPlayer().startPlay(Player.Params.TypeVideoOfflineRecord()
                    .setResourcePath(mFilePath)
                    .setPreview(ttvCapture)
                    .setMixCallback(new VideoCallbackWrapper() {
                        @Override
                        public void onSuccess(VideoParams param) {
                            super.onSuccess(param);
                        }

                        @Override
                        public void onGetVideoRange(VideoParams param, int start, int end) {
                            super.onGetVideoRange(param, start, end);
                        }

                        @Override
                        public void onVideoProgressChanged(VideoParams param, HYPlayer.ProgressType type, int current, int total) {
                            super.onVideoProgressChanged(param, type, current, total);
                        }

                        @Override
                        public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                            super.onVideoStatusChanged(param, msg);
                            if (msg instanceof SdkMsgNotifyPlayStatus){
                                SdkMsgNotifyPlayStatus status = (SdkMsgNotifyPlayStatus) msg;
                                if (status.isStopped()) {
                                    if (!status.isOperationFromUser()) {
                                        playLocaVideoRepeate(ttvCapture);
                                    }

                                }
                            }


                        }

                        @Override
                        public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                            super.onError(param, errorInfo);
                            AppBaseActivity.showToast(errorInfo+"");
                        }
                    }));
        }

    }

    @Override
    public void stopPlayLocaVideoRepeate(TextureView textureView) {
        HYClient.getHYPlayer().stopPlayEx(null, textureView);
    }


    @Override
    public void changeCamera() {
        HYClient.getHYCapture().toggleInnerCamera();
        isForeground = !isForeground;
    }


    @Override
    public void uploadFile(final ModelCallback<Upload> callback, final File file) {
        ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
            @Override
            public void onSuccess(final Upload upload) {
                callback.onSuccess(upload);

            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                callback.onFailure(httpResponse);
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                callback.onFinish(httpResponse);

            }
        }, file, AppDatas.Constants().getFileUploadUri());
    }

}
