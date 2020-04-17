package huaiye.com.vim.ui.meet.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMsgCaptureQualityNotify;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.media.capture.HYCapture;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.concurrent.TimeUnit;

import huaiye.com.vim.R;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.rx.CommonSubscriber;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD720P;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_VGA;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_capture;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: MeetMediaMenuView
 */
@BindLayout(R.layout.view_meet_media_header)
public class MeetMediaMenuTopView extends RelativeLayout {

    @BindView(R.id.ll_left)
    View ll_left;
    @BindView(R.id.menu_iv_voice)
    ImageView menu_iv_voice;
    @BindView(R.id.tv_meet_name)
    TextView tv_meet_name;
    @BindView(R.id.tv_meet_time)
    TextView tv_meet_time;
    /*@BindView(R.id.tv_inhao)
    TextView tv_inhao;*/
    @BindView(R.id.record_status)
    View record_status;
    @BindView(R.id.ll_end)
    TextView ll_end;

    private static Disposable mDisposable;
    CommonSubscriber subscriber;
    private Callback mCallback;

    public interface Callback {
        void onMeetExitClicked();
    }

    public MeetMediaMenuTopView(Context context) {
        this(context, null);
    }

    public MeetMediaMenuTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetMediaMenuTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injectors.get().injectView(this);

        subscriber = new CommonSubscriber<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                mDisposable = d;
            }

            @Override
            public void onNext(Long o) {
                int hour = (int) (o / (60 * 60));
                int min = (int) ((o - hour * 3600) / 60);
                int second = (int) (o - hour * 3600 - min * 60);
                String strH, strM, strS;
                strH = hour < 10 ? "0" + hour : "" + hour;
                strM = min < 10 ? "0" + min : "" + min;
                strS = second < 10 ? "0" + second : "" + second;

                tv_meet_time.setText(strH + ":" + strM + ":" + strS);
            }
        };
    }

    public void setCallback(Callback callback){
        mCallback = callback;
    }

    /**
     * 改变质量
     *
     * @param msg
     */
    public void changeQuality(SdpMsgCaptureQualityNotify msg) {
        int max = 1;
        if (TextUtils.isEmpty(SP.getString(STRING_KEY_capture))) {
            SP.putString(STRING_KEY_capture, HYClient.getSdkOptions().Capture().getCaptureQuality().name());
        }
        switch (SP.getString(STRING_KEY_capture)) {
            case STRING_KEY_VGA:
                max = HYCapture.Config.getVGA().getPublicPresetOption();
                break;
            case STRING_KEY_HD:
                max = HYCapture.Config.getHDVGA().getPublicPresetOption();
                break;
            case STRING_KEY_HD720P:
                max = HYCapture.Config.getHD720P().getPublicPresetOption();
                break;
        }
        if (max < 0) {
            max = 0;
        }
        float pre = ((float) msg.m_nCurQuality + 1) / (max + 1);

        if (pre == 0) {
            menu_iv_voice.setImageResource(R.drawable.xinhao0);
//            tv_inhao.setText("网络质量差");
        } else if (pre <= 0.25) {
            menu_iv_voice.setImageResource(R.drawable.xinhao1);
//            tv_inhao.setText("网络质量差");
        } else if (pre <= 0.5) {
            menu_iv_voice.setImageResource(R.drawable.xinhao2);
//            tv_inhao.setText("网络质量一般");
        } else if (pre <= 0.75) {
            menu_iv_voice.setImageResource(R.drawable.xinhao3);
//            tv_inhao.setText("网络质量好");
        } else {
            menu_iv_voice.setImageResource(R.drawable.xinhao4);
//            tv_inhao.setText("网络质量好");
        }
    }

    /**
     * 计时开始
     */
    public void startTime() {
        if (mDisposable != null && !mDisposable.isDisposed())
            mDisposable.dispose();
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    /**
     * 展示名称
     *
     * @param str
     */
    public void showName(String str) {
        tv_meet_name.setText(str);
    }

    public void showLeft(boolean value) {
        ll_left.setVisibility(value ? INVISIBLE : VISIBLE);
    }

    public void isRecord(boolean value) {
        record_status.setVisibility(value ? VISIBLE : INVISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        if (mDisposable != null) {
            mDisposable = null;
        }
    }

    @OnClick(R.id.ll_end)
    void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.ll_end:
                if(mCallback != null){
                    mCallback.onMeetExitClicked();
                }
                break;
            default:
                break;
        }
    }
}
