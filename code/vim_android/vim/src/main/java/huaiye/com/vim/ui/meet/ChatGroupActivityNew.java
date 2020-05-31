package huaiye.com.vim.ui.meet;

import android.annotation.SuppressLint;
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
import huaiye.com.vim.common.helper.ChatContactsGroupUserListHelper;
import huaiye.com.vim.common.helper.ChatLocalPathHelper;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.ChatUtil;
import huaiye.com.vim.common.utils.SoftKeyboardUtil;
import huaiye.com.vim.common.views.ButtonFocusChangeGroupView;
import huaiye.com.vim.common.views.RecordButton;
import huaiye.com.vim.common.views.pickers.adapter.GlideLoader;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.ChatGroupMsgBean;
import huaiye.com.vim.dao.msgs.ChatMessageBase;
import huaiye.com.vim.dao.msgs.ChatMessageBean;
import huaiye.com.vim.dao.msgs.ContentBean;
import huaiye.com.vim.dao.msgs.SendMsgUserBean;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.map.baidu.LocationStrategy;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.models.contacts.bean.ContactsGroupUserListBean;
import huaiye.com.vim.models.contacts.bean.CreateGroupContactData;
import huaiye.com.vim.models.meet.bean.ChatMoreFunctionBean;
import huaiye.com.vim.ui.contacts.UserDetailActivity;
import huaiye.com.vim.ui.contacts.sharedata.ChoosedContacts;
import huaiye.com.vim.ui.fenxiang.SharePopupLeaveWindow;
import huaiye.com.vim.ui.meet.adapter.ChatContentAdapter;
import huaiye.com.vim.ui.meet.adapter.ChatMoreFunctionAdapter;
import huaiye.com.vim.ui.sendBaiduLocation.function.activity.MapActivity;
import huaiye.com.vim.ui.talk.TalkVoiceActivity;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static com.huaiye.sdk.HYClient.getContext;
import static huaiye.com.vim.common.AppUtils.REQUEST_CODE_CHOOSE_FILE;
import static huaiye.com.vim.common.AppUtils.REQUEST_CODE_CHOOSE_NOTICE;
import static huaiye.com.vim.common.AppUtils.REQUEST_CODE_VIDEO_RECORD;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * Created by LENOVO on 2019/4/1.
 */
@BindLayout(R.layout.activity_chat_content)
public class ChatGroupActivityNew extends AppBaseActivity implements ChatMoreFunctionAdapter.ChatMoreFunctionClickListener, RecordButton.OnFinishedRecordListener {
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
    CreateGroupContactData mContactsBean;

    ArrayList<SendUserBean> mMessageUsersDate;

    ContactsGroupUserListBean mContactsGroupUserListBean;

    private String sessionName;

    private ChatMsgViewModel chatMsgViewModel;
    private ChatContentAdapter mChatContentAdapter;
    private ChatMoreFunctionAdapter mChatMoreFunctionAdapter;

    boolean isNeedScroll2Buttom = true;

    private List<ChatGroupMsgBean> mChatGroupMsgBeans = new ArrayList<>();
    private List<ChatGroupMsgBean> allMsg = new ArrayList<>();
    private boolean isLoadingData = false;
    private boolean containSelf = false;
    private boolean canSendMsg = true;
    ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();
    ArrayList<ChatGroupMsgBean> data = new ArrayList<>();
    ArrayList<Long> sessionId = new ArrayList<>();
    ArrayList<String> msgChatId = new ArrayList<>();

    Map<String, String> mapImg = new HashMap<>();
    Map<String, String> mapLocal = new HashMap<>();
    int imageSize = 1;
    int lastLength = 0;

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
        initNavigateView(mContactsBean.sessionName);
        initData();
        initView();
//        setMsgMonitor();
        updateAllRead();

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

    private void initData() {
        if (null != mContactsBean) {
            if (null == mMessageUsersDate) {
                if (null != ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(mContactsBean.strGroupID)) {
                    mContactsGroupUserListBean = ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(mContactsBean.strGroupID);
                    sessionName = mContactsGroupUserListBean.strGroupName;
                    initNavigateView(sessionName);
                }
                queryGroupChatInfo(true);
            } else {
                setContactsGroupUserListBean();
            }
        }

    }

