package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.google.gson.Gson;
import com.huaiye.cmf.JniIntf;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.SDKInnerMessageCode;
import com.huaiye.sdk.sdpmsgs.social.CSendMsgToMuliteUserRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.huaiye.sdk.sdpmsgs.talk.CStartTalkbackReq;
import com.lcw.library.imagepicker.ImagePicker;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.helper.ChatLocalPathHelper;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.ChatUtil;
import huaiye.com.vim.common.utils.SoftKeyboardUtil;
import huaiye.com.vim.common.views.ButtonFocusChangeGroupView;
import huaiye.com.vim.common.views.RecordButton;
import huaiye.com.vim.common.views.pickers.adapter.GlideLoader;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.constants.AppFrequentlyConstants;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.dao.msgs.ChatSingleMsgBean;
import huaiye.com.vim.dao.msgs.ContentBean;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.map.baidu.LocationStrategy;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.models.meet.bean.ChatMoreFunctionBean;
import huaiye.com.vim.ui.chat.dialog.ChatSendLocationDialog;
import huaiye.com.vim.ui.contacts.UserDetailActivity;
import huaiye.com.vim.ui.fenxiang.SharePopupLeaveWindow;
import huaiye.com.vim.ui.meet.adapter.ChatContentAdapter;
import huaiye.com.vim.ui.meet.adapter.ChatMoreFunctionAdapter;
import huaiye.com.vim.ui.meet.presenter.ChatPresent;
import huaiye.com.vim.ui.sendBaiduLocation.function.activity.MapActivity;
import huaiye.com.vim.ui.talk.TalkActivity;
import huaiye.com.vim.ui.talk.TalkVoiceActivity;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.REQUEST_CODE_CHOOSE_FILE;
import static huaiye.com.vim.common.AppUtils.REQUEST_CODE_VIDEO_RECORD;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * Created by LENOVO on 2019/4/1.
 */
@BindLayout(R.layout.activity_chat_content)
public class ChatSingleActivity extends AppBaseActivity implements ChatMoreFunctionAdapter.ChatMoreFunctionClickListener, RecordButton.OnFinishedRecordListener {

    private final static int PAGE_SIZE = 15;

    @BindView(R.id.ll_root)
    View ll_root;
    @BindView(R.id.chat_recycler)
    RecyclerView chat_recycler;
    @BindView(R.id.chat_edit)
    EditText chat_edit;
    @BindView(R.id.chat_send)
    TextView chat_send;
    @BindView(R.id.chat_recycler_more_function)
    RecyclerView chat_recycler_more_function;
    @BindView(R.id.chat_user_input_style)
    LinearLayout chat_user_input_style;
    @BindView(R.id.chat_user_voice_style)
    LinearLayout chat_user_voice_style;
    @BindView(R.id.chat_more_function)
    LinearLayout chat_more_function;
    @BindView(R.id.chat_voice_record_over)
    ButtonFocusChangeGroupView chat_voice_record_over;
    @BindView(R.id.chat_voice)
    ImageView chat_voice;
    @BindView(R.id.chat_user_input)
    ImageView chat_user_input;
    @BindView(R.id.chat_more)
    ImageView chat_more;
    @BindView(R.id.chat_voice_btn)
    RecordButton chatVoiceBtn;
    @BindView(R.id.chat_txt_lin)
    LinearLayout chatTxtLin;
    @BindView(R.id.chat_txt_more)
    ImageView chatTxtMore;
    @BindView(R.id.refresh_view)
    SwipeRefreshLayout refresh_view;
    @BindView(R.id.chat_title_bar_title1)
    TextView chatTitleBarTitle;
    @BindView(R.id.chat_title_bar_video_chat_btn)
    ImageView chatTitleBarVideoChatBtn;
    @BindView(R.id.chat_title_bar_voice_chat_btn)
    ImageView chatTitleBarVoiceChatBtn;
    @BindView(R.id.chat_title_bar_detail_btn)
    ImageView chatTitleBarDetailBtn;
    @BindView(R.id.chat_title_bar)
    LinearLayout chatTitleBar;

    @BindExtra
    String from;
    @BindExtra
    User nUser;
    @BindExtra
    ArrayList<SendUserBean> sessionUserList;
    private String mMeetID;
    private String mMeetName;
    private String mMeetDomain;
    private String mUserDomainCode;
    private String mOtherUserName;
    private String mOtherUserId;
    private String mSessionID;
    private String mSessionName;
    private ChatContentAdapter mChatContentAdapter;
    private ChatMoreFunctionAdapter mChatMoreFunctionAdapter;

    ChatMsgViewModel mChatMsgViewM;

    boolean isNeedScroll2Buttom = true;

    private ChatSendLocationDialog mChatSendLocationDialog;
    private List<ChatSingleMsgBean> mChatSingleMsgBeans = new ArrayList<>();
    private List<ChatSingleMsgBean> allMsg = new ArrayList<>();
    private boolean isLoadingData = false;

    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();
    ArrayList<ChatSingleMsgBean> data = new ArrayList<>();
    ArrayList<Long> sessionId = new ArrayList<>();
    ArrayList<String> msgChatId = new ArrayList<>();

    List<String> imagePaths;
    Map<String, String> mapImg = new HashMap<>();
    Map<String, String> mapLocal = new HashMap<>();
    int imageSize = 1;
    int indexCount = 1;

