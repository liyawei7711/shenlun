package huaiye.com.vim.ui.home.adapter;

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

import huaiye.com.vim.R;
import huaiye.com.vim.common.helper.ChatContactsGroupUserListHelper;
import huaiye.com.vim.common.views.CheckableLinearLayout;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.GroupInfo;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

public class GroupContactsItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<GroupInfo> mDataList;
    private ArrayList<GroupInfo> mSelectedList;
    private GroupContactsItemAdapter.OnLoadMoreListener mOnLoadMoreListener;
    private GroupContactsItemAdapter.OnItemClickLinstener mOnItemClickLinstener;
    private char mLetter;
    private boolean mIsChoice;
    private ArrayList<GroupInfo> mChoicedContacts;
    private int nJoinStatus;
    private RequestOptions requestOptions;


    public interface OnLoadMoreListener {
        void onLoadMore(char letter);
    }

    public interface OnItemClickLinstener {
        void onClick(int position, GroupInfo user);
    }

    public void setDatas(ArrayList<GroupInfo> list) {
        mDataList = list;
    }

    public GroupContactsItemAdapter(Context context, ArrayList<GroupInfo> list, boolean isChoice,
                                    ArrayList<GroupInfo> choicedContacts) {
        mContext = context;
        mDataList = list;
        mIsChoice = isChoice;
        mChoicedContacts = choicedContacts;
        requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.ic_group_chat)
                .error(R.drawable.ic_group_chat)
                .optionalTransform(new CircleCrop());
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public void setOnItemClickLinstener(OnItemClickLinstener linstener) {
        mOnItemClickLinstener = linstener;
    }

    public void setSelected(ArrayList<GroupInfo> list) {
        mSelectedList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupContactsItemAdapter.CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_contacts_person, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final GroupContactsItemAdapter.CustomViewHolder viewHolder = (GroupContactsItemAdapter.CustomViewHolder) holder;
        GroupInfo groupInfo = mDataList.get(position);
        Glide.with(mContext)
                .load(AppDatas.Constants().getAddressWithoutPort() + groupInfo.strHeadUrl)
                .apply(requestOptions)
                .into(viewHolder.iv_user_head);

        if (TextUtils.isEmpty(mDataList.get(position).strGroupName)) {
            ModelApis.Contacts().requestqueryGroupChatInfo(mDataList.get(position).strGroupDomainCode, mDataList.get(position).strGroupID,
                    new ModelCallback<ContactsGroupUserListBean>() {
                        @Override
                        public void onSuccess(final ContactsGroupUserListBean contactsBean) {
                            if(contactsBean != null) {
                                ChatContactsGroupUserListHelper.getInstance().cacheContactsGroupDetail(mDataList.get(position).strGroupID + "", contactsBean);
                            }
                            if (null != contactsBean && null != contactsBean.lstGroupUser && contactsBean.lstGroupUser.size() > 0) {
                                StringBuilder sb = new StringBuilder("");
                                for (ContactsGroupUserListBean.LstGroupUser temp : contactsBean.lstGroupUser) {
                                    sb.append(temp.strUserName + "、");
                                }
                                if (null != sb && sb.indexOf("、") >= 0) {
                                    sb.deleteCharAt(sb.lastIndexOf("、"));
                                }
                                viewHolder.tv_user_name.setText(sb);
                                mDataList.get(position).strGroupName = sb.toString();
                            }
                        }

                        @Override
                        public void onFailure(HTTPResponse httpResponse) {
                            super.onFailure(httpResponse);
                        }
                    });
        } else {
            viewHolder.tv_user_name.setText(mDataList.get(position).strGroupName);
        }

//        if(mChoicedContacts != null && mChoicedContacts.size() > 0 && mIsChoice) {
//            for (GroupInfo item : mChoicedContacts) {
//                if (mDataList.get(position).strGroupID.equals(item.strGroupID)) {
//                    nJoinStatus = item.nJoinStatus;
//                }
//            }
//        }
//        if(nJoinStatus == 2){
//            viewHolder.tv_choose_added.setText("已添加");
//            mDataList.get(position).nJoinStatus = 2;
//            nJoinStatus = 0;
//        } else {
//            if (mIsChoice) {
//                viewHolder.iv_choice.setVisibility(View.VISIBLE);
//            }
//            if (mChoicedContacts != null && mChoicedContacts.size() > 0 && mIsChoice) {
//                boolean isSelected = false;
//                for (User item : mChoicedContacts) {
//                    if (item.strUserID.equals(mDataList.get(position).strUserID)) {
//                        isSelected = true;
//                        break;
//                    }
//                }
//                if (isSelected) {
//                    viewHolder.iv_choice.setImageResource(R.drawable.ic_choice_checked);
//                } else {
//                    viewHolder.iv_choice.setImageResource(R.drawable.ic_choice);
//                }
//            }

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

        if (position == getItemCount() - 1) {
            viewHolder.view_divider.setVisibility(View.GONE);
        } else {
            viewHolder.view_divider.setVisibility(View.VISIBLE);
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
        private View view_divider;

        CustomViewHolder(View itemView) {
            super(itemView);
            tv_checklayout = (CheckableLinearLayout) itemView.findViewById(R.id.tv_checklayout);
            iv_choice = (ImageView) itemView.findViewById(R.id.iv_choice);
            iv_user_head = (ImageView) itemView.findViewById(R.id.iv_user_head);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_user_master = (TextView) itemView.findViewById(R.id.tv_user_master);
            tv_choose_added = (TextView) itemView.findViewById(R.id.tv_choose_added);
            view_divider = (View) itemView.findViewById(R.id.view_divider);
        }
    }
}
