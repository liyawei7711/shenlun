package huaiye.com.vim.ui.Capture;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.views.WindowManagerUtils;
import huaiye.com.vim.dao.msgs.CaptureMessage;
import huaiye.com.vim.dao.msgs.StopCaptureMessage;
import huaiye.com.vim.ui.home.view.CaptureViewLayout;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

@BindLayout(R.layout.activity_capture)
public class CaptureGuanMoOrPushActivity extends AppBaseActivity {


    @BindView(R.id.capture_view)
    CaptureViewLayout captureView;

    CaptureMessage captureMessage;
    StopCaptureMessage stopCaptureMessage;
    ArrayList<SendUserBean> userList = new ArrayList<>();
    private SdpMessageCmStartSessionRsp sessionRsp;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }


    @Override
    public void doInitDelay() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        new RxUtils().doDelay(2000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                initData();
            }
        }, "onNewIntent");
    }

    private void initData() {
        if (null != getIntent() && null != getIntent().getExtras()) {
            captureMessage = (CaptureMessage) getIntent().getSerializableExtra("captureMessage");
            stopCaptureMessage = (StopCaptureMessage) getIntent().getSerializableExtra("stopCaptureMessage");
            userList = (ArrayList<SendUserBean>) getIntent().getSerializableExtra("userList");
        }
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            if (userList != null) {
                if (userList.isEmpty()) {
                    userList.add(new SendUserBean(captureMessage.fromUserId, captureMessage.fromUserDomain, captureMessage.fromUserName));
                }
            } else {
                userList = new ArrayList<>();
                userList.add(new SendUserBean(captureMessage.fromUserId, captureMessage.fromUserDomain, captureMessage.fromUserName));
            }

            EncryptUtil.startEncrypt(captureMessage == null, userList.get(0).strUserID, userList.get(0).strUserDomainCode,
                    "", "", new SdkCallback<SdpMessageCmStartSessionRsp> () {
                        @Override
                        public void onSuccess(SdpMessageCmStartSessionRsp sessionRsp) {
                            CaptureGuanMoOrPushActivity.this.sessionRsp = sessionRsp;
                            captureView.setSession(sessionRsp);
                            startCapturePushVideo(captureMessage, stopCaptureMessage, userList);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("开启采集推送失败");
                            finish();
                        }
                    });
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            startCapturePushVideo(captureMessage, stopCaptureMessage, userList);
        }
    }

    private void startCapturePushVideo(CaptureMessage captureMessage, StopCaptureMessage stopCaptureMessage, ArrayList<SendUserBean> userList) {

        captureView.setiCaptureStateChangeListener(new CaptureViewLayout.ICaptureStateChangeListener() {

            @Override
            public void onOpen() {

            }

            @Override
            public void onClose() {
                finish();
            }

            @Override
            public void toggleBigSmall() {
                finish();
                if (null != captureMessage) {
                    WindowManagerUtils.createOriginalView(captureMessage, WindowManagerUtils.CaptureModel.CAPTURE_GUANMO_MODEL);
                } else if (null != userList) {
                    WindowManagerUtils.createOriginalView(userList, WindowManagerUtils.CaptureModel.CAPTURE_PUSH_MODEL);
                }


            }
        });
        if (null != captureMessage) {
            captureView.startCaptureFromUser(captureMessage);
        } else if (null != stopCaptureMessage) {
            captureView.stopCaptureFromUser(stopCaptureMessage);
        } else if (null != userList) {
            captureView.startCapturePushVideo(userList);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != captureMessage) {
            WindowManagerUtils.createOriginalView(captureMessage, WindowManagerUtils.CaptureModel.CAPTURE_GUANMO_MODEL);
        } else if (null != userList) {
            WindowManagerUtils.createOriginalView(userList, WindowManagerUtils.CaptureModel.CAPTURE_PUSH_MODEL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.isCaptureLayoutShowing = true;
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        if (null != messageEvent) {
            switch (messageEvent.what) {
                case AppUtils.EVENT_MESSAGE_CLOSE_ACPTURE:
                    finish();

                    break;
                case AppUtils.EVENT_MESSAGE_KEY_HOME:
                    if (null != captureMessage) {
                        WindowManagerUtils.createOriginalView(captureMessage, WindowManagerUtils.CaptureModel.CAPTURE_GUANMO_MODEL);
                    } else if (null != userList) {
                        WindowManagerUtils.createOriginalView(userList, WindowManagerUtils.CaptureModel.CAPTURE_PUSH_MODEL);
                    }
                    finish();
                    break;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        captureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureView.onPause();
    }

}
