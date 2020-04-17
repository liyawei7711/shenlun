package huaiye.com.vim.ui.meet.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;

/**
 * Created by ywt on 2019/3/23.
 */

public class MoreControlWindow extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private CGetMeetingInfoRsp.UserInfo mUserinfo;
    private ConfirmClickListener mConfirmClickListener;

    public interface ConfirmClickListener {
        void onJingyanClick(CGetMeetingInfoRsp.UserInfo userInfo);

        void onZhujiangrenClick(CGetMeetingInfoRsp.UserInfo userInfo);

        void onKickOutClick(CGetMeetingInfoRsp.UserInfo userInfo);

        void onCancelSpeakerClick(CGetMeetingInfoRsp.UserInfo userInfo);
    }

    public MoreControlWindow(Activity context, CGetMeetingInfoRsp.UserInfo userInfo, String strKeynoteSpeakerUserID) {
        super(context);
        mContext = context;
        mUserinfo = userInfo;
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        Drawable drawable = new ColorDrawable(Color.parseColor("#00000000"));
        setBackgroundDrawable(drawable);// 点击外部消失
        setOutsideTouchable(true); // 点击外部消失
        setFocusable(true); // 点击back键消失

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        View contentView = LayoutInflater.from(context).inflate(R.layout.more_control_window, null);
        setContentView(contentView);
        /*contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/

        TextView control_jingyan = (TextView) contentView.findViewById(R.id.control_jingyan);
        if (userInfo.isSpeakerMute()) {
            control_jingyan.setText(AppUtils.getString(R.string.talk_notice9));
        } else {
            control_jingyan.setText(AppUtils.getString(R.string.jingyan));
        }
        control_jingyan.setOnClickListener(this);
        if(strKeynoteSpeakerUserID == "") {
            TextView control_zhujiangren = (TextView) contentView.findViewById(R.id.control_zhujiangren);
            control_zhujiangren.setVisibility(View.VISIBLE);
            TextView control_cancelspeaker = (TextView) contentView.findViewById(R.id.control_cancelspeaker);
            control_cancelspeaker.setVisibility(View.GONE);
            contentView.findViewById(R.id.control_zhujiangren).setOnClickListener(this);
        }else {
            TextView control_zhujiangren = (TextView) contentView.findViewById(R.id.control_zhujiangren);
            control_zhujiangren.setVisibility(View.GONE);
            TextView control_cancelspeaker = (TextView) contentView.findViewById(R.id.control_cancelspeaker);
            control_cancelspeaker.setVisibility(View.VISIBLE);
            contentView.findViewById(R.id.control_cancelspeaker).setOnClickListener(this);
        }
        contentView.findViewById(R.id.control_kick_out).setOnClickListener(this);
    }

    public void setConfirmClickListener(ConfirmClickListener listener) {
        mConfirmClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.control_jingyan:
                if (mConfirmClickListener != null) {
                    mConfirmClickListener.onJingyanClick(mUserinfo);
                }
                dismiss();
                break;
            case R.id.control_zhujiangren:
                if (mConfirmClickListener != null) {
                    mConfirmClickListener.onZhujiangrenClick(mUserinfo);
                }
                dismiss();
                break;
            case R.id.control_cancelspeaker:
                if (mConfirmClickListener != null) {
                    mConfirmClickListener.onCancelSpeakerClick(mUserinfo);
                }
                dismiss();
                break;
            case R.id.control_kick_out:
                if (mConfirmClickListener != null) {
                    mConfirmClickListener.onKickOutClick(mUserinfo);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    public void showView(View view) {
        this.showAsDropDown(view);
    }
}
