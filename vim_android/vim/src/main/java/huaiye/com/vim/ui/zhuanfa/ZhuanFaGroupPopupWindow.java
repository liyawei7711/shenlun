package huaiye.com.vim.ui.zhuanfa;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToMuliteUserRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.wxiwei.office.fc.usermodel.Hyperlink;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.CloseZhuanFa;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.downloadutils.ChatContentDownload;
import huaiye.com.vim.common.helper.ChatLocalPathHelper;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.ChatUtil;
import huaiye.com.vim.common.utils.WeiXinDateFormat;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatGroupMsgBean;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.dao.msgs.ContentBean;
import huaiye.com.vim.dao.msgs.SendMsgUserBean;
import huaiye.com.vim.dao.msgs.UserInfo;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.GroupInfo;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppBaseActivity.showToast;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

public class ZhuanFaGroupPopupWindow extends PopupWindow {
    private Context mContext;
    ChatMessageBase data;

    String strUserID; //老对话的id
    String strUserDomainCode;//老对话的domain
    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();//老对话的所有user

    boolean isGroup;
    String strGroupID;
    String strGroupDomain;

    GroupInfo groupInfo;//转发的对象
    ArrayList<SdpMessageCmProcessIMReq.UserInfo> usersNew = new ArrayList<>();//转发的对话user
    ArrayList<SendUserBean> sendUserBeans = new ArrayList<>();

    ImageView iv_head;
    TextView tv_name;
    TextView tv_content;
    ImageView iv_content;

    View fl_common;
    View ll_share;
    TextView tv_title;
    TextView tv_content_share;
    ImageView iv_content_share;
    TextView tv_from;
    TextView tv_send;

    RequestOptions requestOptions;
    File fC;

    public ZhuanFaGroupPopupWindow(Context context, ArrayList<UserInfo> users, String strUserID, String strUserDomainCode,
                                   boolean isGroup, String strGroupID, String strGroupDomain) {
        super(context);
        mContext = context;
        for (UserInfo temp : users) {
            SdpMessageCmProcessIMReq.UserInfo userInfo = new SdpMessageCmProcessIMReq.UserInfo();
            userInfo.strUserID = temp.strUserID;
            userInfo.strUserDomainCode = temp.strUserDomainCode;
            this.users.add(userInfo);
        }
        this.strUserID = strUserID;
        this.strUserDomainCode = strUserDomainCode;
        this.isGroup = isGroup;
        this.strGroupID = strGroupID;
        this.strGroupDomain = strGroupDomain;
        initView();
    }

    public void initView() {
        fC = new File(mContext.getExternalFilesDir(null) + File.separator + "Vim/chat/");

        setBackgroundDrawable(null);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        requestOptions = new RequestOptions().centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());

        Drawable drawable = new ColorDrawable(Color.parseColor("#00000000"));
        setBackgroundDrawable(drawable);// 点击外部消失
        setOutsideTouchable(true); // 点击外部消失
        setFocusable(true); // 点击back键消失

