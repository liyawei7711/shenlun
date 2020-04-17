package huaiye.com.vim.ui.chat.holder;

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
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;

import java.util.List;

import butterknife.BindView;
import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.helper.ChatContactsGroupUserListHelper;
import huaiye.com.vim.common.recycle.LiteViewHolder;
import huaiye.com.vim.common.utils.ChatUtil;
import huaiye.com.vim.common.utils.WeiXinDateFormat;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.msgs.ContentBean;
import huaiye.com.vim.dao.msgs.VimMessageListBean;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_ADDRESS;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_AUDIO_FILE;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_FILE;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_GROUP_MEET;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_IMG;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_JINJI;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_SHARE;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_TEXT;
import static huaiye.com.vim.common.AppUtils.MESSAGE_TYPE_VIDEO_FILE;
import static huaiye.com.vim.common.AppUtils.NOTIFICATION_TYPE_DEVICE_PUSH;
import static huaiye.com.vim.common.AppUtils.NOTIFICATION_TYPE_PERSON_PUSH;
import static huaiye.com.vim.common.AppUtils.getString;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;
import static huaiye.com.vim.ui.meet.adapter.ChatContentAdapter.CHAT_CONTENT_CUSTOM_NOTICE_ITEM;

/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: ChatViewHolder
 */
