package huaiye.com.vim.ui.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import huaiye.com.vim.R;
import huaiye.com.vim.common.views.CheckableLinearLayout;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.User;

/**
 * Created by ywt on 2019/2/25.
 */

public class VimContactsItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<User> mDataList;
    private ArrayList<User> mSelectedList;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnItemClickLinstener mOnItemClickLinstener;
    private char mLetter;
    private boolean mIsChoice;
    private RequestOptions requestOptions;

    private ArrayList<User> mChoicedContacts;
    private int nJoinStatus;

    public interface OnLoadMoreListener {
        void onLoadMore(char letter);
    }

    public interface OnItemClickLinstener {
        void onClick(int position, User user);
    }

    public VimContactsItemAdapter(Context context, ArrayList<User> list, char letter, boolean isChoice,
                                  ArrayList<User> choicedContacts) {
        mContext = context;
        mDataList = list;
        mLetter = letter;
        mIsChoice = isChoice;
        mChoicedContacts = choicedContacts;
        requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public void setOnItemClickLinstener(OnItemClickLinstener linstener) {
        mOnItemClickLinstener = linstener;
    }

    public void setSelected(ArrayList<User> list) {
        mSelectedList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_contacts_person, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CustomViewHolder viewHolder = (CustomViewHolder) holder;
        User user = mDataList.get(position);
        if(null == user){
            return;
        }
        Glide.with(mContext)
                .load(AppDatas.Constants().getAddressWithoutPort() + user.strHeadUrl)
                .apply(requestOptions)
                .into(viewHolder.iv_user_head);
        viewHolder.tv_user_name.setText(mDataList.get(position).strUserName);

        if(mChoicedContacts != null && mChoicedContacts.size() > 0 && mIsChoice) {
            for (User item : mChoicedContacts) {
                if (mDataList.get(position).strUserName.equals(item.strUserName)) {
                    nJoinStatus = item.nJoinStatus;
                }
            }
        }
        if(nJoinStatus == 2){
            viewHolder.tv_choose_added.setText("已添加");
            viewHolder.tv_choose_added.setVisibility(View.GONE);
            viewHolder.iv_choice.setVisibility(View.VISIBLE);
            viewHolder.iv_choice.setImageResource(R.drawable.shijian_xuanze_unclick);
            mDataList.get(position).nJoinStatus = 2;
            nJoinStatus = 0;
        } else {
            if (mIsChoice) {
                viewHolder.iv_choice.setVisibility(View.VISIBLE);
            }
            if (mChoicedContacts != null && mChoicedContacts.size() > 0 && mIsChoice) {
                boolean isSelected = false;
                for (User item : mChoicedContacts) {
                    if (item.strUserID.equals(mDataList.get(position).strUserID)) {
                        isSelected = true;
                        break;
                    }
                }
                if (isSelected) {
                    viewHolder.iv_choice.setImageResource(R.drawable.ic_choice_checked);
                } else {
                    viewHolder.iv_choice.setImageResource(R.drawable.ic_choice);
                }
            }
        }
        /*viewHolder.tv_checklayout.setOnCheckedListener(new CheckableLinearLayout.OnCheckedChangedListener() {
            @Override
            public void onCheckedChanged(View parent, boolean isChecked) {
                if (isChecked) {
                    viewHolder.iv_choice.setImageResource(R.drawable.ic_choice_checked);
                } else {
                    viewHolder.iv_choice.setImageResource(R.drawable.ic_choice);
                }
            }
        });*/
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickLinstener != null) {
                    mOnItemClickLinstener.onClick(position, mDataList.get(position));
                }
            }
        });
        if (position >= mDataList.size() - 1 && mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore(mLetter);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        private CheckableLinearLayout tv_checklayout;
        private ImageView iv_choice;
        private ImageView iv_user_head;
        private TextView tv_user_name;
        private TextView tv_user_master;
        private TextView tv_choose_added;

        CustomViewHolder(View itemView) {
            super(itemView);
            tv_checklayout = (CheckableLinearLayout) itemView.findViewById(R.id.tv_checklayout);
            iv_choice = (ImageView) itemView.findViewById(R.id.iv_choice);
            iv_user_head = (ImageView) itemView.findViewById(R.id.iv_user_head);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_user_master = (TextView) itemView.findViewById(R.id.tv_user_master);
            tv_choose_added = (TextView) itemView.findViewById(R.id.tv_choose_added);
        }
    }
}
