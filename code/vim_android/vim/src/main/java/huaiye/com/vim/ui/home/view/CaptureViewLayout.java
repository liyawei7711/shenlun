package huaiye.com.vim.ui.home.view;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.capture.Capture;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.huaiye.sdk.sdpmsgs.video.CStartMobileCaptureRsp;

import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.dao.MediaFileDao;
import huaiye.com.vim.dao.msgs.CaptureMessage;
import huaiye.com.vim.dao.msgs.ChatUtil;
import huaiye.com.vim.dao.msgs.StopCaptureMessage;

import static huaiye.com.vim.common.AppBaseActivity.showToast;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD1080P;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD720P;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_VGA;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_camera;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_capture;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class CaptureViewLayout extends FrameLayout implements View.OnClickListener {

    public View iv_camera;
    ImageView iv_shanguang;
    View iv_change;
    View iv_waizhi;
    View iv_close;
    View iv_suofang;
    View view_cover;
    View fl_root;
    TextureView ttv_capture;


    private final int CAPTURE_STATUS_NONE = 0;
    private final int CAPTURE_STATUS_STARTING = 1;
    private final int CAPTURE_STATUS_CAPTURING = 2;

    int captureStatus;
    boolean isPaused;
    boolean isFromGuanMo;//是否是观摩启动
    private SdpMessageCmStartSessionRsp sessionRsp;
    private CaptureMessage bean;

    public boolean isCapture() {
        return captureStatus > CAPTURE_STATUS_NONE;
    }

    public CaptureViewLayout(@NonNull Context context) {
        this(context, null);
    }

    public CaptureViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CaptureViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.main_capture_layout, null);

        iv_camera = view.findViewById(R.id.iv_camera);
        iv_shanguang = view.findViewById(R.id.iv_shanguang);
        iv_change = view.findViewById(R.id.iv_change);
        iv_waizhi = view.findViewById(R.id.iv_waizhi);
        ttv_capture = view.findViewById(R.id.ttv_capture);
        view_cover = view.findViewById(R.id.view_cover);
        iv_close = view.findViewById(R.id.iv_close);
        fl_root = view.findViewById(R.id.fl_root);
        iv_suofang = view.findViewById(R.id.iv_suofang);

        addView(view);

        iv_close.setOnClickListener(this);
        iv_camera.setOnClickListener(this);
        iv_shanguang.setOnClickListener(this);
        iv_change.setOnClickListener(this);
        iv_waizhi.setOnClickListener(this);
        iv_suofang.setOnClickListener(this);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Point point = AppUtils.getPoint(ttv_capture);
        FrameLayout.LayoutParams layoutParams = (LayoutParams) ttv_capture.getLayoutParams();
        layoutParams.width = point.x;
        layoutParams.height = point.y;
        setLayoutParamsNew(layoutParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_camera:
                if (HYClient.getMemoryChecker().checkEnough()) {
                    HYClient.getHYCapture().snapShotCapture(MediaFileDao.get()
                            .getImgRecordFile(), new SdkCallback<String>() {
                        @Override
                        public void onSuccess(String resp) {
                            if (getVisibility() != GONE) {
                                showToast(AppUtils.getString(R.string.jieping_success));
                            }
                        }

                        @Override
                        public void onError(ErrorInfo error) {

                        }
                    });
                } else {
                    if (getVisibility() != GONE) {
                        showToast(AppUtils.getString(R.string.local_size_max));
                    }
                }
                break;
            case R.id.iv_shanguang:
                if (HYClient.getHYCapture().getCurrentCameraIndex() == HYCapture.Camera.Foreground) {
                    showToast(AppUtils.getString(R.string.cameraindex_notice));
                    return;
                }
                HYClient.getHYCapture().setTorchOn(!HYClient.getHYCapture().isTorchOn());
                if (HYClient.getHYCapture().isTorchOn()) {
                    iv_shanguang.setImageResource(R.drawable.btn_shanguangdeng_press);
                } else {
                    iv_shanguang.setImageResource(R.drawable.btn_shanguangdeng);
                }
                break;
            case R.id.iv_change:
                HYClient.getHYCapture().toggleInnerCamera();
                break;
            case R.id.iv_waizhi:
                HYClient.getHYCapture().requestUsbCamera();

                if (HYClient.getHYCapture().getCurrentCameraIndex() == HYCapture.Camera.USB) {

                } else {
                    showToast(AppUtils.getString(R.string.out_camera));
                }
                break;
            case R.id.iv_close:
                stopCapture(true);
                break;
            case R.id.iv_suofang:
                toggleBigSmall();
                break;
        }
    }

    /**
     * Add a listener that will be called when the bounds of the view change due to
     * layout processing.
     */
    public void addOnLayoutChangeListenerNew(OnLayoutChangeListener listener1, TextureView.SurfaceTextureListener listener2) {
        ttv_capture.addOnLayoutChangeListener(listener1);
        ttv_capture.setSurfaceTextureListener(listener2);
    }

    public void onResume() {
        Logger.debug("CaptureiewLayout  onResume() " + isCapturing());
        isPaused = false;
        if (isCapturing())
            HYClient.getHYCapture().setPreviewWindow(ttv_capture);
    }

    public void onPause() {
        isPaused = true;
        if (isCapturing())
            HYClient.getHYCapture().setPreviewWindow(null);
    }


    public void onDestroy(boolean needCallBack) {
        if (isCapturing() || isCapturStarting()) {
            stopCapture(needCallBack);
        }
    }

    public void setLayoutParamsNew(ViewGroup.LayoutParams params) {
        if (params == null) {
            throw new NullPointerException("Layout parameters cannot be null");
        }
        ttv_capture.setLayoutParams(params);
    }



    public void stopCapture(boolean needCallBack) {
        if (isCapturing() || isCapturStarting()) {
            if (sessionRsp != null) {
                EncryptUtil.endEncrypt(sessionRsp.m_nCallId);
            }
            HYClient.getHYCapture().stopCapture(null);
            HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
        }
        view_cover.setVisibility(VISIBLE);
        VIMApp.userId.clear();
        VIMApp.pendingMsg.clear();
        if (getVisibility() != GONE) {
            showToast(AppUtils.getString(R.string.stop_capture_success));
        }
        isFromGuanMo = false;
        captureStatus = CAPTURE_STATUS_NONE;
        if (iCaptureStateChangeListener != null && needCallBack) {
            iCaptureStateChangeListener.onClose();
        }


    }

    /**
     * 开启观摩
     *
     * @param bean
     */
    public void startCaptureFromUser(CaptureMessage bean) {
        if (!VIMApp.userId.contains(bean.fromUserId)) {
            VIMApp.userId.add(bean.fromUserId);
        }
        if (isCapturStarting()) {
            VIMApp.pendingMsg.add(bean);
            Logger.debug("CaptureViewLayout startCaptureFromUser isCapturStarting");
            //do nothing
            return;
        }
        if (isCapturing()) {
            Logger.debug("CaptureViewLayout startCaptureFromUser");
            sendPlayerMessage(bean);
        } else {
            this.bean = bean;
            isFromGuanMo = true;
            toggleCapture(bean);
        }
    }

    /**
     * 关闭观摩
     *
     * @param bean
     */
    public void stopCaptureFromUser(StopCaptureMessage bean) {
        if (VIMApp.userId.contains(bean.fromUserId)) {
            VIMApp.userId.remove(bean.fromUserId);
        }
        if (VIMApp.userId.size() <= 0) {
            if (isFromGuanMo) {
                stopCapture(true);
            }
        }

    }

    private void sendPlayerMessage(CaptureMessage user) {
        if (user == null) {
            return;
        }
        com.huaiye.sdk.logger.Logger.debug("CaptureViewLayout sendPlayerMessage ");
        ChatUtil.get().rspGuanMo(user.fromUserId, user.fromUserDomain, user.fromUserName, user.sessionID);
    }

    public void toggleCapture(final CaptureMessage users) {
        AppUtils.isCaptureLayoutShowing = true;
        captureStatus = CAPTURE_STATUS_STARTING;
        Capture.Params params = Capture.Params.get()
                .setEnableServerRecord(true)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setPreview(ttv_capture);
        if(sessionRsp != null) {
            params.setCallId(sessionRsp.m_nCallId);
        }
        if (isPaused) {
            params.setPreview(null);
        }
        HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
        HYClient.getHYCapture().startCapture(params,
                new Capture.Callback() {
                    @Override
                    public void onRepeatCapture() {
                        captureStatus = CAPTURE_STATUS_CAPTURING;
                        view_cover.setVisibility(GONE);
                        Logger.debug("CaptureViewLayout onRepeatCapture");
                        sendPlayerMessage(users);
                    }

                    @Override
                    public void onSuccess(CStartMobileCaptureRsp resp) {
                        view_cover.setVisibility(GONE);
                        captureStatus = CAPTURE_STATUS_CAPTURING;
                        if (resp != null) {
                            Logger.debug("CaptureViewLayout onSuccess " + resp.toString());
                        } else {
                            Logger.debug("CaptureViewLayout onSuccess resp null ");
                        }
                        if (getVisibility() != GONE) {
                            showToast(AppUtils.getString(R.string.capture_success));
                        }
                        sendPlayerMessage(users);
                        if (VIMApp.pendingMsg.size() > 0) {
                            for (int i = 0; i < VIMApp.pendingMsg.size(); i++) {
                                sendPlayerMessage(VIMApp.pendingMsg.get(i));
                            }
                            VIMApp.pendingMsg.clear();
                        }
                        if (iCaptureStateChangeListener != null) {
                            iCaptureStateChangeListener.onOpen();
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (getVisibility() != GONE) {
                            showToast(AppUtils.getString(R.string.capture_false));
                            onDestroy(true);
                        }
                    }

                    @Override
                    public void onCaptureStatusChanged(SdpMessageBase msg) {
                    }
                });

    }

    /**
     * 开启推送采集
     *
     * @param contacts
     */
    public void startCapturePushVideo(ArrayList<SendUserBean> contacts) {
        if (isCapturing()) {
            for (SendUserBean sendUserBean : contacts) {
                if (!VIMApp.userId.contains(sendUserBean.strUserID)) {
                    VIMApp.userId.add(sendUserBean.strUserID);
                }
            }
            if(!isFromGuanMo) {
                ChatUtil.get().broadcastPushVideo(contacts);
            } else {
                sendPlayerMessage(bean);
            }
            return;
        }
        toggleCapture(contacts);
    }


    public void toggleCapture(final ArrayList<SendUserBean> contacts) {
        Capture.Params params = Capture.Params.get()
                .setEnableServerRecord(true)
                .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                .setPreview(ttv_capture);
        if(sessionRsp != null) {
            params.setCallId(sessionRsp.m_nCallId);
        }
        if (isPaused) {
            HYClient.getHYCapture().setPreviewWindow(null);
        }
        HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
        HYClient.getHYCapture().startCapture(params,
                new Capture.Callback() {
                    @Override
                    public void onRepeatCapture() {
                        captureStatus = CAPTURE_STATUS_CAPTURING;

                        view_cover.setVisibility(GONE);
                        Logger.debug("CaptureViewLayout onRepeatCapture");

                    }

                    @Override
                    public void onSuccess(CStartMobileCaptureRsp resp) {
                        captureStatus = CAPTURE_STATUS_CAPTURING;

                        view_cover.setVisibility(GONE);
                        if (resp != null) {
                            Logger.debug("CaptureViewLayout onSuccess " + resp.toString());
                        } else {
                            Logger.debug("CaptureViewLayout onSuccess resp null ");
                        }
                        if (getVisibility() != GONE) {
                            showToast(AppUtils.getString(R.string.capture_success));
                        }

                        if (iCaptureStateChangeListener != null) {
                            iCaptureStateChangeListener.onOpen();
                        }
                        if(!isFromGuanMo) {
                            ChatUtil.get().broadcastPushVideo(contacts);
                        } else {
                            sendPlayerMessage(bean);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (getVisibility() != GONE) {
                            showToast(AppUtils.getString(R.string.capture_false));
                            onDestroy(true);
                        }
                    }

                    @Override
                    public void onCaptureStatusChanged(SdpMessageBase msg) {
                    }
                });

    }

    private boolean isCapturing() {
        return captureStatus == CAPTURE_STATUS_CAPTURING;
    }

    private boolean isCapturStarting() {
        return captureStatus == CAPTURE_STATUS_STARTING;
    }

    public void toggleBigSmall() {
        if (null != iCaptureStateChangeListener) {
            iCaptureStateChangeListener.toggleBigSmall();
        }
    }

    private int calcHeightHeight() {
        int height = AppUtils.getSize(332);
        switch (SP.getParam(STRING_KEY_capture, "").toString()) {
            case STRING_KEY_VGA:
                height = AppUtils.getSize(332);
                break;
            case STRING_KEY_HD720P:
                height = AppUtils.getSize(415);
                break;
            case STRING_KEY_HD1080P:
                height = AppUtils.getSize(415);
                break;
        }
        return height;
    }


    ICaptureStateChangeListener iCaptureStateChangeListener;

    public void setiCaptureStateChangeListener(ICaptureStateChangeListener iCaptureStateChangeListener) {
        this.iCaptureStateChangeListener = iCaptureStateChangeListener;
    }

    public void setSession(SdpMessageCmStartSessionRsp sessionRsp) {
        this.sessionRsp = sessionRsp;
    }

    public interface ICaptureStateChangeListener {
        void onOpen();

        void onClose();

        void toggleBigSmall();
    }


}
