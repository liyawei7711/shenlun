package huaiye.com.vim.ui.sendBaiduLocation.function.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;


/**
 * Created by xz on 2017/8/9 0009.
 * 地图 地址列表 适配器
 *
 * @author xz
 */

public class MapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int mIndexTag = 0;
    private List<PoiInfo> datas;
    private PoiInfo mUserPoiInfo;

    private Context mContext;
    private OnItemClickListener mOnItemClickLinstener;


    public MapAdapter(Context context) {
        this.mContext = context;
    }

    public int getmIndexTag() {
        return mIndexTag;
    }

    public void setmUserPoiInfo(PoiInfo userPoiInfo) {
        this.mUserPoiInfo = userPoiInfo;
    }

    public void setmIndexTag(int mIndexTag) {
        this.mIndexTag = mIndexTag;
        notifyDataSetChanged();
    }

    /**
     * 重写此方法，每次更新数据后，item为第一个
     *
     * @param datas     数据
     * @param isRefresh 是否刷新
     */
    public void setDatas(List<PoiInfo> datas, boolean isRefresh) {
        if (mUserPoiInfo != null && datas != null) {
            datas.add(0, mUserPoiInfo);
        }
        this.datas = datas;
        mIndexTag = 0;
        if(isRefresh){
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_map, parent, false));
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

        TextView minTv = nCustomViewHolder.im_migtv;

        PoiInfo poiInfo = datas.get(position);

        bigTv.setText(poiInfo.name);

        minTv.setText(poiInfo.address);

        if (mIndexTag == position) {
            bigTv.setTextColor(ContextCompat.getColor(AppUtils.ctx, R.color.app_sub_color));
            minTv.setTextColor(ContextCompat.getColor(AppUtils.ctx, R.color.app_sub_color));
        } else {
            bigTv.setTextColor(ContextCompat.getColor(AppUtils.ctx, R.color.app_txt_black));
            minTv.setTextColor(ContextCompat.getColor(AppUtils.ctx, R.color.app_txt_gray_light));
        }
    }

    public void setOnItemClickListener(OnItemClickListener nOnItemClickLinstener) {
        mOnItemClickLinstener = nOnItemClickLinstener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public Object getItem(int position) {
        if(null==datas||datas.size()<position+1){
            return null;
        }
        return datas.get(position);
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }
}
