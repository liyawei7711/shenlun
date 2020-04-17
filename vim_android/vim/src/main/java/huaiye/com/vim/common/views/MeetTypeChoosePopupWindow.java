package huaiye.com.vim.common.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import huaiye.com.vim.R;

public class MeetTypeChoosePopupWindow extends PopupWindow {
    private Context mContext;
    private OnMeetTypeBtnClickLinsenter mOnMeetTypeBtnClickLinsenter;

    public MeetTypeChoosePopupWindow(Context context,OnMeetTypeBtnClickLinsenter onMeetTypeBtnClickLinsenter) {
        super(context);
        setFocusable(true);
        setOutsideTouchable(true);
        mContext = context;
        mOnMeetTypeBtnClickLinsenter = onMeetTypeBtnClickLinsenter;
    }

    public void initView() {
        setBackgroundDrawable(null);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        View view = LayoutInflater.from(mContext).inflate(R.layout.popwindow_choose_meet_type, null);
        setContentView(view);
        TextView meet_type_audio_video = view.findViewById(R.id.meet_type_audio_video);
        meet_type_audio_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnMeetTypeBtnClickLinsenter){
                    mOnMeetTypeBtnClickLinsenter.onMeetTypeAudioVideoClick();
                }
                dismiss();

            }
        });
        TextView meet_type_just_audio = view.findViewById(R.id.meet_type_just_audio);
        meet_type_just_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnMeetTypeBtnClickLinsenter){
                    mOnMeetTypeBtnClickLinsenter.onMeetJustAudioClick();
                }
                dismiss();
            }
        });
        TextView meet_type_cancle = view.findViewById(R.id.meet_type_cancle);
        meet_type_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnMeetTypeBtnClickLinsenter){
                    mOnMeetTypeBtnClickLinsenter.onMeetTypecancleClick();
                }
                dismiss();
            }
        });

    }

    public interface OnMeetTypeBtnClickLinsenter{
        void onMeetTypeAudioVideoClick();
        void onMeetJustAudioClick();
        void onMeetTypecancleClick();

    }

}
