package huaiye.com.vim.common.recycle;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.List;


/**
 * Author：liyawei
 * Time: 2016/10/17 15:19
 * Email：liyawei@haiye.com
 */
public class LiteBaseAdapter<T> extends RecyclerView.Adapter<LiteViewHolder> {
    private final View.OnClickListener ocl;
    private final Object obj;
    private final int layoutId;
    private final Class clazz; //holderview

    private List<T> datas;
    private LoadListener loadListener;
    private LiteViewHolder headerViewHolder;
    public LiteViewHolder holder;
    protected Context context;


    public LiteBaseAdapter(Context context, List<T> datas, Class clazz, int layoutId, View.OnClickListener ocl, Object obj) {
        this.datas = datas;
        this.context = context;
        this.layoutId = layoutId;
        this.clazz = clazz;
        this.ocl = ocl;
        this.obj = obj;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }
    @Override
    public LiteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (headerView != null && viewType == TYPE_HEADER) {
            return headerViewHolder;
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        Class[] paramTypes = {Context.class, View.class, View.OnClickListener.class};
        Object[] params = {context, view, ocl}; // 方法传入的参数

        Constructor con = null;
        try {
            con = clazz.getConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        holder = null;
        try {
            holder = (LiteViewHolder) con.newInstance(params);
            holder.setMyAdapter(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(LiteViewHolder holder, int position) {
        if (headerView != null) {
            if (position == 0) {
                holder.bindData(holder, position, "", datas.size(), datas, obj);
            } else {
                holder.bindData(holder, position, datas.get(--position), datas.size(), datas, obj);
            }
        } else {
            if (position > datas.size() - 1) {
                holder.bindData(holder, position, "", datas.size(), datas, obj);
            } else {
                holder.bindData(holder, position, datas.get(position), datas.size(), datas, obj);
            }

            if (position == getItemCount() - 1) {
                lazyLoad();
            }
        }
    }

    private void lazyLoad() {
        if (loadListener != null
                && loadListener.isLoadOver()
                && !loadListener.isEnd()) {
            loadListener.lazyLoad();
        }
    }

    @Override
    public int getItemCount() {
        int litmit = getLitmit();
        if (headerView != null) {
            litmit++;
        }
        if (footerView != null) {
            litmit++;
        }
//        if(headerView == null)
//            return litmit > datas.size() ? datas.size() : litmit;
        return litmit;
    }

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    @Override
    public int getItemViewType(int position) {
        if (headerView == null) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    public int getLitmit() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    public interface HolderData<T, K> {
        void bindData(K holder, int position, T data, int size, List<T> datas, Object extr);
    }

    View headerView;
    View footerView;

    public void setHeaderView(View headerView, LiteViewHolder headerViewHolder) {
        this.headerView = headerView;
        this.headerViewHolder = headerViewHolder;
        notifyDataSetChanged();
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
        notifyDataSetChanged();
    }

    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public interface LoadListener {
        boolean isLoadOver();

        boolean isEnd();

        void lazyLoad();
    }

}