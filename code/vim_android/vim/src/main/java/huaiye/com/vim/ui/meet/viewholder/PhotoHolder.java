package huaiye.com.vim.ui.meet.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.R;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.models.meet.bean.Bean;

/**
 * Created by Administrator on 2018\2\27 0027.
 */

public class PhotoHolder extends LiteViewHolder {

    @BindView(R.id.iv_image)
    ImageView iv_image;
    @BindView(R.id.cb_status)
    CheckBox cb_status;
    @BindView(R.id.cb_view)
    View cb_view;

    public PhotoHolder(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);
        itemView.setOnClickListener(ocl);
        cb_view.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        Bean bean = (Bean) data;
        itemView.setTag(bean);
        cb_view.setTag(bean);
        Glide.with(context)
                .asBitmap()
                .load(new String(bean.data, 0, bean.data.length - 1))
                .apply(new RequestOptions().centerCrop())
                .into(iv_image);
        cb_status.setChecked(bean.isChecked);
    }
}