public class ChatListViewHolder extends LiteViewHolder {
    @BindView(R.id.tv_notice)
    View tv_notice;
    @BindView(R.id.view_divider)
    View view_divider;
    @BindView(R.id.view_point)
    TextView view_point;
    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.left_Image)
    ImageView left_Image;
    @BindView(R.id.item_name)
    TextView item_name;
    @BindView(R.id.item_content)
    TextView item_content;
    @BindView(R.id.message_history_bg)
    RelativeLayout message_history_bg;
    @BindView(R.id.message_history_miandarao)
    ImageView message_history_miandarao;

    String strUserID;
    String strUserDomainCode;

    private RequestOptions requestFriendHeadOptions;
    private RequestOptions requestGroupHeadOptions;


    public ChatListViewHolder(Context context, View view, View.OnClickListener ocl) {
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

        time.setVisibility(View.VISIBLE);

        try {
            time.setText(WeiXinDateFormat.getChatTimeYingWen(bean.time));
        } catch (Exception e) {
        }


        if (TextUtils.isEmpty(bean.sessionName)) {
            if (ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(bean.groupID + "") != null) {
                bean.sessionName = ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(bean.groupID).strGroupName;
            } else {
                ModelApis.Contacts().requestqueryGroupChatInfo(bean.groupDomainCode, bean.groupID,
                        new ModelCallback<ContactsGroupUserListBean>() {
                            @Override
                            public void onSuccess(final ContactsGroupUserListBean contactsBean) {
                                if (contactsBean != null) {
                                    ChatContactsGroupUserListHelper.getInstance().cacheContactsGroupDetail(bean.groupID + "", contactsBean);
                                }
                                if (null != contactsBean && null != contactsBean.lstGroupUser && contactsBean.lstGroupUser.size() > 0) {
                                    StringBuilder sb = new StringBuilder("");
                                    for (ContactsGroupUserListBean.LstGroupUser temp : contactsBean.lstGroupUser) {
                                        sb.append(temp.strUserName + "、");
                                    }
                                    if (null != sb && sb.indexOf("、") >= 0) {
                                        sb.deleteCharAt(sb.lastIndexOf("、"));
                                    }
                                    bean.sessionName = sb.toString();
                                    item_name.setText(bean.sessionName);
                                }
                            }

                            @Override
                            public void onFailure(HTTPResponse httpResponse) {
                                super.onFailure(httpResponse);
                            }
                        });
            }
        }
        item_name.setText(bean.sessionName);
        //session=0为广播
        if (1 == bean.bFire && (AppUtils.MESSAGE_TYPE_TEXT == bean.type || AppUtils.MESSAGE_TYPE_IMG == bean.type || AppUtils.MESSAGE_TYPE_AUDIO_FILE == bean.type || AppUtils.MESSAGE_TYPE_VIDEO_FILE == bean.type)) {
            item_content.setText("[" + AppUtils.getString(R.string.yuehoujifen) + "]");
        } else if (bean.type == MESSAGE_TYPE_IMG) {
            item_content.setText("[" + AppUtils.getString(R.string.img) + "]");
//            left_Image.setImageResource("0".equals(bean.sessionID)?R.drawable.zhilingdiaodu_guangbo:R.drawable.zhilingdiaodu_putongxiaoxi);
        } else if (bean.type == MESSAGE_TYPE_FILE) {
            item_content.setText("[" + AppUtils.getString(R.string.notice_file) + "]");
//            left_Image.setImageResource("0".equals(bean.sessionID)?R.drawable.zhilingdiaodu_guangbo:R.drawable.zhilingdiaodu_putongxiaoxi);
        } else if (bean.type == MESSAGE_TYPE_TEXT) {
            showTextContent2(bean, item_content);
//            left_Image.setImageResource("0".equals(bean.sessionID)?R.drawable.zhilingdiaodu_guangbo:R.drawable.zhilingdiaodu_putongxiaoxi);
        } else if (bean.type == NOTIFICATION_TYPE_PERSON_PUSH || bean.type == NOTIFICATION_TYPE_DEVICE_PUSH) {
            if (bean.type == NOTIFICATION_TYPE_PERSON_PUSH) {
                item_content.setText("[" + AppUtils.getString(R.string.person_share) + "]");
            } else {
                item_content.setText("[" + AppUtils.getString(R.string.device_share) + "]");
            }
//            left_Image.setImageResource("0".equals(bean.sessionID)?R.drawable.zhilingdiaodu_guangbo:R.drawable.zhilingdiaodu_tuisong);
        } else if (bean.type == MESSAGE_TYPE_AUDIO_FILE) {
            item_content.setText("[" + AppUtils.getString(R.string.audio) + "]");
//            left_Image.setImageResource(R.drawable.zhilingdiaodu_guangbo);
        } else if (bean.type == MESSAGE_TYPE_VIDEO_FILE) {
            item_content.setText("[" + AppUtils.getString(R.string.video) + "]");
//            left_Image.setImageResource(R.drawable.zhilingdiaodu_guangbo);
        } else if (bean.type == MESSAGE_TYPE_SINGLE_CHAT_VOICE) {
            item_content.setText("[" + AppUtils.getString(R.string.chat_voice_content) + "]");
//            left_Image.setImageResource(R.drawable.zhilingdiaodu_guangbo);
        } else if (bean.type == MESSAGE_TYPE_SINGLE_CHAT_VIDEO) {
            item_content.setText("[" + AppUtils.getString(R.string.chat_video_content) + "]");
//            left_Image.setImageResource(R.drawable.zhilingdiaodu_guangbo);
        } else if (bean.type == MESSAGE_TYPE_ADDRESS) {
            item_content.setText("[" + AppUtils.getString(R.string.chat_address) + "]");
        } else if (bean.type == MESSAGE_TYPE_GROUP_MEET) {
            item_content.setText("[" + AppUtils.getString(R.string.chat_group_video) + "]");
        } else if (bean.type == MESSAGE_TYPE_JINJI) {
            item_content.setText("[" + AppUtils.getString(R.string.chat_jinji) + "]");
        } else if (bean.type == MESSAGE_TYPE_SHARE) {
            showTextContent2(bean, item_content);
        } else if (bean.type == CHAT_CONTENT_CUSTOM_NOTICE_ITEM) {
            item_content.setText(bean.msgTxt);
        } else {
            item_content.setText("");
        }

//        if (bean.isRead == 1) {
//            view_point.setVisibility(View.GONE);
//        } else {
//            view_point.setVisibility(View.VISIBLE);
//        }
        if (bean.groupType == 1) {
            int num = AppDatas.MsgDB()
                    .chatGroupMsgDao()
                    .getGroupUnreadNum(bean.groupID);
            if (num > 0) {
                view_point.setVisibility(View.VISIBLE);
            } else {
                view_point.setVisibility(View.GONE);
            }

            view_point.setText(num > 99 ? "99+" : num + "");
        } else {
            int num = AppDatas.MsgDB()
                    .chatSingleMsgDao()
                    .getUnreadNum(bean.sessionID);
            if (num > 0) {
                view_point.setVisibility(View.VISIBLE);
            } else {
                view_point.setVisibility(View.GONE);
            }

            view_point.setText(num > 99 ? "99+" : num + "");
        }

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
            tv_notice.setVisibility(View.VISIBLE);
        } else {
            view_divider.setVisibility(View.GONE);
            tv_notice.setVisibility(View.GONE);
        }

        if (bean.nMsgTop == 1) {
            message_history_bg.setBackgroundResource(R.color.back_feeeee);
        } else {
            message_history_bg.setBackgroundResource(R.color.white);

        }

        if (bean.nNoDisturb == 1) {
            message_history_miandarao.setVisibility(View.VISIBLE);
        } else {
            message_history_miandarao.setVisibility(View.GONE);

        }
        setHeadImage(left_Image, bean);
    }

    private void showTextContent2(VimMessageListBean bean, TextView textView) {
        if (!TextUtils.isEmpty(bean.msgTxt)) {
            if (bean.bEncrypt == 1 && !bean.isUnEncrypt) {
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    EncryptUtil.localEncryptText(bean.msgTxt, false,
                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                                    bean.isUnEncrypt = true;
                                    bean.mStrEncrypt = bean.msgTxt;
                                    ContentBean cb = ChatUtil.analysisChatContentJson(sessionRsp.m_lstData.get(0).strData);
                                    bean.msgID = cb.msgID;
                                    bean.msgTxt = cb.msgTxt;
                                    bean.fileUrl = cb.fileUrl;
                                    bean.bFire = cb.bFire;
                                    bean.fireTime = cb.fireTime;
                                    bean.fileSize = cb.fileSize;
                                    bean.nCallState = cb.nCallState;
                                    bean.nDuration = cb.nDuration;
                                    if (bean.type == MESSAGE_TYPE_SHARE) {
                                        textView.setText("[" + AppUtils.getString(R.string.chat_link) + "]" + bean.msgTxt);
                                    } else {
                                        textView.setText(bean.msgTxt + "");
                                    }
                                }

                                @Override
                                public void onError(ErrorInfo sessionRsp) {
                                    if (bean.type == MESSAGE_TYPE_SHARE) {
                                        textView.setText("[" + AppUtils.getString(R.string.chat_link) + "]" + getString(R.string.jiami_notice3));
                                    } else {
                                        textView.setText(getString(R.string.jiami_notice3));
                                    }
                                }
                            });
                } else {
                    if (bean.type == MESSAGE_TYPE_SHARE) {
                        textView.setText("[" + AppUtils.getString(R.string.chat_link) + "]" + getString(R.string.jiami_notice3));
                    } else {
                        textView.setText(getString(R.string.jiami_notice3));
                    }
                }
            } else {
                if (bean.type == MESSAGE_TYPE_SHARE) {
                    textView.setText("[" + AppUtils.getString(R.string.chat_link) + "]" + bean.msgTxt);
                } else {
                    textView.setText(bean.msgTxt);
                }
            }
        } else {
            if (bean.type == MESSAGE_TYPE_SHARE) {
                textView.setText("[" + AppUtils.getString(R.string.chat_link) + "]" + bean.msgTxt);
            } else {
                textView.setText(bean.msgTxt + "");
            }
        }
    }

    private void setHeadImage(ImageView headPicView, VimMessageListBean bean) {
        Glide.with(context)
                .load(AppDatas.Constants().getAddressWithoutPort() + bean.strHeadUrl)
                .apply(bean.groupType == 1 ? requestGroupHeadOptions : requestFriendHeadOptions)
                .into(headPicView);
        /*if(!TextUtils.isEmpty(bean.strHeadUrl)){

        }else{
            new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                @Override
                public Object doOnThread() {
                    if(bean.groupType==1){
                        bean.strHeadUrl = AppDatas.MsgDB().getGroupListDao().getGroupHeadPic(bean.groupID,bean.groupDomainCode);
                    }else{
                        //单聊
                        ArrayList<SendUserBean> messageUsers = bean.sessionUserList;
                        if (messageUsers != null && messageUsers.size() > 0) {
                            if (messageUsers.size() != 2) {
                                Logger.err("receive single chat list not 2 is " + messageUsers.size());
                                return "";
                            }
                            SendUserBean friend = null;
                            for(SendUserBean sendUserBean:messageUsers){
                                if(!sendUserBean.strUserID.equals(AppDatas.Auth().getUserID())){
                                    friend = sendUserBean;
                                    break;
                                }
                            }
                            bean.strHeadUrl = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(friend.strUserID,friend.strUserDomainCode);

                        }

                    }
                    return "";
                }

                @Override
                public void doOnMain(Object data) {
                    if(TextUtils.isEmpty(bean.strHeadUrl)){
                        headPicView.setImageResource(bean.groupType==1?R.drawable.ic_group_chat:R.drawable.default_image_personal);
                    }else{
                        Glide.with(context)
                                .load(AppDatas.Constants().getAddressWithoutPort() + bean.strHeadUrl)
                                .apply(bean.groupType==1?requestGroupHeadOptions:requestFriendHeadOptions)
                                .into(headPicView);
                    }

                }
            });

        }*/

    }

}
