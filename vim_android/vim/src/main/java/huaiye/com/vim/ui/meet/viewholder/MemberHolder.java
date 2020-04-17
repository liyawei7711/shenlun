package huaiye.com.vim.ui.meet.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;

import static huaiye.com.vim.common.AppUtils.getString;

/**
 * Created by Administrator on 2018\2\27 0027.
 */

public class MemberHolder extends LiteViewHolder {
    public static boolean isMeetStarter;
    public static String masterLogoinName;
    public static ArrayList<String> handUser = new ArrayList<>();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_mainer)
    View iv_mainer;
    @BindView(R.id.iv_hand)
    ImageView iv_hand;
    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.iv_jinyan)
    ImageView ivJinyan;
    @BindView(R.id.iv_tichu)
    ImageView ivTichu;
    @BindView(R.id.iv_chonghu)
    ImageView ivChonghu;

    @BindView(R.id.fl_speak)
    View fl_speak;
    @BindView(R.id.fl_kickout)
    View fl_kickout;
    @BindView(R.id.fl_recall)
    View fl_recall;

    public MemberHolder(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);

        if (isMeetStarter) {
            ivJinyan.setOnClickListener(ocl);
            ivTichu.setOnClickListener(ocl);
            ivChonghu.setOnClickListener(ocl);
            fl_recall.setOnClickListener(ocl);
            fl_kickout.setOnClickListener(ocl);
            fl_speak.setOnClickListener(ocl);
        }

    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        CGetMeetingInfoRsp.UserInfo bean = (CGetMeetingInfoRsp.UserInfo) data;

        if (bean.strUserID.equals(masterLogoinName)) {
            iv_mainer.setVisibility(View.VISIBLE);
        } else {
            iv_mainer.setVisibility(View.GONE);
        }

        if (isMeetStarter) {
            ivJinyan.setTag(bean);
            ivTichu.setTag(bean);
            ivChonghu.setTag(bean);
            fl_speak.setTag(bean);
            fl_kickout.setTag(bean);
            fl_recall.setTag(bean);

            fl_speak.setVisibility(View.VISIBLE);
            fl_kickout.setVisibility(View.VISIBLE);
            fl_recall.setVisibility(View.VISIBLE);
            if (bean.nJoinStatus == 2) {
                fl_recall.setVisibility(View.GONE);
            } else {
                fl_speak.setVisibility(View.GONE);
                fl_kickout.setVisibility(View.GONE);
                fl_recall.setVisibility(View.VISIBLE);
            }
        } else {
            fl_speak.setVisibility(View.GONE);
            fl_kickout.setVisibility(View.GONE);
            fl_recall.setVisibility(View.GONE);
        }

        if (bean.strUserID.equals(HYClient.getSdkOptions().User().getUserId())) {
            fl_kickout.setVisibility(View.GONE);
        }

        if (handUser.contains(bean.strUserID)) {
            iv_hand.setVisibility(View.VISIBLE);
        } else {
            iv_hand.setVisibility(View.GONE);
        }

        if (bean.isSpeakerMute()) {
            ivJinyan.setImageResource(R.drawable.selector_jinyan_statue);
        } else {
            ivJinyan.setImageResource(R.drawable.ico_weijinyan);
        }

        tvName.setText(bean.strUserName);
        if (position == 0) {
            if(bean.nJoinStatus != 2) {
                tvTitle.setText(getString(R.string.no_join_notice));
            } else {
                tvTitle.setText(getString(R.string.has_join_notice));
            }
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            if (bean.nJoinStatus != ((CGetMeetingInfoRsp.UserInfo) datas.get(position - 1)).nJoinStatus
                    && ((CGetMeetingInfoRsp.UserInfo) datas.get(position - 1)).nJoinStatus == 2) {
                tvTitle.setText(getString(R.string.no_join_notice));
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
        }
    }
}