        View view = LayoutInflater.from(mContext).inflate(R.layout.zhuanfa_popwindow, null);
        setContentView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        iv_head = view.findViewById(R.id.iv_head);
        tv_name = view.findViewById(R.id.tv_name);
        fl_common = view.findViewById(R.id.fl_common);
        ll_share = view.findViewById(R.id.ll_share);
        tv_content = view.findViewById(R.id.tv_content);
        iv_content = view.findViewById(R.id.iv_content);
        tv_title = view.findViewById(R.id.tv_title);
        tv_content_share = view.findViewById(R.id.tv_content_share);
        iv_content_share = view.findViewById(R.id.iv_content_share);
        tv_from = view.findViewById(R.id.tv_from);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_send = view.findViewById(R.id.tv_send);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_send.setEnabled(false);
                sendMessage();
            }
        });
    }

    public void setSendUser(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
        initUserEncrypt();
    }

    private void sendMessage() {
        if (AppUtils.MESSAGE_TYPE_TEXT == data.type) {
            sendTxtMsg();
        } else if (AppUtils.MESSAGE_TYPE_IMG == data.type) {
            sendImg();
        } else if (AppUtils.MESSAGE_TYPE_FILE == data.type) {
            sendFile();
        } else if (AppUtils.MESSAGE_TYPE_VIDEO_FILE == data.type) {
            sendVideo();
        } else if (AppUtils.MESSAGE_TYPE_AUDIO_FILE == data.type) {
            sendAudio();
        } else if (AppUtils.MESSAGE_TYPE_SHARE == data.type) {
            sendShareMsg();
        } else {
            sendTxtMsg();
        }

    }

    private void sendImg() {
        File file = new File(EncryptUtil.getNewFile(data.localFilePath));
        File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptFile(fileun.getPath(), EncryptUtil.getNewFile(fileun.getPath()),
                    true, true, groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                    "", "", usersNew, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            upFileImg(fileun, new File(resp.m_strData));
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("对方未开启加密,无法发送");
                        }
                    }
            );
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            upFileImg(fileun, fileun);
        }
    }

    private void upFileImg(File fileOld, File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
                    @Override
                    public void onSuccess(final Upload upload) {
                        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                String httpFile = upload.file1_name;

                                ChatLocalPathHelper.getInstance().cacheChatLoaclPath(httpFile, file.getPath());
                                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                    encrypt(httpFile, true, false,
                                            0, file.length(),
                                            data.fileName, true);
                                } else {
                                    if(nEncryptIMEnable) {
                                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                        return;
                                    }
                                    String msgContent = ChatUtil.getChatContentJson(mContext, "", "",
                                            httpFile, 0, file.length(),
                                            false,
                                            10, 0, 0, 0,
                                            data.fileName);
                                    sendRealMsg(msgContent);
                                }
                            }
                        }, "upFileImg");

                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        showToast("文件上传失败");
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                }, file, AppDatas.Constants().getFileUploadUri());
            }
        }).start();

    }

    private void sendVideo() {
        ((AppBaseActivity) mContext).mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();

        String fileLocal = "";
        if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
            fileLocal = data.localFilePath;
        } else {
            if ("文件上传失败".equals(data.fileUrl)) {
                showToast("文件加载失败");
                dismiss();
                return;
            }
            try {
                fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
            } catch (Exception e) {
                dismiss();
            }
        }
        File file = new File(EncryptUtil.getNewFile(fileLocal));
        File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptFile(fileun.getPath(), EncryptUtil.getNewFile(fileun.getPath()),
                    true, true, groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                    "", "", usersNew, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            upFile(fileun, new File(resp.m_strData), true);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("文件加密失败");
                            ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                            dismiss();
                        }
                    }
            );
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            upFile(fileun, fileun, true);
        }

    }

    private void sendFile() {
        ((AppBaseActivity) mContext).mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();

        String localFilePath = "";
        if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
            localFilePath = data.localFilePath;
        } else {
            try {
                localFilePath = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
            } catch (Exception e) {
            }
        }
        File file = new File(EncryptUtil.getNewFile(localFilePath));
        File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
        if (!fileun.exists()) {
            showToast("文件下载失败，请重试");
            ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
            dismiss();
            return;
        }
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptFile(fileun.getPath(), EncryptUtil.getNewFile(fileun.getPath()),
                    true, true, groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                    "", "", usersNew, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            upFile(fileun, new File(resp.m_strData), false);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                            showToast("文件加密失败");
                        }
                    }
            );
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            upFile(fileun, fileun, false);
        }
    }

    private void upFile(File oldFile, File file, boolean isVideo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
                    @Override
                    public void onSuccess(Upload upload) {
                        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                ChatLocalPathHelper.getInstance().cacheChatLoaclPath(upload.file1_name, file.getPath());
                                int recordTime = JniIntf.GetRecordFileDuration(oldFile.getPath());
                                if (isVideo) {
                                    if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                        encrypt(upload.file1_name, true, true, recordTime,
                                                oldFile.length(), data.fileName, false);
                                    } else {
                                        if(nEncryptIMEnable) {
                                            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                            return;
                                        }
                                        String msgContent = ChatUtil.getChatContentJson(mContext, "", "",
                                                upload.file1_name,
                                                0, oldFile.length(),
                                                false,
                                                recordTime, 0, 0, 0, data.fileName);
                                        sendRealMsg(msgContent);
                                    }
                                } else {
                                    if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                        encrypt(upload.file1_name, true, false, 0,
                                                oldFile.length(), data.fileName, false);
                                    } else {
                                        if(nEncryptIMEnable) {
                                            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                            return;
                                        }
                                        String msgContent = ChatUtil.getChatContentJson(mContext, "", "",
                                                upload.file1_name,
                                                0, oldFile.length(), false, 0, 0, 0,
                                                0, data.fileName);
                                        sendRealMsg(msgContent);
                                    }
                                }
                            }
                        }, "upFile");
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        ((AppBaseActivity) mContext).showToast("文件上传失败");
                        dismiss();
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                            }
                        }, "end");

                    }
                }, file, AppDatas.Constants().getFileUploadUri());
            }
        }).start();
    }

    private void sendAudio() {
        ((AppBaseActivity) mContext).mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();
        String fileLocal = "";
        try {
            fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
        } catch (Exception e) {

        }
        if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
            fileLocal = data.localFilePath;
        }
        File file = new File(EncryptUtil.getNewFile(fileLocal));
        File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
        if (!fileun.exists()) {
            showToast("文件下载失败，请重试");
            return;
        }
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptFile(fileun.getPath(), EncryptUtil.getNewFile(fileun.getPath()),
                    true, true, groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                    "", "", usersNew, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            upFileVoice(fileun, new File(resp.m_strData));
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("对方未开启加密,无法发送");
                            if (((AppBaseActivity) mContext).mZeusLoadView != null && ((AppBaseActivity) mContext).mZeusLoadView.isShowing())
                                ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                        }
                    }
            );
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            upFileVoice(fileun, fileun);
        }
    }

    private void upFileVoice(File fileOld, File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
                    @Override
                    public void onSuccess(final Upload upload) {
                        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                if (((AppBaseActivity) mContext).mZeusLoadView != null && ((AppBaseActivity) mContext).mZeusLoadView.isShowing())
                                    ((AppBaseActivity) mContext).mZeusLoadView.dismiss();

                                if (upload.file1_name == null) {
                                    ((AppBaseActivity) mContext).showToast(AppUtils.getString(R.string.file_upload_false));
                                    return;
                                }

                                ChatLocalPathHelper.getInstance().cacheChatLoaclPath(upload.file1_name, file.getPath());
                                int recordTime = JniIntf.GetRecordFileDuration(fileOld.getPath());
                                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                    encrypt(upload.file1_name, true, true, recordTime, file.length(), fileOld.getAbsolutePath().substring(fileOld.getAbsolutePath().lastIndexOf("/") + 1), false);
                                } else {
                                    if(nEncryptIMEnable) {
                                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                        return;
                                    }
                                    String msgContentVideoSuccess = ChatUtil.getChatContentJson(mContext, "", "",
                                            upload.file1_name, recordTime,
                                            file.length(),
                                            false,
                                            recordTime, 0, 0, 0, fileOld.getAbsolutePath().substring(fileOld.getAbsolutePath().lastIndexOf("/") + 1));
                                    sendRealMsg(msgContentVideoSuccess);
                                }
                            }
                        }, "upFileVoice");

                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        ((AppBaseActivity) mContext).showToast(AppUtils.getString(R.string.file_upload_false));
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                        if (((AppBaseActivity) mContext).mZeusLoadView != null && ((AppBaseActivity) mContext).mZeusLoadView.isShowing())
                            ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                    }
                }, file, AppDatas.Constants().getFileUploadUri());
            }
        }).start();

    }

    private void sendTxtMsg() {
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            encrypt(data.msgTxt, false, false, 0, -1, "", false);
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            String msgContent = ChatUtil.getChatContentJson(mContext, data.msgTxt, "", "", 0, 0,
                    false,
                    data.msgTxt.length(), 0, 0, 0, "");
            sendRealMsg(msgContent);
        }
    }

    private void sendShareMsg() {
        if(TextUtils.isEmpty(tv_title.getHint())) {
            tv_title.setHint("");
        }
        if(TextUtils.isEmpty(tv_title.getText())) {
            tv_title.setText("");
        }
        if(TextUtils.isEmpty(tv_content_share.getText())) {
            tv_content_share.setText("");
        }
        String msgContent = ChatUtil.getChatContentJson(mContext, tv_title.getText().toString(),
                tv_content_share.getText().toString(),
                tv_title.getHint().toString(), 0, 0,
                false,
                tv_title.getText().toString().length(), 0, 0, 0, "");
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            encrypt(msgContent);
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            sendWetherEncryptShare(false, msgContent);
        }
    }

    void encrypt(String str) {
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptTxt(str, true, true,
                    groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                    "", "", usersNew, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                            sendWetherEncryptShare(true, sessionRsp);
                        }

                        @Override
                        public void onError(ErrorInfo sessionRsp) {
//                            AppBaseActivity.showToast("对方未开启加密,无法发送");
                        }
                    });
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            sendWetherEncryptShare(false, str);
        }
    }

    void encrypt(String str, boolean isFile, boolean isVoice, int recordTime, long size, String fileName, boolean isImg) {
        final String[] msgContent = new String[1];
        int longTime = isVoice ? recordTime : str.length();
        if (isImg) {
            longTime = 10;
        }
        if (data.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
            msgContent[0] = str;
        } else {
            msgContent[0] = ChatUtil.getChatContentJson(mContext, isFile ? "" : str, "",
                    isFile ? str : "", recordTime, size,
                    false,
                    longTime,
                    0, 0, 0, fileName);
        }

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptTxt(str, true, true,
                    groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                    "", "", usersNew, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                            sendWetherEncrypt(true, sessionRsp, isFile, isVoice, recordTime, size, fileName, str, isImg);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
//                            AppBaseActivity.showToast("对方未开启加密,无法发送");
                        }
                    });
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                return;
            }
            sendWetherEncrypt(false, msgContent[0]);
        }
    }

    void sendRealMsg(String msgContent) {
        sendWetherEncrypt(false, msgContent);
    }

    /**
     * 加密群组发送
     *
     * @param sessionRsp
     */
    private void sendWetherEncrypt(boolean isEncrypt, SdpMessageCmProcessIMRsp sessionRsp, boolean isFile, boolean isVoice, int recordTime, long size, String fileName, String msgOld, boolean isImg) {
        int longTime = isVoice ? recordTime : msgOld.length();
        if (isImg) {
            longTime = 10;
        }
        for (SdpMessageCmProcessIMRsp.UserData temp : sessionRsp.m_lstData) {
            String msgText = temp.strData;
            String str;
            if (data.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                str = msgText;
            } else {
                str = ChatUtil.getChatContentJson(mContext,
                        isFile ? "" : msgText, "",
                        isFile ? msgText : "",
                        recordTime,
                        size,
                        false,
                        longTime,
                        0,
                        0,
                        0, fileName);
            }
            ChatMessageBean bean = new ChatMessageBean();
            bean.content = str;
            bean.type = data.type;
            bean.sessionID = getGroupSessionId();
            bean.sessionName = getSessionName();
            bean.fromUserDomain = AppDatas.Auth().getDomainCode();
            bean.fromUserId = AppDatas.Auth().getUserID() + "";
            bean.fromUserName = AppDatas.Auth().getUserName();
            bean.groupType = 1;
            bean.groupDomainCode = groupInfo.strGroupDomainCode;
            bean.groupID = groupInfo.strGroupID;
            bean.bEncrypt = isEncrypt ? 1 : 0;
            bean.time = System.currentTimeMillis() / 1000;
            bean.sessionUserList = new ArrayList<>();
            bean.sessionUserList.addAll(sendUserBeans);

            ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
            sessionUserList.add(new SendUserBean(temp.strUserID, temp.strUserDomainCode, temp.strUserID));

            Gson gson = new Gson();
            HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                            .setIsImportant(true)
                            .setMessage(gson.toJson(bean))
                            .setUser(sessionUserList), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                        @Override
                        public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                            ContentBean content = huaiye.com.vim.common.utils.ChatUtil.analysisChatContentJson(bean.content);

                            String unEncryptStr;
                            if (bean.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                                unEncryptStr = content.msgTxt;
                            } else {
                                unEncryptStr = TextUtils.isEmpty(content.fileUrl) ? content.msgTxt : content.fileUrl;
                            }

                            EncryptUtil.converEncryptText(unEncryptStr, true,
                                    groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                                    temp.strUserID, temp.strUserDomainCode,
                                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                        @Override
                                        public void onSuccess(SdpMessageCmProcessIMRsp rsp) {
                                            if (rsp.m_nResultCode == 0) {
                                                if (bean.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                                                    content.msgTxt = rsp.m_lstData.get(0).strData;
                                                } else {
                                                    if (TextUtils.isEmpty(content.fileUrl)) {
                                                        content.msgTxt = rsp.m_lstData.get(0).strData;
                                                    } else {
                                                        content.fileUrl = rsp.m_lstData.get(0).strData;
                                                    }
                                                }
                                                bean.content = gson.toJson(content);
                                            }
                                            dealSaveMessageAndLoad(sessionRsp, temp, bean);
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                            dealSaveMessageAndLoad(sessionRsp, temp, bean);
                                        }
                                    });
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            if (sessionRsp.m_lstData.indexOf(temp) == sessionRsp.m_lstData.size() - 1) {
                                ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                                showToast("发送失败" + errorInfo.getMessage());
                                dismiss();
                            }

                        }
                    }
            );
        }
    }

    private void sendWetherEncrypt(boolean isEncrypt, SdpMessageCmProcessIMRsp sessionRsp) {
        for (SdpMessageCmProcessIMRsp.UserData temp : sessionRsp.m_lstData) {
            ChatMessageBean bean = new ChatMessageBean();
            bean.content = temp.strData;
            bean.type = AppUtils.MESSAGE_TYPE_SHARE;
            bean.sessionID = getGroupSessionId();
            bean.sessionName = getSessionName();
            bean.fromUserDomain = AppDatas.Auth().getDomainCode();
            bean.fromUserId = AppDatas.Auth().getUserID() + "";
            bean.fromUserName = AppDatas.Auth().getUserName();
            bean.groupType = 1;
            bean.groupDomainCode = groupInfo.strGroupDomainCode;
            bean.groupID = groupInfo.strGroupID;
            bean.bEncrypt = isEncrypt ? 1 : 0;
            bean.time = System.currentTimeMillis() / 1000;
            bean.sessionUserList = new ArrayList<>();
            bean.sessionUserList.addAll(sendUserBeans);

            ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
            sessionUserList.add(new SendUserBean(temp.strUserID, temp.strUserDomainCode, temp.strUserID));

            Gson gson = new Gson();
            HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                            .setIsImportant(true)
                            .setMessage(gson.toJson(bean))
                            .setUser(sessionUserList), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                        @Override
                        public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                            EncryptUtil.converEncryptText(bean.content, true,
                                    groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                                    temp.strUserID, temp.strUserDomainCode,
                                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                        @Override
                                        public void onSuccess(SdpMessageCmProcessIMRsp rsp) {
                                            if (rsp.m_nResultCode == 0) {
                                                bean.content = rsp.m_lstData.get(0).strData;
                                            }
                                            dealSaveMessageAndLoad(sessionRsp, temp, bean);
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                            dealSaveMessageAndLoad(sessionRsp, temp, bean);
                                        }
                                    });
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            if (sessionRsp.m_lstData.indexOf(temp) == sessionRsp.m_lstData.size() - 1) {
                                ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                                showToast("分享失败" + errorInfo.getMessage());
                                dismiss();
                            }

                        }
                    }
            );
        }
    }

    private void sendWetherEncryptShare(boolean isEncrypt, SdpMessageCmProcessIMRsp sessionRsp) {
        for (SdpMessageCmProcessIMRsp.UserData temp : sessionRsp.m_lstData) {
            ChatMessageBean bean = new ChatMessageBean();
            bean.content = temp.strData;
            bean.type = AppUtils.MESSAGE_TYPE_SHARE;
            bean.sessionID = getGroupSessionId();
            bean.sessionName = getSessionName();
            bean.fromUserDomain = AppDatas.Auth().getDomainCode();
            bean.fromUserId = AppDatas.Auth().getUserID() + "";
            bean.fromUserName = AppDatas.Auth().getUserName();
            bean.groupType = 1;
            bean.groupDomainCode = groupInfo.strGroupDomainCode;
            bean.groupID = groupInfo.strGroupID;
            bean.bEncrypt = isEncrypt ? 1 : 0;
            bean.time = System.currentTimeMillis() / 1000;
            bean.sessionUserList = new ArrayList<>();
            bean.sessionUserList.addAll(sendUserBeans);

            ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
            sessionUserList.add(new SendUserBean(temp.strUserID, temp.strUserDomainCode, temp.strUserID));

            Gson gson = new Gson();
            HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                            .setIsImportant(true)
                            .setMessage(gson.toJson(bean))
                            .setUser(sessionUserList), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                        @Override
                        public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                            EncryptUtil.converEncryptText(bean.content, true,
                                    groupInfo.strGroupID, groupInfo.strGroupDomainCode,
                                    temp.strUserID, temp.strUserDomainCode,
                                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                        @Override
                                        public void onSuccess(SdpMessageCmProcessIMRsp rsp) {
                                            if (rsp.m_nResultCode == 0) {
                                                bean.content = rsp.m_lstData.get(0).strData;
                                            }
                                            dealSaveMessageAndLoad(sessionRsp, temp, bean);
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                            dealSaveMessageAndLoad(sessionRsp, temp, bean);
                                        }
                                    });
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            if (sessionRsp.m_lstData.indexOf(temp) == sessionRsp.m_lstData.size() - 1) {
                                ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                                showToast("分享失败" + errorInfo.getMessage());
                                dismiss();
                            }

                        }
                    }
            );
        }
    }

    private void sendWetherEncryptShare(boolean isEncrypt, String msgContent) {
        final ChatMessageBean bean = new ChatMessageBean();
        bean.content = msgContent;
        bean.type = AppUtils.MESSAGE_TYPE_SHARE;
        bean.sessionID = getGroupSessionId();
        bean.sessionName = getSessionName();
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 1;
        bean.bEncrypt = isEncrypt ? 1 : 0;
        bean.groupDomainCode = groupInfo.strGroupDomainCode;
        bean.groupID = groupInfo.strGroupID;
        bean.time = System.currentTimeMillis() / 1000;
        bean.sessionUserList = new ArrayList<>();
        bean.sessionUserList.addAll(sendUserBeans);
        Gson gson = new Gson();
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(true)
                        .setMessage(gson.toJson(bean))
                        .setUser(sendUserBeans), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                        ChatGroupMsgBean groupMsgBean = ChatGroupMsgBean.from(bean);
                        groupMsgBean.read = 1;
                        AppDatas.MsgDB()
                                .chatGroupMsgDao()
                                .insert(groupMsgBean);
                        VimMessageBean vimMessageBean = VimMessageBean.from(bean);
                        huaiye.com.vim.dao.msgs.ChatUtil.get().saveChangeMsg(vimMessageBean, true);

                        for (SendUserBean temp : bean.sessionUserList) {
                            SendMsgUserBean sendUserInfo = AppDatas.MsgDB().getSendUserListDao().getSendUserInfo(groupMsgBean.sessionID);
                            if (sendUserInfo != null) {
                                sendUserInfo.strUserID = temp.strUserID;
                                sendUserInfo.strUserDomainCode = temp.strUserDomainCode;
                                AppDatas.MsgDB().getSendUserListDao().update(sendUserInfo);
                            } else {
                                AppDatas.MsgDB().getSendUserListDao().insert(new SendMsgUserBean(groupMsgBean.sessionID, temp.strUserID, temp.strUserDomainCode));
                            }
                        }

                        ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                        showToast("分享成功");
                        dismiss();
                        EventBus.getDefault().post(new CloseZhuanFa());
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                        showToast("分享失败" + errorInfo.getMessage());
                        dismiss();
                    }
                }
        );
    }

    private void dealSaveMessageAndLoad(SdpMessageCmProcessIMRsp sessionRsp, SdpMessageCmProcessIMRsp.UserData temp, ChatMessageBean bean) {
        if (sessionRsp.m_lstData.indexOf(temp) == sessionRsp.m_lstData.size() - 1) {
            ChatGroupMsgBean groupMsgBean = ChatGroupMsgBean.from(bean);
            groupMsgBean.read = 1;
            AppDatas.MsgDB()
                    .chatGroupMsgDao()
                    .insert(groupMsgBean);

            SendMsgUserBean sendUserInfo = AppDatas.MsgDB().getSendUserListDao().getSendUserInfo(groupMsgBean.sessionID);
            if (sendUserInfo != null) {
                sendUserInfo.strUserID = temp.strUserID;
                sendUserInfo.strUserDomainCode = temp.strUserDomainCode;
                AppDatas.MsgDB().getSendUserListDao().update(sendUserInfo);
            } else {
                AppDatas.MsgDB().getSendUserListDao().insert(new SendMsgUserBean(groupMsgBean.sessionID, temp.strUserID, temp.strUserDomainCode));
            }

            VimMessageBean vimMessageBean = VimMessageBean.from(bean);
            huaiye.com.vim.dao.msgs.ChatUtil.get().saveChangeMsg(vimMessageBean, true);

            ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
            showToast("转发成功");
            dismiss();
            EventBus.getDefault().post(new CloseZhuanFa());
        }
    }

    private void sendWetherEncrypt(boolean isEncrypt, String msgContent) {
        final ChatMessageBean bean = new ChatMessageBean();
        bean.content = msgContent;
        bean.type = data.type;
        bean.sessionID = getGroupSessionId();
        bean.sessionName = getSessionName();
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 1;
        bean.bEncrypt = isEncrypt ? 1 : 0;
        bean.groupDomainCode = groupInfo.strGroupDomainCode;
        bean.groupID = groupInfo.strGroupID;
        bean.time = System.currentTimeMillis() / 1000;
        bean.sessionUserList = new ArrayList<>();
        bean.sessionUserList.addAll(sendUserBeans);
        Gson gson = new Gson();
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(true)
                        .setMessage(gson.toJson(bean))
                        .setUser(sendUserBeans), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                        ChatGroupMsgBean groupMsgBean = ChatGroupMsgBean.from(bean);
                        groupMsgBean.read = 1;
                        AppDatas.MsgDB()
                                .chatGroupMsgDao()
                                .insert(groupMsgBean);
                        VimMessageBean vimMessageBean = VimMessageBean.from(bean);
                        huaiye.com.vim.dao.msgs.ChatUtil.get().saveChangeMsg(vimMessageBean, true);

                        for (SendUserBean temp : bean.sessionUserList) {
                            SendMsgUserBean sendUserInfo = AppDatas.MsgDB().getSendUserListDao().getSendUserInfo(groupMsgBean.sessionID);
                            if (sendUserInfo != null) {
                                sendUserInfo.strUserID = temp.strUserID;
                                sendUserInfo.strUserDomainCode = temp.strUserDomainCode;
                                AppDatas.MsgDB().getSendUserListDao().update(sendUserInfo);
                            } else {
                                AppDatas.MsgDB().getSendUserListDao().insert(new SendMsgUserBean(groupMsgBean.sessionID, temp.strUserID, temp.strUserDomainCode));
                            }
                        }

                        ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                        showToast("转发成功");
                        dismiss();
                        EventBus.getDefault().post(new CloseZhuanFa());
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        ((AppBaseActivity) mContext).mZeusLoadView.dismiss();
                        showToast("发送失败" + errorInfo.getMessage());
                        dismiss();
                    }
                }
        );
    }

    public void showData(ChatMessageBase data) {
        if (tv_send != null) {
            tv_send.setEnabled(true);
        }
        this.data = data;

        Glide.with(mContext)
                .load(AppDatas.Constants().getFileServerURL() + groupInfo.strHeadUrl)
                .apply(requestOptions)
                .into(iv_head);
        tv_name.setText(groupInfo.strGroupName);

        if (AppUtils.MESSAGE_TYPE_TEXT == data.type) {
            fl_common.setVisibility(View.VISIBLE);
            ll_share.setVisibility(View.GONE);
            iv_content.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                tv_content.setText("信息已加密");
            } else {
                tv_content.setText(data.msgTxt);
            }
        } else if (AppUtils.MESSAGE_TYPE_IMG == data.type) {
            fl_common.setVisibility(View.VISIBLE);
            ll_share.setVisibility(View.GONE);
            iv_content.setVisibility(View.VISIBLE);
            tv_content.setVisibility(View.GONE);
            showImg();
        } else if (AppUtils.MESSAGE_TYPE_FILE == data.type) {
            fl_common.setVisibility(View.VISIBLE);
            ll_share.setVisibility(View.GONE);
            iv_content.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(data.fileName == null ? data.fileUrl.substring(data.fileUrl.lastIndexOf("_") + 1) : data.fileName);
            loadFile();
        } else if (AppUtils.MESSAGE_TYPE_AUDIO_FILE == data.type) {
            fl_common.setVisibility(View.VISIBLE);
            ll_share.setVisibility(View.GONE);
            iv_content.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText("转发语音信息:" + WeiXinDateFormat.getChatTime(data.time));
            loadAudio();
        } else if (AppUtils.MESSAGE_TYPE_VIDEO_FILE == data.type) {
            fl_common.setVisibility(View.VISIBLE);
            ll_share.setVisibility(View.GONE);
            iv_content.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText("转发视频信息:" + WeiXinDateFormat.getChatTime(data.time));
            loadVideo();
        } else if (AppUtils.MESSAGE_TYPE_SHARE == data.type) {
            fl_common.setVisibility(View.GONE);
            ll_share.setVisibility(View.VISIBLE);
            try {
                tv_title.setText(data.msgTxt);
                tv_title.setHint(data.fileUrl);
            } catch (Exception e) {
            }
            tv_content_share.setHint(data.summary);
        } else {
            fl_common.setVisibility(View.VISIBLE);
            ll_share.setVisibility(View.GONE);
            iv_content.setVisibility(View.GONE);
            tv_content.setVisibility(View.VISIBLE);
            if (data.bEncrypt == 1 && !data.isUnEncrypt) {
                tv_content.setText("信息已加密");
            } else {
                tv_content.setText(data.msgTxt);
            }
        }

