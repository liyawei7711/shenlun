package huaiye.com.vim.ui.meet.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.ui.meet.basemodel.SelectedModel;

/**
 * Created by Administrator on 2018\2\27 0027.
 */

public class MemberLayoutHolder extends LiteViewHolder {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_state)
    ImageView ivState;

    public MemberLayoutHolder(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        SelectedModel<CGetMeetingInfoRsp.UserInfo> bean = (SelectedModel<CGetMeetingInfoRsp.UserInfo>) data;
        itemView.setTag(bean);

        tvName.setText(bean.bean.strUserName);

        if (bean.isChecked) {
            ivState.setImageResource(R.drawable.ic_choice_checked);
        } else {
            ivState.setImageResource(R.drawable.ic_choice);
        }

        if (position == 0) {
            tvTitle.setText("已上墙");
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            if (bean.bean.inVideo() != ((SelectedModel<CGetMeetingInfoRsp.UserInfo>) datas.get(position - 1)).bean.inVideo()
                    && ((SelectedModel<CGetMeetingInfoRsp.UserInfo>) datas.get(position - 1)).bean.nCombineStatus == 1) {
                tvTitle.setText("未上墙");
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
        }
    }
}
