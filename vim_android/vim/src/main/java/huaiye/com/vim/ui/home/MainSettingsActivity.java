package huaiye.com.vim.ui.home;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.huaiye.cmf.sdp.SdpMessageCmCtrlRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._options.symbols.SDKTransformMethod;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.encrypt.ParamsEncryptResetIm;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.VIMApp;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.bus.UploadFile;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;
import huaiye.com.vim.common.constant.SPConstant;
import huaiye.com.vim.common.dialog.LogicDialog;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.FileLocalNameBean;
import huaiye.com.vim.dao.msgs.VimMessageBean;
import huaiye.com.vim.dao.msgs.VimMessageListMessages;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.AuthApi;
import huaiye.com.vim.models.config.ConfigApi;
import huaiye.com.vim.models.config.bean.GetConfigResponse;
import huaiye.com.vim.models.contacts.ContactsApi;
import huaiye.com.vim.models.contacts.bean.ContactsBean;
import huaiye.com.vim.push.MessageReceiver;
import huaiye.com.vim.ui.about.AboutActivity;
import huaiye.com.vim.ui.auth.ChangePwdActivity;
import huaiye.com.vim.ui.auth.StartActivity;
import huaiye.com.vim.ui.chat.dialog.CustomTipDialog;
import huaiye.com.vim.ui.contacts.sharedata.VimChoosedContacts;
import huaiye.com.vim.ui.home.transfile.ChooseEncryptFileActivity;
import huaiye.com.vim.ui.setting.ModifyHeadPicActivity;
import huaiye.com.vim.ui.setting.SettingActivity;

import static huaiye.com.vim.common.AppUtils.REQUEST_CODE_MODIFY_PIC;
import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: FragmentSettings
 */
