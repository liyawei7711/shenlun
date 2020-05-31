package huaiye.com.vim.ui.home;

import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAccelerateMethod;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioAEC;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioAGC;
import com.huaiye.sdk.sdkabi._options.symbols.SDKAudioNS;
import com.huaiye.sdk.sdkabi._options.symbols.SDKCaptureQuality;
import com.huaiye.sdk.sdkabi._options.symbols.SDKTransformMethod;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;

import huaiye.com.vim.R;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SP;

import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD1080P;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_HD720P;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_VGA;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_aec;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_agc;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_bitrate;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_camera;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_capture;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_capturebianma;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_kbps;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_mPublishPresetoption;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_ns;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_player;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_playerjiema;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_qos;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_recapture;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_soft;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_tcp;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_trans;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_udp;
import static huaiye.com.vim.common.AppUtils.STRING_KEY_ying;

/**
 * author: admin
 * date: 2018/06/15
 * version: 0
 * mail: secret
 * desc: AudioSettingActivity
 */

@BindLayout(R.layout.activity_audio_setting)
public class AudioSettingActivity extends AppBaseActivity {

    @BindView(R.id.rg_camera)
    RadioGroup rg_camera;
    @BindView(R.id.rg_capture_bianma)
    RadioGroup rg_capture_bianma;
    @BindView(R.id.rg_trans)
    RadioGroup rg_trans;
    @BindView(R.id.rg_player_jiema)
    RadioGroup rg_player_jiema;
    @BindView(R.id.rg_agc)
    RadioGroup rg_agc;
    @BindView(R.id.rg_aec)
    RadioGroup rg_aec;
    @BindView(R.id.rg_ns)
    RadioGroup rg_ns;

    @BindView(R.id.rg_tvbox_recapture)
    RadioGroup rg_tvbox_recapture;
    @BindView(R.id.rg_capture_qos)
    RadioGroup rg_capture_qos;

    @BindView(R.id.rg_capture_level)
    RadioGroup rg_capture_level;
    @BindView(R.id.rg_player_level)
    RadioGroup rg_player_level;
    @BindView(R.id.rg_capture_framerate)
    RadioGroup rg_capture_framerate;
    @BindView(R.id.ll_seekbar)
    View ll_seekbar;
    @BindView(R.id.seekbar_framerate)
    SeekBar seekbar_framerate;
    @BindView(R.id.tv_framerate_num)
    TextView tv_framerate_num;

