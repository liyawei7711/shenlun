package huaiye.com.vim.ui.meet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.models.meet.bean.ChatMoreFunctionBean;

/**
 * Created by LENOVO on 2019/3/29.
 */

public class ChatMoreFunctionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ChatMoreFunctionClickListener mChatMoreFunctionClickListener;
    private static List<ChatMoreFunctionBean> mDataList = new ArrayList<ChatMoreFunctionBean>();
    private String sessionID;


    public ChatMoreFunctionAdapter(Context context, boolean isGroup,String sessionID) {
        mContext = context;
        this.sessionID = sessionID;
        if (isGroup) {
            initGroupData();
        } else {
            initSingleChatData();
        }

    }

    private void initSingleChatData() {
        mDataList.clear();
        ChatMoreFunctionBean function = new ChatMoreFunctionBean(0, R.drawable.selector_chat_more_img, AppUtils.getString(R.string.img));
        mDataList.add(function);
        function = new ChatMoreFunctionBean(1, R.drawable.selector_chat_more_paishe, AppUtils.getString(R.string.string_name_paishe));
        mDataList.add(function);
        function = new ChatMoreFunctionBean(2, R.drawable.selector_chat_more_weizhi, AppUtils.getString(R.string.dialog_chat_send_location_title));
        mDataList.add(function);
        function = new ChatMoreFunctionBean(5, R.drawable.selector_chat_more_wenjian, AppUtils.getString(R.string.string_name_send_file));
        mDataList.add(function);
        function = new ChatMoreFunctionBean(6, R.drawable.selector_chat_more_yuehoujifeng, AppUtils.getString(R.string.string_name_yuehoujifeng));
        mDataList.add(function);
    }

    private void initGroupData() {
        mDataList.clear();
        ChatMoreFunctionBean function = new ChatMoreFunctionBean(0, R.drawable.selector_chat_more_img, AppUtils.getString(R.string.img));
        mDataList.add(function);
        function = new ChatMoreFunctionBean(1, R.drawable.selector_chat_more_paishe, AppUtils.getString(R.string.string_name_paishe));
        mDataList.add(function);
        function = new ChatMoreFunctionBean(2, R.drawable.selector_chat_more_weizhi, AppUtils.getString(R.string.dialog_chat_send_location_title));
        mDataList.add(function);
//        function=new ChatMoreFunctionBean(3,R.drawable.selector_chat_more_yuying,"语音通话");
//        mDataList.add(function);
//        function = new ChatMoreFunctionBean(4, R.drawable.selector_chat_more_shiping, "视频会议");
//        mDataList.add(function);
        function = new ChatMoreFunctionBean(5, R.drawable.selector_chat_more_wenjian, AppUtils.getString(R.string.string_name_send_file));
        mDataList.add(function);
        function = new ChatMoreFunctionBean(6, R.drawable.selector_chat_more_yuehoujifeng, AppUtils.getString(R.string.string_name_yuehoujifeng));
        mDataList.add(function);
    }


    public void setOnItemClickListener(ChatMoreFunctionClickListener chatMoreFunctionClickListener) {
        mChatMoreFunctionClickListener = chatMoreFunctionClickListener;
    }

    public void setDatas(List<ChatMoreFunctionBean> list) {
        mDataList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_meet_chat_function_more, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CustomViewHolder viewHolder = (CustomViewHolder) holder;
        final ChatMoreFunctionBean msg = mDataList.get(position);
        if (msg.functionType == 6) {
            viewHolder.chat_function_more_checkbox.setVisibility(View.VISIBLE);
            viewHolder.chat_function_more_image.setVisibility(View.GONE);
            viewHolder.chat_function_more_checkbox.setBackgroundResource(msg.functionDrawable);
            if (SP.getBoolean(sessionID+AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false)) {
                viewHolder.chat_function_more_checkbox.setChecked(true);
            } else {
                viewHolder.chat_function_more_checkbox.setChecked(false);
            }
            viewHolder.chat_function_more_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SP.putBoolean(sessionID+AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, isChecked);
                }
            });
        } else {
            viewHolder.chat_function_more_checkbox.setVisibility(View.GONE);
            viewHolder.chat_function_more_image.setVisibility(View.VISIBLE);
            viewHolder.chat_function_more_image.setBackgroundResource(msg.functionDrawable);

        }
        viewHolder.chat_function_more_txt.setText(msg.funDescribe);
        viewHolder.chat_function_more_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mChatMoreFunctionClickListener) {
                    mChatMoreFunctionClickListener.onItemClick(msg);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout chat_function_more_item;
        ImageView chat_function_more_image;
        CheckBox chat_function_more_checkbox;

        TextView chat_function_more_txt;

        private CustomViewHolder(View itemView) {
            super(itemView);
            chat_function_more_item = itemView.findViewById(R.id.chat_function_more_item);
            chat_function_more_image = itemView.findViewById(R.id.chat_function_more_image);
            chat_function_more_checkbox = itemView.findViewById(R.id.chat_function_more_checkbox);
            chat_function_more_txt = itemView.findViewById(R.id.chat_function_more_txt);
        }
    }

    public interface ChatMoreFunctionClickListener {
        void onItemClick(ChatMoreFunctionBean chatMoreFunctionBean);
    }

}