@BindLayout(R.layout.fragment_settings)
public class MainSettingsActivity extends AppBaseActivity {

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.cb_mobile_notanswer)
    CheckBox cb_mobile_notanswer;
    @BindView(R.id.rg_trans)
    RadioGroup rg_trans;
    @BindView(R.id.rg_trans_title)
    View rg_trans_title;
    @BindView(R.id.activity_setting_head)
    ImageView activity_setting_head;

    private String headUrl;

    private CustomTipDialog mCustomTipDialogl;
    private CustomTipDialog mCustomTipDialog2;
    private boolean isSOS;

    @Override
    protected void initActionBar() {
        getNavigate()
                .setTitlText(getString(R.string.home_tab_my))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
        EventBus.getDefault().register(this);

        initHeadVIew();
        tv_name.setText(AppDatas.Auth().getUserName());

        cb_mobile_notanswer.setChecked(!SP.getBoolean("MOBILE_RING_ANSWER", true));

        cb_mobile_notanswer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SP.putBoolean("MOBILE_RING_ANSWER", !isChecked);
            }
        });

        if (AppUtils.is9) {
            rg_trans.setVisibility(View.VISIBLE);
            rg_trans_title.setVisibility(View.VISIBLE);

            rg_trans.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.rbt_tcp) {
                        SP.putString("trans", "tcp");
                        HYClient.getSdkOptions().Capture().setTransformMethod(SDKTransformMethod.TCP);
                        HYClient.getSdkOptions().Player().setTransformMethod(SDKTransformMethod.TCP);
                    } else {
                        SP.putString("trans", "udp");
                        HYClient.getSdkOptions().Capture().setTransformMethod(SDKTransformMethod.UDP);
                        HYClient.getSdkOptions().Player().setTransformMethod(SDKTransformMethod.UDP);
                    }
                }
            });

        } else {
            rg_trans.setVisibility(View.GONE);
            rg_trans_title.setVisibility(View.GONE);
        }

    }

    @Override
    public void doInitDelay() {
        getUserInfos();
    }

    private void initData() {
        getUserInfos();
    }

    private void getUserInfos() {
        List<String> userSelf = new ArrayList<>();
        userSelf.add(AppDatas.Auth().getUserID());
        ContactsApi.get().requestUserInfoList(AppDatas.Auth().getDomainCode(), userSelf, new ModelCallback<ContactsBean>() {
            @Override
            public void onSuccess(ContactsBean contactsBean) {
                if (null != contactsBean && null != contactsBean.userList && contactsBean.userList.size() > 0) {
                    headUrl = contactsBean.userList.get(0).strHeadUrl;
                    AppDatas.Auth().put(AppDatas.Auth().getUserID() + SPConstant.STR_HEAD_URL, headUrl);
                    AppDatas.MsgDB().getFriendListDao().insertAll(contactsBean.userList);
                    refreshHeadView(headUrl);

                }
            }
        });
    }

    private void initHeadVIew() {
        headUrl = AppDatas.Auth().getHeadUrl(AppDatas.Auth().getUserID() + SPConstant.STR_HEAD_URL);
        if (!TextUtils.isEmpty(headUrl)) {
            refreshHeadView(headUrl);
        }
    }

    private void refreshHeadView(String headUrl) {
        this.headUrl = headUrl;
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());
        if (null != activity_setting_head && !TextUtils.isEmpty(headUrl)) {
            Glide.with(this)
                    .load(AppDatas.Constants().getAddressWithoutPort() + headUrl)
                    .apply(requestOptions)
                    .into(activity_setting_head);
        }
    }

    @OnClick({R.id.view_logout,
            R.id.view_about,
            R.id.view_clear,
            R.id.view_upload_log,
            R.id.view_audio_setting,
            R.id.view_clear_bendi,
            R.id.view_trans_file,
            R.id.view_change_pwd})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view_trans_file:
                startActivity(new Intent(this, ChooseEncryptFileActivity.class));
                break;
            case R.id.view_logout:
                showLogoutDialog();
                break;
            case R.id.view_clear_bendi:
                showLogoutDestoryDialog();
                break;
            case R.id.view_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.view_audio_setting:
                startActivity(new Intent(this, SettingActivity.class).putExtra("isSOS", isSOS));
                break;
            case R.id.view_clear:
                if (isSOS) {
                    return;
                }
                final LogicDialog logicDialog = new LogicDialog(this);
                logicDialog.setMessageText(getString(R.string.common_notice6));
                logicDialog.setConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                            ParamsEncryptResetIm im = SdkParamsCenter.Encrypt.EncryptResetIM();
                            HYClient.getModule(ApiEncrypt.class).encryptResetIm(im, new SdkCallback<SdpMessageCmCtrlRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmCtrlRsp sdpMessageCmCtrlRsp) {

                                }

                                @Override
                                public void onError(ErrorInfo errorInfo) {
                                }
                            });
                        }
                        clearData(false);
                    }
                }).setCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logicDialog.dismiss();
                    }
                }).show();
                break;
            case R.id.user_info_layout:
                break;
            case R.id.view_change_pwd:
                if (isSOS) {
                    return;
                }
                startActivity(new Intent(this, ChangePwdActivity.class).putExtra("isQiangZhi", false));
                break;
            case R.id.view_upload_log:
                mZeusLoadView.loadingText(getString(R.string.is_upload_ing)).setLoading();
                getUploadLogHost();
                break;
        }
    }

    private void showLogoutDialog() {
        if (null == mCustomTipDialogl) {
            mCustomTipDialogl = new CustomTipDialog(this, getString(R.string.logout_security));
            mCustomTipDialogl.setOnFunctionClickedListener(new CustomTipDialog.IFunctionClickedListener() {
                @Override
                public void onClickedLeftFunction() {
                    mCustomTipDialogl.dismiss();
                    customLogout();
                }

                @Override
                public void onClickedRightFunction() {
                    mCustomTipDialogl.dismiss();
                    securityLogout();

                }
            });
            mCustomTipDialogl.setLeftFunctionText(AppUtils.getString(R.string.logout_security_dialog_left));
            mCustomTipDialogl.setRightFunctionText(AppUtils.getString(R.string.logout_security_dialog_right));

        }
        mCustomTipDialogl.show();
    }

    private void showLogoutDestoryDialog() {
        if (null == mCustomTipDialog2) {
            mCustomTipDialog2 = new CustomTipDialog(this, getString(R.string.common_notice7));
            mCustomTipDialog2.setOnFunctionClickedListener(new CustomTipDialog.IFunctionClickedListener() {
                @Override
                public void onClickedLeftFunction() {
                    mCustomTipDialog2.dismiss();
                }

                @Override
                public void onClickedRightFunction() {
                    mCustomTipDialog2.dismiss();
                    securityLogout();
                    MessageReceiver.destoryKey(null, true);
                }
            });
            mCustomTipDialog2.setLeftFunctionText(AppUtils.getString(R.string.cancel));
            mCustomTipDialog2.setRightFunctionText(AppUtils.getString(R.string.makesure));

        }
        mCustomTipDialog2.show();
    }

    private void securityLogout() {
        clearData(true);
    }

    private void customLogout() {
        HYClient.getModule(ApiAuth.class).logout(null);
        AppAuth.get().setAutoLogin(false);
        VimChoosedContacts.get().destory();
        Intent intent = new Intent(this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(intent);
        this.finish();
    }


    private void clearData(boolean issecuritylogout) {
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
            @Override
            public Object doOnThread() {
                ArrayList<FileLocalNameBean> allFile = (ArrayList<FileLocalNameBean>) AppDatas.MsgDB().getFileLocalListDao().getFileLocalList();
                for (FileLocalNameBean bean : allFile) {
                    File file = new File(bean.localFile);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                AppDatas.MsgDB().chatGroupMsgDao().clearData();
                AppDatas.MsgDB().chatSingleMsgDao().clearData();
                AppDatas.MsgDB().getFriendListDao().clearData();
                AppDatas.MsgDB().getGroupListDao().clearData();
                AppDatas.MsgDB().getSendUserListDao().clearData();
                AppDatas.MsgDB().getFileLocalListDao().clearData();
                AppDatas.Messages().clear();
                VimMessageListMessages.get().clear();
                if (issecuritylogout) {
                    SP.clear();//安全退出的时候才清除配置信息
                }

                Glide.get(VIMApp.getInstance().getApplicationContext()).clearDiskCache();
                //删除缓存文件夹
                File fC = new File(VIMApp.getInstance().getApplicationContext().getExternalFilesDir(null) + File.separator + "Vim/");
                if (fC.exists()) {
                    deleteDir(fC.getPath());
                }
                return "";
            }

            @Override
            public void doOnMain(Object data) {
                Glide.get(VIMApp.getInstance().getApplicationContext()).clearMemory();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(getString(R.string.common_notice8));
                        EventBus.getDefault().post(new VimMessageBean());//刷新通知页面的yemian
                        VimMessageListMessages.get().getMessagesUnReadNum();
                        if (issecuritylogout) {
                            customLogout();
                        }
                    }
                });
            }
        });

    }

    public static void deleteDir(final String pPath) {
        File dir = new File(pPath);
        deleteDirWihtFile(dir);
    }

    public static void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }


    private String ip = null;
    private int port = -1;

    private void getUploadLogHost() {
        ConfigApi.get().getAllConfig(new ModelCallback<GetConfigResponse>() {
            @Override
            public void onSuccess(GetConfigResponse response) {
                if (response.nResultCode == 0) {
                    if (response.lstVssConfigParaInfo != null && response.lstVssConfigParaInfo.size() > 1) {
                        for (GetConfigResponse.Data data : response.lstVssConfigParaInfo) {
                            if (data.strVssConfigParaName.equals("FILE_SERVICE_IP")) {
                                ip = data.strVssConfigParaValue;
                            }
                            if (data.strVssConfigParaName.equals("FILE_SERVICE_PORT")) {
                                port = Integer.parseInt(data.strVssConfigParaValue);
                            }
                        }
                        if (TextUtils.isEmpty(ip) || port < 0) {
                            mZeusLoadView.setLoadingText(getString(R.string.meet_start_upload_error));
                            new RxUtils().doDelay(500, new RxUtils.IMainDelay() {
                                @Override
                                public void onMainDelay() {
                                    mZeusLoadView.dismiss();
                                }
                            }, "dismiss");
                            return;
                        }
                        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                            @Override
                            public Object doOnThread() {
                                AuthApi.get().uploadLogOnCrash(ip, port, true, AppDatas.Constants().getFileUploadUri());
                                return "";
                            }

                            @Override
                            public void doOnMain(Object data) {
                            }
                        });
                    }
                } else {
                    mZeusLoadView.setLoadingText(getString(R.string.meet_start_upload_error));
                    new RxUtils().doDelay(500, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            mZeusLoadView.dismiss();
                        }
                    }, "dismiss");
                    showToast(response.strResultDescribe);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UploadFile status) {
        if (status.code == 0) {
            mZeusLoadView.setLoadingText(getString(R.string.meet_start_upload_success));
        } else if (status.code == 1) {
            mZeusLoadView.setLoadingText(getString(R.string.meet_start_upload_error));
        } else {
            mZeusLoadView.setLoadingText(getString(R.string.meet_file_too_big));
        }
        new RxUtils().doDelay(500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                mZeusLoadView.dismiss();
            }
        }, "dismiss");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        if (null != messageEvent) {
            switch (messageEvent.what) {
                case AppUtils.EVENT_MESSAGE_MODIFY_SELF_HEAD_PIC:
                    refreshHeadView(messageEvent.msgContent);
                    break;
                default:
                    break;
            }
        }

    }

    @OnClick(R.id.activity_setting_head)
    void uploadHeadImage() {
        if (isSOS) {
            return;
        }
        Intent intent = new Intent(this, ModifyHeadPicActivity.class);
        intent.putExtra("headPic", headUrl);
        intent.putExtra("isGroup", false);
        startActivityForResult(intent, REQUEST_CODE_MODIFY_PIC);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_MODIFY_PIC) {

        }

    }

    public void setSos(boolean isSOS) {
        this.isSOS = isSOS;
    }

}