    int min = 250;
    int max = 1000;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.activity_setting_call))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {

        rg_camera.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_camera_qian:
                        SP.putInteger(STRING_KEY_camera, 1);
                        break;
                    case R.id.rbt_camera_hou:
                        SP.putInteger(STRING_KEY_camera, 2);
                        break;
                }
            }
        });

        rg_trans.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbt_tcp) {
                    SP.putString(STRING_KEY_trans, STRING_KEY_tcp);
                    HYClient.getSdkOptions().Capture().setTransformMethod(SDKTransformMethod.TCP);
                    HYClient.getSdkOptions().Player().setTransformMethod(SDKTransformMethod.TCP);
                } else {
                    SP.putString(STRING_KEY_trans, STRING_KEY_udp);
                    HYClient.getSdkOptions().Capture().setTransformMethod(SDKTransformMethod.UDP);
                    HYClient.getSdkOptions().Player().setTransformMethod(SDKTransformMethod.UDP);
                }
            }
        });

        if (TextUtils.isEmpty(SP.getString(STRING_KEY_trans))) {
            SP.setParam(STRING_KEY_trans, STRING_KEY_udp);
        }
        if (SP.getString(STRING_KEY_trans).equals(STRING_KEY_tcp)) {
            rg_trans.check(R.id.rbt_tcp);
        } else {//VGA
            rg_trans.check(R.id.rbt_udp);
        }

        rg_tvbox_recapture.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_tvbox_recapture_0:
                        JniIntf.SetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE, 0);
                        break;
                    case R.id.rbt_tvbox_recapture_1:
                        JniIntf.SetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE, 1);
                        break;
                }
                SP.putLong(STRING_KEY_recapture, JniIntf.GetSystemProperty(JniIntf.SYSTEM_PROPERTY_ENABLE_RESAMPLE));
            }
        });

        rg_capture_level.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_lvhd720p:
                        min = 38;
                        max = 492;
                        seekbar_framerate.setMax(max - min);

                        HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                                HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HD720P));
                        break;
                    case R.id.rbt_lvhdvga:
                        min = 38;
                        max = 492;
                        seekbar_framerate.setMax(max - min);

                        HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                                HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                        );
                        break;
                    case R.id.rbt_lvvga:
                        min = 12;
                        max = 344;
                        seekbar_framerate.setMax(max - min);

                        HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                                HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                        );
                        break;
                    case R.id.rbt_lvhd1080p:
                        min = 125;
                        max = 1025;
                        seekbar_framerate.setMax(max - min);

                        HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                                HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                        );
                        break;
                }
                SP.putString(STRING_KEY_capture, HYClient.getSdkOptions().Capture().getCaptureQuality().name());
                changePublishPresetoption();
            }
        });

        rg_capture_framerate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_framerate_lower:
                        ll_seekbar.setVisibility(View.GONE);
                        SP.putInteger(STRING_KEY_mPublishPresetoption, 0);
                        changePublishPresetoption();
                        break;
                    case R.id.rbt_framerate_middle:
                        ll_seekbar.setVisibility(View.GONE);
                        SP.putInteger(STRING_KEY_mPublishPresetoption, 1);
                        changePublishPresetoption();
                        break;
                    case R.id.rbt_framerate_high:
                        ll_seekbar.setVisibility(View.GONE);
                        SP.putInteger(STRING_KEY_mPublishPresetoption, 2);
                        changePublishPresetoption();
                        break;
                    case R.id.rbt_framerate_customer:
                        ll_seekbar.setVisibility(View.VISIBLE);
                        SP.putInteger(STRING_KEY_mPublishPresetoption, -1);
                        seekbar_framerate.setProgress(SP.getInteger(STRING_KEY_bitrate));
                        break;
                }
            }
        });

        seekbar_framerate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_framerate_num.setText((progress + min) * 8 + " kbps");
                SP.putInteger(STRING_KEY_bitrate, (progress + min));
                changePublishPresetoption();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (SP.getInteger(STRING_KEY_qos, -1) == 1) {
            rg_capture_qos.check(R.id.rbt_qos_open);
        } else {
            rg_capture_qos.check(R.id.rbt_qos_close);
        }
        rg_capture_qos.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_qos_open:
                        SP.putInteger(STRING_KEY_qos, 1);
                        break;
                    case R.id.rbt_qos_close:
                        SP.putInteger(STRING_KEY_qos, 0);
                        break;
                }
                HYClient.getSdkOptions().Capture().setOpenQOS(SP.getInteger(STRING_KEY_qos) == 1);
            }
        });

        rg_aec.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_aec_0:
                        HYClient.getSdkOptions().Capture().setAudioEnableAEC(SDKAudioAEC.CLOSE);
                        break;
                    case R.id.rbt_aec_1:
                        HYClient.getSdkOptions().Capture().setAudioEnableAEC(SDKAudioAEC.OPEN);
                        break;
                }
                SP.putInteger(STRING_KEY_aec, HYClient.getSdkOptions().Capture().getAudioEnableAEC().value());
//                tv_arc_status.setText("当前AEC模式为: " + HYClient.getSdkOptions().Capture().getAudioEnableAEC().value());
            }
        });
        rg_agc.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_agc_0:
                        HYClient.getSdkOptions().Capture().setAudioEnableAGC(SDKAudioAGC.CLOSE);
                        break;
                    case R.id.rbt_agc_1:
                        HYClient.getSdkOptions().Capture().setAudioEnableAGC(SDKAudioAGC.OPEN);
                        break;
                }
                SP.putInteger(STRING_KEY_agc, HYClient.getSdkOptions().Capture().getAudioEnableAGC().value());
