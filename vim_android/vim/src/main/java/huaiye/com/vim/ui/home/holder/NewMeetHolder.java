package huaiye.com.vim.ui.home.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.models.meet.bean.MeetList;

/**
 * Created by ywt on 2019/2/21.
 */

public class NewMeetHolder extends LiteViewHolder {
    @BindView(R.id.meeting_name)
    TextView meeting_name;
    @BindView(R.id.meeting_state)
    TextView meeting_state;
    @BindView(R.id.meeting_item_id)
    TextView meeting_item_id;
    @BindView(R.id.meeting_compere)
    TextView meeting_compere;
    @BindView(R.id.meeting_time)
    TextView meeting_time;

    public NewMeetHolder(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        MeetList.Data bean = (MeetList.Data) data;

        itemView.setTag(data);

        meeting_name.setText(bean.strMeetingName);
        meeting_time.setText(bean.strStartTime);
        meeting_item_id.setText(String.valueOf(bean.nMeetingID));

        if (bean.nStatus == 1) {
            meeting_state.setText(R.string.meeting_starting);
        } else if (bean.nStatus == 4) {
            meeting_state.setText(R.string.present_order);
        }
    }
}
