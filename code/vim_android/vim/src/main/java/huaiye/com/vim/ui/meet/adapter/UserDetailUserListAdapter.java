package huaiye.com.vim.ui.meet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.User;

/**
 * Created by LENOVO on 2019/3/29.
 */

public class UserDetailUserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private List<User> mUserList;
    private RequestOptions requestOptions;


    public static final String TYPE_ADD="ADDUSER";

    public static final String TYPE_DEL="DELUSER";


    public void setData(ArrayList<User> userList){
        mUserList = userList;
    }


    public UserDetailUserListAdapter(Context context) {
        mContext = context;
        requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_user_detail_user_list, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CustomViewHolder viewHolder = (CustomViewHolder) holder;
        final User msg = mUserList.get(position);
        if(null==msg){
            return;
        }
        if(TYPE_ADD.equals(msg.strUserID)){
            viewHolder.user_detail_image.setImageResource(R.drawable.selector_user_detail_add);
        }else if(TYPE_DEL.equals(msg.strUserID)){
            viewHolder.user_detail_image.setImageResource(R.drawable.selector_user_detail_del);
        }else{
            if(TextUtils.isEmpty( msg.strHeadUrl)){
                viewHolder.user_detail_image.setImageResource(R.drawable.default_image_personal);
                new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<String>() {
                    @Override
                    public String doOnThread() {
                        return AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(msg.strUserID,msg.strDomainCode);
                    }

                    @Override
                    public void doOnMain(String data) {
                        msg.strHeadUrl = data;
                        Glide.with(mContext)
                                .load(AppDatas.Constants().getAddressWithoutPort() + data)
                                .apply(requestOptions)
                                .into(viewHolder.user_detail_image);
                    }
                });
            }else{
                Glide.with(mContext)
                        .load(AppDatas.Constants().getAddressWithoutPort() + msg.strHeadUrl)
                        .apply(requestOptions)
                        .into(viewHolder.user_detail_image);
            }



        }
        viewHolder.user_detail_txt.setText(msg.strUserName);
        viewHolder.user_detail_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mOnItemClickListener){
                    mOnItemClickListener.onItemClick(msg);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return mUserList == null ? 0 : mUserList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);


    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView user_detail_image;
        TextView user_detail_txt;

        private CustomViewHolder(View itemView) {
            super(itemView);
            user_detail_image  = itemView.findViewById(R.id.user_detail_image);
            user_detail_txt = itemView.findViewById(R.id.user_detail_txt);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(User item);
    }

}