    private void queryGroupChatInfo(boolean needRefMessage) {
        ModelApis.Contacts().requestqueryGroupChatInfo(mContactsBean.strGroupDomainCode, mContactsBean.strGroupID, new ModelCallback<ContactsGroupUserListBean>() {
            @Override
            public void onSuccess(final ContactsGroupUserListBean contactsBean) {
                if (contactsBean != null) {
                    ChatContactsGroupUserListHelper.getInstance().cacheContactsGroupDetail(mContactsBean.strGroupID + "", contactsBean);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContactsGroupUserListBean = contactsBean;
//                        refreshHead(mContactsGroupUserListBean);
                        setMessageUsersDate(mContactsGroupUserListBean, needRefMessage);
                        getGroupUserHead(mContactsGroupUserListBean, needRefMessage);
                    }
                });

            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("onFailure");
                    }
                });
            }
        });
    }

    private void getGroupUserHead(ContactsGroupUserListBean contactsGroupUserListBean, boolean needRefMessage) {
        if (null != contactsGroupUserListBean && null != contactsGroupUserListBean.lstGroupUser && contactsGroupUserListBean.lstGroupUser.size() > 0) {
            ModelApis.Contacts().requestGetUserHead(contactsGroupUserListBean.lstGroupUser, new ModelCallback<ContactsBean>() {
                @Override
                public void onSuccess(ContactsBean contactsBean) {
                    if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() > 0) {
                        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                            @Override
                            public Object doOnThread() {
                                for (User user : contactsBean.userList) {
                                    user.strDomainCode = user.strUserDomainCode;
                                }
                                AppDatas.MsgDB().getFriendListDao().insertAll(contactsBean.userList);

                                return "";
                            }

                            @Override
                            public void doOnMain(Object data) {
                                refreshHead(contactsGroupUserListBean, needRefMessage);
                            }
                        });
                    }
                }
            });
        }

    }

    private void refreshHead(ContactsGroupUserListBean contactsBean, boolean needRefMessage) {
        if (null != contactsBean && null != contactsBean.lstGroupUser && contactsBean.lstGroupUser.size() > 0) {
            new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                @Override
                public Object doOnThread() {
                    for (ContactsGroupUserListBean.LstGroupUser nLstGroupUser : contactsBean.lstGroupUser) {
                        nLstGroupUser.strHeadUrl = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(nLstGroupUser.strUserID, nLstGroupUser.strUserDomainCode);
                    }
                    return "";
                }

                @Override
                public void doOnMain(Object data) {
                    ChatContactsGroupUserListHelper.getInstance().cacheContactsGroupDetail(contactsBean.strGroupID + "", contactsBean);
                }
            });
        }
    }

    private void setContactsGroupUserListBean() {
        if (null != mMessageUsersDate) {
            mContactsGroupUserListBean = new ContactsGroupUserListBean();
            mContactsGroupUserListBean.lstGroupUser = new ArrayList<ContactsGroupUserListBean.LstGroupUser>();
            for (SendUserBean nMessageUsers : mMessageUsersDate) {
                ContactsGroupUserListBean.LstGroupUser nLstGroupUser = new ContactsGroupUserListBean.LstGroupUser();
                nLstGroupUser.strUserDomainCode = nMessageUsers.strUserDomainCode;
                nLstGroupUser.strUserID = nMessageUsers.strUserID;
                nLstGroupUser.strUserName = nMessageUsers.strUserName;
                mContactsGroupUserListBean.lstGroupUser.add(nLstGroupUser);
            }

        }
    }

    /**
     * 群信息以及成员信息
     *
     * @param mContactsGroupUserListBean
     */
    private void setMessageUsersDate(ContactsGroupUserListBean mContactsGroupUserListBean, boolean needRefMessage) {
        mMessageUsersDate = new ArrayList<>();
        containSelf = false;
        if (null != mContactsGroupUserListBean) {
            sessionName = mContactsGroupUserListBean.strGroupName;
            if (TextUtils.isEmpty(sessionName)) {
                if (null != mContactsGroupUserListBean && null != mContactsGroupUserListBean.lstGroupUser && mContactsGroupUserListBean.lstGroupUser.size() > 0) {
                    StringBuilder sb = new StringBuilder("");
                    for (ContactsGroupUserListBean.LstGroupUser temp : mContactsGroupUserListBean.lstGroupUser) {
                        sb.append(temp.strUserName + "、");
                    }
                    if (null != sb && sb.indexOf("、") >= 0) {
                        sb.deleteCharAt(sb.lastIndexOf("、"));
                    }
                    sessionName = sb.toString();
                }
            }
            initNavigateView(sessionName);
            if (null != mContactsGroupUserListBean.lstGroupUser && mContactsGroupUserListBean.lstGroupUser.size() > 0) {
                for (ContactsGroupUserListBean.LstGroupUser mLstGroupUser : mContactsGroupUserListBean.lstGroupUser) {
                    if (mLstGroupUser.strUserID.equals(AppDatas.Auth().getUserID())) {
                        containSelf = true;
                    }
                    SendUserBean nMessageUsers = new SendUserBean(mLstGroupUser.strUserID, mLstGroupUser.strUserDomainCode, mLstGroupUser.strUserName);
                    mMessageUsersDate.add(nMessageUsers);
                }
            }
//            if (null != mMessageUsersDate && mMessageUsersDate.size() > 0) {
//                initNavigateView("群聊(" + mMessageUsersDate.size() + ")");
//            }
        }
        mChatContentAdapter.setCustomer(mMessageUsersDate);
        initUserEncrypt();

        if (needRefMessage) {
            loadFirstPage();
        }
    }

    private void initNavigateView(String mOtherUserName) {
        getNavigate().setVisibility(View.GONE);
        chatTitleBarTitle.setText(mOtherUserName);
        chatTitleBarVoiceChatBtn.setVisibility(View.GONE);
    }

    private void initView() {
        mChatContentAdapter = new ChatContentAdapter(this, true, mContactsBean.strGroupID, mContactsBean.strGroupDomainCode, mMessageUsersDate);
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
                    new RxUtils<Integer>()
                            .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<Integer>() {
                                @Override
                                public Integer doOnThread() {
                                    int scroll2position = allMsg.size();
                                    mChatGroupMsgBeans.clear();
                                    List<ChatGroupMsgBean> nChatGroupMsgBean = AppDatas.MsgDB()
                                            .chatGroupMsgDao()
                                            .queryPagingItemWithoutLive(mContactsBean.strGroupID, allMsg.size(), PAGE_SIZE);
                                    if (null != nChatGroupMsgBean && nChatGroupMsgBean.size() > 0) {
                                        int i = 0;
                                        for (ChatGroupMsgBean temp : nChatGroupMsgBean) {
                                            if (!msgChatId.contains(temp.msgID)) {
                                                if (TextUtils.isEmpty(temp.headPic)) {
                                                    temp.headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(temp.fromUserId, temp.fromUserDomain);
                                                }
                                                mChatGroupMsgBeans.add(i++, temp);
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
                                        if (mChatGroupMsgBeans.isEmpty()) {
                                            isLoadingData = false;
                                            return;
                                        }
                                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                            int i = 0;
                                            for (ChatGroupMsgBean temp : mChatGroupMsgBeans) {
                                                unEncryptSingle(temp, true, scroll2position, i++);
                                            }
                                        } else {
                                            if (nEncryptIMEnable) {
                                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                                finish();
                                                return;
                                            }
                                            allMsg.addAll(0, mChatGroupMsgBeans);
                                            showMsgRel(null, scroll2position, -1);
                                        }
                                    }
                                }
                            });
                }
            }
        });

        LinearLayoutManager nLinearLayoutManager = new LinearLayoutManager(this);
        chat_recycler.setLayoutManager(nLinearLayoutManager);

        chat_recycler.setAdapter(mChatContentAdapter);
        mChatContentAdapter.setDatas(allMsg);
        mChatMoreFunctionAdapter = new ChatMoreFunctionAdapter(this, true, getGroupSessionId());
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
                if (!TextUtils.isEmpty(s.toString())) {
                    if (s.toString().length() > lastLength && s.toString().endsWith("@")) {
                        lastLength = s.toString().length();
                        Intent intent = new Intent(getContext(), NoticeChooseActivity.class);
                        intent.putExtra("nMeetType", 1);
                        intent.putExtra("mGroupInfoListBean", mContactsGroupUserListBean);
                        startActivityForResult(intent, REQUEST_CODE_CHOOSE_NOTICE);
                    } else {
                        lastLength = s.toString().length();
                    }
                } else {
                    lastLength = s.toString().length();
                }
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
                if (null != chat_more_function && chat_more_function.getVisibility() == View.VISIBLE) {
                    chat_more_function.setVisibility(View.GONE);
                }
            }
        });
    }

    private void loadPageData(final int index, final int limit) {
        isLoadingData = true;
        new RxUtils<List<ChatGroupMsgBean>>()
                .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<List<ChatGroupMsgBean>>() {
                    @Override
                    public List<ChatGroupMsgBean> doOnThread() {
                        List<ChatGroupMsgBean> mLocalBeans = new ArrayList<>();
                        List<ChatGroupMsgBean> nChatGroupMsgBeans = AppDatas.MsgDB()
                                .chatGroupMsgDao()
                                .queryPagingItemWithoutLive(mContactsBean.strGroupID, index, limit);
                        if (null != nChatGroupMsgBeans && nChatGroupMsgBeans.size() > 0) {
                            for (ChatGroupMsgBean temp : nChatGroupMsgBeans) {
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
                    public void doOnMain(List<ChatGroupMsgBean> dataAll) {
                        if (null != mChatContentAdapter) {
                            if (dataAll.isEmpty()) {
                                allMsg.clear();
                                showMsgRel(null, -1, -1);
                                return;
                            }
                            if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                                for (ChatGroupMsgBean temp : dataAll) {
                                    unEncryptSingle(temp, false, -1, -1);
                                }
                            } else {
                                if (nEncryptIMEnable) {
                                    EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                    finish();
                                    return;
                                }
                                allMsg.clear();
                                allMsg.addAll(dataAll);
                                showMsgRel(null, -1, -1);
                            }
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
                .chatGroupMsgDao()
                .updateAllRead(mContactsBean.strGroupID);
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
            if (nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
            String msgContentStr = ChatUtil.getChatContentJson(ChatGroupActivityNew.this, msgText,
                    "", "", 0, 0,
                    SP.getBoolean(getGroupSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                    msgText.length(), 0, 0, 0, "");
            sendRealMsg(AppUtils.MESSAGE_TYPE_TEXT, msgContentStr, false);
        }

    }

    void encrypt(int msgType, String str, boolean isFile, boolean isVoice, int recordTime, long size, String fileName, boolean isImg) {
        if (str == null) {
            str = "";
        }
        if (null == mMessageUsersDate || mMessageUsersDate.size() <= 0 || !containSelf || !canSendMsg) {
            showToast(AppUtils.getString(R.string.chat_group_not_contains_self));
            return;
        }

        initUserEncrypt();

        final String[] msgContent = new String[1];
        int longTime = isVoice ? recordTime : str.length();
        if (isImg) {
            longTime = 10;
        }
        if (msgType == AppUtils.MESSAGE_TYPE_ADDRESS) {
            msgContent[0] = str;
        } else {
            msgContent[0] = ChatUtil.getChatContentJson(ChatGroupActivityNew.this, isFile ? "" : str, "",
                    isFile ? str : "", recordTime, size,
                    SP.getBoolean(getGroupSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                    longTime,
                    0, 0, 0, fileName);
        }

        String finalStr = str;
        EncryptUtil.encryptTxt(str, true, true,
                mContactsBean.strGroupID, mContactsBean.strGroupDomainCode,
                "", "", users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmProcessIMRsp sessionRsp) {
                        sendWetherEncrypt(true, msgType, sessionRsp, isFile, isVoice, recordTime, size, fileName, finalStr, isImg);
                    }

                    @Override
                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
//                            showToast("对方未开启加密,无法发送");
                    }
                });
    }

    void sendRealMsg(int msgType, String msgContent, boolean isImg) {
        if (null == mMessageUsersDate || mMessageUsersDate.size() <= 0 || !containSelf || !canSendMsg) {
            showToast(AppUtils.getString(R.string.chat_group_not_contains_self));
            return;
        }

        sendWetherEncrypt(false, msgType, msgContent);

    }

    /**
     * 加密群组发送
     *
     * @param msgType
     * @param sessionRsp
     */
    private void sendWetherEncrypt(boolean isEncrypt, int msgType, SdpMessageCmProcessIMRsp sessionRsp, boolean isFile, boolean isVoice, int recordTime, long size, String fileName, String msgOld, boolean isImg) {
        int longTime = isVoice ? recordTime : msgOld.length();
        if (isImg) {
            longTime = 10;
        }
        for (SdpMessageCmProcessIMRsp.UserData temp : sessionRsp.m_lstData) {
            String msgText = temp.strData;
            String str;
            if (msgType == AppUtils.MESSAGE_TYPE_ADDRESS) {
                str = msgText;
            } else {
                str = ChatUtil.getChatContentJson(ChatGroupActivityNew.this,
                        isFile ? "" : msgText, "",
                        isFile ? msgText : "",
                        recordTime,
                        size,
                        SP.getBoolean(getGroupSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                        longTime,
                        0,
                        0,
                        0, fileName);
            }
            ChatMessageBean bean = new ChatMessageBean();
            bean.content = str;
            bean.type = msgType;
            bean.sessionID = getGroupSessionId();
            bean.sessionName = sessionName;
            bean.fromUserDomain = AppDatas.Auth().getDomainCode();
            bean.fromUserId = AppDatas.Auth().getUserID() + "";
            bean.fromUserName = AppDatas.Auth().getUserName();
            bean.groupType = 1;
            bean.groupDomainCode = mContactsBean.strGroupDomainCode;
            bean.groupID = mContactsBean.strGroupID;
            bean.bEncrypt = isEncrypt ? 1 : 0;
            bean.time = System.currentTimeMillis() / 1000;
            bean.sessionUserList = new ArrayList<>();
            bean.sessionUserList.addAll(mMessageUsersDate);

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
                                    mContactsBean.strGroupID, mContactsBean.strGroupDomainCode,
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
                                if (null != errorInfo && SDKInnerMessageCode.NOT_LOGIN == errorInfo.getCode()) {
                                    needLoad(AppUtils.getString(R.string.string_name_login_error));
                                }
                                showToast("发送失败" + errorInfo.getMessage());
                            }

                        }
                    }
            );
        }

        chat_edit.setText("");
    }

    private void dealSaveMessageAndLoad(SdpMessageCmProcessIMRsp sessionRsp, SdpMessageCmProcessIMRsp.UserData temp, ChatMessageBean bean) {
        if (sessionRsp.m_lstData.indexOf(temp) == sessionRsp.m_lstData.size() - 1) {
            ChatGroupMsgBean groupMsgBean = ChatGroupMsgBean.from(bean);
            if (groupMsgBean.bFire == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_has_send));
                    }
                });
            }

            groupMsgBean.extend1 = temp.strUserID;
            groupMsgBean.extend2 = temp.strUserDomainCode;

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
            loadMore();

            Logger.debug("singleMsg 发送成功");
        }
    }

    private void sendWetherEncrypt(boolean isEncrypt, int msgType, String msgContent) {
        final ChatMessageBean bean = new ChatMessageBean();
        bean.content = msgContent;
        bean.type = msgType;
        bean.sessionID = getGroupSessionId();
        bean.sessionName = sessionName;
        bean.fromUserDomain = AppDatas.Auth().getDomainCode();
        bean.fromUserId = AppDatas.Auth().getUserID() + "";
        bean.fromUserName = AppDatas.Auth().getUserName();
        bean.groupType = 1;
        bean.bEncrypt = isEncrypt ? 1 : 0;
        bean.groupDomainCode = mContactsBean.strGroupDomainCode;
        bean.groupID = mContactsBean.strGroupID;
        bean.time = System.currentTimeMillis() / 1000;
        bean.sessionUserList = new ArrayList<>();
        bean.sessionUserList.addAll(mMessageUsersDate);
        Gson gson = new Gson();
        HYClient.getModule(ApiSocial.class).sendMessage(SdkParamsCenter.Social.SendMuliteMessage()
                        .setIsImportant(true)
                        .setMessage(gson.toJson(bean))
                        .setUser(getSendUserDate()), new SdkCallback<CSendMsgToMuliteUserRsp>() {
                    @Override
                    public void onSuccess(CSendMsgToMuliteUserRsp cSendMsgToMuliteUserRsp) {
                        ChatGroupMsgBean groupMsgBean = ChatGroupMsgBean.from(bean);
                        if (groupMsgBean.bFire == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_has_send));
                                }
                            });
                        }
                        groupMsgBean.read = 1;
                        AppDatas.MsgDB()
                                .chatGroupMsgDao()
                                .insert(groupMsgBean);
                        VimMessageBean vimMessageBean = VimMessageBean.from(bean);
                        huaiye.com.vim.dao.msgs.ChatUtil.get().saveChangeMsg(vimMessageBean, true);
                        loadMore();


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

                        Logger.debug("singleMsg 发送成功");
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

    private String getGroupSessionId() {
        if (null != mContactsBean) {
            return mContactsBean.strGroupDomainCode + mContactsBean.strGroupID;
        } else {
            return null;
        }
    }

    @OnClick(R.id.chat_voice)
    void showVoiceView() {
        SoftKeyboardUtil.hideSoftKeyboard(this);
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

    private void onSendLocation() {
        boolean isYueHouJiFeng = SP.getBoolean(getGroupSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false);
        if (!isYueHouJiFeng) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("nMeetID", mContactsBean.strGroupID);
            intent.putExtra("nMeetDomain", mContactsBean.strGroupDomainCode);
            intent.putExtra("isGroup", true);
            intent.putExtra("mMessageUsersDate", mMessageUsersDate);
            startActivityForResult(intent, AppUtils.REQUEST_CODE_SEND_LOCATION);
        } else {
            showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_un_support));
        }

    }

    private void onUploadFileClicked() {
        boolean isYueHouJiFeng = SP.getBoolean(getGroupSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false);
        if (!isYueHouJiFeng) {
            Intent intent = new Intent(this, ChooseFilesActivity.class);
            intent.putExtra("mMeetID", mContactsBean.strGroupID);
            intent.putExtra("nMeetDomain", mContactsBean.strGroupDomainCode);
            intent.putExtra("isGroup", true);
            intent.putExtra("mMessageUsersDate", mMessageUsersDate);
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_FILE);
        } else {
            showToast(AppUtils.getString(R.string.string_name_yuehoujifeng_un_support));
        }

    }

    private void onVideoRecordClicked() {
        Intent intent = new Intent(this, VideoRecordUploadActivity.class);
        intent.putExtra("mMeetID", mContactsBean.strGroupID);
        intent.putExtra("nMeetDomain", mContactsBean.strGroupDomainCode);
        intent.putExtra("isGroup", true);
        intent.putExtra("mMessageUsersDate", mMessageUsersDate);
        startActivityForResult(intent, REQUEST_CODE_VIDEO_RECORD);
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
                .start(ChatGroupActivityNew.this, AppUtils.REQUEST_CODE_SELECT_IMAGES_CODE);//REQEST_SELECT_IMAGES_CODE为Intent调用的requestCode
    }

    /* 音频通话 */
    @OnClick(R.id.chat_title_bar_voice_chat_btn)
    public void onVoiceClicked() {
        Intent intent = new Intent(this, TalkVoiceActivity.class);
        intent.putExtra("isTalkStarter", true);
        startActivity(intent);
    }

    /* 视频通话 */
    @OnClick(R.id.chat_title_bar_video_chat_btn)
    public void onVideoClicked() {
        ChoosedContacts.get().clear();
        Intent intent = new Intent(getContext(), MeetCreateByGroupUserActivity.class);
        intent.putExtra("nMeetType", 1);
        intent.putExtra("mGroupInfoListBean", mContactsGroupUserListBean);
        startActivity(intent);
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
                    if (nEncryptIMEnable) {
                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                        finish();
                        return;
                    }
                    String msgContentStr = ChatUtil.getChatContentJson(ChatGroupActivityNew.this,
                            "", "", upload.file1_name, 0, fileSize, false,
                            0, 0, 0, 0, fileName);
                    sendRealMsg(AppUtils.MESSAGE_TYPE_FILE, msgContentStr, false);
                }
            } else if (null != upload && requestCode == REQUEST_CODE_VIDEO_RECORD) {
                long fileSize = data.getLongExtra("fileSize", 0);
                int recordTime = data.getIntExtra("recordTime", 0);
                String fileName = data.getStringExtra("fileName");
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    encrypt(AppUtils.MESSAGE_TYPE_VIDEO_FILE, upload.file1_name, true, true, recordTime, fileSize, fileName, false);
                } else {
                    if (nEncryptIMEnable) {
                        EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                        finish();
                        return;
                    }
                    String msgContentStr = ChatUtil.getChatContentJson(ChatGroupActivityNew.this,
                            "", "", upload.file1_name, 0, fileSize,
                            SP.getBoolean(getGroupSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                            recordTime, 0, 0, 0, fileName);
                    sendRealMsg(AppUtils.MESSAGE_TYPE_VIDEO_FILE, msgContentStr, false);
                }
            } else if (resultCode == RESULT_OK && requestCode == AppUtils.REQUEST_CODE_SELECT_IMAGES_CODE) {
                List<String> imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
                mapImg.clear();
                imageSize = imagePaths.size();
                for (String image : imagePaths) {
                    final File file = new File(image);
                    if (file.length() > 1028 * 1028 * 50) {
                        showToast("文档大于50M");
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
                                true, true, mContactsBean.strGroupID, mContactsBean.strGroupDomainCode,
                                "", "", users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                    @Override
                                    public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                        upFile(file, new File(resp.m_strData));
                                    }

                                    @Override
                                    public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                        mapImg.put(image, "对方未开启加密,无法发送");
                                        mapLocal.put(image, "对方未开启加密,无法发送");
//                                        showToast("对方未开启加密,无法发送");
                                        if (mapImg.size() == imageSize) {
                                            sendImageFile();
                                        }
                                    }
                                });
                    } else {
                        if (nEncryptIMEnable) {
                            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                            finish();
                            return;
                        }
                        upFile(file, file);
                    }

                }
            } else if (resultCode == RESULT_OK && requestCode == AppUtils.REQUEST_CODE_CHOOSE_NOTICE) {
                ContactsGroupUserListBean.LstGroupUser user = (ContactsGroupUserListBean.LstGroupUser) data.getSerializableExtra("notice_user");
                chat_edit.setText(chat_edit.getText().toString() + user.strUserName + " ");
                chat_edit.setSelection(chat_edit.length());
                chat_edit.setFocusable(true);
                chat_edit.setFocusableInTouchMode(true);
                chat_edit.requestFocus();
                AppUtils.showKeyboard(chat_edit);
            }

        }
    }

    private void sendImageFile() {
        for (Map.Entry<String, String> entry : mapImg.entrySet()) {
            try {
                File file = new File(mapLocal.get(entry.getKey()));
                File fileOld = new File(entry.getKey());
                if (fileOld == null) {
                    fileOld = new File("");
                }
                if (file == null) {
                    file = new File("");
                }
                File finalFile = file;
                File finalFileOld = fileOld;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChatLocalPathHelper.getInstance().cacheChatLoaclPath(entry.getValue(), finalFile.getPath());
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            encrypt(AppUtils.MESSAGE_TYPE_IMG, entry.getValue(), true, false, 0, finalFile.length(), finalFileOld.getAbsolutePath().substring(finalFileOld.getAbsolutePath().lastIndexOf("/") + 1), true);
                        } else {
                            if (nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            String msgContentStr = ChatUtil.getChatContentJson(ChatGroupActivityNew.this,
                                    "", "", entry.getValue(), 0, finalFile.length(),
                                    SP.getBoolean(getGroupSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                                    10, 0, 0, 0,
                                    finalFileOld.getAbsolutePath().substring(finalFileOld.getAbsolutePath().lastIndexOf("/") + 1));
                            sendRealMsg(AppUtils.MESSAGE_TYPE_IMG, msgContentStr, true);
                        }
                    }
                });
            } catch (Exception e) {
                System.out.println("VIMApp, Group eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee " + e);
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
    public void onEventMainThread(VimMessageBean obj) {
//        if (obj.groupID.equals(mContactsBean.strGroupID) && !obj.isSend) {
//            showToast("群组已被解散");
//            finish();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final MessageEvent messageEvent) {

        switch (messageEvent.what) {
            case AppUtils.EVENT_REFRESH_GROUP_DETAIL:
            case AppUtils.EVENT_ADD_PEOPLE_TO_GROUP_SUCCESS://add
            case AppUtils.EVENT_KICKOUT_PEOPLE_TO_SUCCESS://kickout
                if (AppUtils.EVENT_REFRESH_GROUP_DETAIL == messageEvent.what) {
                    addNotice(messageEvent.msgContent);
                }
                queryGroupChatInfo(false);
                break;
            case AppUtils.EVENT_DEL_GROUP_SUCCESS://解散群
            case AppUtils.EVENT_LEAVE_GROUP_SUCCESS://退群
                if (messageEvent.what == AppUtils.EVENT_DEL_GROUP_SUCCESS) {
                    addNotice("群组已解散");
                } else {
                    addNotice("已退出群组");
                }
                canSendMsg = false;
                queryGroupChatInfo(false);
//                AppDatas.MsgDB()
//                        .chatGroupMsgDao()
//                        .deleteGroup(mContactsBean.strGroupID);
//                VimMessageListMessages.get().del(mContactsBean.strGroupID);
//                finish();
                break;
            case AppUtils.EVENT_UPDATE_GROUP_DETAIL:
            case AppUtils.EVENT_MESSAGE_MODIFY_GROUP:
                if (!TextUtils.isEmpty(messageEvent.msgContent) && messageEvent.msgContent.equals(mContactsBean.strGroupID)) {//只有当前正在聊天的群成员有变动才刷新用户信息
                    mContactsGroupUserListBean = ChatContactsGroupUserListHelper.getInstance().getContactsGroupDetail(mContactsBean.strGroupID);
                    setMessageUsersDate(mContactsGroupUserListBean, false);
                }

                if (AppUtils.EVENT_MESSAGE_MODIFY_GROUP == messageEvent.what) {
                    addNotice(messageEvent.argStr1);
                }
                break;
            case AppUtils.EVENT_INTENT_CHATSINGLEACTIVITY:
                finish();
                break;
            case AppUtils.EVENT_COMING_NEW_MESSAGE:
                if (getGroupSessionId().equals(messageEvent.obj2)) {
                    loadMore();
                }
                break;
            case AppUtils.EVENT_CLEAR_MESSAGE_SUCCESS:
                mChatContentAdapter.resetLastDealposition();//重置上一次点击的录音位置放置index异常
                loadPageData(0, PAGE_SIZE);
                break;
            case AppUtils.EVENT_CREATE_MEETTING_SUCCESS:
                @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String msgContentStrSuccess = ChatUtil.getChatContentJson(ChatGroupActivityNew.this,
                        getString(R.string.chat_group_create_meet, AppDatas.Auth().getUserName()),
                        "", "", 0, 0, false,
                        0, 0, 0, 0, "");

//                sendRealMsg(AppUtils.MESSAGE_TYPE_GROUP_MEET, msgContentStrSuccess, false);
                break;
            case AppUtils.EVENT_CLOSE_MEETTING:
                String msgContentStrOver = ChatUtil.getChatContentJson(ChatGroupActivityNew.this,
                        AppUtils.getString(R.string.chat_group_meet_over), "", "",
                        0, 0, false, 0, 0, 0, 0, "");
//                sendRealMsg(AppUtils.MESSAGE_TYPE_GROUP_MEET, msgContentStrOver, false);

                break;
            case AppUtils.EVENT_MODIFY_GROUPNAME_SUCCESS://修改名称
                if (null != mContactsBean && !TextUtils.isEmpty(messageEvent.argStr0) && messageEvent.argStr0.equals(mContactsBean.strGroupID)) {
                    sessionName = messageEvent.msgContent;
                }
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

                    String msgContentAddress = ChatUtil.getChatContentJson(ChatGroupActivityNew.this, nPoiInfo.getAddress(),
                            "", messageEvent.msgContent, 0,
                            0, false, 0, 0,
                            latLng.latitude, latLng.longitude, "");
                    if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                        encrypt(AppUtils.MESSAGE_TYPE_ADDRESS, msgContentAddress, false, false, 0, -1, "", false);
                    } else {
                        if (nEncryptIMEnable) {
                            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                            finish();
                            return;
                        }
                        sendRealMsg(AppUtils.MESSAGE_TYPE_ADDRESS, msgContentAddress, false);
                    }
                }
                break;
            case AppUtils.EVENT_MODIFY_GROUP_ANNOUNCEMENT_SUCCESS://群公告
                break;
            default:
                break;
        }
    }

    private void addNotice(String notice) {
        loadMore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSpeakerLound();
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
        initUserEncrypt();
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            EncryptUtil.encryptFile(file.getPath(), EncryptUtil.getNewFile(file.getPath()),
                    true, true, mContactsBean.strGroupID, mContactsBean.strGroupDomainCode,
                    "", "", users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                        @Override
                        public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                            upFileVoice(file, new File(resp.m_strData));
                        }

                        @Override
                        public void onError(SdkCallback.ErrorInfo sessionRsp) {
//                            showToast("对方未开启加密,无法发送");
                            if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                mZeusLoadView.dismiss();
                        }
                    });
        } else {
            if (nEncryptIMEnable) {
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
                            if (nEncryptIMEnable) {
                                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                                finish();
                                return;
                            }
                            String msgContentStr = ChatUtil.getChatContentJson(ChatGroupActivityNew.this,
                                    "", "", upload.file1_name, recordTime, file.length(),
                                    SP.getBoolean(getGroupSessionId() + AppUtils.SP_CHAT_SETTING_YUEHOUJIFENG, false),
                                    recordTime, 0, 0, 0, fileOld.getAbsolutePath().substring(fileOld.getAbsolutePath().lastIndexOf("/") + 1));
                            sendRealMsg(AppUtils.MESSAGE_TYPE_AUDIO_FILE, msgContentStr, false);
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

    private ArrayList<SendUserBean> getSendUserDate() {
        ArrayList<SendUserBean> sessionMessageUsersList = new ArrayList<>();
        if (null != mMessageUsersDate && mMessageUsersDate.size() > 0) {
            for (SendUserBean messageUsers : mMessageUsersDate) {
                if (!messageUsers.strUserID.equals(AppDatas.Auth().getUserID())) {
                    sessionMessageUsersList.add(new SendUserBean(messageUsers.strUserID, messageUsers.strUserDomainCode, messageUsers.strUserName));
                }
            }
        }
        return sessionMessageUsersList;
    }

    @OnClick(R.id.chat_title_bar_detail_btn)
    public void onDetailClicked() {
        Intent intent = new Intent(ChatGroupActivityNew.this, UserDetailActivity.class);
        intent.putParcelableArrayListExtra("mUserList", null);
        intent.putExtra("mContactsGroupUserListBean", mContactsGroupUserListBean);
        intent.putExtra("isGroupChat", true);
        intent.putExtra("strGroupDomainCode", mContactsBean.strGroupDomainCode);
        intent.putExtra("strGroupID", mContactsBean.strGroupID);
        intent.putExtra("strGroupName", sessionName);
        intent.putExtra("sessionID", getGroupSessionId());
        ChatGroupActivityNew.this.startActivity(intent);
    }

    @OnClick(R.id.chat_title_bar_title)
    void backPresses() {
        finish();
    }

    void initUserEncrypt() {
        users.clear();
        if (mMessageUsersDate != null) {
            for (SendUserBean temp : mMessageUsersDate) {
                if (!AppAuth.get().getUserID().equals(temp.strUserID)) {
                    SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                    info.strUserDomainCode = temp.strUserDomainCode;
                    info.strUserID = temp.strUserID;
                    users.add(info);
                }
            }
        }
    }

    private void loadMore() {
        ChatGroupMsgBean temp = AppDatas.MsgDB()
                .chatGroupMsgDao()
                .queryLastItem(mContactsBean.strGroupID);
        if (TextUtils.isEmpty(temp.headPic)) {
            temp.headPic = AppDatas.MsgDB().getFriendListDao().getFriendHeadPic(temp.fromUserId, temp.fromUserDomain);
        }
        unEncryptSingle(temp, false, -1, -1);
    }

    private void unEncryptSingle(ChatGroupMsgBean temp, boolean isMore, int position, int addIndex) {
        VimMessageListMessages.get().isRead(temp.sessionID);
        AppDatas.MsgDB()
                .chatGroupMsgDao()
                .updateReadWithMsgID(temp.groupID, temp.msgID);
        if (temp.bEncrypt == 1 && !temp.isUnEncrypt) {
            users.clear();
            if (AppAuth.get().getUserID().equals(temp.fromUserId)) {
                SendMsgUserBean sendMsgUserBean = AppDatas.MsgDB().getSendUserListDao().getSendUserInfo(temp.sessionID);
                if (sendMsgUserBean != null) {
                    SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                    info.strUserDomainCode = sendMsgUserBean.strUserDomainCode;
                    info.strUserID = sendMsgUserBean.strUserID;
                    users.add(info);
                }
            } else {
                SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                info.strUserDomainCode = temp.fromUserDomain;
                info.strUserID = temp.fromUserId;
                users.add(info);
            }

            String unEncryptStr;
            if (temp.type == AppUtils.MESSAGE_TYPE_SHARE) {
                unEncryptStr = temp.msgTxt;
            } else {
                if (temp.type == AppUtils.MESSAGE_TYPE_ADDRESS) {
                    unEncryptStr = temp.msgTxt;
                } else {
                    if (TextUtils.isEmpty(temp.fileUrl)) {
                        unEncryptStr = temp.msgTxt;
                    } else {
                        unEncryptStr = temp.fileUrl;
                    }
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

    private void showMsgRel(ChatGroupMsgBean temp, int position, int addIndex) {
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
        Collections.sort(allMsg, new Comparator<ChatGroupMsgBean>() {
            @Override
            public int compare(ChatGroupMsgBean o1, ChatGroupMsgBean o2) {
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