//        sendWetherEncrypt(data, user, groupInfo);
//        encrypt(data.type, data);
    }

    private String getGroupSessionId() {
        return groupInfo.strGroupDomainCode + groupInfo.strGroupID;
    }

    private String getSessionName() {
        return groupInfo.strGroupName;
    }

    private void unEncryptImage() {
        File file = new File(EncryptUtil.getNewFile(data.localFilePath));
        if (file.exists()) {
            Glide.with(mContext)
                    .load(file)
                    .into(iv_content);
        } else {
            EncryptUtil.encryptFile(data.localFilePath, file.getAbsolutePath(),
                    false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                    isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            try {
                                Glide.with(mContext)
                                        .load(new File(resp.m_strData))
                                        .into(iv_content);
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("文件解密失败");
                        }
                    }
            );
        }
    }

    private void unEncryptImage2() {
        File file = new File(EncryptUtil.getNewFile(data.localFilePath));
        File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
        if (file.exists()) {
            if (fileun.exists()) {
                Glide.with(mContext)
                        .load(fileun)
                        .into(iv_content);
            } else {
                EncryptUtil.localEncryptFile(file.getAbsolutePath(), fileun.getAbsolutePath(), false,
                        new SdkCallback<SdpMessageCmProcessIMRsp>() {
                            @Override
                            public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                try {
                                    Glide.with(mContext)
                                            .load(new File(resp.m_strData))
                                            .into(iv_content);
                                } catch (Exception e) {

                                }
                            }

                            @Override
                            public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                showToast("文件解密失败");
                            }
                        }
                );
            }
        } else {
            EncryptUtil.converEncryptFile(data.localFilePath, file.getAbsolutePath(),
                    isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                    isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode,
                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            if (fileun.exists()) {
                                Glide.with(mContext)
                                        .load(fileun)
                                        .into(iv_content);
                            } else {
                                EncryptUtil.localEncryptFile(resp.m_strData, fileun.getAbsolutePath(), false,
                                        new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                            @Override
                                            public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                try {
                                                    Glide.with(mContext)
                                                            .load(new File(resp.m_strData))
                                                            .into(iv_content);
                                                } catch (Exception e) {

                                                }
                                            }

                                            @Override
                                            public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                showToast("文件解密失败");
                                            }
                                        }
                                );
                            }
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("文件解密失败");
                        }
                    }
            );

        }
    }

    private void showImg() {
        if (data.bEncrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                String fileLocal = "";
                try {
                    fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
                } catch (Exception e) {
                }
                final File ffLocal = new File(fileLocal);
                if (null != data && ffLocal.exists()) {
                    data.localFilePath = ffLocal.getAbsolutePath();
                    unEncryptImage2();
                } else {
                    if ("文件上传失败".equals(data.fileUrl)) {
                        return;
                    }
                    String finalFileLocal = fileLocal;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (ChatContentDownload.downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, finalFileLocal, data.type)) {
                                new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                                    @Override
                                    public void onMainDelay() {
                                        data.localFilePath = ffLocal.getAbsolutePath();
                                        unEncryptImage2();
                                    }
                                }, "showImg");
                            }
                        }
                    }).start();
                }
            } else {
                if(nEncryptIMEnable) {
                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                    return;
                }
                Glide.with(mContext)
                        .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
//                        .apply(requestOptions)
                        .into(iv_content);
            }
        } else {
            Glide.with(mContext)
                    .load(AppDatas.Constants().getFileServerURL() + data.fileUrl)
//                    .apply(requestOptions)
                    .into(iv_content);
        }
    }

    private void loadVideo() {
        if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
            go2PlayVideo2(data.localFilePath, data.bEncrypt);
        } else {
            if ("文件上传失败".equals(data.fileUrl)) {
                showToast("文件加载失败");
                return;
            }
            String fileLocal = "";
            try {
                fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
            } catch (Exception e) {
            }
            String finalFileLocal = fileLocal;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ChatContentDownload.downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, finalFileLocal, data.type)) {
                        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                go2PlayVideo2(finalFileLocal, data.bEncrypt);
                            }
                        }, "loadVideo");
                    }
                }
            }).start();

        }
    }

    private void go2PlayVideo(String localFilePath, int encrypt) {
        if (encrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(localFilePath));
                if (!file.exists()) {
                    EncryptUtil.encryptFile(localFilePath, file.getAbsolutePath(),
                            false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                if(nEncryptIMEnable) {
                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                    return;
                }
            }
        }
    }

    private void go2PlayVideo2(String localFilePath, int encrypt) {
        if (encrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(localFilePath));
                File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
                if (file.exists()) {
                    if (fileun.exists()) {
                    } else {
                        EncryptUtil.localEncryptFile(localFilePath, fileun.getAbsolutePath(), false,
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    }

                                    @Override
                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                        showToast("文件解密失败");
                                    }
                                }
                        );
                    }
                } else {

                    EncryptUtil.converEncryptFile(localFilePath, file.getAbsolutePath(),
                            false, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode,
                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    if (fileun.exists()) {
                                    } else {
                                        EncryptUtil.localEncryptFile(resp.m_strData, fileun.getAbsolutePath(), false,
                                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                    @Override
                                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                    }

                                                    @Override
                                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                        showToast("文件解密失败");
                                                    }
                                                }
                                        );
                                    }
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                if(nEncryptIMEnable) {
                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                    return;
                }
            }
        }
    }

    private void loadFile() {
        if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
            openFile2(data.localFilePath, data.bEncrypt, data.fileName);
        } else {
            if ("文件上传失败".equals(data.fileUrl)) {
                return;
            }
            String fileLocal = "";
            try {
                fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
            } catch (Exception e) {

            }
            String finalFileLocal = fileLocal;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ChatContentDownload.downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, finalFileLocal, data.type)) {
                        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                openFile2(finalFileLocal, data.bEncrypt, data.fileName);
                            }
                        }, "loadFile");
                    }
                }
            }).start();

        }
    }

    private void openFile(String localFilePath, int encrypt, String name) {
        if (encrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(localFilePath));
                if (!file.exists()) {
                    EncryptUtil.encryptFile(localFilePath, file.getAbsolutePath(),
                            false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                showToast("文件解密失败");
            }
        }
    }

    private void openFile2(String localFilePath, int encrypt, String name) {
        if (encrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(localFilePath));
                File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
                if (file.exists()) {
                    if (fileun.exists()) {
                    } else {
                        EncryptUtil.localEncryptFile(file.getAbsolutePath(), fileun.getAbsolutePath(), false,
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    }

                                    @Override
                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                        showToast("文件解密失败");
                                    }
                                }
                        );
                    }
                } else {
                    EncryptUtil.converEncryptFile(localFilePath, file.getAbsolutePath(),
                            false, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode,
                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    if (fileun.exists()) {
                                    } else {
                                        EncryptUtil.localEncryptFile(resp.m_strData, fileun.getAbsolutePath(), false,
                                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                    @Override
                                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                    }

                                                    @Override
                                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                        showToast("文件解密失败");
                                                    }
                                                }
                                        );
                                    }
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                showToast("文件解密失败");
            }
        }
    }

    private void loadAudio() {
        String fileLocal = "";
        try {
            fileLocal = fC + data.fileUrl.substring(data.fileUrl.lastIndexOf("/"));
        } catch (Exception e) {

        }
        final File ffLocal = new File(fileLocal);
        data.localFilePath = ffLocal.getAbsolutePath();
        if (null != data && !TextUtils.isEmpty(data.localFilePath) && new File(data.localFilePath).exists()) {
            File localFile = new File(data.localFilePath);
            unEncryptVoice2(localFile.getPath());
        } else {
            String finalFileLocal = fileLocal;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (ChatContentDownload.downloadFileByUrl(AppDatas.Constants().getFileServerURL() + data.fileUrl, finalFileLocal, data.type)) {
                        new RxUtils().doDelay(100, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                unEncryptVoice2(finalFileLocal);
                            }
                        }, "loadAudio");
                    }
                }
            }).start();

        }
    }

    private void unEncryptVoice(final String path) {
        if (data.bEncrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(path));
                if (!file.exists()) {
                    EncryptUtil.encryptFile(path, EncryptUtil.getNewFile(path),
                            false, isGroup, isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                showToast("文件解密失败");
            }
        }
    }

    private void unEncryptVoice2(final String path) {
        if (data.bEncrypt == 1) {
            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                File file = new File(EncryptUtil.getNewFile(path));
                File fileun = new File(EncryptUtil.getNewFile(file.getAbsolutePath()));
                if (file.exists()) {
                    if (fileun.exists()) {
                    } else {
                        EncryptUtil.localEncryptFile(file.getAbsolutePath(), fileun.getAbsolutePath(), false,
                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    }

                                    @Override
                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                        showToast("文件解密失败");
                                    }
                                }
                        );
                    }
                } else {
                    EncryptUtil.converEncryptFile(path, file.getAbsolutePath(), false,
                            isGroup ? strGroupID : "", isGroup ? strGroupDomain : "",
                            isGroup ? "" : strUserID, isGroup ? "" : strUserDomainCode,
                            new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    if (fileun.exists()) {
                                    } else {
                                        EncryptUtil.localEncryptFile(resp.m_strData, fileun.getAbsolutePath(), false,
                                                new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                                    @Override
                                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                                    }

                                                    @Override
                                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                                        showToast("文件解密失败");
                                                    }
                                                }
                                        );
                                    }
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    showToast("文件解密失败");
                                }
                            }
                    );
                }
            } else {
                showToast("文件解密失败");
            }
        }
    }

    void initUserEncrypt() {
        ModelApis.Contacts().requestqueryGroupChatInfo(groupInfo.strGroupDomainCode, groupInfo.strGroupID,
                new ModelCallback<ContactsGroupUserListBean>() {
                    @Override
                    public void onSuccess(final ContactsGroupUserListBean contactsBean) {
                        sendUserBeans.clear();
                        usersNew.clear();
                        if (contactsBean != null && contactsBean.lstGroupUser != null) {
                            for (ContactsGroupUserListBean.LstGroupUser temp : contactsBean.lstGroupUser) {
                                if (!AppAuth.get().getUserID().equals(temp.strUserID)) {
                                    SendUserBean sendUserBean = new SendUserBean();
                                    sendUserBean.strUserID = temp.strUserID;
                                    sendUserBean.strUserDomainCode = temp.strUserDomainCode;
                                    sendUserBean.strUserName = temp.strUserName;
                                    sendUserBeans.add(sendUserBean);

                                    SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                                    info.strUserDomainCode = temp.strUserDomainCode;
                                    info.strUserID = temp.strUserID;
                                    usersNew.add(info);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        showToast("onFailure");
                    }
                });
    }

}
