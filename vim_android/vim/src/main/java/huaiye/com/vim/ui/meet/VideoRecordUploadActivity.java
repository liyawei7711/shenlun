package huaiye.com.vim.ui.meet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.helper.ChatLocalPathHelper;
import huaiye.com.vim.common.views.CircleButtonView;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.ui.meet.presenter.VideoRecordPresenterHelper;
import huaiye.com.vim.ui.meet.presenter.VideoRecordPresenterImpl;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

@BindLayout(R.layout.activity_video_record_layout)
public class VideoRecordUploadActivity extends AppBaseActivity implements VideoRecordPresenterHelper.View {

    @BindView(R.id.ttv_capture)
    TextureView ttvCapture;
    @BindView(R.id.video_record_root)
    FrameLayout videoRecordRoot;
    @BindView(R.id.video_record_tip)
    TextView videoRecordTip;
    @BindView(R.id.video_record_btn)
    CircleButtonView videoRecordBtn;
    @BindView(R.id.video_record_bottom)
    FrameLayout videoRecordBottom;
    @BindView(R.id.chat_video_record_back)
    ImageView chatVideoRecordBack;
    @BindView(R.id.chat_video_record_change)
    ImageView chatVideoRecordChange;
    @BindView(R.id.video_recording_layout)
    LinearLayout videoRecordingLayout;
    @BindView(R.id.chat_record_video_over_back)
    ImageView chatRecordVideoOverBack;
    @BindView(R.id.chat_record_video_over_send)
    ImageView chatRecordVideoOverSend;
    @BindView(R.id.video_record_over_layout)
    FrameLayout videoRecordOverLayout;

    @BindExtra
    public String mMeetID;
    @BindExtra
    public String nMeetDomain;
    @BindExtra
    User nUser;
    @BindExtra
    boolean isGroup;
    @BindExtra
    ArrayList<SendUserBean> mMessageUsersDate;

    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();

    private VideoRecordPresenterImpl presenter;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        presenter = new VideoRecordPresenterImpl(this, this);
        presenter.startRecordVideo(ttvCapture, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HYClient.getHYCapture().setPreviewWindow(ttvCapture);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HYClient.getHYCapture().setPreviewWindow(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            if (isGroup) {
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

        videoRecordBtn.setOnClickListener(new CircleButtonView.OnClickListener() {
            @Override
            public void onClick() {
                Logger.debug("onClick");

            }
        });
        videoRecordBtn.setOnLongClickListener(new CircleButtonView.OnLongClickListener() {
            @Override
            public void onLongClick() {
                presenter.startRecordVideo(ttvCapture, true);
            }

            @Override
            public void onNoMinRecord(int currentTime) {

            }

            @Override
            public void onRecordFinishedListener() {
                presenter.stopRecordVideo();
            }
        });
    }

    @Override
    public void recordOver(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }
        chatVideoRecordChange.setVisibility(View.GONE);
        videoRecordingLayout.setVisibility(View.GONE);
        videoRecordOverLayout.setVisibility(View.VISIBLE);
        presenter.playLocaVideoRepeate(ttvCapture);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.stopPlayLocaVideoRepeate(ttvCapture);
    }

    @OnClick({R.id.chat_video_record_back})
    void back() {
        onBackPressed();
    }

    @OnClick(R.id.chat_record_video_over_back)
    void back2Record() {
        chatVideoRecordChange.setVisibility(View.VISIBLE);
        presenter.stopPlayLocaVideoRepeate(ttvCapture);
        presenter.startRecordVideo(ttvCapture, false);
        videoRecordingLayout.setVisibility(View.VISIBLE);
        videoRecordOverLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.chat_video_record_change)
    void changeVideoRecordCamera() {
        presenter.changeCamera();
    }

    @OnClick(R.id.chat_record_video_over_send)
    void uploadVideo() {
        presenter.stopPlayLocaVideoRepeate(ttvCapture);
        mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();
        final File file = new File(presenter.getFilePath());
        if(!file.exists()) {
            showToast("视频录制失败");
            return;
        }
        if(file.length() <= 0) {
            showToast("视频录制失败");
            return;
        }
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptFile(file.getPath(), EncryptUtil.getNewFile(file.getPath()),
                    true, isGroup, isGroup ? mMeetID + "" : "", isGroup ? nMeetDomain : "",
                    isGroup ? "" : nUser.strUserID, isGroup ? "" : nUser.strDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            upFile(file, new File(resp.m_strData));
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("文件加密失败");
                            mZeusLoadView.dismiss();
                        }
                    }
            );
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            upFile(file, file);
        }

    }

    private void upFile(File fileOld, File file) {
        presenter.uploadFile(new ModelCallback<Upload>() {
            @Override
            public void onSuccess(final Upload upload) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mZeusLoadView != null && mZeusLoadView.isShowing())
                            mZeusLoadView.dismiss();

                        if (upload.file1_name == null) {
                            showToast(AppUtils.getString(R.string.file_upload_false));
                            return;
                        }
                        presenter.setFilePath(null);
                        ChatLocalPathHelper.getInstance().cacheChatLoaclPath(upload.file1_name, file.getPath());
                        Intent intent = new Intent();
                        intent.putExtra("updata", upload);
                        intent.putExtra("fileSize", fileOld.length());
                        intent.putExtra("fileName", fileOld.getAbsolutePath().substring(fileOld.getAbsolutePath().lastIndexOf("/") + 1));
                        int recordTime = JniIntf.GetRecordFileDuration(fileOld.getPath());
                        intent.putExtra("recordTime", recordTime);
                        setResult(Activity.RESULT_OK, intent);
                        ChatLocalPathHelper.getInstance().cacheChatLoaclPath(upload.file1_name, file.getPath());
                        VideoRecordUploadActivity.this.finish();
                    }
                });


            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(AppUtils.getString(R.string.file_upload_false));
                    }
                });
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mZeusLoadView != null && mZeusLoadView.isShowing())
                            mZeusLoadView.dismiss();
                    }
                });

            }
        }, file);
    }


}