//                tv_agc_status.setText("当前AGC模式为: " + HYClient.getSdkOptions().Capture().getAudioEnableAGC().value());
            }
        });
        rg_ns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_ns_0:
                        HYClient.getSdkOptions().Capture().setAudioNS(SDKAudioNS.CLOSE);
                        break;
                    case R.id.rbt_ns_1:
                        HYClient.getSdkOptions().Capture().setAudioNS(SDKAudioNS.OPEN);
                        break;
                }
                SP.putInteger(STRING_KEY_ns, HYClient.getSdkOptions().Capture().getAudioNS().value());
//                tv_ns_status.setText("当前NS模式为: " + HYClient.getSdkOptions().Capture().getAudioNS().value());
            }
        });

        rg_capture_bianma.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_capture_ying:
                        SP.putString(STRING_KEY_capturebianma, STRING_KEY_ying);
                        HYClient.getSdkOptions().Capture().setAccelerateMethod(SDKAccelerateMethod.Hardware);
                        break;
                    case R.id.rbt_capture_soft:
                        SP.putString(STRING_KEY_capturebianma, STRING_KEY_soft);
                        HYClient.getSdkOptions().Capture().setAccelerateMethod(SDKAccelerateMethod.Software);
                        break;
                }
            }
        });

        rg_player_jiema.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_player_ying:
                        SP.putString(STRING_KEY_playerjiema, STRING_KEY_ying);
                        HYClient.getSdkOptions().Player().setAccelerateMethod(SDKAccelerateMethod.Hardware);
                        break;
                    case R.id.rbt_player_soft:
                        SP.putString(STRING_KEY_playerjiema, STRING_KEY_soft);
                        HYClient.getSdkOptions().Player().setAccelerateMethod(SDKAccelerateMethod.Software);
                        break;
                }
            }
        });

        rg_player_level.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbt_play_lvhd:
                        SP.putString(STRING_KEY_player, STRING_KEY_HD);
                        break;
                    case R.id.rbt_play_vga:
                        SP.putString(STRING_KEY_player, STRING_KEY_VGA);
                        break;
                }
            }
        });

        if (TextUtils.isEmpty(SP.getString(STRING_KEY_player))) {
            SP.putString(STRING_KEY_player, STRING_KEY_VGA);
        }
        if (SP.getString(STRING_KEY_player).equals(STRING_KEY_HD)) {
            rg_player_level.check(R.id.rbt_play_lvhd);
        } else {//VGA
            rg_player_level.check(R.id.rbt_play_vga);
        }

        if (SP.getInteger(STRING_KEY_aec, -1) == -1) {
            SP.putInteger(STRING_KEY_aec, HYClient.getSdkOptions().Capture().getAudioEnableAEC().value());
        }
        if (SP.getInteger(STRING_KEY_aec, -1) == 0) {
            rg_aec.check(R.id.rbt_aec_0);
        } else {
            rg_aec.check(R.id.rbt_aec_1);
        }


        if (SP.getInteger(STRING_KEY_agc, -1) == -1) {
            SP.putInteger(STRING_KEY_agc, HYClient.getSdkOptions().Capture().getAudioEnableAGC().value());
        }
        if (SP.getInteger(STRING_KEY_agc, -1) == 0) {
            rg_agc.check(R.id.rbt_agc_0);
        } else {
            rg_agc.check(R.id.rbt_agc_1);
        }

        if (SP.getInteger(STRING_KEY_ns, -1) == -1) {
            SP.putInteger(STRING_KEY_ns, HYClient.getSdkOptions().Capture().getAudioNS().value());
        }
        if (SP.getInteger(STRING_KEY_ns, -1) == 0) {
            rg_ns.check(R.id.rbt_ns_0);
        } else {
            rg_ns.check(R.id.rbt_ns_1);
        }

        if (SP.getInteger(STRING_KEY_camera, -1) == -1) {
            SP.putInteger(STRING_KEY_camera, 1);
        }
        if (SP.getInteger(STRING_KEY_camera, -1) == 1) {
            rg_camera.check(R.id.rbt_camera_qian);
        } else {
            rg_camera.check(R.id.rbt_camera_hou);
        }

        if (TextUtils.isEmpty(SP.getString(STRING_KEY_capturebianma))) {
            if (HYClient.getSdkOptions().Capture().getAccelerateMethod() == SDKAccelerateMethod.Hardware) {
                SP.putString(STRING_KEY_capturebianma, STRING_KEY_ying);
            } else {
                SP.putString(STRING_KEY_capturebianma, STRING_KEY_soft);
            }
        }
        if (SP.getString(STRING_KEY_capturebianma).equals(STRING_KEY_ying)) {
            rg_capture_bianma.check(R.id.rbt_capture_ying);
        } else {//VGA
            rg_capture_bianma.check(R.id.rbt_capture_soft);
        }


        if (TextUtils.isEmpty(SP.getString(STRING_KEY_playerjiema))) {
            if (HYClient.getSdkOptions().Player().getAccelerateMethod() == SDKAccelerateMethod.Hardware) {
                SP.putString(STRING_KEY_playerjiema, STRING_KEY_ying);
            } else {
                SP.putString(STRING_KEY_playerjiema, STRING_KEY_soft);
            }
        }
        if (SP.getString(STRING_KEY_playerjiema).equals(STRING_KEY_ying)) {
            rg_player_jiema.check(R.id.rbt_player_ying);
        } else {//VGA
            rg_player_jiema.check(R.id.rbt_player_soft);
        }