    ChatPresent chatPresent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        if (!SP.getBoolean(AppUtils.SP_SETTING_VOICE, false)) {//使用公放播放
            startSpeakerLound();
        } else {
            if (!AppUtils.isPad(this)) {
                startSpeakerSmall();
            }
        }

    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {
        chatPresent = new ChatPresent();

        mMeetID = getIntent().getStringExtra("mMeetID");
        mMeetName = getIntent().getStringExtra("mMeetName");
        mMeetDomain = getIntent().getStringExtra("mMeetDomain");
        mOtherUserName = getIntent().getStringExtra("mOtherUserName");
        mOtherUserId = getIntent().getStringExtra("mOtherUserId");
        mUserDomainCode = getIntent().getStringExtra("mOtherUserDomainCode");
        initNavigateView(mOtherUserName);

        initView();
//        setMsgMonitor();
        loadFirstPage();
        updateAllRead();

        SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
        info.strUserDomainCode = nUser.strDomainCode;
        info.strUserID = nUser.strUserID;
        users.add(info);

        if (!TextUtils.isEmpty(from)) {
            SharePopupLeaveWindow sharePopupLeaveWindow = new SharePopupLeaveWindow(this);
            sharePopupLeaveWindow.showAtLocation(ll_root, Gravity.CENTER, 0, 0);
        }
    }

    private void loadFirstPage() {
        refresh_view.setRefreshing(true);
        mChatContentAdapter.resetLastDealposition();//重置上一次点击的录音位置放置index异常
        loadPageData(0, PAGE_SIZE);
        refresh_view.setRefreshing(false);
    }

    private void initNavigateView(String mOtherUserName) {
        /*getNavigate().setTitlText(mOtherUserName)
                .setRight3Icon(R.drawable.selector_navi_right_imgbtn)
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).setRight3ClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatSingleActivity.this, UserDetailActivity.class);
                intent.putParcelableArrayListExtra("mUserList", getUserList());
                intent.putExtra("isGroupChat", false);
                intent.putExtra("sessionID", getSessionId());
                ChatSingleActivity.this.startActivity(intent);
            }
        });*/
        getNavigate().setVisibility(View.GONE);
        chatTitleBarTitle.setText(mOtherUserName);
    }

    private ArrayList<User> getUserList() {
        ArrayList<User> userList = new ArrayList<>();
        userList.add(nUser);
        return userList;
    }

    private void initView() {
        mChatContentAdapter = new ChatContentAdapter(this, false, null, null, sessionUserList);
        mChatContentAdapter.setUserInfo(nUser.strUserID, nUser.strDomainCode);
        chat_recycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    chat_recycler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chat_recycler.scrollToPosition(mChatContentAdapter.getItemCount() - 1);

                        }
                    }, 100);

                }
            }
        });

        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoadingData) {
                    isLoadingData = true;

                    //到顶
                    new RxUtils<Integer>()
                            .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<Integer>() {
                                @Override
                                public Integer doOnThread() {
                                    int scroll2position = allMsg.size();
                                    mChatSingleMsgBeans.clear();
                                    List<ChatSingleMsgBean> nChatSingleMsgBeans = AppDatas.MsgDB()
                                            .chatSingleMsgDao()
                                            .queryPagingItemWithoutLive(mOtherUserId, AppAuth.get().getUserID() + "", allMsg.size(), PAGE_SIZE);
                                    if (null != nChatSingleMsgBeans && nChatSingleMsgBeans.size() > 0) {
                                        int i = 0;
                                        for (ChatSingleMsgBean temp : nChatSingleMsgBeans) {
                                            if (!msgChatId.contains(temp.msgID)) {
                                                if (TextUtils.isEmpty(temp.headPic)) {
                                                    temp.headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(temp.fromUserId, temp.fromUserDomain);
                                                }
                                                mChatSingleMsgBeans.add(i++, temp);
                                            }
                                        }
                                    }
                                    return scroll2position;
                                }

                                @Override
                                public void doOnMain(Integer scroll2position) {
                                    refresh_view.setRefreshing(false);
                                    isLoadingData = false;

                                    if (null != mChatContentAdapter) {
                                        if (mChatSingleMsgBeans.isEmpty()) {
                                            isLoadingData = false;
                                            return;
                                        }
                                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                            int i = 0;
                                            for (ChatSingleMsgBean temp : mChatSingleMsgBeans) {
                                                unEncryptSingle(temp, true, scroll2position, i++);
                                            }
                                        } else {
                                            if(nEncryptIMEnable) {
                                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                                finish();
                                                return;
                                            }
                                            allMsg.addAll(0, mChatSingleMsgBeans);
                                            showMsgRel(null, scroll2position, -1);
                                        }
                                    }
                                }
                            });
                }
            }
        });

        LinearLayoutManager nLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        nLinearLayoutManager.setStackFromEnd(true);
        chat_recycler.setLayoutManager(nLinearLayoutManager);
        chat_recycler.setAdapter(mChatContentAdapter);
