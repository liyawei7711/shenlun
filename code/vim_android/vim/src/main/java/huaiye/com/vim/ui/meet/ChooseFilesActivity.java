package huaiye.com.vim.ui.meet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.huaiye.cmf.sdp.SdpMessageCmProcessIMReq;
import com.huaiye.cmf.sdp.SdpMessageCmProcessIMRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.huaiye.sdk.sdpmsgs.whiteboard.CStopWhiteboardShareRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import huaiye.com.vim.EncryptUtil;
import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.SDCardUtils;
import huaiye.com.vim.common.helper.ChatLocalPathHelper;
import huaiye.com.vim.common.recycle.LiteBaseAdapter;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.dao.auth.AppAuth;
import huaiye.com.vim.dao.msgs.User;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.models.meet.bean.FileBean;
import huaiye.com.vim.ui.meet.viewholder.FileHolder;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.nEncryptIMEnable;
import static huaiye.com.vim.common.AppUtils.rootPath;

/**
 * author: admin
 * date: 2018/04/23
 * version: 0
 * mail: secret
 * desc: ChoosePhotoActivity
 */

@BindLayout(R.layout.activity_choose_photo)
public class ChooseFilesActivity extends AppBaseActivity {

    private static String NEIZHI = "内置存储";
    private static String WAIZHI = "外置SD卡";

    private static int HAS_CARD = 1;
    private static int NO_CARD = 0;

    @BindView(R.id.rv_data)
    RecyclerView rv_data;

    @BindExtra
    public String mMeetID;
    @BindExtra
    public int nMeetID;
    @BindExtra
    public String nMeetDomain;
    @BindExtra
    User nUser;
    @BindExtra
    boolean isGroup;
    @BindExtra
    ArrayList<SendUserBean> mMessageUsersDate;

    LiteBaseAdapter<FileBean> adapter;

    ArrayList<FileBean> arrays = new ArrayList<>();
    FileBean currentBean;
    String currentPath;

    LinkedHashMap<String, ArrayList<FileBean>> map = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> mapIndex = new LinkedHashMap<>();

    LinearLayoutManager linearLayoutManager;
    Pattern pattern = Pattern.compile("[0-9]*");

