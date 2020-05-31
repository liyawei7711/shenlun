package huaiye.com.vim.ui.sendBaiduLocation.function.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.SuggestionResult;

import java.util.List;

import huaiye.com.vim.R;

/**
 * Created by xz on 2017/8/9 0009.
 * 地图 地址列表搜索 适配器
 * @author xz
 */

public class MapSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SuggestionResult.SuggestionInfo> datas;
    private Context mContext;
    private OnItemClickListener mOnItemClickLinstener;
    public MapSearchAdapter(Context context) {
        this.mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_map_search, parent, false));
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        LinearLayout item_content;
        TextView im_bigtv;
        TextView im_migtv;


        private CustomViewHolder(View itemView) {
            super(itemView);
            item_content = itemView.findViewById(R.id.item_content);
            im_bigtv = itemView.findViewById(R.id.im_bigtv);
            im_migtv = itemView.findViewById(R.id.im_migtv);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CustomViewHolder nCustomViewHolder = (CustomViewHolder) holder;
        nCustomViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnItemClickLinstener){
                    mOnItemClickLinstener.onItemClick(nCustomViewHolder.itemView,holder,position);
                }
            }
        });
        TextView bigTv = nCustomViewHolder.im_bigtv;

        SuggestionResult.SuggestionInfo ss=datas.get(position);
        bigTv.setText(ss.city+ss.district+ss.key);
    }

    /**
     * 重写此方法，每次更新数据后，item为第一个
     */
    public void setDatas(List<SuggestionResult.SuggestionInfo> datas, boolean isRefresh) {
        this.datas = datas;
        if(isRefresh){
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener nOnItemClickLinstener) {
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