//        if (SP.getLong(STRING_KEY_recapture, (long) -1) == -1) {
//            SP.putLong(STRING_KEY_recapture, 0);
//        }
        if (SP.getLong(STRING_KEY_recapture, (long) -1) == 1) {
            rg_tvbox_recapture.check(R.id.rbt_tvbox_recapture_1);
        } else {//VGA
            rg_tvbox_recapture.check(R.id.rbt_tvbox_recapture_0);
        }

    }

    /**
     * 改变mPublishPresetoption
     */
    private void changePublishPresetoption() {
        int current = SP.getInteger(STRING_KEY_mPublishPresetoption);

        switch (SP.getString(STRING_KEY_capture)) {
            case STRING_KEY_VGA:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger(STRING_KEY_bitrate) * 8 * 1000)
                    );
                } else {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case STRING_KEY_HD:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger(STRING_KEY_bitrate) * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case STRING_KEY_HD720P:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD720P)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger(STRING_KEY_bitrate) * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD720P)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
            case STRING_KEY_HD1080P:

                if (current == -1) {
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                                    .setmPublishPresetoption(current)
                                    .setBitrate(SP.getInteger(STRING_KEY_bitrate) * 8 * 1000)
                    );
                } else {

                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture()
                                    .getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                                    .setmPublishPresetoption(current)
                    );
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (SP.getString(STRING_KEY_capture)) {
            case STRING_KEY_VGA:
                rg_capture_level.check(R.id.rbt_lvvga);
                min = 12;
                max = 344;
                seekbar_framerate.setMax(max - min);
                break;
            case STRING_KEY_HD:
                rg_capture_level.check(R.id.rbt_lvhdvga);
                min = 38;
                max = 492;
                seekbar_framerate.setMax(max - min);
                break;
            case STRING_KEY_HD720P:
                rg_capture_level.check(R.id.rbt_lvhd720p);
                min = 38;
                max = 492;
                seekbar_framerate.setMax(max - min);
                break;
            case STRING_KEY_HD1080P:
                rg_capture_level.check(R.id.rbt_lvhd1080p);
                min = 125;
                max = 1025;
                seekbar_framerate.setMax(max - min);
                break;
        }

        switch (SP.getInteger(STRING_KEY_mPublishPresetoption)) {
            case 0:
                rg_capture_framerate.check(R.id.rbt_framerate_lower);
                break;
            case 1:
                rg_capture_framerate.check(R.id.rbt_framerate_middle);
                break;
            case 2:
                rg_capture_framerate.check(R.id.rbt_framerate_high);
                break;
            case -1:
                rg_capture_framerate.check(R.id.rbt_framerate_customer);
                ll_seekbar.setVisibility(View.VISIBLE);
                seekbar_framerate.setProgress(SP.getInteger(STRING_KEY_bitrate) - min);
                tv_framerate_num.setText((seekbar_framerate.getProgress() * 8 + min * 8) + " " + STRING_KEY_kbps);
                break;
        }
    }

}
