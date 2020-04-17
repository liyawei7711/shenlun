package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.ErrorMsg;

/**
 * author: admin
 * date: 2018/01/15
 * version: 0
 * mail: secret
 * desc: MeetCreateActivity
 */
@BindLayout(R.layout.activity_meet_joine)
public class MeetJoineActivity extends AppBaseActivity {

    /*@BindView(R.id.tv_hint)
    TextView tv_hint;*/
    @BindView(R.id.join_meet_name)
    EditText join_meet_name;
    /*@BindView(R.id.cb_close_voice)
    CheckBox cb_close_voice;
    @BindView(R.id.cb_close_camera)
    CheckBox cb_close_camera;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setTitlText(getString(R.string.join_meeting))
                .setRightIcon(R.drawable.nav_btn_scanning)
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
//                .setRightIcon(R.drawable.ico_saomiao)
//                .setRightClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        scanCode();
//                    }
//                });

        /*tv_hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_meet_name.requestFocus();
                AppUtils.showKeyboard(edt_meet_name);
                edt_meet_name.setSelection(edt_meet_name.getText().toString().length());
            }
        });*/

        /*edt_meet_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    tv_hint.setHint("请输入会议号");
                } else {
                    tv_hint.setHint("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });*/

    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK
                && requestCode == 1000) {
            String str = data.getStringExtra("code");
        }
    }

    void scanCode() {
        Intent intent = new Intent(this, CodeScanActivity.class);
        startActivityForResult(intent, 1000);
    }

    @OnClick({R.id.tv_join, R.id.tv_join_voice})
    public void clickView(View view) {
        switch (view.getId()) {
            case R.id.tv_join:
                joinMeet(1);
                break;
            case R.id.tv_join_voice:
                joinMeet(2);
                break;
            default:
                break;
        }
    }

    public void joinMeet(int type) {
        if (TextUtils.isEmpty(join_meet_name.getText().toString())) {
            showToast(getString(R.string.join_meeting_notice1));
            return;
        }
        try {
            Integer.parseInt(join_meet_name.getText().toString());
        } catch (Exception e) {
            showToast(getString(R.string.join_meeting_notice2));
            return;
        }
        requestDatas(type);
    }

    void requestDatas(final int type) {
        HYClient.getModule(ApiMeet.class).requestMeetDetail(
                SdkParamsCenter.Meet.RequestMeetDetail()
                        .setMeetDomainCode(HYClient.getSdkOptions().User().getDomainCode())
                        .setnListMode(1)
                        .setMeetID(Integer.parseInt(join_meet_name.getText().toString())), new SdkCallback<CGetMeetingInfoRsp>() {
                    @Override
                    public void onSuccess(CGetMeetingInfoRsp cGetMeetingInfoRsp) {
                        boolean isMaster;
                        if (cGetMeetingInfoRsp.strMainUserID.equals(HYClient.getSdkOptions().User().getUserId())) {
                            isMaster = true;
                        } else {
                            isMaster = false;
                        }

                        String strMeetDomain = HYClient.getSdkOptions().User().getDomainCode();
                        int nMeetID = Integer.parseInt(join_meet_name.getText().toString());

                        Intent intent = new Intent(getSelf(), MeetNewActivity.class);
                        for (CGetMeetingInfoRsp.UserInfo temp : cGetMeetingInfoRsp.listUser) {
                            if (temp.strUserID.equals(HYClient.getSdkOptions().User().getUserId())) {
                                intent.putExtra("isWatch", temp.nJoinStatus == 99);
                            }
                        }
                        intent.putExtra("strMeetDomainCode", strMeetDomain);
                        intent.putExtra("strInviteUserId", HYClient.getSdkOptions().User().getUserId());
                        intent.putExtra("isMeetStarter", isMaster);
                        intent.putExtra("nMeetID", nMeetID);

                        intent.putExtra("mMediaMode",
                                type == 1 ? SdkBaseParams.MediaMode.AudioAndVideo : SdkBaseParams.MediaMode.Audio);
//                        intent.putExtra("isCloseVoice", cb_close_voice.isChecked());
                        intent.putExtra("isCloseVideo", type == 1 ? false : true);

                        startActivity(intent);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        showToast(ErrorMsg.getMsg(errorInfo.getCode()));
                    }
                });
    }

}
