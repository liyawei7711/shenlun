package huaiye.com.vim.ui.meet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.auth.CUserRegisterRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;

import java.util.UUID;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.ErrorMsg;
import huaiye.com.vim.dao.AppDatas;

/**
 * author: admin
 * date: 2018/01/23
 * version: 0
 * mail: secret
 * desc: NickJoinMeetActivity
 */
@BindLayout(R.layout.activity_nick_join_meet)
public class NickJoinMeetActivity extends AppBaseActivity {

    @BindView(R.id.edt_meet_id)
    EditText edt_meet_id;
    @BindView(R.id.edt_user_name)
    EditText edt_user_name;
    /*@BindView(R.id.cb_close_voice)
    CheckBox cb_close_voice;*/
    /*@BindView(R.id.cb_close_camera)
    CheckBox cb_close_camera;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setTitlText(getString(R.string.title_notice8))
                .setLeftIcon(R.drawable.selector_navi_back_blue)
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });

        try {
            final Uri data = getIntent().getData();
            String params = data.getQueryParameter("meetId");
            edt_meet_id.setText(params);
        } catch (Exception e) {

        }
        edt_meet_id.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {

    }

    @OnClick(R.id.tv_join)
    void onJoinClicked() {
        if (TextUtils.isEmpty(edt_meet_id.getText())
                || TextUtils.isEmpty(edt_user_name.getText())) {
            showToast(getString(R.string.common_notice32));
            return;
        }
        try {
            Integer.parseInt(edt_meet_id.getText().toString());
        } catch (Exception e) {
            showToast(getString(R.string.join_meeting_notice2));
            return;
        }
        UUID deviceUuid = UUID.randomUUID();
        String strUserID = deviceUuid.toString();
        HYClient.getModule(ApiAuth.class).login(SdkParamsCenter.Auth.Login()
                .setAddress(AppDatas.Constants().getAddressIP(), AppDatas.Constants().getSiePort())
                .setUserName(edt_user_name.getText().toString())
                .setUserId(strUserID), new SdkCallback<CUserRegisterRsp>() {
            @Override
            public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                HYClient.getHYCapture().stopCapture(null);
                joinMeet(1);
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(ErrorMsg.getMsg(ErrorMsg.login_err_code));
            }
        });

    }

    @OnClick(R.id.cb_close_camera)
    void onVoiceJoinClicked() {
        if (TextUtils.isEmpty(edt_meet_id.getText())
                || TextUtils.isEmpty(edt_user_name.getText())) {
            showToast(getString(R.string.common_notice32));
            return;
        }
        try {
            Integer.parseInt(edt_meet_id.getText().toString());
        } catch (Exception e) {
            showToast(getString(R.string.join_meeting_notice2));
            return;
        }
        UUID deviceUuid = UUID.randomUUID();
        String strUserID = deviceUuid.toString();
        HYClient.getModule(ApiAuth.class).login(SdkParamsCenter.Auth.Login()
                .setAddress(AppDatas.Constants().getAddressIP(), AppDatas.Constants().getSiePort())
                .setUserName(edt_user_name.getText().toString())
                .setUserId(strUserID), new SdkCallback<CUserRegisterRsp>() {
            @Override
            public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                HYClient.getHYCapture().stopCapture(null);
                joinMeet(2);
            }

            @Override
            public void onError(ErrorInfo errorInfo) {
                showToast(ErrorMsg.getMsg(ErrorMsg.login_err_code));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HYClient.getModule(ApiAuth.class).logout(null);
    }

    void joinMeet(int type) {
        String strOSDCommand = "drawtext=fontfile="
                + HYClient.getSdkOptions().Capture().getOSDFontFile()
                + ":fontcolor=white:x=0:y=0:fontsize=26:box=1:boxcolor=black:alpha=0.8:text=' "
                + edt_user_name.getText().toString()
                + "'";
        // OSD名称初始化
//        HYClient.getSdkOptions().Capture().setOSDCustomCommand(strOSDCommand);

        String strMeetDomain = HYClient.getSdkOptions().User().getDomainCode();
        int nMeetID = Integer.parseInt(edt_meet_id.getText().toString());

        Intent intent = new Intent(getSelf(), MeetNewActivity.class);
        intent.putExtra("strMeetDomainCode", strMeetDomain);
        intent.putExtra("nMeetID", nMeetID);
        intent.putExtra("strInviteUserId", HYClient.getSdkOptions().User().getUserId());
        intent.putExtra("mMediaMode", type == 1 ? SdkBaseParams.MediaMode.AudioAndVideo : SdkBaseParams.MediaMode.Audio);
//        intent.putExtra("isCloseVoice", cb_close_voice.isChecked());
//        intent.putExtra("isCloseVideo", cb_close_camera.isChecked());
        intent.putExtra("isCloseVideo", type == 1 ? false : true);
        intent.putExtra("closeInvisitor", true);

        startActivity(intent);
    }

}
