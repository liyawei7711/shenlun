package huaiye.com.vim.ui.meet.views;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.constant.CommonConstant;
import huaiye.com.vim.ui.contacts.ContactsChoiceByAllFriendActivity;
import huaiye.com.vim.ui.meet.MeetDetailActivity;
import huaiye.com.vim.ui.meet.MeetDetailEditActivity;

/**
 * Created by ywt on 2019/2/21.
 */

@BindLayout(R.layout.meet_detail_head)
public class MeetDetailHeaderView extends RelativeLayout implements View.OnClickListener {
    @BindView(R.id.meet_name)
    TextView meet_name;
    @BindView(R.id.meet_edit)
    ImageView meet_edit;
    @BindView(R.id.meet_detail)
    TextView meet_detail;
    @BindView(R.id.meet_time)
    TextView meet_time;
    @BindView(R.id.meet_add_person)
    LinearLayout meet_add_person;
    private CGetMeetingInfoRsp mCGetMeetingInfoRsp;
    private AppBaseActivity mActivity;
    private boolean mMeetStarting;

    public MeetDetailHeaderView(AppBaseActivity context, boolean starting) {
        this(context, null);
        mActivity = context;
        mMeetStarting = starting;
        if (mMeetStarting) {
            meet_edit.setVisibility(View.GONE);
        }
        meet_edit.setOnClickListener(this);
        meet_add_person.setOnClickListener(this);
    }

    public MeetDetailHeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeetDetailHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Injectors.get().injectView(this);
    }

    /**
     * 展示信息
     *
     * @param cGetMeetingInfoRsp
     */
    public void showInfo(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
        if (cGetMeetingInfoRsp == null) {
            return;
        }
        mCGetMeetingInfoRsp = cGetMeetingInfoRsp;
        meet_name.setText(getContext().getString(R.string.meet_name,
                TextUtils.isEmpty(cGetMeetingInfoRsp.strMeetingName) ? "" : cGetMeetingInfoRsp.strMeetingName));
        meet_detail.setText(cGetMeetingInfoRsp.strMeetingDesc);
        meet_time.setText(cGetMeetingInfoRsp.strStartTime.substring(5, 16));
        if (cGetMeetingInfoRsp.nStatus == 1) {
            meet_add_person.setVisibility(View.GONE);
        } else {
            meet_add_person.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meet_edit:
                if (mCGetMeetingInfoRsp == null) {
                    return;
                }
                Intent intent = new Intent(getContext(), MeetDetailEditActivity.class);
                intent.putExtra("nMeetingID", mCGetMeetingInfoRsp.nMeetingID);
                intent.putExtra("strMeetingName", mCGetMeetingInfoRsp.strMeetingName);
                intent.putExtra("strMeetingDesc", mCGetMeetingInfoRsp.strMeetingDesc);
                intent.putExtra("strStartTime", mCGetMeetingInfoRsp.strStartTime);
                intent.putExtra("nMeetingMode", mCGetMeetingInfoRsp.nMeetingMode);
                intent.putExtra("nTimeDuration", mCGetMeetingInfoRsp.nTimeDuration);
                intent.putExtra("strMeetDomainCode", mCGetMeetingInfoRsp.strMainUserDomainCode);
                mActivity.startActivityForResult(intent, CommonConstant.ACTIVITY_REQUEST_CODE);
                break;
            case R.id.meet_add_person:
                intent = new Intent(getContext(), ContactsChoiceByAllFriendActivity.class);
                if (getContext() instanceof MeetDetailActivity) {
                    MeetDetailActivity activity = (MeetDetailActivity) getContext();
                    intent.putExtra("titleName", getContext().getString(R.string.add_meet_person));
                    intent.putExtra("isSelectUser", true);
                    if(mCGetMeetingInfoRsp != null) {
                        intent.putExtra("strInviteUserId", mCGetMeetingInfoRsp.strMainUserID);
                    }
                    activity.startActivityForResult(intent, 1000);
                } else {
                    getContext().startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
}
