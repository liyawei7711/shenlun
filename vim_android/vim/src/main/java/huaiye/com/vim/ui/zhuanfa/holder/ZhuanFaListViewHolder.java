package huaiye.com.vim.ui.zhuanfa.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.ChatUtil;
import huaiye.com.vim.common.utils.WeiXinDateFormat;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatSingleMsgBean;
import huaiye.com.vim.dao.msgs.ContentBean;
import huaiye.com.vim.dao.msgs.SendMsgUserBean;
import huaiye.com.vim.dao.msgs.VimMessageListBean;

import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_ADDRESS;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_AUDIO_FILE;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_FILE;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_GROUP_MEET;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_IMG;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_TEXT;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_VIDEO_FILE;
import static huaiye.com.vim.common.AppUtils.NOTIFICATION_TYPE_DEVICE_PUSH;
import static huaiye.com.vim.common.AppUtils.NOTIFICATION_TYPE_PERSON_PUSH;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: ChatViewHolder
 */
public class ZhuanFaListViewHolder extends LiteViewHolder {
    @BindView(R.id.view_divider)
    View view_divider;

    @BindView(R.id.left_Image)
    ImageView left_Image;
    @BindView(R.id.item_name)
    TextView item_name;
    @BindView(R.id.message_history_bg)
    RelativeLayout message_history_bg;

    String strUserID;
    String strUserDomainCode;

    private RequestOptions requestFriendHeadOptions;
    private RequestOptions requestGroupHeadOptions;


    public ZhuanFaListViewHolder(Context context, View view, View.OnClickListener ocl) {
        super(context, view, ocl);
        requestFriendHeadOptions = new RequestOptions();
        requestFriendHeadOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());
        requestGroupHeadOptions = new RequestOptions();
        requestGroupHeadOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.ic_group_chat)
                .error(R.drawable.ic_group_chat)
                .optionalTransform(new CircleCrop());

        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        VimMessageListBean bean = (VimMessageListBean) data;
        itemView.setTag(bean);

        item_name.setText(bean.sessionName);

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }

        if (bean.nMsgTop == 1) {
            message_history_bg.setBackgroundResource(R.color.back_feeeee);
        } else {
            message_history_bg.setBackgroundResource(R.color.white);
        }

        setHeadImage(left_Image, bean);
    }

    private void setHeadImage(ImageView headPicView, VimMessageListBean bean) {
        Glide.with(context)
                .load(AppDatas.Constants().getAddressWithoutPort() + bean.strHeadUrl)
                .apply(bean.groupType == 1 ? requestGroupHeadOptions : requestFriendHeadOptions)
                .into(headPicView);
    }

}
