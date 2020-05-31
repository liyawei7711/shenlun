package huaiye.com.vim.ui.chat.dialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.models.map.bean.DaoHangAppInfo;

public class DaohangAppItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DaoHangAppInfo> datas;
    private Context mContext;
    private DaohangAppItemAdapter.OnItemClickListener mOnItemClickLinstener;
    private boolean isDaoHang;
    public DaohangAppItemAdapter(Context context) {
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DaohangAppItemAdapter.CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_daohang_app, parent, false));
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView daohang_icon;
        TextView daohang_name;


        private CustomViewHolder(View itemView) {
            super(itemView);
            daohang_icon = itemView.findViewById(R.id.daohang_icon);
            daohang_name = itemView.findViewById(R.id.daohang_name);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DaohangAppItemAdapter.CustomViewHolder nCustomViewHolder = (DaohangAppItemAdapter.CustomViewHolder) holder;
        nCustomViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnItemClickLinstener){
                    mOnItemClickLinstener.onItemClick(nCustomViewHolder.itemView,holder,position);
                }
            }
        });

        DaoHangAppInfo daoHangAppInfo=datas.get(position);
        if(null!=daoHangAppInfo.appIcon){
            nCustomViewHolder.daohang_icon.setVisibility(View.VISIBLE);
            nCustomViewHolder.daohang_icon.setBackground(daoHangAppInfo.appIcon);

        }else{
            nCustomViewHolder.daohang_icon.setVisibility(View.GONE);

        }
        nCustomViewHolder.daohang_name.setText(daoHangAppInfo.appName);
    }

    /**
     * 重写此方法，每次更新数据后，item为第一个
     */
    public void setDatas(List<DaoHangAppInfo> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }


    public void setDaoHangFlag(boolean isDaoHang){
        this.isDaoHang =isDaoHang;
    }

    public boolean isDaoHang(){
        return isDaoHang;
    }

    public void setOnItemClickListener(DaohangAppItemAdapter.OnItemClickListener nOnItemClickLinstener) {
        mOnItemClickLinstener = nOnItemClickLinstener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }

}