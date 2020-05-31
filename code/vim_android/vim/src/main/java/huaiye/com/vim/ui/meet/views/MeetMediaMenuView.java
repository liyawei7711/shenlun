package huaiye.com.vim.ui.meet.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vim.R;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: MeetMediaMenuView
 */
@BindLayout(R.layout.view_meet_media)
public class MeetMediaMenuView extends RelativeLayout {
    boolean isVoiceOpened = true;

    @BindView(R.id.menu_iv_voice_layer)
    View menu_iv_voice_layer;
    @BindView(R.id.menu_iv_mic_layer)
    View menu_iv_mic_layer;
    @BindView(R.id.menu_iv_mic)
    TextView menu_iv_mic;
    @BindView(R.id.menu_iv_camera_video)
    View menu_iv_camera_video;

    @BindView(R.id.tv_voice)
    TextView tv_voice_title;
    @BindView(R.id.tv_video_title)
    TextView tv_video_title;
    @BindView(R.id.ll_control)
    View ll_control;
    @BindView(R.id.ll_control_txt)
    TextView ll_control_txt;
    @BindView(R.id.ll_more)
    LinearLayout ll_more;
    @BindView(R.id.ll_more_txt)
    TextView ll_more_txt;

    /*@BindView(R.id.menu_iv_camera_layer)
    View menu_iv_camera_layer;*/

    /*@BindView(R.id.menu_iv_video)
    ImageView menu_iv_video;
    @BindView(R.id.menu_iv_hand_up)
    View menu_iv_hand_up;
    @BindView(R.id.menu_iv_layout)
    View menu_iv_layout;
    @BindView(R.id.view_red)
    View view_red;
    @BindView(R.id.menu_iv_change)
    View menu_iv_change;

    @BindView(R.id.ll_more_txt)
    TextView ll_more_txt;*/
    //    @BindView(R.id.tv_white_board)
//    TextView tv_white_board;
    /*@BindView(R.id.iv_white_img)
    ImageView iv_white_img;*/

    boolean isVideoOpened = true;
    boolean isSharePrepare = true;
    boolean isMorePrepare = true;

    /*@BindView(R.id.menu_iv_mic)
    ImageView menu_iv_mic;
    @BindView(R.id.tv_video_title)
    TextView tv_video_title;
    @BindView(R.id.menu_iv_invite_layer)
    LinearLayout menu_iv_invite_layer;
    @BindView(R.id.menu_iv_startrecord)
    LinearLayout menu_iv_startrecord;*/

    Callback mCallback;
    boolean isCloseVideo;
    boolean isAudioOn;
    private boolean mIsJingYan;

    public MeetMediaMenuView(Context context) {
        this(context, null);
    }

