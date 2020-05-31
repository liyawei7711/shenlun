package huaiye.com.vim.ui.meet.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.media.player.sdk.params.user.UserReal;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;

import java.util.ArrayList;

import huaiye.com.vim.R;

/**
 * Created by ywt on 2019/3/5.
 */

public class MeetMultipleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<CGetMeetingInfoRsp.UserInfo> mDataList;
    private int mItemHeight;

    public MeetMultipleAdapter(Context context, int itemHeight) {
        mContext = context;
        mItemHeight = itemHeight;
        Log.d("VIMApp", "mItemHeight=" + mItemHeight);
    }

    public void setDatas(ArrayList<CGetMeetingInfoRsp.UserInfo> listUser) {
        mDataList = listUser;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(mContext).inflate(R.layout.meet_multiple_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final CustomViewHolder viewHolder = (CustomViewHolder) holder;
        final UserReal videoParams = Player.Params.TypeUserReal();
        Log.d("VIMApp", "videoParams=" + videoParams);
        videoParams.setUserDomainCode(mDataList.get(position).strUserDomainCode)
                .setUserTokenID(mDataList.get(position).strUserTokenID)
                .setPreview(viewHolder.multiple_textureView)
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        switch (msg.GetMessageType()) {
                            case SdkMsgNotifyPlayStatus.SelfMessageId:
                                /*SdkMsgNotifyPlayStatus playStatus = (SdkMsgNotifyPlayStatus) msg;
                                Log.d("VIMApp", "param=" + param);
                                Log.d("VIMApp", "isStopped=" + playStatus.isStopped());
                                if (playStatus.isStopped()) {
                                    if (!playStatus.isOperationFromUser()) {
                                        HYClient.getHYPlayer().stopPlayEx(null, viewHolder.multiple_textureView);
                                        viewHolder.multiple_textureView.setVisibility(View.GONE);
                                        viewHolder.video_close_layout.setVisibility(View.VISIBLE);
                                        viewHolder.meet_member_name.setText(mDataList.get(position).strUserName);
                                    }
                                } else {
                                    viewHolder.video_close_layout.setVisibility(View.GONE);
                                    viewHolder.multiple_textureView.setVisibility(View.VISIBLE);
                                    HYClient.getHYPlayer().startPlay(videoParams);
                                }*/
                                break;
                        }
                    }
                });
        //只播放自己的画面
        if (HYClient.getSdkOptions().User().getUserId().equals(mDataList.get(position).strUserID)) {
            videoParams.setAudioOn(false);
        }
        HYClient.getHYPlayer().startPlay(videoParams);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextureView multiple_textureView;
        private LinearLayout item_root;
        private LinearLayout video_close_layout;
        private TextView meet_member_name;

        private CustomViewHolder(View itemView) {
            super(itemView);
            item_root = (LinearLayout) itemView.findViewById(R.id.item_root);
            video_close_layout = (LinearLayout) itemView.findViewById(R.id.video_close_layout);
            multiple_textureView = (TextureView) itemView.findViewById(R.id.multiple_textureView);
            meet_member_name = (TextView) itemView.findViewById(R.id.meet_member_name);
            ViewGroup.LayoutParams params = item_root.getLayoutParams();
            params.height = mItemHeight;
            item_root.setLayoutParams(params);
        }
    }
}
