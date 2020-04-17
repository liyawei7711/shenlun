package huaiye.com.vim.common.recycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;


/**
 * Author：liyawei
 * Time: 2016/10/26 15:07
 * Email：liyawei@haiye.com
 */

public abstract class LiteViewHolder extends RecyclerView.ViewHolder implements LiteBaseAdapter.HolderData {
    protected View.OnClickListener ocl;
    protected Context context;
    protected LiteBaseAdapter adapter;

    public LiteViewHolder(Context context, View view, View.OnClickListener ocl) {
        super(view);
        this.ocl = ocl;
        this.context = context;
        ButterKnife.bind(this, view);

    }

    public void setMyAdapter(LiteBaseAdapter adapter) {
        this.adapter = adapter;
    }


}
