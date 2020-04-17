package huaiye.com.vim.ui.meet.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.RefMessageList;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.glide.GlideRoundedCornersTransform;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.WeiXinDateFormat;
import huaiye.com.vim.common.views.PopupWindowList;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import huaiye.com.vim.dao.msgs.ChatUtil;
import huaiye.com.vim.dao.msgs.SendMsgUserBean;
import huaiye.com.vim.dao.msgs.UserInfo;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.ui.chat.YueHouJiFengAudioActivity;
import huaiye.com.vim.ui.chat.YueHouJiFengImgActivity;
import huaiye.com.vim.ui.chat.YueHouJiFengTextActivity;
import huaiye.com.vim.ui.chat.YueHouJiFengVideoActivity;
import huaiye.com.vim.ui.chat.dialog.Go2DaoHangDialog;
import huaiye.com.vim.ui.meet.ImageShowActivity;
import huaiye.com.vim.ui.meet.MediaLocalVideoPlayActivity;
import huaiye.com.vim.ui.sendBaiduLocation.function.activity.MapLocationActivity;
import huaiye.com.vim.ui.showfile.ExcelActivity;
import huaiye.com.vim.ui.showfile.WebPageFileActivity;
import huaiye.com.vim.ui.zhuanfa.ZhuanFaChooseActivity;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * Created by ywt on 2019/4/2.
 */