//        mChatContentAdapter.setDatas(data);
        mChatContentAdapter.setDatas(allMsg);
        mChatMoreFunctionAdapter = new ChatMoreFunctionAdapter(this, false, getSessionId());
        mChatMoreFunctionAdapter.setOnItemClickListener(this);
        chat_recycler_more_function.setLayoutManager(new GridLayoutManager(this, 3));
        chat_recycler_more_function.setAdapter(mChatMoreFunctionAdapter);
        chatVoiceBtn.setOnFinishedRecordListener(this);
        chat_voice_record_over.setOnButtonFocusChangeListener(new ButtonFocusChangeGroupView.ButtonFocusChangeListener() {
            @Override
            public void onFocusLeft() {
                chat_voice_record_over.cancelRecord();
            }

            @Override
            public void onFocusRight() {
                showToast("右面按钮被点击了");

            }

            @Override
            public void onFocusSend(String voiceRecordPath) {
                onFinishedRecord(voiceRecordPath, 0);
            }
        });
        addTextChangeLis();

    }

    private void addTextChangeLis() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != s && s.toString().length() > 0) {
                    chatTxtMore.setVisibility(View.GONE);
                    chatTxtLin.setVisibility(View.VISIBLE);
                } else {
                    chatTxtMore.setVisibility(View.VISIBLE);
                    chatTxtLin.setVisibility(View.GONE);
                }
            }
        };
        chat_edit.addTextChangedListener(watcher);
        chat_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_recycler.scrollToPosition(mChatContentAdapter.getItemCount());
                if (null != chat_more_function && chat_more_function.getVisibility() == View.VISIBLE) {
                    chat_more_function.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadPageData(final int index, final int limit) {
        isLoadingData = true;
        new RxUtils<List<ChatSingleMsgBean>>()
                .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<List<ChatSingleMsgBean>>() {
                    @Override
                    public List<ChatSingleMsgBean> doOnThread() {
                        List<ChatSingleMsgBean> mLocalBeans = new ArrayList<>();
                        List<ChatSingleMsgBean> nChatSingleMsgBeans = AppDatas.MsgDB()
                                .chatSingleMsgDao()
                                .queryPagingItemWithoutLive(mOtherUserId, AppAuth.get().getUserID() + "", index, limit);
                        if (null != nChatSingleMsgBeans && nChatSingleMsgBeans.size() > 0) {
                            for (ChatSingleMsgBean temp : nChatSingleMsgBeans) {
                                if (!msgChatId.contains(temp.msgID)) {
                                    if (TextUtils.isEmpty(temp.headPic)) {
                                        temp.headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(temp.fromUserId, temp.fromUserDomain);
                                    }
                                    mLocalBeans.add(temp);
                                }
                            }
                        } else {
                            isLoadingData = false;
                        }
                        return mLocalBeans;
                    }

                    @Override
                    public void doOnMain(List<ChatSingleMsgBean> dataAll) {
                        if (dataAll.isEmpty()) {
                            allMsg.clear();
                            showMsgRel(null, -1, -1);
                            return;
                        }
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            for (ChatSingleMsgBean temp : dataAll) {
                                unEncryptSingle(temp, false, -1, -1);
                            }
                        } else {
                            if(nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            allMsg.clear();
                            allMsg.addAll(dataAll);
                            showMsgRel(null, -1, -1);
                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (null != chat_more_function && chat_more_function.getVisibility() == View.VISIBLE) {
                chat_more_function.setVisibility(View.GONE);
            } else if (null != chat_voice_record_over && chat_voice_record_over.getVisibility() == View.VISIBLE) {
                chat_voice_record_over.setVisibility(View.GONE);
            } else {
                return super.onKeyDown(keyCode, event);
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void updateAllRead() {
        AppDatas.MsgDB()
                .chatSingleMsgDao()
                .updateAllRead(mOtherUserId, AppAuth.get().getUserID() + "");
    }

    /**
     * 进入和退出的时候,把群聊消息标记为已读
     * 因为聊天中收到的消息需要标记为已读
     */
    @Override
    protected void onPause() {
        super.onPause();
        updateAllRead();
    }

    @OnClick(R.id.chat_send)
    void sendMsg() {
        String msgText = chat_edit.getText().toString();
        chat_edit.setText("");
        if (TextUtils.isEmpty(msgText)) {
            return;
        }
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            encrypt(AppUtils.MESSAGE_TYPE_TEXT, msgText, false, false, 0, -1, "", false);
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            String msgContent = ChatUtil.getChatContentJson(this, msgText, "", "", 0, 0,
                    SP.getBoolean(getSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                    msgText.length(), 0, 0, 0, "");
            sendWetherEncrypt(AppUtils.MESSAGE_TYPE_TEXT, msgContent, false);
        }
    }

    void encrypt(int msgType, String str, boolean isFile, boolean isVoice, int recordTime, long size, String fileName, boolean isImg) {
        if (str == null) {
            str = "";
        }
        final String[] msgContent = new String[1];
        int longTime = isVoice ? recordTime : str.length();
        if (isImg) {
            longTime = 10;
        }
        if (msgType == AppUtils.MESSAGE_TYPE_ADDRESS) {
            msgContent[0] = str;
        } else {
            msgContent[0] = ChatUtil.getChatContentJson(ChatSingleActivity.this, isFile ? "" : str, "",
                    isFile ? str : "", recordTime, size,
                    SP.getBoolean(getSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                    longTime,
                    0, 0, 0, fileName);
        }

        String finalStr = str;
        EncryptUtil.encryptTxt(str, true, false, "", "",
                nUser.strUserID, nUser.strDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                        String msgText = sessionRsp.m_lstData.get(0).strData;
                        int longTime = isVoice ? recordTime : finalStr.length();
                        if (isImg) {
                            longTime = 10;
                        }
                        if (msgType == AppUtils.MESSAGE_TYPE_ADDRESS) {
                            msgContent[0] = msgText;
                        } else {
                            msgContent[0] = ChatUtil.getChatContentJson(ChatSingleActivity.this,
                                    isFile ? "" : msgText, "",
                                    isFile ? msgText : "",
                                    recordTime,
                                    size,
                                    SP.getBoolean(getSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                                    longTime,
                                    0,
                                    0,
                                    0, fileName);
                        }
                        sendWetherEncrypt(msgType, msgContent[0], true);
                    }

                    @Override
                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                        showToast("对方未开启加密,无法发送");
                    }
                });
    }

    private void sendWetherEncrypt(int msgType, String msgContent, boolean isEncrypt) {
        ChatMessageBean bean = new ChatMessageBean();
        bean.content = msgContent;
        bean.type = msgType;
        bean.sessionID = getSessionId();
        bean.sessionName = getSessionName();
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 0;
        bean.bEncrypt = isEncrypt ? 1 : 0;
        bean.groupDomainCode = nUser.strUserDomainCode;
//        bean.groupDomainCode = mMeetDomain;
        bean.groupID = nUser.strUserID;
//        bean.groupID = mMeetID;
        bean.time = System.currentTimeMillis() / 1000;

        SendUserBean mySelf = new SendUserBean(AppAuth.get().getUserID() + "", AppAuth.get().getDomainCode(), AppAuth.get().getUserName());
        SendUserBean otherUser = new SendUserBean(mOtherUserId, TextUtils.isEmpty(nUser.strUserDomainCode) ? nUser.strDomainCode : nUser.strUserDomainCode, mOtherUserName);

        bean.sessionUserList = new ArrayList<>();
        bean.sessionUserList.add(mySelf);
        bean.sessionUserList.add(otherUser);

        ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
//        sessionUserList.add(new SendUserBean(mySelf.strUserID, mySelf.strDomainCode, mySelf.strUserName));
        sessionUserList.add(new SendUserBean(otherUser.strUserID, otherUser.strUserDomainCode, otherUser.strUserName));
        Gson gson = new Gson();
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(true)
                        .setMessage(gson.toJson(bean))
                        .setUser(sessionUserList), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {

                        if (bean.bEncrypt == 1) {
                            ContentBean content = huaiye.com.vim.common.utils.ChatUtil.analysisChatContentJson(bean.content);

                            String unEncryptStr;
                            if (bean.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                                unEncryptStr = content.msgTxt;
                            } else {
                                unEncryptStr = TextUtils.isEmpty(content.fileUrl) ? content.msgTxt : content.fileUrl;
                            }

                            EncryptUtil.converEncryptText(unEncryptStr, false,
                                    "", "",
                                    otherUser.strUserID, otherUser.strUserDomainCode,
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
                                            dealSaveMessageAndLoad(bean, otherUser);
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                            dealSaveMessageAndLoad(bean, otherUser);
                                        }
                                    });
                            return;
                        } else {
                            dealSaveMessageAndLoad(bean, otherUser);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        if (null != errorInfo && SDKInnerMessageCode.NOT_LOGIN == errorInfo.getCode()) {
                            needLoad(AppUtils.getString(R.string.string_name_login_error));
                        }
                        showToast("发送失败" + errorInfo.getMessage());
                    }
                }
        );
        chat_edit.setText("");
    }

    private void dealSaveMessageAndLoad(ChatMessageBean bean, SendUserBean otherUser) {
        ChatSingleMsgBean singleMsgBean = ChatSingleMsgBean.from(bean, otherUser);
        if (singleMsgBean.bFire == 1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_has_send));
                }
            });
        }
        singleMsgBean.read = 1;
        AppDatas.MsgDB()
                .chatSingleMsgDao()
                .insertAll(singleMsgBean);
        VimMessageBean vimMessageBean = VimMessageBean.from(bean);
        vimMessageBean.sessionID = singleMsgBean.sessionID;
        huaiye.com.vim.dao.msgs.ChatUtil.get().saveChangeMsg(vimMessageBean, true);

        loadMore();
        Logger.debug("singleMsg 发送成功");
    }

    private String getSessionId() {

        if (null == nUser) {
            return null;
        }
        if (TextUtils.isEmpty(mSessionID)) {
            return nUser.strDomainCode + nUser.strUserID;
        }
        return mSessionID;
    }

    private String getSessionName() {
        if (TextUtils.isEmpty(mSessionName)) {
            return mOtherUserName;
        }
        return mSessionName;
    }

    @OnClick(R.id.chat_voice)
    void showVoiceView() {
        SoftKeyboardUtil.hideSoftKeyboard(this);
        chat_recycler.scrollToPosition(mChatContentAdapter.getItemCount());

        chat_user_input_style.setVisibility(View.GONE);
        chat_user_voice_style.setVisibility(View.VISIBLE);
        chat_more_function.setVisibility(View.GONE);
        chat_voice_record_over.setVisibility(View.GONE);
    }

    @OnClick(R.id.chat_user_input)
    void showInputView() {
        chat_user_input_style.setVisibility(View.VISIBLE);
        chat_user_voice_style.setVisibility(View.GONE);
        chat_more_function.setVisibility(View.GONE);
        chat_voice_record_over.setVisibility(View.GONE);

    }

    @OnClick({R.id.chat_more, R.id.chat_txt_more})
    void showMoreView() {
        SoftKeyboardUtil.hideSoftKeyboard(this);
        chat_recycler.scrollToPosition(mChatContentAdapter.getItemCount());

        chat_voice_record_over.setVisibility(View.GONE);
        if (chat_more_function.getVisibility() == View.VISIBLE) {
            chat_more_function.setVisibility(View.GONE);
        } else {
            chat_more_function.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onItemClick(ChatMoreFunctionBean chatMoreFunctionBean) {
        dealClickEvent(chatMoreFunctionBean);
    }

    private void dealClickEvent(ChatMoreFunctionBean msg) {
        switch (msg.functionType) {
            case 0:
                onImageClicked();
                break;
            case 1:
                onVideoRecordClicked();
                break;
            case 2:
                onSendLocation();
                break;
            case 3:
                onVoiceClicked();
                break;
            case 4:
                onVideoClicked();
                break;
            case 5:
                onUploadFileClicked();
                break;
            default:
        }
    }

    private void onSendLocationClicked() {
        if (null == mChatSendLocationDialog) {
            mChatSendLocationDialog = new ChatSendLocationDialog(this);
            mChatSendLocationDialog.setOnFunctionClickedListener(new ChatSendLocationDialog.IFunctionClickedListener() {

                @Override
                public void onClickedSendCustomLocation() {
                    showToast("onClickedSendCustomLocation");
                }

                @Override
                public void onClickedAlwaysSendCustomLocation() {
                    showToast("onClickedAlwaysSendCustomLocation");

                }
            });
        }

        if (!mChatSendLocationDialog.isShowing()) {
            mChatSendLocationDialog.show();
        }
    }

    private void onUploadFileClicked() {
        boolean isYueHouJifeng = SP.getBoolean(getSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false);
        if (!isYueHouJifeng) {
            Intent intent = new Intent(this, ChooseFilesActivity.class);
            intent.putExtra("nUser", nUser);
            intent.putExtra("isGroup", false);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
        } else {
            showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_un_support));
        }

    }

    private void onVideoRecordClicked() {
        Intent intent = new Intent(this, VideoRecordUploadActivity.class);
        intent.putExtra("nUser", nUser);
        intent.putExtra("isGroup", false);
        startActivityForResult(intent, REQUEST_CODE_VIDEO_RECORD);
    }

    private void onSendLocation() {
        boolean isYueHouJifeng = SP.getBoolean(getSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false);
        if (!isYueHouJifeng) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("nUser", nUser);
            intent.putExtra("isGroup", false);
            startActivityForResult(intent, AppUtils.REQUEST_CODE_SEND_LOCATION);
        } else {
            showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_un_support));
        }

    }

    //发送图片
    public void onImageClicked() {
        ImagePicker.getInstance()
                .setTitle(AppUtils.getString(R.string.select_image_for_send))//设置标题
                .showCamera(true)//设置是否显示拍照按钮
                .showImage(true)//设置是否展示图片
                .showVideo(false)//设置是否展示视频
                .setSingleType(true)//设置图片视频不能同时选择
                .setMaxCount(9)//设置最大选择图片数目(默认为1，单选)
//                .setImagePaths(mImageList)//保存上一次选择图片的状态，如果不需要可以忽略
                .setImageLoader(new GlideLoader())//设置自定义图片加载器
                .start(ChatSingleActivity.this, AppUtils.REQUEST_CODE_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
    }

    /* 音频通话 */
    @OnClick(R.id.chat_title_bar_voice_chat_btn)
    public void onVoiceClicked() {
        if (nUser == null) {
            showToast("获取人员详情失败，无法对讲");
            return;
        }
        if (nUser.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
            showToast("不能与自己通话");
            return;
        }
        CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
        toUser.strToUserDomainCode = nUser.strDomainCode;
        toUser.strToUserID = nUser.strUserID;
        toUser.strToUserName = nUser.strUserName;

        /* 将用户加入常用联系人 */
        AppFrequentlyConstants.get().AddContacts(new ArrayList<User>() {{
            add(nUser);
        }});

        Intent intent = new Intent(this, TalkVoiceActivity.class);
        intent.putExtra("isTalkStarter", true);
        intent.putExtra("toUser", toUser);
        startActivity(intent);
    }

    /* 视频通话 */
    @OnClick(R.id.chat_title_bar_video_chat_btn)
    public void onVideoClicked() {
        if (nUser == null) {
            showToast("获取人员详情失败，无法通话");
            return;
        }
        if (nUser.strUserID.equals(String.valueOf(AppDatas.Auth().getUserID()))) {
            showToast("不能与自己通话");
            return;
        }
        CStartTalkbackReq.ToUser toUser = new CStartTalkbackReq.ToUser();
        toUser.strToUserDomainCode = nUser.strDomainCode;
        toUser.strToUserID = nUser.strUserID;
        toUser.strToUserName = nUser.strUserName;

        /* 将用户加入常用联系人 */
        AppFrequentlyConstants.get().AddContacts(new ArrayList<User>() {{
            add(nUser);
        }});

        Intent intent = new Intent(this, TalkActivity.class);
        intent.putExtra("isTalkStarter", true);
        intent.putExtra("toUser", toUser);
        startActivityForResult(intent, 1004);
    }

    @OnClick(R.id.chat_title_bar_detail_btn)
    public void onDetailClicked() {
        Intent intent = new Intent(ChatSingleActivity.this, UserDetailActivity.class);
        intent.putParcelableArrayListExtra("mUserList", getUserList());
        intent.putExtra("isGroupChat", false);
        intent.putExtra("sessionID", getSessionId());
        ChatSingleActivity.this.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Upload upload;
        if (null != data) {
            upload = (Upload) data.getSerializableExtra("updata");
            if (null != upload && requestCode == REQUEST_CODE_CHOOSE_FILE) {
                long fileSize = data.getLongExtra("fileSize", 0);
                String fileName = data.getStringExtra("fileName");
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    encrypt(AppUtils.MESSAGE_TYPE_FILE, upload.file1_name, true, false, 0, fileSize, fileName, false);
                } else {
                    if(nEncryptIMEnable) {
                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                        finish();
                        return;
                    }
                    String msgContent = ChatUtil.getChatContentJson(this, "", "", upload.file1_name,
                            0, fileSize, false, 0, 0, 0,
                            0, fileName);
                    sendWetherEncrypt(AppUtils.MESSAGE_TYPE_FILE, msgContent, false);
                }
            } else if (null != upload && requestCode == REQUEST_CODE_VIDEO_RECORD) {
                long fileSize = data.getLongExtra("fileSize", 0);
                int recordTime = data.getIntExtra("recordTime", 0);
                String fileName = data.getStringExtra("fileName");
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    encrypt(AppUtils.MESSAGE_TYPE_VIDEO_FILE, upload.file1_name, true, true, recordTime, fileSize, fileName, false);
                } else {
                    if(nEncryptIMEnable) {
                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                        finish();
                        return;
                    }
                    String msgContent = ChatUtil.getChatContentJson(this, "", "", upload.file1_name,
                            0, fileSize,
                            SP.getBoolean(getSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                            recordTime, 0, 0, 0, fileName);
                    sendWetherEncrypt(AppUtils.MESSAGE_TYPE_VIDEO_FILE, msgContent, false);
                }
            } else if (resultCode == RESULT_OK && requestCode == AppUtils.REQUEST_CODE_SELECT_IMAGES_CODE) {
                imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                mapImg.clear();
                imageSize = imagePaths.size();
                for (String image : imagePaths) {
                    final File file = new File(image);
                    if (file.length() > 1028 * 1028 * 50) {
                        showToast("文档大于50M");
                        mapImg.put(image, "文档大于50M");
                        mapLocal.put(image, "文档大于50M");
                        if (mapImg.size() == imageSize) {
                            sendImageFile();
                        }
                        return;
                    }
                    if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                        EncryptUtil.encryptFile(file.getPath(), EncryptUtil.getNewFile(file.getPath()),
                                true, false, "", "",
                                nUser.strUserID, nUser.strDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                        upFile(file, new File(resp.m_strData));
                                    }

                                    @Override
                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                        mapImg.put(image, "对方未开启加密,无法发送");
                                        mapLocal.put(image, "对方未开启加密,无法发送");
                                        showToast("对方未开启加密,无法发送");
                                        if (mapImg.size() == imageSize) {
                                            sendImageFile();
                                        }
                                    }
                                }
                        );
                    } else {
                        if(nEncryptIMEnable) {
                            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                            finish();
                            return;
                        }
                        upFile(file, file);
                    }
                }
            }

        }
    }

    private void sendImageFile() {
        for (String temp : imagePaths) {
            try {
                String httpFile = mapImg.get(temp);
                File file = new File(mapLocal.get(temp));
                if (httpFile == null) {
                    httpFile = "";
                }
                if (file == null) {
                    file = new File("");
                }
                String finalHttpFile = httpFile;
                File finalFile = file;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChatLocalPathHelper.getInstance().cacheChatLoaclPath(finalHttpFile, finalFile.getPath());
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            encrypt(AppUtils.MESSAGE_TYPE_IMG, finalHttpFile, true, false,
                                    0, finalFile.length(),
                                    temp.substring(temp.lastIndexOf("/") + 1), true);
                        } else {
                            if(nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            String msgContent = ChatUtil.getChatContentJson(ChatSingleActivity.this, "", "",
                                    finalHttpFile, 0, finalFile.length(),
                                    SP.getBoolean(getSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                                    10, 0, 0, 0,
                                    temp.substring(temp.lastIndexOf("/") + 1));
                            sendWetherEncrypt(AppUtils.MESSAGE_TYPE_IMG, msgContent, false);
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println("VIMApp, eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee " + e);
            }
        }
    }

    private void upFile(File fileOld, File file) {
        ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
            @Override
            public void onSuccess(final Upload upload) {
                mapImg.put(fileOld.getAbsolutePath(), upload.file1_name);
                mapLocal.put(fileOld.getAbsolutePath(), file.getAbsolutePath());
                if (mapImg.size() == imageSize) {
                    sendImageFile();
                }
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                mapImg.put(fileOld.getAbsolutePath(), "文件上传失败");
                mapLocal.put(fileOld.getAbsolutePath(), "文件上传失败");
                if (mapImg.size() == imageSize) {
                    sendImageFile();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("文件上传失败");
                    }
                });
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {

            }
        }, file, AppDatas.Constants().getFileUploadUri());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        switch (messageEvent.what) {
            case AppUtils.EVENT_CREATE_GROUP_SUCCESS:
                finish();
                break;
            case AppUtils.EVENT_COMING_NEW_MESSAGE:
                if (getSessionId().equals(messageEvent.obj2)) {
                    loadMore();
                }
//                comingNewMessage();
                break;
            case AppUtils.EVENT_CLEAR_MESSAGE_SUCCESS:
                mChatContentAdapter.resetLastDealposition();//重置上一次点击的录音位置放置index异常
                loadPageData(0, PAGE_SIZE);
                break;
            case AppUtils.EVENT_VOICE_CANCLE:
                String msgContentVoiceCancle = ChatUtil.getChatContentJson(ChatSingleActivity.this,
                        messageEvent.msgContent, "", "", 0, 0, false,
                        0, 0, 0, 0, "");
//                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
//                    encrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, messageEvent.msgContent, false, true, 0, -1);
//                } else {
                sendWetherEncrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, msgContentVoiceCancle, false);