    public MeetMediaMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetMediaMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injectors.get().injectView(this);
    }

    @OnClick({
//            R.id.menu_iv_exit_layer,
//            R.id.menu_iv_members_layer,
//            R.id.menu_iv_invite_layer,
//            R.id.menu_iv_camera_layer,
            R.id.menu_iv_voice_layer,
            R.id.menu_iv_camera_video,
//            R.id.menu_iv_hand_up,
            R.id.menu_iv_mic_layer,
            R.id.ll_control,
            R.id.ll_more,
            /*R.id.menu_iv_startrecord,
            R.id.menu_iv_change,*/
//            R.id.menu_iv_layout
    })
    void onViewClicked(View v) {
        switch (v.getId()) {
            /*case R.id.menu_iv_exit_layer:
                if (mCallback != null) {
                    mCallback.onMeetExitClicked();
                }
                break;*/
            /*case R.id.menu_iv_members_layer:
                if (mCallback != null) {
                    mCallback.onMemberListClicked();
                }
                break;*/
            /*case R.id.menu_iv_invite_layer:
                if (mCallback != null) {
                    mCallback.onMeetInviteClicked();
                }
                break;*/
            /*case R.id.menu_iv_camera_layer:
                if (mCallback != null) {
                    mCallback.onInnerCameraClicked();
                }
                break;*/
            case R.id.menu_iv_voice_layer:
                toggleVoice();
                break;
            case R.id.menu_iv_mic_layer:
                if (mCallback != null) {
                    if(mIsJingYan){
                        //举手
                        mCallback.onHandUp();
                    } else {
                        mCallback.onCaptureVoiceClicked();
                    }
                }
                break;
            /*case R.id.menu_iv_hand_up:
                if (mCallback != null) {
                    mCallback.onHandUp();
                }
                break;*/
            /*case R.id.menu_iv_layout:
                if (mCallback != null) {
                    mCallback.showLayoutChange();
                }
                break;*/
            case R.id.menu_iv_camera_video:
                toggleVideo();
                break;
            case R.id.ll_control:
                if (mCallback != null) {
                    mCallback.onControlClick();
                }
                break;
            /*case R.id.ll_share:
                share();
                break;*/
            case R.id.ll_more:
                more();
                break;
            /*case R.id.menu_iv_startrecord:
                startRecord();
                break;
            case R.id.menu_iv_change:
                if (mCallback != null) {
                    mCallback.onYuLanClick();
                }
                break;*/
        }
    }

    /**
     * 开启录像
     */
    /*private void startRecord() {
        int nMeetID;
        String strMeetDomainCode;
        if (getContext() instanceof MeetActivity) {
            nMeetID = ((MeetActivity) getContext()).nMeetID;
            strMeetDomainCode = ((MeetActivity) getContext()).strMeetDomainCode;
        } else {
            nMeetID = ((MeetWatchActivity) getContext()).nMeetID;
            strMeetDomainCode = ((MeetWatchActivity) getContext()).strMeetDomainCode;
        }
        HYClient.getModule(ApiMeet.class).startMeetingRecord(SdkParamsCenter.Meet.StartMeetRecord()
                        .setnMeetingID(nMeetID)
                        .setStrMeetingDomainCode(strMeetDomainCode),
                new SdkCallback<CBeginMeetingRecordRsp>() {
                    @Override
                    public void onSuccess(CBeginMeetingRecordRsp cBeginMeetingRecordRsp) {
                        menu_iv_startrecord.setVisibility(GONE);
                        if (mCallback != null) {
                            mCallback.startRecordSuccess();
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(ErrorMsg.start_record_code));
                    }
                });
    }*/

    /**
     * 控制声音
     */
    private void toggleVoice() {
        isVoiceOpened = !isVoiceOpened;
        enableLoudspeaker(isVoiceOpened);

        if (isVoiceOpened) {
            HYClient.getHYAudioMgr().from(getContext()).setSpeakerphoneOn(true);
            tv_voice_title.setText("免提");
        } else {
            HYClient.getHYAudioMgr().from(getContext()).setSpeakerphoneOn(false);
            tv_voice_title.setText("听筒");
        }

//        if (mCallback != null) {
//            mCallback.onPlayerVoiceClicked(isVoiceOpened);
//        }
    }

    public void enableLoudspeaker(boolean isSpeakerOn){
        Drawable d = ContextCompat.getDrawable(getContext(), isSpeakerOn ? R.drawable.ic_loudspeaker_on : R.drawable.ic_loudspeaker_off);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        tv_voice_title.setCompoundDrawables(null, d, null, null);
    }

    public void setWhiteBoardTxt(String str, boolean isClose, boolean isOwner) {
//        tv_white_board.setText(str);
        if (isOwner) {
//            iv_white_img.setImageResource(isClose ? R.drawable.bendigongxiang_normal : R.drawable.jieshubendigongxiang_focus);
        } else {
//            iv_white_img.setImageResource(R.drawable.bendigongxiang_normal);
        }
    }

    public void hideInvisitor() {
//        menu_iv_invite_layer.setVisibility(GONE);
    }

    /**
     * 关闭视频
     */
    public void closeVideo() {
        isVideoOpened = false;
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.huiyi_btn_kaiqiishipin);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        tv_video_title.setText("打开视频");
        tv_video_title.setCompoundDrawables(null, d, null, null);
//        tv_video_title.setCompoundDrawablePadding(5);
    }

    /**
     * 开启视频
     */
    public void openVideo() {
        isVideoOpened = true;
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.huiyi_btn_guanbishipin);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        tv_video_title.setText("关闭视频");
        tv_video_title.setCompoundDrawables(null, d, null, null);
//        tv_video_title.setCompoundDrawablePadding(5);
    }

    /**
     * 控制视频
     */
    private void toggleVideo() {
        if (isCloseVideo) {
            return;
        }
        isVideoOpened = !isVideoOpened;
        Drawable d = ContextCompat.getDrawable(getContext(),
                isVideoOpened ? R.drawable.huiyi_btn_guanbishipin : R.drawable.huiyi_btn_kaiqiishipin);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        tv_video_title.setText(isVideoOpened ? "关闭视频" : "打开视频");
        tv_video_title.setCompoundDrawables(null, d, null, null);
//        tv_video_title.setCompoundDrawablePadding(5);

        if (mCallback != null) {
            mCallback.onPlayerVideoClicked(isVideoOpened);
        }
    }

    public void closeShare(){
        /*isSharePrepare = true;
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.huiyi_btn_gongxiang);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ll_share_txt.setText("共享");
        ll_share_txt.setCompoundDrawables(null, d, null, null);
        ll_share_txt.setCompoundDrawablePadding(5);*/
    }

    private void share(){
        /*isSharePrepare = !isSharePrepare;
        Drawable d = ContextCompat.getDrawable(getContext(),
                isSharePrepare ? R.drawable.huiyi_btn_gongxiang : R.drawable.huiyi_btn_tingzhigongxiang);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ll_share_txt.setText(isSharePrepare ? "共享" : "共享关闭");
        ll_share_txt.setCompoundDrawables(null, d, null, null);
        ll_share_txt.setCompoundDrawablePadding(5);

        if (mCallback != null) {
            mCallback.showSharePop(ll_share);
        }*/

    }

    public void closeMore(){
        isMorePrepare = true;
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.huiyi_btn_more);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ll_more_txt.setText("更多");
        ll_more_txt.setCompoundDrawables(null, d, null, null);
