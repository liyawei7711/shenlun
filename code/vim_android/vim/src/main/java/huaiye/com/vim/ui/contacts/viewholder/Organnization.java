package huaiye.com.vim.ui.contacts.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.models.contacts.bean.ContactOrganizationBean;

/**
 * Created by ywt on 2019/2/25.
 */

public class Organnization extends LiteViewHolder {
    @BindView(R.id.organization_state)
    ImageView organization_state;
    @BindView(R.id.organization_name)
    TextView organization_name;
    @BindView(R.id.organization_line)
    View organization_line;

    private Context mContext;

    public Organnization(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);
        mContext = context;
        itemView.setOnClickListener(ocl);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        itemView.setTag(position);
        /*if (data instanceof ContactOrganization.Enterprise) {
            ContactOrganization.Enterprise bean = (ContactOrganization.Enterprise) data;
            organization_name.setText(bean.entName + "                                                                                                                                                                                                              ");
        } else {
            ContactOrganization.Department bean = (ContactOrganization.Department) data;
            organization_name.setText(bean.name + "                                                                                                                                                                                                                   ");
        }*/
        ContactOrganizationBean.Data bean = (ContactOrganizationBean.Data) data;
        /*int num = bean.depList == null ? 0 : bean.depList.size();
        organization_name.setText(bean.name + "(" + num + ")");*/
        organization_state.setVisibility(View.GONE);
        organization_name.setText(bean.strName);
        if (datas.size() - 1 == position) {
            organization_line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            organization_line.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black666));
        }
    }
}