//                }
                break;
            case AppUtils.EVENT_VOICE_REFUSE:
                String msgContentVoiceRefuse = ChatUtil.getChatContentJson(ChatSingleActivity.this,
                        messageEvent.msgContent, "", "", 0, 0,
                        false, 0, 1, 0, 0, "");
//                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
//                    encrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, messageEvent.msgContent, false, true,0, -1);
//                } else {
                sendWetherEncrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, msgContentVoiceRefuse, false);
//                }
                break;
            case AppUtils.EVENT_VOICE_SUCCESS:
                String msgContentVoiceSuccess = ChatUtil.getChatContentJson(ChatSingleActivity.this,
                        messageEvent.msgContent, "", "", 0, 0,
                        false, 0, 2, 0, 0, "");
//                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
//                    encrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, messageEvent.msgContent, false, true, 0, -1, "");
//                } else {
                sendWetherEncrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VOICE, msgContentVoiceSuccess, false);
//                }
                break;
            case AppUtils.EVENT_VIDEO_CANCLE:
                String msgContentVideoCancle = ChatUtil.getChatContentJson(ChatSingleActivity.this,
                        messageEvent.msgContent, "", "", 0, 0,
                        false, 0, 0, 0, 0, "");
//                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
//                    encrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, messageEvent.msgContent, false, true,0, -1);
//                } else {
                sendWetherEncrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, msgContentVideoCancle, false);