//        ll_more_txt.setCompoundDrawablePadding(5);
    }

    private void more(){
        isMorePrepare = !isMorePrepare;
        Drawable d = ContextCompat.getDrawable(getContext(),
                isMorePrepare ? R.drawable.huiyi_btn_more : R.drawable.huiyi_btn_more);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ll_more_txt.setText(isMorePrepare ? "更多" : "关闭更多");
        ll_more_txt.setCompoundDrawables(null, d, null, null);
//        ll_more_txt.setCompoundDrawablePadding(5);

        if (mCallback != null) {
            mCallback.showMorePop(ll_more);
        }
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    public boolean toggleCaptureAudio() {
        mIsJingYan = false;
        isAudioOn = HYClient.getHYCapture().toggleCaptureAudio();
        Drawable d = ContextCompat.getDrawable(getContext(),
                isAudioOn ? R.drawable.huiyi_btn_kaimai_open : R.drawable.huiyi_btn_kaimai);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        menu_iv_mic.setText(isAudioOn ? "关闭麦克" : "打开麦克");
        menu_iv_mic.setCompoundDrawables(null, d, null, null);
//        menu_iv_mic.setCompoundDrawablePadding(5);
        return isAudioOn;
    }

    public void closeMic(boolean isJingyan){
        mIsJingYan = isJingyan;
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.huiyi_btn_kaimai);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        menu_iv_mic.setText("打开麦克");
        menu_iv_mic.setCompoundDrawables(null, d, null, null);
//        menu_iv_mic.setCompoundDrawablePadding(5);
    }

    public void openMic(){
        mIsJingYan = false;
        Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.huiyi_btn_kaimai_open);
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        menu_iv_mic.setText("关闭麦克");
        menu_iv_mic.setCompoundDrawables(null, d, null, null);
//        menu_iv_mic.setCompoundDrawablePadding(5);
    }

    /**
     * 关闭声音 和 听筒
     */
    public void closeVoice() {
        HYClient.getHYCapture().setCaptureAudioOn(false);
//        menu_iv_mic.setImageResource(R.drawable.ic_meet_mic_checked);
        enableLoudspeaker(false);
        if (mCallback != null) {
            mCallback.onPlayerVoiceClicked(false);
        }

    }

    public void reSetVoice() {
        HYClient.getHYCapture().setCaptureAudioOn(isAudioOn);
//        menu_iv_mic.setImageResource(isAudioOn ? R.drawable.ic_meet_mic : R.drawable.ic_meet_mic_checked);
        enableLoudspeaker(isVoiceOpened);
        if (mCallback != null) {
            mCallback.onPlayerVoiceClicked(isVoiceOpened);
        }
    }

    /**
     * 开启声音
     */
    public void openVoice() {
        HYClient.getHYCapture().setCaptureAudioOn(true);
//        menu_iv_mic.setImageResource(R.drawable.ic_meet_mic);
    }

    /**
     * 创建人员特有的view展示
     *
     * @param isMeetStarter
     */
    public void hideMasterView(boolean isMeetStarter) {
//        menu_iv_hand_up.setVisibility(isMeetStarter ? GONE : VISIBLE);
//        menu_iv_layout.setVisibility(isMeetStarter ? VISIBLE : GONE);
    }

    public void showHandUpRed(boolean value) {
//        view_red.setVisibility(value ? VISIBLE : GONE);
    }

    public void setVideoEnable(boolean isCloseVideo) {
        this.isCloseVideo = isCloseVideo;
    }

    /**
     * 观摩模式的菜单状态
     *
     * @param isWatch
     */
    public void isWatch(boolean isWatch) {
        if (isWatch) {
            menu_iv_mic_layer.setVisibility(GONE);
            menu_iv_camera_video.setVisibility(GONE);
            /*menu_iv_camera_layer.setVisibility(GONE);
            menu_iv_change.setVisibility(GONE);*/
//            ll_share.setVisibility(GONE);
        } else {
            menu_iv_mic_layer.setVisibility(VISIBLE);
            menu_iv_camera_video.setVisibility(VISIBLE);
            /*menu_iv_camera_layer.setVisibility(GONE);
            menu_iv_change.setVisibility(VISIBLE);*/
//            ll_share.setVisibility(VISIBLE);
        }
    }

    public void canStartRecord(boolean canOpen) {
//        menu_iv_startrecord.setVisibility(canOpen ? VISIBLE : GONE);
    }

    public interface Callback {

        void onMeetExitClicked();

        void onMemberListClicked();

        void onMeetInviteClicked();

        void onInnerCameraClicked();

        void onCaptureVoiceClicked();

        void onPlayerVoiceClicked(boolean isVoiceOpened);

        void onPlayerVideoClicked(boolean isVideoOpened);

        void showLayoutChange();

        void onYuLanClick();

        void showSharePop(View view);

        void showMorePop(View view);

        void onHandUp();

        void startRecordSuccess();

        void onControlClick();
    }

}
