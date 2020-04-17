package huaiye.com.vim.ui.home.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.models.meet.bean.MeetList;

/**
 * author: admin
 * date: 2018/02/24
 * version: 0
 * mail: secret
 * desc: MeetHolder
 */

public class MeetHolder extends LiteViewHolder {
    @BindView(R.id.iv_icon)
    ImageView iv_icon;
    @BindView(R.id.tv_meet_name)
    TextView tv_meet_name;
    @BindView(R.id.tv_meet_date)
    TextView tv_meet_date;
    @BindView(R.id.tv_meet_id)
    TextView tv_meet_id;
    @BindView(R.id.view_badger)
    ImageView view_badger;
    @BindView(R.id.line)
    View line;

    public MeetHolder(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        MeetList.Data bean = (MeetList.Data) data;

        itemView.setTag(data);

        tv_meet_name.setText(bean.strMeetingName);
        tv_meet_date.setText(bean.strStartTime);
        tv_meet_id.setText("会议号:" + bean.nMeetingID);

        if (bean.nStatus == 1) {
            view_badger.setImageResource(R.drawable.icon_jingzingzhong);
        } else if (bean.nStatus == 4) {
            view_badger.setImageResource(R.drawable.icon_weikaishi);
        }

        boolean isShow = (boolean) extr;
        if (isShow) {
            view_badger.setVisibility(View.VISIBLE);
        } else {
            view_badger.setVisibility(View.GONE);
        }

        if (bean.nMeetingType == 1) {
            iv_icon.setImageResource(R.drawable.icon_liebiao_yuyuehuiyi);
        } else {
            iv_icon.setImageResource(R.drawable.icon_liebiao_jishihuiyi);
        }

    }
}