//                }
                break;
            case AppUtils.EVENT_VIDEO_REFUSE:
                String msgContentVideoRefuse = ChatUtil.getChatContentJson(ChatSingleActivity.this,
                        messageEvent.msgContent, "", "", 0, 0,
                        false, 0, 1, 0, 0, "");
//                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
//                    encrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, messageEvent.msgContent, false, true,0, -1);
//                } else {
                sendWetherEncrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, msgContentVideoRefuse, false);
//                }
                break;
            case AppUtils.EVENT_VIDEO_SUCCESS:
                String msgContentVideoSuccess = ChatUtil.getChatContentJson(ChatSingleActivity.this,
                        messageEvent.msgContent, "", "", 0, 0,
                        false, 0, 2, 0, 0, "");
//                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
//                    encrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, messageEvent.msgContent, false, true, 0, -1, "");
//                } else {
                sendWetherEncrypt(AppUtils.MESSAGE_TYPE_SINGLE_CHAT_VIDEO, msgContentVideoSuccess, false);
//                }
                break;
            case AppUtils.EVENT_MESSAGE_YUEHOUJIFENG:
                if (null != mChatContentAdapter) {
                    mChatContentAdapter.deleByChatMessageBase((ChatMessageBase) messageEvent.obj1);
                }
                break;
            case AppUtils.EVENT_MESSAGE_UPLOAD_BAIDU_SNAP_PIX:
                PoiInfo nPoiInfo = (PoiInfo) messageEvent.obj1;
                if (null != nPoiInfo) {
                    LatLng latLng = LocationStrategy.convertBaiduToGPS(new LatLng(nPoiInfo.location.latitude, nPoiInfo.location.longitude));

                    String msgContentAddress = ChatUtil.getChatContentJson(ChatSingleActivity.this,
                            nPoiInfo.getAddress(), "", messageEvent.msgContent, 0, 0,
                            false, 0, 0, latLng.latitude, latLng.longitude, "");
                    if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                        encrypt(AppUtils.MESSAGE_TYPE_ADDRESS, msgContentAddress, false, false, 0, -1, "", false);
                    } else {
                        if(nEncryptIMEnable) {
                            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                            finish();
                            return;
                        }
                        sendWetherEncrypt(AppUtils.MESSAGE_TYPE_ADDRESS, msgContentAddress, false);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSpeakerLound();
        if (null != mChatSendLocationDialog && mChatSendLocationDialog.isShowing()) {
            mChatSendLocationDialog.dismiss();
            mChatSendLocationDialog = null;
        }
        mChatContentAdapter.dismissDialog();
        mChatContentAdapter.stopVoice();
        EventBus.getDefault().unregister(this);

        VIMApp.getInstance().removeLinShiFile();
    }

    @Override
    public void onFinishedRecord(String audioPath, final int time) {
        if (TextUtils.isEmpty(audioPath)) {
            showToast("音频录制失败");
            return;
        }
        final File file = new File(audioPath);
        if (!file.exists()) {
            showToast("音频录制失败");
            return;
        }
        if (file.length() <= 0) {
            showToast("音频录制失败");
            return;
        }
        mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();

        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptFile(file.getPath(), EncryptUtil.getNewFile(file.getPath()),
                    true, false, "", "",
                    nUser.strUserID, nUser.strDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            upFileVoice(file, new File(resp.m_strData));
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showToast("对方未开启加密,无法发送");
                            if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                mZeusLoadView.dismiss();
                        }
                    }
            );
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            upFileVoice(file, file);
        }
    }

    private void upFileVoice(File fileOld, File file) {
        ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
            @Override
            public void onSuccess(final Upload upload) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mZeusLoadView != null && mZeusLoadView.isShowing())
                            mZeusLoadView.dismiss();

                        if (TextUtils.isEmpty(upload.file1_name)) {
                            showToast(AppUtils.getString(R.string.file_upload_false));
                            return;
                        }

                        ChatLocalPathHelper.getInstance().cacheChatLoaclPath(upload.file1_name, file.getPath());
                        int recordTime = JniIntf.GetRecordFileDuration(fileOld.getPath());
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            encrypt(AppUtils.MESSAGE_TYPE_AUDIO_FILE, upload.file1_name, true, true, recordTime, file.length(), fileOld.getAbsolutePath().substring(fileOld.getAbsolutePath().lastIndexOf("/") + 1), false);
                        } else {
                            if(nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            String msgContentVideoSuccess = ChatUtil.getChatContentJson(ChatSingleActivity.this, "", "",
                                    upload.file1_name, recordTime,
                                    file.length(),
                                    SP.getBoolean(getSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                                    recordTime, 0, 0, 0, fileOld.getAbsolutePath().substring(fileOld.getAbsolutePath().lastIndexOf("/") + 1));
                            sendWetherEncrypt(AppUtils.MESSAGE_TYPE_AUDIO_FILE, msgContentVideoSuccess, false);
                        }
                    }
                });


            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(AppUtils.getString(R.string.file_upload_false));
                    }
                });
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mZeusLoadView != null && mZeusLoadView.isShowing())
                            mZeusLoadView.dismiss();
                    }
                });

            }
        }, file, AppDatas.Constants().getFileUploadUri());
    }

    @OnClick(R.id.chat_title_bar_title)
    void backPresses() {
        finish();
    }

    private void loadMore() {
        ChatSingleMsgBean temp = AppDatas.MsgDB()
                .chatSingleMsgDao()
                .queryLastItem(mOtherUserId, AppAuth.get().getUserID() + "");
        if (TextUtils.isEmpty(temp.headPic)) {
            temp.headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(temp.fromUserId, temp.fromUserDomain);
        }
        unEncryptSingle(temp, false, -1, -1);
    }

    private void unEncryptSingle(ChatSingleMsgBean temp, boolean isMore, int position, int addIndex) {
        VimMessageListMessages.get().isRead(temp.sessionID);
        if (temp.bEncrypt == 1 && !temp.isUnEncrypt) {

            String unEncryptStr;
            if (temp.type == AppUtils.MESSAGE_TYPE_SHARE) {
                unEncryptStr = temp.msgTxt;
            } else {
                if (temp.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                    unEncryptStr = temp.msgTxt;
                } else {
                    unEncryptStr = TextUtils.isEmpty(temp.fileUrl) ? temp.msgTxt : temp.fileUrl;
                }
            }

            EncryptUtil.localEncryptText(unEncryptStr, false,
                    new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                            if (!msgChatId.contains(temp.msgID)) {
                                msgChatId.add(temp.msgID);
                            }
                            temp.isUnEncrypt = true;
                            temp.mStrEncrypt = unEncryptStr;
                            if (temp.type == AppUtils.MESSAGE_TYPE_SHARE) {
                                ContentBean contentBean = ChatUtil.analysisChatContentJson(sessionRsp.m_lstData.get(0).strData);
                                temp.msgID = contentBean.msgID;
                                temp.msgTxt = contentBean.msgTxt;
                                temp.summary = contentBean.summary;
                                temp.fileUrl = contentBean.fileUrl;
                                temp.fileName = contentBean.fileName;
                                temp.nDuration = contentBean.nDuration;
                                temp.fileSize = contentBean.fileSize;
                                temp.bFire = contentBean.bFire;
                                temp.nCallState = contentBean.nCallState;
                                temp.fireTime = contentBean.fireTime;
                                temp.latitude = contentBean.latitude;
                                temp.longitude = contentBean.longitude;
                            } else {
                                if (temp.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                                    ContentBean contentBean = ChatUtil.analysisChatContentJson(sessionRsp.m_lstData.get(0).strData);
                                    temp.msgID = contentBean.msgID;
                                    temp.msgTxt = contentBean.msgTxt;
                                    temp.fileUrl = contentBean.fileUrl;
                                    temp.fileName = contentBean.fileName;
                                    temp.nDuration = contentBean.nDuration;
                                    temp.fileSize = contentBean.fileSize;
                                    temp.bFire = contentBean.bFire;
                                    temp.nCallState = contentBean.nCallState;
                                    temp.fireTime = contentBean.fireTime;
                                    temp.latitude = contentBean.latitude;
                                    temp.longitude = contentBean.longitude;
                                } else {
                                    if (TextUtils.isEmpty(temp.fileUrl)) {
                                        temp.msgTxt = sessionRsp.m_lstData.get(0).strData;
                                    } else {
                                        temp.fileUrl = sessionRsp.m_lstData.get(0).strData;
                                    }
                                }
                            }

                            showMsgRel(temp, position, addIndex);
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
                            showMsgRel(temp, position, addIndex);
                        }
                    });
        } else {
            showMsgRel(temp, position, addIndex);
        }
    }

    private void showMsgRel(ChatSingleMsgBean temp, int position, int addIndex) {
        try {
            if (temp != null) {
                if (addIndex == -1) {
                    allMsg.add(temp);
                } else {
                    allMsg.add(addIndex, temp);
                }
            }
        } catch (Exception e) {

        }
        Collections.sort(allMsg, new Comparator<ChatSingleMsgBean>() {
            @Override
            public int compare(ChatSingleMsgBean o1, ChatSingleMsgBean o2) {
                if (o1.time - o2.time > 0) {
                    return 1;
                }
                if (o1.time - o2.time < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        mChatContentAdapter.notifyDataSetChanged();
        if (position != -1) {
            chat_recycler.scrollToPosition(PAGE_SIZE);
        } else {
            chat_recycler.scrollToPosition(allMsg.size() - 1);
        }
        isLoadingData = false;
    }

}