    SDCardUtils sdCardUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initActionBar() {
        ArrayList<SdpMessageCmProcessIMReq.UserInfo> users = new ArrayList<>();
        if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
            if (isGroup) {
                for (SendUserBean temp : mMessageUsersDate) {
                    if (!AppAuth.get().getUserID().equals(temp.strUserID)) {
                        SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                        info.strUserDomainCode = temp.strUserDomainCode;
                        info.strUserID = temp.strUserID;
                        users.add(info);
                    }
                }
            } else {
                SdpMessageCmProcessIMReq.UserInfo info = new SdpMessageCmProcessIMReq.UserInfo();
                info.strUserDomainCode = nUser.strDomainCode;
                info.strUserID = nUser.strUserID;
                users.add(info);
            }
        } else {
            if(nEncryptIMEnable) {
                EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_INIT_FAILED, -4, "error"));
                finish();
                return;
            }
        }

        getNavigate().setLeftClickListener(v -> {
            onBackPressed();
        }).setTitlText("共享文件").setRightText("确定").setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBean == null) {
                    showToast("请选择要分享的文件");
                    return;
                }
                final File file = new File(currentBean.path);
                if (HYClient.getSdkOptions().encrypt().isEncryptBind() && nEncryptIMEnable) {
                    EncryptUtil.encryptFile(file.getPath(), EncryptUtil.getNewFile(file.getPath()),
                            true, isGroup, isGroup ? mMeetID + "" : "", isGroup ? nMeetDomain : "",
                            isGroup ? "" : nUser.strUserID, isGroup ? "" : nUser.strDomainCode, users, new SdkCallback<SdpMessageCmProcessIMRsp>() {
                                @Override
                                public void onSuccess(SdpMessageCmProcessIMRsp resp) {
                                    upFile(file, new File(resp.m_strData));
                                }

                                @Override
                                public void onError(SdkCallback.ErrorInfo sessionRsp) {
                                    showToast("文件加密失败");
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

        });
    }

    private void upFile(File oldFile, File file) {
        if (file.length() > 1028 * 1028 * 50) {
            showToast("文档大于50M");
            return;
        }
        mZeusLoadView.loadingText("正在上传").setLoading();
        ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
            @Override
            public void onSuccess(Upload upload) {
                Intent intent = new Intent();
                intent.putExtra("updata", upload);
                intent.putExtra("fileSize", oldFile.length());
                intent.putExtra("fileName", oldFile.getAbsolutePath().substring(oldFile.getAbsolutePath().lastIndexOf("/") + 1));

                setResult(Activity.RESULT_OK, intent);
                ChatLocalPathHelper.getInstance().cacheChatLoaclPath(upload.file1_name, file.getPath());

                ChooseFilesActivity.this.finish();
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                showToast("文件上传失败");
            }

            @Override
            public void onFinish(HTTPResponse httpResponse) {
                if (mZeusLoadView != null && mZeusLoadView.isShowing())
                    mZeusLoadView.dismiss();
            }
        }, file, AppDatas.Constants().getFileUploadUri());
    }

    @Override
    public void doInitDelay() {
        sdCardUtils = new SDCardUtils();

        adapter = new LiteBaseAdapter<>(this,
                arrays,
                FileHolder.class,
                R.layout.item_file,
                v -> {
                    FileBean bean = (FileBean) v.getTag();
                    if (bean.isFile) {
                        bean.isChecked = !bean.isChecked;
                        if (currentBean != null && currentBean != bean) {
                            currentBean.isChecked = false;
                        }
                        currentBean = bean;
                        adapter.notifyDataSetChanged();
                    } else {

                        if (bean.name.equals(WAIZHI) && bean.has_sd_card == NO_CARD) {
                            showToast("外置SD卡不存在");
                            return;
                        }
                        mapIndex.put(bean.mapKey, linearLayoutManager.findLastVisibleItemPosition());
                        getFiles(bean.parent, bean.name);
                    }
                }, null);
        rv_data.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        rv_data.setAdapter(adapter);

//        getFiles(rootPath, "");

        initLocal();
    }

    private void initLocal() {

        arrays.clear();

        FileBean neizhi = new FileBean();
        neizhi.name = "";
        neizhi.showName = NEIZHI;
        neizhi.first = "N";
        neizhi.path = "";
        neizhi.parent = rootPath;
        neizhi.isFile = false;
        neizhi.mapKey = NEIZHI;
        arrays.add(neizhi);

        FileBean waizhi = new FileBean();
        waizhi.name = "";
        waizhi.showName = WAIZHI;
        waizhi.first = "W";
        waizhi.has_sd_card = sdCardUtils.getSecondaryStoragePath(this) != null ? HAS_CARD : NO_CARD;
        waizhi.path = "";
        waizhi.parent = sdCardUtils.getSecondaryStoragePath(this) != null ?
                sdCardUtils.getSecondaryStoragePath(this) : "";
        waizhi.isFile = false;
        waizhi.mapKey = WAIZHI;
        arrays.add(waizhi);

        adapter.notifyDataSetChanged();
    }

    /**
     * 加载文档
     *
     * @param path
     * @param current
     */
    public void getFiles(String path, String current) {
        File file = new File(path, current);
        currentPath = file.toString();
        ArrayList<FileBean> arrayList = new ArrayList<>();
        if (!map.containsKey(file.toString())) {
            File[] files = file.listFiles();
            if(files == null) {
                files = new File[0];
            }
            for (File f : files) {
//                if (f.canRead() && !f.isHidden() && f.canWrite() && f.length() > 0) {//可读且不是隐藏文件
                if (!f.isHidden()) {//可读且不是隐藏文件
                    FileBean bean = new FileBean();
                    bean.name = f.getName();
                    bean.showName = f.getName();
                    bean.first = AppUtils.getFirstSpell(f.getName()).substring(0, 1);
                    bean.path = f.getPath();
//                bean.end = f.getName().substring(f.getName().lastIndexOf("."));
                    bean.parent = f.getParent();
                    bean.isFile = f.isFile();
                    bean.mapKey = file.toString();
                    arrayList.add(bean);
                }

            }
            Collections.sort(arrayList, new Comparator<FileBean>() {
                @Override
                public int compare(FileBean o1, FileBean o2) {
                    return o1.first.compareTo(o2.first);
                }
            });
            Collections.sort(arrayList, new Comparator<FileBean>() {
                @Override
                public int compare(FileBean o1, FileBean o2) {
                    if (pattern.matcher(o1.first).matches() &&
                            pattern.matcher(o2.first).matches()) {
                        return 1;
                    } else if (pattern.matcher(o1.first).matches()) {
                        return 1;
                    } else if (pattern.matcher(o2.first).matches()) {
                        return -
                                1;
                    }
                    return 0;
                }
            });
//            Collections.sort(arrayList, new Comparator<FileBean>() {
//                @Override
//                public int compare(FileBean o1, FileBean o2) {0
//                        if (o1.isFile && !o2.isFile) return 1;
//                        if (!o1.isFile && o2.isFile) return -1;
//                        if (o1.isFile && o2.isFile) return 0;
//                        return 0;
//                }
//            });
            map.put(file.toString(), arrayList);
        } else {
            arrayList = map.get(file.toString());
        }
        arrays.clear();
        arrays.addAll(arrayList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(currentPath)) {
            stop();
            super.onBackPressed();
        } else {
            if("/storage/emulated/0".equalsIgnoreCase(currentPath)) {
                currentPath = "";
                initLocal();
            } else {
                String lastPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                getFiles(lastPath, "");

                if (mapIndex.containsKey(lastPath)) {
                    int index = mapIndex.get(lastPath);
                    linearLayoutManager.scrollToPosition(index);
                }
            }
        }
    }

    private void stop() {
        HYClient.getModule(ApiMeet.class)
                .stopWhiteBoard(SdkParamsCenter.Meet.StopWhiteBoard().setnMeetingID(nMeetID),
                        new SdkCallback<CStopWhiteboardShareRsp>() {
                            @Override
                            public void onSuccess(CStopWhiteboardShareRsp cStopWhiteboardShareRsp) {
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                            }
                        });
    }
}