public class ChatContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int LEFT_MESSAGE_TYPE_TEXT = 11;
    private final static int LEFT_MESSAGE_TYPE_IMAGE = 12;
    private final static int LEFT_MESSAGE_TYPE_VIDEO = 13;
    private final static int LEFT_MESSAGE_TYPE_VOICE = 14;
    private final static int LEFT_MESSAGE_TYPE_FILE = 15;
    private final static int LEFT_MESSAGE_TYPE_YUEHOUJIFENG = 16;
    private final static int LEFT_MESSAGE_TYPE_ADDRESS = 17;
    private final static int LEFT_MESSAGE_TYPE_SHARE = 18;

    private final static int RIGHT_MESSAGE_TYPE_TEXT = 21;
    private final static int RIGHT_MESSAGE_TYPE_IMAGE = 22;
    private final static int RIGHT_MESSAGE_TYPE_VIDEO = 23;
    private final static int RIGHT_MESSAGE_TYPE_VOICE = 24;
    private final static int RIGHT_MESSAGE_TYPE_FILE = 25;
    private final static int RIGHT_MESSAGE_TYPE_YUEHOUJIFENGH = 26;
    private final static int RIGHT_MESSAGE_TYPE_ADDRESS = 27;
    private final static int RIGHT_MESSAGE_TYPE_SHARE = 28;

    private final static int CHAT_CONTENT_CUSTOM_MEET_ITEM = 30;

    public final static int CHAT_CONTENT_CUSTOM_NOTICE_ITEM = 66;
    public final static int CHAT_CONTENT_CUSTOM_QIUJIU_ITEM = 67;

    private Context mContext;
    private RequestOptions requestOptions;
    private RequestOptions requestOptionsAddress;
    private RequestOptions requestHeadOptions;
    private RequestListener requestListener;
    private List<? extends ChatMessageBase> mDataList;

    private int lastDealposition = -1;

    private boolean isGroup;
    private String strGroupID;
    private String strGroupDomain;
    private ArrayList<SendUserBean> mMessageUsersDate;
    private PopupWindowList mPopupWindowList;

    File fC;
    private static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/msword"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".JPEG", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            //{".xml",    "text/xml"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/zip"},
            {"", "*/*"}
    };
    private String strUserID;
    private String strUserDomainCode;
    private ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();
    private ArrayList<UserInfo> usersTrans = new ArrayList<>();
    private ArrayList<File> linShiFile = new ArrayList<>();

    public void resetLastDealposition() {
        this.lastDealposition = -1;
    }

    private String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0)
            return type;
        /* 获取文件的后缀名 */
        String fileType = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (fileType == null || "".equals(fileType))
            return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (fileType.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public ChatContentAdapter(Context context, boolean isGroup, String strGroupID, String strGroupDomain, ArrayList<SendUserBean> mMessageUsersDate) {
        mContext = context;
        this.isGroup = isGroup;
        this.strGroupID = strGroupID;
        this.strGroupDomain = strGroupDomain;
        this.mMessageUsersDate = mMessageUsersDate;
        fC = new File(mContext.getExternalFilesDir(null) + File.separator + "Vim/chat/");
        if (!fC.exists()) {
            fC.mkdirs();
        }
        requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.icon_image_error)
//                .skipMemoryCache(false)
                .error(R.drawable.icon_image_error)
                .optionalTransform(new GlideRoundedCornersTransform(8,
                        GlideRoundedCornersTransform.CornerType.ALL));
        requestOptionsAddress = new RequestOptions();
        requestOptionsAddress.centerCrop()
                .dontAnimate()
//                .skipMemoryCache(false)
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.icon_image_error)
                .error(R.drawable.icon_image_error);
        requestHeadOptions = new RequestOptions();
        requestHeadOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());

        requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    //加载一次
                    ((GifDrawable) resource).setLoopCount(100);
                }
                return false;
            }
        };
    }

    public void setDatas(List<? extends ChatMessageBase> list) {
        mDataList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case LEFT_MESSAGE_TYPE_TEXT:
                viewHolder = new LeftCustomTextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_left_text_item, parent, false));
                break;
            case LEFT_MESSAGE_TYPE_IMAGE:
                viewHolder = new LeftCustomImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_left_image_item, parent, false));
                break;
            case LEFT_MESSAGE_TYPE_VIDEO:
                viewHolder = new LeftCustomVideoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_left_video_item, parent, false));
                break;
            case LEFT_MESSAGE_TYPE_VOICE:
                viewHolder = new LeftCustomVoiceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_left_voice_item, parent, false));
                break;
            case LEFT_MESSAGE_TYPE_FILE:
                viewHolder = new LeftCustomFileViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_left_file_item, parent, false));
                break;
            case LEFT_MESSAGE_TYPE_YUEHOUJIFENG:
                viewHolder = new LeftCustomYueHouJiFengViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_left_yuehoujifeng_item, parent, false));
                break;
            case LEFT_MESSAGE_TYPE_ADDRESS:
                viewHolder = new LeftCustomAddressViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_left_address_item, parent, false));
                break;
            case LEFT_MESSAGE_TYPE_SHARE:
                viewHolder = new LeftCustomShareViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_left_share_item, parent, false));
                break;
            case RIGHT_MESSAGE_TYPE_TEXT:
                viewHolder = new RightCustomTextViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_right_text_item, parent, false));
                break;
            case RIGHT_MESSAGE_TYPE_IMAGE:
                viewHolder = new RightCustomImageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_right_image_item, parent, false));
                break;
            case RIGHT_MESSAGE_TYPE_VIDEO:
                viewHolder = new RightCustomVideoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_right_video_item, parent, false));
                break;
            case RIGHT_MESSAGE_TYPE_FILE:
                viewHolder = new RightCustomFileViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_right_file_item, parent, false));
                break;
            case RIGHT_MESSAGE_TYPE_VOICE:
                viewHolder = new RightCustomVoiceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_right_voice_item, parent, false));
                break;
            case RIGHT_MESSAGE_TYPE_YUEHOUJIFENGH:
                viewHolder = new RightCustomYueHouJiFengViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_right_yuehoujifeng_item, parent, false));
                break;
            case RIGHT_MESSAGE_TYPE_ADDRESS:
                viewHolder = new RightCustomAddressViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_right_address_item, parent, false));
                break;
            case RIGHT_MESSAGE_TYPE_SHARE:
                viewHolder = new RightCustomShareViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_right_share_item, parent, false));
                break;
            case CHAT_CONTENT_CUSTOM_MEET_ITEM:
                viewHolder = new CustomMeetViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_custom_meet_item, parent, false));
                break;
            case CHAT_CONTENT_CUSTOM_NOTICE_ITEM:
                viewHolder = new NoticeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_custom_notice_item, parent, false));
                break;
            case CHAT_CONTENT_CUSTOM_QIUJIU_ITEM:
                viewHolder = new QiuJiuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.chat_content_custom_qiujiu_item, parent, false));
                break;
            default:
                break;
        }

        return viewHolder;
    }

    void initUser(ChatMessageBase data) {
        users.clear();
        usersTrans.clear();
        if (isGroup) {
            if (mMessageUsersDate != null) {
                for (SendUserBean temp : mMessageUsersDate) {
                    if (!AppAuth.get().getUserID().equals(temp.strUserID)) {
                        usersTrans.add(new UserInfo(temp.strUserID, temp.strUserDomainCode));
                    }
                }
            }

            if (AppAuth.get().getUserID().equals(data.fromUserId)) {
                SendMsgUserBean sendMsgUserBean = AppDatas.MsgDB().getSendUserListDao().getSendUserInfo(data.sessionID);
                if (sendMsgUserBean != null) {
                    SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                    info.strUserDomainCode = sendMsgUserBean.strUserDomainCode;
                    info.strUserID = sendMsgUserBean.strUserID;
                    users.add(info);
                }
            } else {
                SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                info.strUserDomainCode = data.fromUserDomain;
                info.strUserID = data.fromUserId;
                users.add(info);
            }
        } else {
            SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
            info.strUserDomainCode = strUserDomainCode;
            info.strUserID = strUserID;
            users.add(info);

            usersTrans.add(new UserInfo(strUserID, strUserDomainCode));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ChatMessageBase data = mDataList.get(position);
        initUser(data);
        OnChatItemLongClickListener onLongClick = new OnChatItemLongClickListener(holder, data);
        if (null == data) {
            return;
        }
        boolean needShowTime = true;
        if (position > 0) {
            long previousTime = mDataList.get(position - 1).time;
            long currentTime = data.time;
            if (currentTime - previousTime < 60 * 1000) {
                needShowTime = false;
            } else {
                needShowTime = true;
            }

        }
        if (needShowTime || data.needShowTime) {
            needShowTime = true;
            data.needShowTime = needShowTime;
        }
        unEncrypt(holder, position, data, onLongClick);
    }

    private void unEncrypt(RecyclerView.ViewHolder holder, int position, ChatMessageBase data, OnChatItemLongClickListener onLongClick) {
        if (holder instanceof CustomMeetViewHolder) {
            dealCustomMeetViewHolder(holder, data, onLongClick);//
        } else if (holder instanceof LeftCustomTextViewHolder) {
            dealLeftCustomTextViewHolder(holder, data, onLongClick);//
        } else if (holder instanceof LeftCustomImageViewHolder) {
            dealLeftCustomImageViewHolder(holder, data, onLongClick);//
        } else if (holder instanceof LeftCustomVideoViewHolder) {
            dealLeftCustomVideoViewHolder(holder, data, onLongClick, position);//
        } else if (holder instanceof LeftCustomVoiceViewHolder) {
            dealLeftCustomVoiceViewHolder(holder, data, onLongClick, position);//
        } else if (holder instanceof LeftCustomFileViewHolder) {
            dealLeftCustomFileViewHolder(holder, data, onLongClick, position);//
        } else if (holder instanceof LeftCustomYueHouJiFengViewHolder) {
            dealLeftCustomYueHouJiFengViewHolder(holder, data, onLongClick, position);
        } else if (holder instanceof LeftCustomAddressViewHolder) {
            dealLeftCustomAddressViewHolder(holder, data, onLongClick, position);
        } else if (holder instanceof LeftCustomShareViewHolder) {
            dealLeftCustomShareViewHolder(holder, data, onLongClick, position);
        } else if (holder instanceof RightCustomTextViewHolder) {
            dealRightCustomTextViewHolder(holder, data, onLongClick, position);//
        } else if (holder instanceof RightCustomImageViewHolder) {
            dealRightCustomImageViewHolder(holder, data, onLongClick, position);//
        } else if (holder instanceof RightCustomVideoViewHolder) {
            dealRightCustomVideoViewHolder(holder, data, onLongClick, position);//
        } else if (holder instanceof RightCustomVoiceViewHolder) {
            dealRightCustomVoiceViewHolder(holder, data, onLongClick, position);//
        } else if (holder instanceof RightCustomFileViewHolder) {
            dealRightCustomFileViewHolder(holder, data, onLongClick, position);//
        } else if (holder instanceof RightCustomYueHouJiFengViewHolder) {
            dealRightCustomYueHouJiFengViewHolder(holder, data, onLongClick, position);
        } else if (holder instanceof RightCustomAddressViewHolder) {
            dealRightCustomAddressViewHolder(holder, data, onLongClick, position);
        } else if (holder instanceof RightCustomShareViewHolder) {
            dealRightCustomShareViewHolder(holder, data, onLongClick, position);
        } else if (holder instanceof NoticeViewHolder) {
            dealCustomNoticeViewHolder(holder, data);
        } else if (holder instanceof QiuJiuViewHolder) {
            dealCustomQiuJiuViewHolder(holder, data, onLongClick, position);
        }
    }

    private void dealLeftCustomShareViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        LeftCustomShareViewHolder viewHolder = (LeftCustomShareViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.left_name);

        if (data.bEncrypt == 1 && !data.isUnEncrypt) {
            viewHolder.left_title.setText("信息已加密");
            viewHolder.left_content_url.setText("信息已加密");
        } else {
            viewHolder.left_title.setText("【" + data.msgTxt + "】");
            viewHolder.left_content_url.setText(data.fileUrl);
        }

        viewHolder.left_content_all.setOnLongClickListener(onLongClick);
        viewHolder.left_content_all.setOnClickListener(new OnShareClicked(data));

        if (data.needShowTime) {
            viewHolder.left_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.left_time.setVisibility(View.GONE);
        }
    }

    private void dealLeftCustomAddressViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        LeftCustomAddressViewHolder viewHolder = (LeftCustomAddressViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);
        showUserName(viewHolder.left_name);
        viewHolder.left_content.setText(data.msgTxt);

        if (data.bEncrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                String fileLocal = "";
                try {
                    fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                } catch (Exception e) {

                }

                Glide.with(mContext)
                        .load(R.drawable.icon_image_error)
                        .apply(requestOptionsAddress)
                        .into(viewHolder.address_img);

                final File ffLocal = new File(fileLocal);
                if (null != data && ffLocal.exists()) {
                    data.localFilePath = ffLocal.getAbsolutePath();
                    unEncryptImage2(viewHolder.address_img, data, true);
                } else {
                    if ("文件上传失败".equals(data.fileUrl)) {
                        return;
                    }
                    data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                    data.localFilePath = "";
                    String finalFileLocal = fileLocal;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, finalFileLocal, data.type)) {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                        data.localFilePath = ffLocal.getAbsolutePath();
                                        unEncryptImage2(viewHolder.address_img, data, true);
                                    }
                                }, "loadImageSuccess");
                            } else {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;
                                        data.localFilePath = "";
                                        updateDownloadState(data);
                                    }
                                }, "loadImageSuccess");
                            }
                        }
                    }).start();
                }
            } else {
                Glide.with(mContext)
                        .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
                        .apply(requestOptionsAddress)
                        .into(viewHolder.address_img);
            }
        } else {
            Glide.with(mContext)
                    .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
                    .apply(requestOptionsAddress)
                    .into(viewHolder.address_img);
        }
        viewHolder.left_content_layout.setOnClickListener(new OnAddressClicked(data, position, false));
        viewHolder.left_content_layout.setOnLongClickListener(onLongClick);

        if (data.needShowTime) {
            viewHolder.left_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.left_time.setVisibility(View.GONE);
        }
    }

    private void dealRightCustomShareViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        RightCustomShareViewHolder viewHolder = (RightCustomShareViewHolder) holder;
        viewHolder.right_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.right_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.right_name);

        if (data.bEncrypt == 1 && !data.isUnEncrypt) {
            viewHolder.right_title.setText("信息已加密");
            viewHolder.right_content_url.setText("信息已加密");
        } else {
            viewHolder.right_title.setText("【" + data.msgTxt + "】");
            viewHolder.right_content_url.setText(data.fileUrl);
        }

        viewHolder.right_content_all.setOnLongClickListener(onLongClick);
        viewHolder.right_content_all.setOnClickListener(new OnShareClicked(data));

        if (data.needShowTime) {
            viewHolder.right_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.right_time.setVisibility(View.GONE);
        }
    }

    private void dealRightCustomAddressViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        RightCustomAddressViewHolder viewHolder = (RightCustomAddressViewHolder) holder;
        viewHolder.right_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.right_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);
        showUserName(viewHolder.right_name);
        viewHolder.right_content.setText(data.msgTxt);

        if (data.bEncrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                String fileLocal = "";
                try {
                    fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                } catch (Exception e) {

                }
                Glide.with(mContext)
                        .load(R.drawable.icon_image_error)
                        .apply(requestOptionsAddress)
                        .into(viewHolder.address_img);

                final File ffLocal = new File(fileLocal);
                if (null != data && ffLocal.exists()) {
                    data.localFilePath = ffLocal.getAbsolutePath();
                    unEncryptImage2(viewHolder.address_img, data, true);
                } else {
                    if ("文件上传失败".equals(data.fileUrl)) {
                        return;
                    }
                    data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                    data.localFilePath = "";
                    String finalFileLocal = fileLocal;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, finalFileLocal, data.type)) {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                        data.localFilePath = ffLocal.getAbsolutePath();
                                        unEncryptImage2(viewHolder.address_img, data, true);
                                    }
                                }, "loadImageSuccess");
                            } else {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;
                                        data.localFilePath = "";
                                    }
                                }, "loadImageSuccess");
                            }
                        }
                    }).start();
                }
            } else {
                if (!TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
                    Glide.with(mContext)
                            .load(new File(data.localFilePath))
                            .apply(requestOptionsAddress)
                            .into(viewHolder.address_img);
                } else {
                    Glide.with(mContext)
                            .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
                            .apply(requestOptionsAddress)
                            .into(viewHolder.address_img);
                }
            }
        } else {
            if (!TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
                Glide.with(mContext)
                        .load(new File(data.localFilePath))
                        .apply(requestOptionsAddress)
                        .into(viewHolder.address_img);
            } else {
                Glide.with(mContext)
                        .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
                        .apply(requestOptionsAddress)
                        .into(viewHolder.address_img);
            }
        }
        viewHolder.right_content_layout.setOnLongClickListener(onLongClick);
        viewHolder.right_content_layout.setOnClickListener(new OnAddressClicked(data, position, false));

        if (data.needShowTime) {
            viewHolder.right_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.right_time.setVisibility(View.GONE);
        }
    }

    private void dealLeftCustomYueHouJiFengViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        LeftCustomYueHouJiFengViewHolder viewHolder = (LeftCustomYueHouJiFengViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.left_name);

        if (isGroup) {
            viewHolder.left_yuehoujifeng_look.setVisibility(View.VISIBLE);
        } else {
            viewHolder.left_yuehoujifeng_look.setVisibility(View.GONE);
        }
        viewHolder.left_yuehoujifeng_img.setImageResource(R.drawable.yuehoujifen_weichakan);
        viewHolder.left_content_yuehoujifeng_lin.setOnClickListener(new OnYueHouJiFengClicked(holder, data, position, true));
        viewHolder.left_content_yuehoujifeng_lin.setOnLongClickListener(onLongClick);
    }

    private void dealRightCustomYueHouJiFengViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        RightCustomYueHouJiFengViewHolder viewHolder = (RightCustomYueHouJiFengViewHolder) holder;
        viewHolder.right_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.right_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.right_name);

        if (data.downloadState == AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING) {
            viewHolder.chat_download_pgb.setVisibility(View.VISIBLE);
            viewHolder.right_content_yuehoujifeng_lin.setClickable(false);
        } else {
            viewHolder.chat_download_pgb.setVisibility(View.GONE);
            viewHolder.right_content_yuehoujifeng_lin.setClickable(true);
        }
        if (isGroup) {
            viewHolder.right_yuehoujifeng_look.setVisibility(View.VISIBLE);
        } else {
            viewHolder.right_yuehoujifeng_look.setVisibility(View.GONE);
        }
        viewHolder.right_yuehoujifeng_img.setImageResource(R.drawable.yuehoujifen_weichakan);
        viewHolder.right_content_yuehoujifeng_lin.setOnLongClickListener(onLongClick);

        if (data.needShowTime) {
            viewHolder.right_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.right_time.setVisibility(View.GONE);
        }
    }

    private void dealRightCustomFileViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        RightCustomFileViewHolder viewHolder = (RightCustomFileViewHolder) holder;
        viewHolder.right_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.right_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.right_name);

        viewHolder.right_content_file_lin.setOnClickListener(new OnFileClicked(holder, data, position, false));
        viewHolder.right_content_file_lin.setOnLongClickListener(onLongClick);

        viewHolder.right_content_file.setText(data.fileName == null ? data.fileUrl.substring(data.fileUrl.lastIndexOf("_") + 1) : data.fileName);
    }

    private void dealRightCustomVoiceViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        final RightCustomVoiceViewHolder viewHolder = (RightCustomVoiceViewHolder) holder;
        viewHolder.right_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.right_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.right_name);

        viewHolder.right_content_voice.setBackground(null);
        viewHolder.right_content_voice.setBackgroundResource(R.drawable.anim_chat_voice_white);
        AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.right_content_voice.getBackground();
        viewHolder.right_content_voice.setBackground(animationDrawable);
        dealAnimation(animationDrawable, data.isPlaying);
        viewHolder.right_content_voice_state.setVisibility(View.GONE);

        if (data.nDuration > 0 && !TextUtils.isEmpty(data.fileUrl)) {
            viewHolder.right_content_voice_lin.setOnClickListener(new OnVoiceClicked(holder, data, data.fileUrl, position, false));
            viewHolder.right_content_voice_lin.setOnLongClickListener(onLongClick);
        }
    }

    private void dealRightCustomVideoViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        RightCustomVideoViewHolder viewHolder = (RightCustomVideoViewHolder) holder;
        viewHolder.right_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.right_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.right_name);

        viewHolder.right_content_video_image.setOnClickListener(new OnVideoClicked(holder, data, position, false));
        viewHolder.right_content_video_image.setOnLongClickListener(onLongClick);

    }

    private void dealRightCustomImageViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        RightCustomImageViewHolder viewHolder = (RightCustomImageViewHolder) holder;
        viewHolder.right_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.right_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.right_name);

        if (data.bEncrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                String fileLocal = "";
                try {
                    fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                } catch (Exception e) {

                }

                Glide.with(mContext)
                        .load(R.drawable.icon_image_error)
                        .apply(requestOptions)
                        .into(viewHolder.right_content_image);

                final File ffLocal = new File(fileLocal);
                if (null != data && ffLocal.exists()) {
                    data.localFilePath = ffLocal.getAbsolutePath();
                    unEncryptImage2(viewHolder.right_content_image, data, false);
                } else {
                    if ("文件上传失败".equals(data.fileUrl)) {
                        return;
                    }
                    data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                    data.localFilePath = "";
                    String finalFileLocal = fileLocal;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, finalFileLocal, data.type)) {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                        data.localFilePath = ffLocal.getAbsolutePath();
                                        unEncryptImage2(viewHolder.right_content_image, data, false);
                                    }
                                }, "loadImageSuccess");
                            } else {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;
                                        data.localFilePath = "";
                                    }
                                }, "loadImageSuccess");

                            }
                        }
                    }).start();
                }
            } else {
                if (data.fileUrl.endsWith(".gif")) {
                    Glide.with(mContext).load(AppDatas.Constants().getFileServerURL() + data.fileUrl).listener(requestListener).into(viewHolder.right_content_image);
                } else {
                    Glide.with(mContext)
                            .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
                            .apply(requestOptions)
                            .into(viewHolder.right_content_image);
                }
                viewHolder.right_content_image.setOnClickListener(new OnImageViewClicked(AppDatas.Constants().getFileServerURL() + data.fileUrl, false));
            }
        } else {
            if (data.fileUrl.endsWith(".gif")) {
                Glide.with(mContext).load(AppDatas.Constants().getFileServerURL() + data.fileUrl).listener(requestListener).into(viewHolder.right_content_image);
            } else {
                Glide.with(mContext)
                        .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
                        .apply(requestOptions)
                        .into(viewHolder.right_content_image);
            }
            viewHolder.right_content_image.setOnClickListener(new OnImageViewClicked(AppDatas.Constants().getFileServerURL() + data.fileUrl, false));
        }

        viewHolder.right_content_image.setOnLongClickListener(onLongClick);

        if (data.needShowTime) {
            viewHolder.right_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.right_time.setVisibility(View.GONE);
        }
    }

    private void dealRightCustomTextViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        RightCustomTextViewHolder viewHolder = (RightCustomTextViewHolder) holder;
        viewHolder.right_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.right_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.right_name);
        dealTextContentView(viewHolder.right_content, data, true);
        viewHolder.right_content.setOnLongClickListener(onLongClick);

        if (data.needShowTime) {
            viewHolder.right_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.right_time.setVisibility(View.GONE);
        }
    }

    private void dealLeftCustomFileViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        LeftCustomFileViewHolder viewHolder = (LeftCustomFileViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.left_name);

        viewHolder.left_content_file_lin.setOnClickListener(new OnFileClicked(holder, data, position, true));
        viewHolder.left_content_file_lin.setOnLongClickListener(onLongClick);
        viewHolder.left_content_file.setText(data.fileName == null ? data.fileUrl.substring(data.fileUrl.lastIndexOf("_") + 1) : data.fileName);
    }

    private void dealLeftCustomVoiceViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        final LeftCustomVoiceViewHolder viewHolder = (LeftCustomVoiceViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.left_name);

        viewHolder.left_content_voice.setBackground(null);
        viewHolder.left_content_voice.setBackgroundResource(R.drawable.anim_chat_voice_gray);
        AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.left_content_voice.getBackground();
        viewHolder.left_content_voice.setBackground(animationDrawable);
        dealAnimation(animationDrawable, data.isPlaying);

        if (data.nDuration > 0 && !TextUtils.isEmpty(data.fileUrl)) {
            viewHolder.left_content_voice_lin.setOnClickListener(new OnVoiceClicked(holder, data, data.fileUrl, position, true));
            viewHolder.left_content_voice_lin.setOnLongClickListener(onLongClick);
        }
    }

    private void dealLeftCustomVideoViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        LeftCustomVideoViewHolder viewHolder = (LeftCustomVideoViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.left_name);

        viewHolder.left_content_video_image.setOnClickListener(new OnVideoClicked(holder, data, position, true));
        viewHolder.left_content_video_image.setOnLongClickListener(onLongClick);
    }

    private void dealLeftCustomImageViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick) {
        LeftCustomImageViewHolder viewHolder = (LeftCustomImageViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);

        showUserName(viewHolder.left_name);

        if (data.bEncrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                String fileLocal = "";
                try {
                    fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                } catch (Exception e) {

                }

                Glide.with(mContext)
                        .load(R.drawable.icon_image_error)
                        .apply(requestOptions)
                        .into(viewHolder.left_content_image);

                final File ffLocal = new File(fileLocal);
                if (null != data && ffLocal.exists()) {
                    data.localFilePath = ffLocal.getAbsolutePath();
                    unEncryptImage2(viewHolder.left_content_image, data, false);
                } else {
                    if ("文件上传失败".equals(data.fileUrl)) {
                        return;
                    }
                    data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                    data.localFilePath = "";
                    String finalFileLocal = fileLocal;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, finalFileLocal, data.type)) {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                        data.localFilePath = ffLocal.getAbsolutePath();
                                        unEncryptImage2(viewHolder.left_content_image, data, false);
                                    }
                                }, "loadImageSuccess");
                            } else {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;
                                        data.localFilePath = "";
                                        updateDownloadState(data);
                                    }
                                }, "loadImageSuccess");
                            }
                        }
                    }).start();
                }
            } else {
                Glide.with(mContext)
                        .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
                        .apply(requestOptions)
                        .into(viewHolder.left_content_image);
                viewHolder.left_content_image.setOnClickListener(new OnImageViewClicked(AppDatas.Constants().getFileServerURL() + data.fileUrl, false));
            }
        } else {
            if (data.fileUrl.endsWith(".gif")) {
                Glide.with(mContext).load(AppDatas.Constants().getFileServerURL() + data.fileUrl).listener(requestListener).into(viewHolder.left_content_image);
            } else {
                Glide.with(mContext)
                        .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
                        .apply(requestOptions)
                        .into(viewHolder.left_content_image);
            }
            viewHolder.left_content_image.setOnClickListener(new OnImageViewClicked(AppDatas.Constants().getFileServerURL() + data.fileUrl, false));
        }

        viewHolder.left_content_image.setOnLongClickListener(onLongClick);

        if (data.needShowTime) {
            viewHolder.left_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.left_time.setVisibility(View.GONE);
        }
    }

    private void dealLeftCustomTextViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick) {
        LeftCustomTextViewHolder viewHolder = (LeftCustomTextViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);
        showUserName(viewHolder.left_name);
        dealTextContentView(viewHolder.left_content, data, false);
        viewHolder.left_content.setOnLongClickListener(onLongClick);

        if (data.needShowTime) {
            viewHolder.left_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.left_time.setVisibility(View.GONE);
        }
    }

    private void setHeadImage(ImageView headPicView, ChatMessageBase data) {
        Glide.with(mContext)
                .load(AppDatas.Constants().getFileServerURL() + data.headPic)
                .apply(requestHeadOptions)
                .into(headPicView);
    }

    private void dealCustomMeetViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick) {
        CustomMeetViewHolder viewHolder = (CustomMeetViewHolder) holder;
        viewHolder.chat_content_custom_meet_item_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.chat_content_custom_meet_item_time.setOnLongClickListener(onLongClick);
        viewHolder.chat_content_custom_meet_item.setText(data.msgTxt);
        if (data.needShowTime) {
            viewHolder.chat_content_custom_meet_item_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.chat_content_custom_meet_item_time.setVisibility(View.GONE);
        }
    }

    private void dealCustomNoticeViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data) {
        NoticeViewHolder viewHolder = (NoticeViewHolder) holder;
        viewHolder.txt_notice.setText(data.msgTxt);
    }

    private void dealCustomQiuJiuViewHolder(RecyclerView.ViewHolder holder, ChatMessageBase data, OnChatItemLongClickListener onLongClick, int position) {
        QiuJiuViewHolder viewHolder = (QiuJiuViewHolder) holder;
        viewHolder.left_time.setText(WeiXinDateFormat.getChatTime(data.time));
        viewHolder.left_name.setText(data.fromUserName);
        setHeadImage(viewHolder.chat_head, data);
        showUserName(viewHolder.left_name);

        if (data.read == 1) {

        } else {
            Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
            long[] patter = {1000, 1000, 2000, 50};
            vibrator.vibrate(patter, -1);

            data.read = 1;
            AppDatas.MsgDB()
                    .chatSingleMsgDao()
                    .updateReadWithMsgID(data.fromUserId, AppAuth.get().getUserID(), data.msgID);
        }

        viewHolder.left_content_layout.setOnClickListener(new OnQiuJiuClicked(data, position, false));
        viewHolder.left_content_layout.setOnLongClickListener(onLongClick);

        if (data.needShowTime) {
            viewHolder.left_time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.left_time.setVisibility(View.GONE);
        }
    }

    private void dealTextContentView(TextView text, ChatMessageBase data, boolean isRight) {
        Drawable drawableShipin = mContext.getResources().getDrawable(R.drawable.liaotian_shipin);
        drawableShipin.setBounds(0, 0, drawableShipin.getMinimumWidth(), drawableShipin.getMinimumHeight());
        Drawable drawableyuyin = mContext.getResources().getDrawable(R.drawable.liaotian_yinpin);
        drawableyuyin.setBounds(0, 0, drawableyuyin.getMinimumWidth(), drawableyuyin.getMinimumHeight());
        switch (data.type) {
            case AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE:
                if (isRight) {
                    if (data.nCallState == 1) {
                        text.setText(AppUtils.getString(R.string.single_chat_video_voice_refuse_other));
                    } else if (data.nCallState == 0) {
                        text.setText(AppUtils.getString(R.string.single_chat_video_voice_cancle));
                    } else {
                        text.setText(mContext.getString(R.string.single_chat_video_voice_connection, data.msgTxt));
                    }
                    text.setCompoundDrawables(null, null, drawableyuyin, null);
                } else {
                    if (data.nCallState == 1) {
                        text.setText(AppUtils.getString(R.string.single_chat_video_voice_refuse));
                    } else if (data.nCallState == 0) {
                        text.setText(AppUtils.getString(R.string.single_chat_video_voice_cancle_other));
                    } else {
                        text.setText(mContext.getString(R.string.single_chat_video_voice_connection, data.msgTxt));
                    }
                    text.setCompoundDrawables(drawableyuyin, null, null, null);
                }
                break;
            case AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO:
                if (isRight) {
                    if (data.nCallState == 1) {
                        text.setText(AppUtils.getString(R.string.single_chat_video_voice_refuse_other));
                    } else if (data.nCallState == 0) {
                        text.setText(AppUtils.getString(R.string.single_chat_video_voice_cancle));
                    } else {
                        text.setText(mContext.getString(R.string.single_chat_video_voice_connection, data.msgTxt));
                    }
                    text.setCompoundDrawables(null, null, drawableShipin, null);
                } else {
                    if (data.nCallState == 1) {
                        text.setText(AppUtils.getString(R.string.single_chat_video_voice_refuse));
                    } else if (data.nCallState == 0) {
                        text.setText(AppUtils.getString(R.string.single_chat_video_voice_cancle_other));
                    } else {
                        text.setText(mContext.getString(R.string.single_chat_video_voice_connection, data.msgTxt));
                    }
                    text.setCompoundDrawables(drawableShipin, null, null, null);
                }
                break;
            default:
                if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                    text.setText("信息已加密");
                } else {
                    text.setText(data.msgTxt);
                }
                text.setCompoundDrawables(null, null, null, null);
                break;
        }
    }

    private void showUserName(TextView nameView) {
        if (isGroup) {
            nameView.setVisibility(View.VISIBLE);
        } else {
            nameView.setVisibility(View.GONE);
        }
    }

    public void setUserInfo(String strUserID, String strUserDomainCode) {
        this.strUserID = strUserID;
        this.strUserDomainCode = strUserDomainCode;
    }

    public void setCustomer(ArrayList<SendUserBean> mMessageUsersDate) {
        this.mMessageUsersDate = mMessageUsersDate;
    }

    private class OnYueHouJiFengClicked implements View.OnClickListener {
        private LeftCustomYueHouJiFengViewHolder viewHolderLeft;
        private RightCustomYueHouJiFengViewHolder viewHolderRight;
        private ChatMessageBase data;
        int positopn;
        boolean isLeft;

        public OnYueHouJiFengClicked(RecyclerView.ViewHolder holder, ChatMessageBase data, int position, boolean isLeft) {
            if (holder instanceof LeftCustomYueHouJiFengViewHolder) {
                viewHolderLeft = (LeftCustomYueHouJiFengViewHolder) holder;
                viewHolderRight = null;
            } else {
                viewHolderRight = (RightCustomYueHouJiFengViewHolder) holder;
                viewHolderLeft = null;
            }

            this.data = data;
            this.positopn = position;
            this.isLeft = isLeft;
            changeItem();
        }

        private void changeItem() {
            if (viewHolderLeft != null) {
                if (data.downloadState == AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING) {
                    viewHolderLeft.chat_download_pgb.setVisibility(View.VISIBLE);
                    viewHolderLeft.left_content_yuehoujifeng_lin.setClickable(false);
                } else {
                    viewHolderLeft.chat_download_pgb.setVisibility(View.GONE);
                    viewHolderLeft.left_content_yuehoujifeng_lin.setClickable(true);
                }
                if (data.needShowTime) {
                    viewHolderLeft.left_time.setVisibility(View.VISIBLE);
                } else {
                    viewHolderLeft.left_time.setVisibility(View.GONE);
                }
            } else {

            }

        }

        @Override
        public void onClick(View v) {
            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                AppBaseActivity.showToast("信息尚未解密");
                return;
            }

            switch (data.type) {
                case AppUtils.MESSAGE_TYPE_TEXT:
                    Intent intentText = new Intent(mContext, YueHouJiFengTextActivity.class);
                    intentText.putExtra("chatMessage", data);
                    mContext.startActivity(intentText);
                    break;
                case AppUtils.MESSAGE_TYPE_IMG:
                    Intent intentImg = new Intent(mContext, YueHouJiFengImgActivity.class);
                    intentImg.putExtra("chatMessage", data);
                    intentImg.putExtra("isGroup", isGroup);
                    intentImg.putExtra("strGroupID", strGroupID);
                    intentImg.putExtra("strUserID", strUserID);
                    intentImg.putExtra("strUserDomainCode", strUserDomainCode);
                    intentImg.putExtra("strGroupDomain", strGroupDomain);
                    intentImg.putExtra("usersTrans", usersTrans);
                    mContext.startActivity(intentImg);
                    break;
                case AppUtils.MESSAGE_TYPE_AUDIO_FILE:
                    if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
                        go2YueHouJiFengAudio(data);
                    } else {
                        if ("文件上传失败".equals(data.fileUrl)) {
//                            AppBaseActivity.showToast("文件加载失败");
                            return;
                        }
                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                        data.localFilePath = "";
                        updateDownloadState(data);
                        changeItem();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String fileLocal = "";
                                try {
                                    fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                                } catch (Exception e) {

                                }
                                final File ffLocal = new File(fileLocal);
                                if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, fileLocal, data.type)) {
                                    new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                            data.localFilePath = ffLocal.getAbsolutePath();
                                            updateDownloadState(data);
                                            changeItem();
                                            go2YueHouJiFengAudio(data);
                                        }
                                    }, "yuehoujifengVoiceSuccess");
                                } else {
                                    new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;
                                            data.localFilePath = "";
                                            updateDownloadState(data);
                                            changeItem();
                                            AppBaseActivity.showToast(AppUtils.getString(R.string.update_download_error));
                                        }
                                    }, "yuehoujifengVoiceFailed");
                                }
                            }
                        }).start();
                    }
                    break;
                case AppUtils.MESSAGE_TYPE_VIDEO_FILE:
                    if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
                        go2YueHouJiFengVideo(data);
                    } else {
                        if ("文件上传失败".equals(data.fileUrl)) {
//                            AppBaseActivity.showToast("文件加载失败");
                            return;
                        }
                        data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                        data.localFilePath = "";
                        updateDownloadState(data);
                        changeItem();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String fileLocal = "";
                                try {
                                    fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                                } catch (Exception e) {

                                }
                                final File ffLocal = new File(fileLocal);
                                if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, fileLocal, data.type)) {
                                    new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                            data.localFilePath = ffLocal.getAbsolutePath();
                                            updateDownloadState(data);
                                            changeItem();
                                            go2YueHouJiFengVideo(data);
                                        }
                                    }, "yuehoujifengVideoSuccess");
                                } else {
                                    new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                        @Override
                                        public void onMainDelay() {
                                            data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;
                                            data.localFilePath = "";
                                            updateDownloadState(data);
                                            changeItem();
                                            AppBaseActivity.showToast(AppUtils.getString(R.string.update_download_error));
                                        }
                                    }, "yuehoujifengVideoFailed");
                                }
                            }
                        }).start();
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private void go2YueHouJiFengAudio(ChatMessageBase data) {
        stopVoice();
        Intent intent = new Intent(mContext, YueHouJiFengAudioActivity.class);
        intent.putExtra("chatMessage", data);
        intent.putExtra("isGroup", isGroup);
        intent.putExtra("strGroupID", strGroupID);
        intent.putExtra("strUserID", strUserID);
        intent.putExtra("strUserDomainCode", strUserDomainCode);
        intent.putExtra("strGroupDomain", strGroupDomain);
        intent.putExtra("usersTrans", usersTrans);
        mContext.startActivity(intent);

    }

    private void go2YueHouJiFengVideo(ChatMessageBase data) {
        stopVoice();
        Intent intent = new Intent(mContext, YueHouJiFengVideoActivity.class);
        intent.putExtra("chatMessage", data);
        intent.putExtra("isGroup", isGroup);
        intent.putExtra("strGroupID", strGroupID);
        intent.putExtra("strUserID", strUserID);
        intent.putExtra("strUserDomainCode", strUserDomainCode);
        intent.putExtra("strGroupDomain", strGroupDomain);
        intent.putExtra("usersTrans", usersTrans);
        mContext.startActivity(intent);
    }

    private Go2DaoHangDialog mGo2DaoHangDialog;

    public void dismissDialog() {
        for(File file : linShiFile) {
           if(file.exists()) {
               file.delete();
           }
        }
        if (null != mGo2DaoHangDialog) {
            mGo2DaoHangDialog.dismiss();
        }
    }

    private class OnAddressClicked implements View.OnClickListener {
        private ChatMessageBase data;
        int positopn;
        boolean isLeft;

        public OnAddressClicked(ChatMessageBase data, int position, boolean isLeft) {
            this.data = data;
            this.positopn = position;
            this.isLeft = isLeft;
        }

        @Override
        public void onClick(View v) {

            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                AppBaseActivity.showToast("信息尚未解密");
                return;
            }

            if (null == mGo2DaoHangDialog) {
                mGo2DaoHangDialog = new Go2DaoHangDialog(mContext);
            }
            mGo2DaoHangDialog.setLocationInfo(data);
            mGo2DaoHangDialog.show();
        }

    }

    private class OnQiuJiuClicked implements View.OnClickListener {
        private ChatMessageBase data;
        int positopn;
        boolean isLeft;

        public OnQiuJiuClicked(ChatMessageBase data, int position, boolean isLeft) {
            this.data = data;
            this.positopn = position;
            this.isLeft = isLeft;
        }

        @Override
        public void onClick(View v) {
            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                AppBaseActivity.showToast("信息尚未解密");
                return;
            }
            Intent intent = new Intent(mContext, MapLocationActivity.class);
            intent.putExtra("longitude", data.longitude);
            intent.putExtra("latitude", data.latitude);
            mContext.startActivity(intent);
        }

    }

    private class OnShareClicked implements View.OnClickListener {
        private ChatMessageBase data;

        public OnShareClicked(ChatMessageBase data) {
            this.data = data;
        }

        @Override
        public void onClick(View v) {
            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                AppBaseActivity.showToast("信息尚未解密");
                return;
            }

            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.fileUrl));
                mContext.startActivity(browserIntent);
            } catch (Exception e) {
            }

        }
    }

    private class OnFileClicked implements View.OnClickListener {
        private LeftCustomFileViewHolder viewHolderLeft;
        private RightCustomFileViewHolder viewHolderRight;
        private ChatMessageBase data;
        int positopn;
        boolean isLeft;

        public OnFileClicked(RecyclerView.ViewHolder holder, ChatMessageBase data, int position, boolean isLeft) {
            if (holder instanceof RightCustomFileViewHolder) {
                viewHolderRight = (RightCustomFileViewHolder) holder;
                viewHolderLeft = null;
            } else {
                viewHolderLeft = (LeftCustomFileViewHolder) holder;
                viewHolderRight = null;
            }
            this.data = data;
            this.positopn = position;
            this.isLeft = isLeft;
            changeItem();
        }

        private void changeItem() {
            if (viewHolderLeft != null) {
                if (data.downloadState == AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING) {
                    viewHolderLeft.chat_download_file.setVisibility(View.VISIBLE);
                    viewHolderLeft.left_content_file_lin.setClickable(false);
                } else {
                    viewHolderLeft.chat_download_file.setVisibility(View.GONE);
                    viewHolderLeft.left_content_file_lin.setClickable(true);
                }
                if (data.needShowTime) {
                    viewHolderLeft.left_time.setVisibility(View.VISIBLE);
                } else {
                    viewHolderLeft.left_time.setVisibility(View.GONE);
                }
            } else {
                if (data.downloadState == AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING) {
                    viewHolderRight.chat_download_file.setVisibility(View.VISIBLE);
                    viewHolderRight.right_content_file_lin.setClickable(false);
                } else {
                    viewHolderRight.chat_download_file.setVisibility(View.GONE);
                    viewHolderRight.right_content_file_lin.setClickable(true);
                }
                if (data.needShowTime) {
                    viewHolderRight.right_time.setVisibility(View.VISIBLE);
                } else {
                    viewHolderRight.right_time.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                AppBaseActivity.showToast("信息尚未解密");
                return;
            }

            if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
                File localFile = new File(data.localFilePath);
                openFile2(data.localFilePath, data.bEncrypt, data.fileName, data);
            } else {
                if ("文件上传失败".equals(data.fileUrl)) {
                    return;
                }
                data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                data.localFilePath = "";
                updateDownloadState(data);
                changeItem();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String fileLocal = "";
                        try {
                            fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                        } catch (Exception e) {

                        }
                        final File ffLocal = new File(fileLocal);
                        if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, fileLocal, data.type)) {
                            new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                @Override
                                public void onMainDelay() {
                                    data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                    data.localFilePath = ffLocal.getAbsolutePath();
                                    updateDownloadState(data);
                                    changeItem();
                                    openFile2(ffLocal.getAbsolutePath(), data.bEncrypt, data.fileName, data);
                                }
                            }, "openFileSuccess");


                        } else {
                            new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                @Override
                                public void onMainDelay() {
                                    data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;
                                    data.localFilePath = "";

                                    updateDownloadState(data);
                                    changeItem();
                                }
                            }, "openFileFailed");

                        }


                    }
                }).start();
            }

        }
    }

    private class OnVoiceClicked implements View.OnClickListener {
        LeftCustomVoiceViewHolder viewHolderLeft;
        RightCustomVoiceViewHolder viewHolderRight;
        private ChatMessageBase data;
        private String fileUrl;
        int position;
        boolean isLeft;

        public OnVoiceClicked(RecyclerView.ViewHolder holder, ChatMessageBase data, String fileUrl, int position, boolean isLeft) {
            if (holder instanceof LeftCustomVoiceViewHolder) {
                viewHolderLeft = (LeftCustomVoiceViewHolder) holder;
                viewHolderRight = null;
            } else {
                viewHolderRight = (RightCustomVoiceViewHolder) holder;
                viewHolderLeft = null;
            }
            this.data = data;
            this.fileUrl = fileUrl;
            this.position = position;
            this.isLeft = isLeft;
            changeItem();
        }

        private void changeItem() {
            if (viewHolderLeft != null) {
                if (data.nDuration > 0) {
                    viewHolderLeft.left_content_voice_time.setVisibility(View.VISIBLE);
                    viewHolderLeft.left_content_voice_time.setText(data.nDuration + "''");
                } else {
                    viewHolderLeft.left_content_voice_time.setVisibility(View.GONE);

                }
                if (data.read == 1) {
                    viewHolderLeft.left_content_voice_state.setVisibility(View.GONE);
                } else {
                    viewHolderLeft.left_content_voice_state.setVisibility(View.VISIBLE);
                }
                if (data.needShowTime) {
                    viewHolderLeft.left_time.setVisibility(View.VISIBLE);
                } else {
                    viewHolderLeft.left_time.setVisibility(View.GONE);
                }
            } else {
                if (data.nDuration > 0) {
                    viewHolderRight.right_content_voice_time.setVisibility(View.VISIBLE);
                    viewHolderRight.right_content_voice_time.setText(data.nDuration + "''");
                } else {
                    viewHolderRight.right_content_voice_time.setVisibility(View.GONE);
                }
                if (data.read == 1) {
                    viewHolderRight.right_content_voice_state.setVisibility(View.GONE);
                } else {
                    viewHolderRight.right_content_voice_state.setVisibility(View.VISIBLE);
                }
                if (data.needShowTime) {
                    viewHolderRight.right_time.setVisibility(View.VISIBLE);
                } else {
                    viewHolderRight.right_time.setVisibility(View.GONE);
                }
            }

        }

        @Override
        public void onClick(View v) {
            if (data.isPlaying) {
                stopVoice();
                return;
            }

            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                AppBaseActivity.showToast("信息尚未解密");
                return;
            }

            if (isLeft) {//非自己发送的需要更新阅读状态

                if (isGroup) {
                    if (!TextUtils.isEmpty(strGroupID)) {
                        AppDatas.MsgDB()
                                .chatGroupMsgDao()
                                .updateReadWithMsgID(strGroupID, data.msgID);
                    }

                } else {
                    AppDatas.MsgDB()
                            .chatSingleMsgDao()
                            .updateReadWithMsgID(data.fromUserId, AppAuth.get().getUserID() + "", data.msgID);

                }

                data.read = 1;
            }
            String fileLocal = "";
            try {
                fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
            } catch (Exception e) {

            }
            final File ffLocal = new File(fileLocal);
            data.localFilePath = ffLocal.getAbsolutePath();
            if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
                File localFile = new File(data.localFilePath);
                playVoice2(localFile.getPath(), data, position, viewHolderLeft != null ? viewHolderLeft.left_content_voice_time : viewHolderRight.right_content_voice_time);
            } else {
                String finalFileLocal = fileLocal;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + fileUrl, finalFileLocal, data.type)) {
                            playVoice2(finalFileLocal, data, position, viewHolderLeft != null ? viewHolderLeft.left_content_voice_time : viewHolderRight.right_content_voice_time);
                        }

                    }
                }).start();

            }
            if (lastDealposition > 0 && lastDealposition < getItemCount() && lastDealposition != position) {
                mDataList.get(lastDealposition).isPlaying = false;
            }
            mDataList.get(position).isPlaying = true;
            lastDealposition = position;
            changeItem();
        }
    }

    private class OnImageViewClicked implements View.OnClickListener {
        private String url;
        private boolean isLocal;


        public OnImageViewClicked(String url, boolean isLocal) {
            this.url = url;
            this.isLocal = isLocal;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ImageShowActivity.class);
            ArrayList<String> imageUrls = new ArrayList<>();
            imageUrls.add(url);
            intent.putStringArrayListExtra("imageUrls", imageUrls);
            intent.putExtra("isLocal", isLocal);
            mContext.startActivity(intent);
        }
    }

    private class OnVideoClicked implements View.OnClickListener {
        private LeftCustomVideoViewHolder viewHolderLeft;
        private RightCustomVideoViewHolder viewHolderRight;
        private ChatMessageBase data;
        int position;
        boolean isLeft;

        public OnVideoClicked(RecyclerView.ViewHolder holder, ChatMessageBase data, int position, boolean isLeft) {
            if (holder instanceof LeftCustomVideoViewHolder) {
                viewHolderLeft = (LeftCustomVideoViewHolder) holder;
                viewHolderRight = null;
            } else {
                viewHolderRight = (RightCustomVideoViewHolder) holder;
                viewHolderLeft = null;
            }
            this.data = data;
            this.position = position;
            this.isLeft = isLeft;
            changeItem();
        }

        private void changeItem() {
            if (viewHolderLeft != null) {
                if (data.downloadState == AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING) {
                    viewHolderLeft.chat_download_file.setVisibility(View.VISIBLE);
                    viewHolderLeft.left_content_video_play.setVisibility(View.GONE);
                } else {
                    viewHolderLeft.chat_download_file.setVisibility(View.GONE);
                    viewHolderLeft.left_content_video_play.setVisibility(View.VISIBLE);
                }
                if (data.needShowTime) {
                    viewHolderLeft.left_time.setVisibility(View.VISIBLE);
                } else {
                    viewHolderLeft.left_time.setVisibility(View.GONE);
                }
            } else {
                if (data.downloadState == AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING) {
                    viewHolderRight.chat_download_file.setVisibility(View.VISIBLE);
                    viewHolderRight.right_content_video_play.setVisibility(View.GONE);
                } else {
                    viewHolderRight.chat_download_file.setVisibility(View.GONE);
                    viewHolderRight.right_content_video_play.setVisibility(View.VISIBLE);
                }

                if (data.needShowTime) {
                    viewHolderRight.right_time.setVisibility(View.VISIBLE);
                } else {
                    viewHolderRight.right_time.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                AppBaseActivity.showToast("信息尚未解密");
                return;
            }

            if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
                go2PlayVideo2(data.localFilePath, data.bEncrypt, data);
            } else {
                if ("文件上传失败".equals(data.fileUrl)) {
//                    showToast("文件加载失败");
                    return;
                }
                data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADING;
                data.localFilePath = "";
                updateDownloadState(data);
                changeItem();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String fileLocal = "";
                        try {
                            fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                        } catch (Exception e) {

                        }
                        final File ffLocal = new File(fileLocal);
                        if (downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, fileLocal, data.type)) {
                            String finalFileLocal = fileLocal;
                            new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                @Override
                                public void onMainDelay() {
                                    data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_DOWNLOADED;
                                    data.localFilePath = ffLocal.getAbsolutePath();

                                    updateDownloadState(mDataList.get(position));
                                    changeItem();
                                    go2PlayVideo2(finalFileLocal, data.bEncrypt, data);

                                }
                            }, "playVideoSuccess");

                        } else {
                            new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                @Override
                                public void onMainDelay() {
                                    data.downloadState = AppUtils.CHAT_DOWNLOAD_FILE_STATE_UNDOWNLOAD;
                                    data.localFilePath = "";
                                    updateDownloadState(data);
                                    changeItem();
                                }
                            }, "playVideoFailed");

                        }

                    }
                }).start();
            }
        }
    }

    private void loadLocalImage(ImageView view, String localFilePath) {
        Glide.with(mContext)
                .load(new File(localFilePath))
                .apply(requestOptions)
                .into(view);
        view.setOnClickListener(new OnImageViewClicked(localFilePath, true));
    }

    private void unEncryptImage(ImageView view, ChatMessageBase data, boolean isAddrss) {
        File file = new File(EncryptUtil.getNewFile(data.localFilePath));
        if (file.exists()) {
            Glide.with(mContext)
                    .load(file)
                    .apply(isAddrss ? requestOptionsAddress : requestOptions)
                    .into(view);
            if (!isAddrss) {
                view.setOnClickListener(new OnImageViewClicked(file.getAbsolutePath(), true));
            }
        } else {
            EncryptUtil.encryptFile(data.localFilePath, file.getAbsolutePath(),
                    false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                    isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            try {
                                Glide.with(mContext)
                                        .load(new File(resp.m_strData))
                                        .apply(isAddrss ? requestOptionsAddress : requestOptions)
                                        .into(view);
                                if (!isAddrss) {
                                    view.setOnClickListener(new OnImageViewClicked(resp.m_strData, true));
                                }
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            AppBaseActivity.showToast("文件解密失败");
                        }
                    }
            );
        }
    }

    private void unEncryptImage2(ImageView view, ChatMessageBase data, boolean isAddrss) {
        try {
            File file = new File(EncryptUtil.getNewFile(data.localFilePath));
            File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
            if (file.exists()) {
                if (fileun.exists()) {
                    linShiFile.add(fileun);
                    if (fileun.getAbsolutePath().endsWith(".gif")) {
                        Glide.with(mContext).load(fileun).listener(requestListener).into(view);
                    } else {
                        Glide.with(mContext)
                                .load(fileun)
                                .apply(isAddrss ? requestOptionsAddress : requestOptions)
                                .into(view);
                    }
                    if (!isAddrss) {
                        view.setOnClickListener(new OnImageViewClicked(fileun.getAbsolutePath(), true));
                    }
                } else {
                    EncryptUtil.localEncryptFile(file.getAbsolutePath(), fileun.getAbsolutePath(), false,
                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    try {
                                        linShiFile.add(new File(resp.m_strData));
                                        if (fileun.getAbsolutePath().endsWith(".gif")) {
                                            Glide.with(mContext).load(new File(resp.m_strData)).listener(requestListener).into(view);
                                        } else {
                                            Glide.with(mContext)
                                                    .load(new File(resp.m_strData))
                                                    .apply(isAddrss ? requestOptionsAddress : requestOptions)
                                                    .into(view);
                                        }
                                        if (!isAddrss) {
                                            view.setOnClickListener(new OnImageViewClicked(resp.m_strData, true));
                                        }
                                    } catch (Exception e) {

                                    }
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    AppBaseActivity.showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                EncryptUtil.converEncryptFile(data.localFilePath, file.getAbsolutePath(),
                        isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                        isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                            @Override
                            public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                if (fileun.exists()) {
                                    Glide.with(mContext)
                                            .load(fileun)
                                            .apply(isAddrss ? requestOptionsAddress : requestOptions)
                                            .into(view);
                                    if (!isAddrss) {
                                        view.setOnClickListener(new OnImageViewClicked(fileun.getAbsolutePath(), true));
                                    }
                                } else {
                                    EncryptUtil.localEncryptFile(resp.m_strData, fileun.getAbsolutePath(), false,
                                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                @Override
                                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                    try {
                                                        Glide.with(mContext)
                                                                .load(new File(resp.m_strData))
                                                                .apply(isAddrss ? requestOptionsAddress : requestOptions)
                                                                .into(view);
                                                        if (!isAddrss) {
                                                            view.setOnClickListener(new OnImageViewClicked(resp.m_strData, true));
                                                        }
                                                    } catch (Exception e) {

                                                    }
                                                }

                                                @Override
                                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                    AppBaseActivity.showToast("文件解密失败");
                                                }
                                            }
                                    );
                                }
                            }

                            @Override
                            public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                AppBaseActivity.showToast("文件解密失败");
                            }
                        }
                );
            }
        } catch (Exception e) {

        }

    }

    private void openFile(String localFilePath, int encrypt, String name, ChatMessageBase data) {
        if (encrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(localFilePath));
                if (file.exists()) {
                    openFileReal(file.getAbsolutePath(), name);
                } else {
                    EncryptUtil.encryptFile(localFilePath, file.getAbsolutePath(),
                            false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    openFileReal(resp.m_strData, name);
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    AppBaseActivity.showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                AppBaseActivity.showToast("文件解密失败");
            }
        } else {
            openFileReal(localFilePath, name);
        }
    }

    private void openFile2(String localFilePath, int encrypt, String name, ChatMessageBase data) {
        if (encrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(localFilePath));
                File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
                if (file.exists()) {
                    if (fileun.exists()) {
                        openFileReal(fileun.getAbsolutePath(), name);
                    } else {
                        EncryptUtil.localEncryptFile(file.getAbsolutePath(), fileun.getAbsolutePath(), false,
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                        openFileReal(resp.m_strData, name);
                                    }

                                    @Override
                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                        AppBaseActivity.showToast("文件解密失败");
                                    }
                                }
                        );
                    }
                } else {
                    EncryptUtil.converEncryptFile(localFilePath, file.getAbsolutePath(),
                            isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode,
                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    if (fileun.exists()) {
                                        openFileReal(fileun.getAbsolutePath(), name);
                                    } else {
                                        EncryptUtil.localEncryptFile(resp.m_strData, fileun.getAbsolutePath(), false,
                                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                    @Override
                                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                        openFileReal(resp.m_strData, name);
                                                    }

                                                    @Override
                                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                        AppBaseActivity.showToast("文件解密失败");
                                                    }
                                                }
                                        );
                                    }
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    AppBaseActivity.showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                AppBaseActivity.showToast("文件解密失败");
            }
        } else {
            openFileReal(localFilePath, name);
        }
    }

    private void openFileReal(String m_strData, String name) {
//        text/word/excel/pdf
        linShiFile.add(new File(m_strData));
        String end = m_strData.substring(m_strData.lastIndexOf(".") + 1);
        if ("txt".equalsIgnoreCase(end) || "ppt".equalsIgnoreCase(end) || "docx".equalsIgnoreCase(end) ||
                "pdf".equalsIgnoreCase(end) || "png".equalsIgnoreCase(end) || "jpg".equalsIgnoreCase(end) ||
                "jpeg".equalsIgnoreCase(end) || "pptx".equalsIgnoreCase(end) || "xlw".equalsIgnoreCase(end) ||
                "xlsb".equalsIgnoreCase(end) || "svg".equalsIgnoreCase(end) || "tif".equalsIgnoreCase(end) ||
                "xlsm".equalsIgnoreCase(end) || "csv".equalsIgnoreCase(end) || "dbf".equalsIgnoreCase(end) ||
                "dif".equalsIgnoreCase(end) || "slk".equalsIgnoreCase(end) || "sylk".equalsIgnoreCase(end) ||
                "prn".equalsIgnoreCase(end) || "ods".equalsIgnoreCase(end) || "fods".equalsIgnoreCase(end) ||
                "gif".equalsIgnoreCase(end) || "bmp".equalsIgnoreCase(end) || "tiff".equalsIgnoreCase(end)
//                "log".equals(end) || "cpp".equals(end) || "c".equals(end) || "conf".equals(end) ||
//                "class".equals(end) || "h".equals(end) || "htm".equals(end) || "html".equals(end) ||
//                "js".equals(end) || "java".equals(end) || "mpga".equals(end) || "rar".equals(end) ||
//                "ogg".equals(end) || "msg".equals(end) || "rc".equals(end) || "pptx".equals(end) ||
//                "pps".equals(end) || "prop".equals(end) || "doc".equals(end) || "rtf".equals(end) ||
//                "rmvb".equals(end) || "wps".equals(end) || "tgz".equals(end) || "tar".equals(end) ||
//                "sh".equals(end) || "wma".equals(end) || "wmv".equals(end) ||
//                "wav".equals(end) || "xml".equals(end) || "z".equals(end) || "zip".equals(end)
        ) {
//            Intent intent = new Intent(mContext, FileOpenActivity.class);
            Intent intent = new Intent(mContext, WebPageFileActivity.class);
//            Intent intent = new Intent(mContext, PIOOpenActivity.class);
            intent.putExtra("file", m_strData);
            intent.putExtra("name", name);
            mContext.startActivity(intent);
        } else if ("xls".equalsIgnoreCase(end) || "xlsx".equalsIgnoreCase(end)) {
            Intent intent = new Intent(mContext, ExcelActivity.class);
            intent.putExtra("file", m_strData);
            intent.putExtra("name", name);
            mContext.startActivity(intent);
        } else {
            AppBaseActivity.showToast("不支持该格式文件");
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri contentUri = FileProvider.getUriForFile(mContext, "huaiye.com.vim.fileprovider", new File(m_strData));
//                intent.setDataAndType(contentUri, getMIMEType(new File(m_strData)));
//            } else {
//                intent.setDataAndType(Uri.fromFile(new File(m_strData)), getMIMEType(new File(m_strData)));
//            }
//
//            mContext.startActivity(intent);
        }


    }

    private void updateDownloadState(ChatMessageBase data) {
        if (isGroup) {
            if (!TextUtils.isEmpty(strGroupID)) {
                AppDatas.MsgDB()
                        .chatGroupMsgDao()
                        .updateDownloadState(strGroupID, data.localFilePath, data.id);
            }

        } else {
            AppDatas.MsgDB()
                    .chatSingleMsgDao()
                    .updateDownloadState(data.localFilePath, data.id);

        }
    }

    /**
     * 必须延迟执行 否则动画不动
     *
     * @param animationDrawable
     * @param isPlaying
     */
    private void dealAnimation(final AnimationDrawable animationDrawable, final boolean isPlaying) {
        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                if (isPlaying) {
                    if (animationDrawable != null) {
                        animationDrawable.start();
                    }
                } else {
                    if (animationDrawable != null) {
                        animationDrawable.stop();
                    }
                }
            }
        }, "stopRecording");
    }

    private void playVoice(final String path, final ChatMessageBase data, final int position, TextView view) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (data.bEncrypt == 1) {
                    if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                        File file = new File(EncryptUtil.getNewFile(path));
                        if (file.exists()) {
                            playVoiceReal(path, file.getAbsolutePath(), data, position, view);
                        } else {
                            EncryptUtil.encryptFile(path, EncryptUtil.getNewFile(path),
                                    false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                                    isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                        @Override
                                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                            playVoiceReal(path, resp.m_strData, data, position, view);
                                        }

                                        @Override
                                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                            AppBaseActivity.showToast("文件解密失败");
                                        }
                                    }
                            );
                        }
                    } else {
                        AppBaseActivity.showToast("文件解密失败");
                    }
                } else {
                    playVoiceReal(path, path, data, position, view);
                }
            }
        });
    }

    private void playVoice2(final String path, final ChatMessageBase data, final int position, TextView view) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (data.bEncrypt == 1) {
                    if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                        File file = new File(EncryptUtil.getNewFile(path));
                        File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
                        if (file.exists()) {
                            if (fileun.exists()) {
                                playVoiceReal(path, fileun.getAbsolutePath(), data, position, view);
                            } else {
                                EncryptUtil.localEncryptFile(file.getAbsolutePath(), fileun.getAbsolutePath(), false,
                                        new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                            @Override
                                            public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                playVoiceReal(path, resp.m_strData, data, position, view);
                                            }

                                            @Override
                                            public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                AppBaseActivity.showToast("文件解密失败");
                                            }
                                        }
                                );
                            }
                        } else {
                            EncryptUtil.converEncryptFile(path, file.getAbsolutePath(), isGroup,
                                    isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                                    isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode,
                                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                        @Override
                                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                            if (fileun.exists()) {
                                                playVoiceReal(path, fileun.getAbsolutePath(), data, position, view);
                                            } else {
                                                EncryptUtil.localEncryptFile(resp.m_strData, fileun.getAbsolutePath(), false,
                                                        new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                            @Override
                                                            public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                                playVoiceReal(path, resp.m_strData, data, position, view);
                                                            }

                                                            @Override
                                                            public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                                AppBaseActivity.showToast("文件解密失败");
                                                            }
                                                        }
                                                );
                                            }
                                        }

                                        @Override
                                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                            AppBaseActivity.showToast("文件解密失败");
                                        }
                                    }
                            );
                        }
                    } else {
                        AppBaseActivity.showToast("文件解密失败");
                    }
                } else {
                    playVoiceReal(path, path, data, position, view);
                }
            }
        });
    }

    private void playVoiceReal(final String path, final String playReal, final ChatMessageBase data, final int position, TextView view) {
        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                linShiFile.add(new File(path));
                data.localFilePath = path;
                updateDownloadState(data);
                VideoParams videoParams = Player.Params.TypeVideoOfflineRecord().setResourcePath(playReal);

                stopVoice();
                HYClient.getHYPlayer().startPlay(videoParams
                        .setMixCallback(new VideoCallbackWrapper() {
                            @Override
                            public void onSuccess(VideoParams param) {
                                super.onSuccess(param);
                                mDataList.get(position).isPlaying = true;
//                                notifyItemChanged(position);
                            }

                            @Override
                            public void onGetVideoRange(VideoParams param, int start, int end) {
                                super.onGetVideoRange(param, start, end);
                            }

                            @Override
                            public void onVideoProgressChanged(VideoParams param, HYPlayer.ProgressType type, int current, int total) {
                                super.onVideoProgressChanged(param, type, current, total);
                                new RxUtils().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                                    @Override
                                    public Object doOnThread() {
                                        return "";
                                    }

                                    @Override
                                    public void doOnMain(Object data) {
                                        int time = total - current;
                                        view.setText(time + "''");
                                    }
                                });
                            }

                            @Override
                            public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                                super.onVideoStatusChanged(param, msg);
                                SdkMsgNotifyPlayStatus status = (SdkMsgNotifyPlayStatus) msg;
                                if (status.isStopped()) {
                                    mDataList.get(position).isPlaying = false;
                                    notifyItemChanged(position);
//                                    notifyDataSetChanged();
                                }

                            }

                            @Override
                            public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                                super.onError(param, errorInfo);
                                mDataList.get(position).isPlaying = false;
                                notifyItemChanged(position);
//                                notifyDataSetChanged();
                            }
                        }));

            }
        }, "playRecording");
    }

    /**
     * 播放
     *
     * @param localFilePath
     */
    private void go2PlayVideo(String localFilePath, int encrypt, ChatMessageBase data) {
        if (encrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(localFilePath));
                if (file.exists()) {
                    playVideoReal(file.getAbsolutePath());
                } else {
                    EncryptUtil.encryptFile(localFilePath, file.getAbsolutePath(),
                            false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    playVideoReal(resp.m_strData);
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    AppBaseActivity.showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                AppBaseActivity.showToast("文件解密失败");
            }
        } else {
            playVideoReal(localFilePath);
        }
    }

    private void go2PlayVideo2(String localFilePath, int encrypt, ChatMessageBase data) {
        if (encrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(localFilePath));
                File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
                if (file.exists()) {
                    if (fileun.exists()) {
                        playVideoReal(fileun.getAbsolutePath());
                    } else {
                        EncryptUtil.localEncryptFile(localFilePath, fileun.getAbsolutePath(), false,
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                        playVideoReal(resp.m_strData);
                                    }

                                    @Override
                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                        AppBaseActivity.showToast("文件解密失败");
                                    }
                                }
                        );
                    }
                } else {
                    EncryptUtil.converEncryptFile(localFilePath, file.getAbsolutePath(),
                            isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode,
                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    if (fileun.exists()) {
                                        playVideoReal(fileun.getAbsolutePath());
                                    } else {
                                        EncryptUtil.localEncryptFile(resp.m_strData, fileun.getAbsolutePath(), false,
                                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                    @Override
                                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                        playVideoReal(resp.m_strData);
                                                    }

                                                    @Override
                                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                        AppBaseActivity.showToast("文件解密失败");
                                                    }
                                                }
                                        );
                                    }
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    AppBaseActivity.showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                AppBaseActivity.showToast("文件解密失败");
            }
        } else {
            playVideoReal(localFilePath);
        }
    }

    private void playVideoReal(String m_strData) {
        stopVoice();
        linShiFile.add(new File(m_strData));
        Intent intent = new Intent(mContext, MediaLocalVideoPlayActivity.class);
        intent.putExtra("path", m_strData);
        mContext.startActivity(intent);
    }

    /**
     * 停止播放
     */
    public void stopVoice() {
        HYClient.getHYPlayer().stopPlay(new SdkCallback<VideoParams>() {
            @Override
            public void onSuccess(VideoParams videoParams) {
                Logger.info("stopPlay   onSuccess");
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                Logger.info("stopPlay   onError");

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageBase chatMessageBase = mDataList.get(position);
        if (AppUtils.MESSAGE_TYPE_GROUP_MEET == chatMessageBase.type) {
            return CHAT_CONTENT_CUSTOM_MEET_ITEM;
        }

        if (chatMessageBase.type == CHAT_CONTENT_CUSTOM_NOTICE_ITEM) {
            return CHAT_CONTENT_CUSTOM_NOTICE_ITEM;
        }

        if (chatMessageBase.fromUserId.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
            if (1 == chatMessageBase.bFire && (AppUtils.MESSAGE_TYPE_TEXT == chatMessageBase.type || AppUtils.MESSAGE_TYPE_IMG == chatMessageBase.type || AppUtils.MESSAGE_TYPE_AUDIO_FILE == chatMessageBase.type || AppUtils.MESSAGE_TYPE_VIDEO_FILE == chatMessageBase.type)) {
                return RIGHT_MESSAGE_TYPE_YUEHOUJIFENGH;
            } else if (AppUtils.MESSAGE_TYPE_TEXT == chatMessageBase.type) {
                return RIGHT_MESSAGE_TYPE_TEXT;
            } else if (AppUtils.MESSAGE_TYPE_IMG == chatMessageBase.type) {
                return RIGHT_MESSAGE_TYPE_IMAGE;
            } else if (AppUtils.MESSAGE_TYPE_FILE == chatMessageBase.type) {
                return RIGHT_MESSAGE_TYPE_FILE;
            } else if (AppUtils.MESSAGE_TYPE_AUDIO_FILE == chatMessageBase.type) {
                return RIGHT_MESSAGE_TYPE_VOICE;
            } else if (AppUtils.MESSAGE_TYPE_VIDEO_FILE == chatMessageBase.type) {
                return RIGHT_MESSAGE_TYPE_VIDEO;
            } else if (AppUtils.MESSAGE_TYPE_ADDRESS == chatMessageBase.type) {
                return RIGHT_MESSAGE_TYPE_ADDRESS;
            } else if (AppUtils.MESSAGE_TYPE_SHARE == chatMessageBase.type) {
                return RIGHT_MESSAGE_TYPE_SHARE;
            } else {
                return RIGHT_MESSAGE_TYPE_TEXT;
            }
        } else {
            if (1 == chatMessageBase.bFire && (AppUtils.MESSAGE_TYPE_TEXT == chatMessageBase.type || AppUtils.MESSAGE_TYPE_IMG == chatMessageBase.type || AppUtils.MESSAGE_TYPE_AUDIO_FILE == chatMessageBase.type || AppUtils.MESSAGE_TYPE_VIDEO_FILE == chatMessageBase.type)) {
                return LEFT_MESSAGE_TYPE_YUEHOUJIFENG;
            } else if (AppUtils.MESSAGE_TYPE_TEXT == chatMessageBase.type) {
                return LEFT_MESSAGE_TYPE_TEXT;
            } else if (AppUtils.MESSAGE_TYPE_IMG == chatMessageBase.type) {
                return LEFT_MESSAGE_TYPE_IMAGE;
            } else if (AppUtils.MESSAGE_TYPE_FILE == chatMessageBase.type) {
                return LEFT_MESSAGE_TYPE_FILE;
            } else if (AppUtils.MESSAGE_TYPE_AUDIO_FILE == chatMessageBase.type) {
                return LEFT_MESSAGE_TYPE_VOICE;
            } else if (AppUtils.MESSAGE_TYPE_VIDEO_FILE == chatMessageBase.type) {
                return LEFT_MESSAGE_TYPE_VIDEO;
            } else if (AppUtils.MESSAGE_TYPE_ADDRESS == chatMessageBase.type) {
                return LEFT_MESSAGE_TYPE_ADDRESS;
            } else if (AppUtils.MESSAGE_TYPE_JINJI == chatMessageBase.type) {
                return CHAT_CONTENT_CUSTOM_QIUJIU_ITEM;
            } else if (AppUtils.MESSAGE_TYPE_SHARE == chatMessageBase.type) {
                return LEFT_MESSAGE_TYPE_SHARE;
            } else {
                return LEFT_MESSAGE_TYPE_TEXT;
            }
        }
    }

    private class NoticeViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_notice;

        private NoticeViewHolder(View itemView) {
            super(itemView);
            txt_notice = (TextView) itemView.findViewById(R.id.txt_notice);
        }
    }

    private class QiuJiuViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private LinearLayout left_content_layout;
        private ImageView qiujiu_img;

        public QiuJiuViewHolder(View view) {
            super(view);
            left_time = view.findViewById(R.id.left_time);
            chat_head = view.findViewById(R.id.chat_head);
            left_name = view.findViewById(R.id.left_name);
            left_content_layout = view.findViewById(R.id.left_content_layout);
            qiujiu_img = view.findViewById(R.id.qiujiu_img);
        }
    }

    private class CustomMeetViewHolder extends RecyclerView.ViewHolder {
        private TextView chat_content_custom_meet_item_time;
        private TextView chat_content_custom_meet_item;

        private CustomMeetViewHolder(View itemView) {
            super(itemView);
            chat_content_custom_meet_item_time = (TextView) itemView.findViewById(R.id.chat_content_custom_meet_item_time);
            chat_content_custom_meet_item = (TextView) itemView.findViewById(R.id.chat_content_custom_meet_item);
        }
    }

    private class LeftCustomTextViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private TextView left_content;

        private LeftCustomTextViewHolder(View itemView) {
            super(itemView);
            left_time = (TextView) itemView.findViewById(R.id.left_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            left_name = (TextView) itemView.findViewById(R.id.left_name);
            left_content = (TextView) itemView.findViewById(R.id.left_content);
        }
    }

    private class LeftCustomImageViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private ImageView left_content_image;

        private LeftCustomImageViewHolder(View itemView) {
            super(itemView);
            left_time = (TextView) itemView.findViewById(R.id.left_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            left_name = (TextView) itemView.findViewById(R.id.left_name);
            left_content_image = (ImageView) itemView.findViewById(R.id.left_content_image);
        }
    }

    private class LeftCustomVideoViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private ImageView left_content_video_image;
        private ImageView left_content_video_play;
        private ProgressBar chat_download_file;

        private LeftCustomVideoViewHolder(View itemView) {
            super(itemView);
            left_time = (TextView) itemView.findViewById(R.id.left_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            left_name = (TextView) itemView.findViewById(R.id.left_name);
            left_content_video_image = (ImageView) itemView.findViewById(R.id.left_content_video_image);
            left_content_video_play = (ImageView) itemView.findViewById(R.id.left_content_video_play);
            chat_download_file = (ProgressBar) itemView.findViewById(R.id.chat_download_file);
        }
    }

    private class LeftCustomVoiceViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private LinearLayout left_content_voice_lin;
        private ImageView left_content_voice;
        private ImageView left_content_voice_state;
        private TextView left_content_voice_time;

        private LeftCustomVoiceViewHolder(View itemView) {
            super(itemView);
            left_time = (TextView) itemView.findViewById(R.id.left_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            left_name = (TextView) itemView.findViewById(R.id.left_name);
            left_content_voice_lin = (LinearLayout) itemView.findViewById(R.id.left_content_voice_lin);
            left_content_voice = (ImageView) itemView.findViewById(R.id.left_content_voice);
            left_content_voice_state = (ImageView) itemView.findViewById(R.id.left_content_voice_state);
            left_content_voice_time = (TextView) itemView.findViewById(R.id.left_content_voice_time);
        }
    }

    private class LeftCustomFileViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private LinearLayout left_content_file_lin;
        private TextView left_content_file;
        private ProgressBar chat_download_file;

        private LeftCustomFileViewHolder(View itemView) {
            super(itemView);
            left_time = (TextView) itemView.findViewById(R.id.left_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            left_name = (TextView) itemView.findViewById(R.id.left_name);
            left_content_file_lin = (LinearLayout) itemView.findViewById(R.id.left_content_file_lin);
            left_content_file = (TextView) itemView.findViewById(R.id.left_content_file);
            chat_download_file = (ProgressBar) itemView.findViewById(R.id.chat_download_file);

        }
    }

    private class LeftCustomYueHouJiFengViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private LinearLayout left_content_yuehoujifeng_lin;
        private ImageView left_yuehoujifeng_img;
        private ProgressBar chat_download_pgb;
        private TextView left_yuehoujifeng_look;

        private LeftCustomYueHouJiFengViewHolder(View itemView) {
            super(itemView);
            left_time = (TextView) itemView.findViewById(R.id.left_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            left_name = (TextView) itemView.findViewById(R.id.left_name);
            left_content_yuehoujifeng_lin = (LinearLayout) itemView.findViewById(R.id.left_content_yuehoujifeng_lin);
            left_yuehoujifeng_img = (ImageView) itemView.findViewById(R.id.left_yuehoujifeng_img);
            chat_download_pgb = (ProgressBar) itemView.findViewById(R.id.chat_download_pgb);
            left_yuehoujifeng_look = (TextView) itemView.findViewById(R.id.left_yuehoujifeng_look);

        }
    }

    private class RightCustomTextViewHolder extends RecyclerView.ViewHolder {
        private TextView right_time;
        private ImageView chat_head;
        private TextView right_name;
        private TextView right_content;

        private RightCustomTextViewHolder(View itemView) {
            super(itemView);
            right_time = (TextView) itemView.findViewById(R.id.right_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            right_name = (TextView) itemView.findViewById(R.id.right_name);
            right_content = (TextView) itemView.findViewById(R.id.right_content);
        }
    }

    private class RightCustomImageViewHolder extends RecyclerView.ViewHolder {
        private TextView right_time;
        private ImageView chat_head;
        private TextView right_name;
        private ImageView right_content_image;

        private RightCustomImageViewHolder(View itemView) {
            super(itemView);
            right_time = (TextView) itemView.findViewById(R.id.right_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            right_name = (TextView) itemView.findViewById(R.id.right_name);
            right_content_image = (ImageView) itemView.findViewById(R.id.right_content_image);
        }
    }

    private class RightCustomVideoViewHolder extends RecyclerView.ViewHolder {
        private TextView right_time;
        private ImageView chat_head;
        private TextView right_name;
        private ImageView right_content_video_image;
        private ImageView right_content_video_play;
        private ProgressBar chat_download_file;


        private RightCustomVideoViewHolder(View itemView) {
            super(itemView);
            right_time = (TextView) itemView.findViewById(R.id.right_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            right_name = (TextView) itemView.findViewById(R.id.right_name);
            right_content_video_image = (ImageView) itemView.findViewById(R.id.right_content_video_image);
            right_content_video_play = (ImageView) itemView.findViewById(R.id.right_content_video_play);
            chat_download_file = (ProgressBar) itemView.findViewById(R.id.chat_download_file);

        }
    }

    private class RightCustomFileViewHolder extends RecyclerView.ViewHolder {
        private TextView right_time;
        private ImageView chat_head;
        private TextView right_name;
        private LinearLayout right_content_file_lin;
        private TextView right_content_file;
        private ProgressBar chat_download_file;

        private RightCustomFileViewHolder(View itemView) {
            super(itemView);
            right_time = (TextView) itemView.findViewById(R.id.right_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            right_name = (TextView) itemView.findViewById(R.id.right_name);
            right_content_file_lin = (LinearLayout) itemView.findViewById(R.id.right_content_file_lin);
            right_content_file = (TextView) itemView.findViewById(R.id.right_content_file);
            chat_download_file = (ProgressBar) itemView.findViewById(R.id.chat_download_file);

        }
    }

    private class RightCustomVoiceViewHolder extends RecyclerView.ViewHolder {
        private TextView right_time;
        private ImageView chat_head;
        private TextView right_name;
        private LinearLayout right_content_voice_lin;
        private ImageView right_content_voice;
        private ImageView right_content_voice_state;
        private TextView right_content_voice_time;


        private RightCustomVoiceViewHolder(View itemView) {
            super(itemView);
            right_time = (TextView) itemView.findViewById(R.id.right_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            right_name = (TextView) itemView.findViewById(R.id.right_name);
            right_content_voice_lin = (LinearLayout) itemView.findViewById(R.id.right_content_voice_lin);
            right_content_voice = (ImageView) itemView.findViewById(R.id.right_content_voice);
            right_content_voice_state = (ImageView) itemView.findViewById(R.id.right_content_voice_state);
            right_content_voice_time = (TextView) itemView.findViewById(R.id.right_content_voice_time);
        }
    }

    private class RightCustomYueHouJiFengViewHolder extends RecyclerView.ViewHolder {
        private TextView right_time;
        private ImageView chat_head;
        private TextView right_name;
        private LinearLayout right_content_yuehoujifeng_lin;
        private ImageView right_yuehoujifeng_img;
        private ProgressBar chat_download_pgb;
        private TextView right_yuehoujifeng_look;


        private RightCustomYueHouJiFengViewHolder(View itemView) {
            super(itemView);
            right_time = (TextView) itemView.findViewById(R.id.right_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            right_name = (TextView) itemView.findViewById(R.id.right_name);
            right_content_yuehoujifeng_lin = (LinearLayout) itemView.findViewById(R.id.right_content_yuehoujifeng_lin);
            right_yuehoujifeng_img = (ImageView) itemView.findViewById(R.id.right_yuehoujifeng_img);
            chat_download_pgb = (ProgressBar) itemView.findViewById(R.id.chat_download_pgb);
            right_yuehoujifeng_look = (TextView) itemView.findViewById(R.id.right_yuehoujifeng_look);
        }
    }

    private class LeftCustomAddressViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private LinearLayout left_content_layout;
        private TextView left_content;
        private ImageView address_img;

        public LeftCustomAddressViewHolder(View view) {
            super(view);
            left_time = view.findViewById(R.id.left_time);
            chat_head = view.findViewById(R.id.chat_head);
            left_name = view.findViewById(R.id.left_name);
            left_content_layout = view.findViewById(R.id.left_content_layout);
            left_content = view.findViewById(R.id.left_content);
            address_img = view.findViewById(R.id.address_img);

        }
    }

    private class RightCustomAddressViewHolder extends RecyclerView.ViewHolder {
        private TextView right_time;
        private ImageView chat_head;
        private TextView right_name;
        private LinearLayout right_content_layout;
        private TextView right_content;
        private ImageView address_img;

        public RightCustomAddressViewHolder(View view) {
            super(view);
            right_time = view.findViewById(R.id.right_time);
            chat_head = view.findViewById(R.id.chat_head);
            right_name = view.findViewById(R.id.right_name);
            right_content_layout = view.findViewById(R.id.right_content_layout);
            right_content = view.findViewById(R.id.right_content);
            address_img = view.findViewById(R.id.address_img);
        }
    }

    private class LeftCustomShareViewHolder extends RecyclerView.ViewHolder {
        private TextView left_time;
        private ImageView chat_head;
        private TextView left_name;
        private View left_content_all;
        private TextView left_title;
        private TextView left_content_url;

        private LeftCustomShareViewHolder(View itemView) {
            super(itemView);
            left_time = (TextView) itemView.findViewById(R.id.left_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            left_name = (TextView) itemView.findViewById(R.id.left_name);
            left_content_all = itemView.findViewById(R.id.left_content_all);
            left_title = (TextView) itemView.findViewById(R.id.left_title);
            left_content_url = (TextView) itemView.findViewById(R.id.left_content_url);
        }
    }

    private class RightCustomShareViewHolder extends RecyclerView.ViewHolder {
        private TextView right_time;
        private ImageView chat_head;
        private TextView right_name;
        private View right_content_all;
        private TextView right_title;
        private TextView right_content_url;

        private RightCustomShareViewHolder(View itemView) {
            super(itemView);
            right_time = (TextView) itemView.findViewById(R.id.right_time);
            chat_head = (ImageView) itemView.findViewById(R.id.chat_head);
            right_name = (TextView) itemView.findViewById(R.id.right_name);
            right_content_all = itemView.findViewById(R.id.right_content_all);
            right_title = (TextView) itemView.findViewById(R.id.right_title);
            right_content_url = (TextView) itemView.findViewById(R.id.right_content_url);
        }
    }

    boolean isShow = false;

    /**
     * 下载
     *
     * @param urlLoadPath
     * @param fileName
     * @param type
     * @return
     */
    private boolean downloadFileByUrl(final String urlLoadPath, final String fileName, int type) {
        Log.i("MCApp_tt", "urlLoadPath: " + urlLoadPath + "  fileName:" + fileName);
        isShow = false;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection httpURLConnection = null;

        //创建 这个文件名 命名的 file 对象
        File file = new File(fileName);
        // Log.i(TAG,"file: " + file);
        if (!file.exists()) {     //倘若没有这个文件
            // Log.i(TAG,"创建文件");
            //file.createNewFile();  //创建这个文件
        } else {
            //文件已存在，不重新下载
            return true;
        }
        try {

            String nFileName = urlLoadPath.substring(urlLoadPath.lastIndexOf("/") + 1);
            String urlHost = urlLoadPath.substring(0, urlLoadPath.lastIndexOf("/") + 1);
            URL url = new URL(urlHost + URLEncoder.encode(nFileName, "utf-8"));
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5 * 1000);
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                //网络连接成功
                //根据响应获取文件大小
                int fileSize = httpURLConnection.getContentLength();
                // Log.i(TAG,"文件大小： " + fileSize);
                inputStream = httpURLConnection.getInputStream();
                fileOutputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int tem = 0;
                while ((tem = inputStream.read(b)) != -1) {
                    fileOutputStream.write(b, 0, tem);
                }

            } else {
                if (isShow) {

                } else {
                    isShow = true;
//                    showToast("文件加载失败");
                }
                return false;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 长按删除
     */
    private class OnChatItemLongClickListener implements View.OnLongClickListener {

        private ChatMessageBase data;
        private RecyclerView.ViewHolder holder;
        private boolean isShow = false;

        public OnChatItemLongClickListener(RecyclerView.ViewHolder holder, ChatMessageBase data) {
            this.data = data;
            this.holder = holder;

            if (holder instanceof RightCustomTextViewHolder
                    || holder instanceof LeftCustomTextViewHolder) {
                switch (data.type) {
                    case AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE:
                    case AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO:
                        isShow = false;
                        break;
                    default:
                        if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                            isShow = false;
                        } else {
                            isShow = true;
                        }
                        break;
                }
            } else if (holder instanceof LeftCustomImageViewHolder ||
                    holder instanceof LeftCustomVideoViewHolder ||
                    holder instanceof LeftCustomVoiceViewHolder ||
                    holder instanceof LeftCustomShareViewHolder ||
                    holder instanceof LeftCustomFileViewHolder ||
                    holder instanceof RightCustomImageViewHolder ||
                    holder instanceof RightCustomVideoViewHolder ||
                    holder instanceof RightCustomVoiceViewHolder ||
                    holder instanceof RightCustomShareViewHolder ||
                    holder instanceof RightCustomFileViewHolder) {
                if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                    isShow = false;
                } else {
                    isShow = true;
                }
            } else {
                isShow = false;
            }

        }

        @Override
        public boolean onLongClick(View v) {
            showPopWindows(isShow, v, data);
            return false;
        }
    }

    private void showPopWindows(boolean isShow, View view, ChatMessageBase data) {
        final List<String> dataList = new ArrayList<>();
        if (isShow) {
            dataList.add(AppUtils.getString(R.string.user_detail_zhuanfa_perple));
        }
        dataList.add(AppUtils.getString(R.string.user_detail_del_perple));
        mPopupWindowList = new PopupWindowList(view.getContext());
        mPopupWindowList.setAnchorView(view);
        mPopupWindowList.setItemData(dataList);
        mPopupWindowList.setModal(true);
        mPopupWindowList.show();
        mPopupWindowList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindowList.hide();
                if (dataList.size() == 2) {
                    if (0 == position) {//删除
                        delZhuanFaMessage(data);
                    } else if (1 == position) {//转发
                        if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                            AppBaseActivity.showToast("信息尚未解密");
                        } else {
                            deleChatRecord(data);
                        }
                    }
                } else {
                    if (0 == position) {//删除
                        deleChatRecord(data);
                    }
                }
            }
        });
    }

    private void delZhuanFaMessage(ChatMessageBase data) {
        Intent intent = new Intent(mContext, ZhuanFaChooseActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("users", usersTrans);
        intent.putExtra("strUserID", strUserID);
        intent.putExtra("strUserDomainCode", strUserDomainCode);
        intent.putExtra("isGroup", isGroup);
        intent.putExtra("strGroupID", strGroupID);
        intent.putExtra("strGroupDomain", strGroupDomain);
        intent.putExtra("mMessageUsersDate", mMessageUsersDate);

        mContext.startActivity(intent);
    }

    public void deleChatRecord(ChatMessageBase data) {
        int index = mDataList.indexOf(data);

        if (null != mDataList && mDataList.contains(data)) {

            if (isGroup) {
                AppDatas.MsgDB()
                        .chatGroupMsgDao()
                        .deleteBySessionIDAndId(data.sessionID, data.id);

            } else {
                AppDatas.MsgDB()
                        .chatSingleMsgDao()
                        .deleteBySessionIDAndId(data.sessionID, data.id);

            }

            if (index != mDataList.size() - 1) {
                mDataList.remove(data);
                notifyItemRemoved(index);
//                notifyDataSetChanged();
                return;
            }
            mDataList.remove(data);

            VimMessageListMessages.get().clearMessage(data.sessionID);
            String str = "";
            boolean isDeal = false;
            if (index > 0) {
                isDeal = true;
                mDataList.get(index - 1).sessionUserList = mMessageUsersDate;
                if (mDataList.get(index - 1).type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                    str = mDataList.get(index - 1).msgTxt;
                    mDataList.get(index - 1).msgTxt = mDataList.get(index - 1).mStrEncrypt;
                } else {
                    if (TextUtils.isEmpty(mDataList.get(index - 1).fileUrl)) {
                        str = mDataList.get(index - 1).msgTxt;
                        mDataList.get(index - 1).msgTxt = mDataList.get(index - 1).mStrEncrypt;
                    } else {
                        str = mDataList.get(index - 1).fileUrl;
                        mDataList.get(index - 1).fileUrl = mDataList.get(index - 1).mStrEncrypt;
                    }
                }
                VimMessageBean bean = VimMessageBean.from(mDataList.get(index - 1));
                ChatUtil.get().saveChangeMsg(bean);
            }

            if (isDeal) {
                if (mDataList.get(index - 1).type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                    mDataList.get(index - 1).msgTxt = str;
                } else {
                    if (TextUtils.isEmpty(mDataList.get(index - 1).fileUrl)) {
                        mDataList.get(index - 1).msgTxt = str;
                    } else {
                        mDataList.get(index - 1).fileUrl = str;
                    }
                }
            }
            notifyDataSetChanged();

            EventBus.getDefault().post(new RefMessageList());
        }

    }

    public void deleByChatMessageBase(ChatMessageBase data) {
        if (null != mDataList) {
            new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                @Override
                public Object doOnThread() {

                    int index = -1;

                    ChatMessageBase mdata = null;

                    for (ChatMessageBase ndata : mDataList) {
                        index++;
                        if (ndata.msgID.equals(data.msgID)) {
                            mdata = ndata;
                            break;
                        }
                    }
                    if (null != mdata) {
                        if (isGroup) {
                            AppDatas.MsgDB()
                                    .chatGroupMsgDao()
                                    .deleteBySessionIDAndId(mdata.sessionID, mdata.id);

                        } else {
                            AppDatas.MsgDB()
                                    .chatSingleMsgDao()
                                    .deleteBySessionIDAndId(mdata.sessionID, mdata.id);

                        }
                    }

                    if (index != mDataList.size() - 1) {
                        mDataList.remove(mdata);
                        return "";
                    }
                    mDataList.remove(mdata);
                    VimMessageListMessages.get().clearMessage(data.sessionID);
                    String str = "";
                    boolean isDeal = false;
                    if (index > 0) {
                        isDeal = true;
                        if (mDataList.get(index - 1).type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                            str = mDataList.get(index - 1).msgTxt;
                            mDataList.get(index - 1).msgTxt = mDataList.get(index - 1).mStrEncrypt;
                        } else {
                            if (TextUtils.isEmpty(mDataList.get(index - 1).fileUrl)) {
                                str = mDataList.get(index - 1).msgTxt;
                                mDataList.get(index - 1).msgTxt = mDataList.get(index - 1).mStrEncrypt;
                            } else {
                                str = mDataList.get(index - 1).fileUrl;
                                mDataList.get(index - 1).fileUrl = mDataList.get(index - 1).mStrEncrypt;
                            }
                        }
                        mDataList.get(index - 1).sessionUserList = mMessageUsersDate;
                        VimMessageBean bean = VimMessageBean.from(mDataList.get(index - 1));
                        ChatUtil.get().saveChangeMsg(bean);
                    }

                    if (isDeal) {
                        if (mDataList.get(index - 1).type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                            mDataList.get(index - 1).msgTxt = str;
                        } else {
                            if (TextUtils.isEmpty(mDataList.get(index - 1).fileUrl)) {
                                mDataList.get(index - 1).msgTxt = str;
                            } else {
                                mDataList.get(index - 1).fileUrl = str;
                            }
                        }
                    }

                    return "";
                }

                @Override
                public void doOnMain(Object data) {
                    notifyDataSetChanged();
                }
            });
        }

    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
}
