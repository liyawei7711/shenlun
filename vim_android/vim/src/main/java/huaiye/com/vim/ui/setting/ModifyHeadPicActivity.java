package huaiye.com.vim.ui.setting;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.lcw.library.imagepicker.ImagePicker;
import com.lcw.library.imagepicker.activity.ImagePickerActivity;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import huaiye.com.vim.R;
import huaiye.com.vim.bus.MessageEvent;
import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import huaiye.com.vim.common.rx.RxUtils;
import huaiye.com.vim.common.utils.BitmapResizeUtil;
import huaiye.com.vim.common.views.pickers.adapter.GlideLoader;
import huaiye.com.vim.dao.AppDatas;
import huaiye.com.vim.models.ModelApis;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;
import huaiye.com.vim.models.contacts.ContactsApi;
import huaiye.com.vim.models.contacts.bean.CustomResponse;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static huaiye.com.vim.common.AppUtils.REQUEST_CODE_SELECT_IMAGES_CODE;


@BindLayout(R.layout.activity_modify_head_pic)
public class ModifyHeadPicActivity extends AppBaseActivity {


    @BindView(R.id.modify_head_pic)
    ImageView modifyHeadPic;
    @BindView(R.id.modify_head_pic_btn)
    TextView modifyHeadPicBtn;


    @BindExtra
    String headPic;
    @BindExtra
    boolean isGroup;
    @BindExtra
    String strGroupDomainCode;
    @BindExtra
    String strGroupID;

    @BindExtra
    boolean isGroupOwner;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        initView();
        refreshHeadView(headPic);
    }

    private void initView() {
        if(isGroup&&!isGroupOwner){
            modifyHeadPicBtn.setVisibility(View.GONE);
        }else{
            modifyHeadPicBtn.setVisibility(View.VISIBLE);
        }
    }

    private void refreshHeadView(String headUrl) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .optionalTransform(new CircleCrop());
        if (null != modifyHeadPic) {
            if (!TextUtils.isEmpty(headUrl)) {
                Glide.with(this)
                        .load(AppDatas.Constants().getAddressWithoutPort() + headUrl)
                        .apply(requestOptions)
                        .into(modifyHeadPic);
            }else{
                if(isGroup){
                    Glide.with(this)
                            .load(R.drawable.ic_group_chat)
                            .apply(requestOptions)
                            .into(modifyHeadPic);
                }else{
                    Glide.with(this)
                            .load(R.drawable.default_image_personal)
                            .apply(requestOptions)
                            .into(modifyHeadPic);
                }
            }
        }
    }

    @OnClick(R.id.modify_head_pic_btn)
    void intent2ModifyPic() {
        ImagePicker.getInstance()
                .setTitle(AppUtils.getString(R.string.select_image_for_upload))//设置标题
                .showCamera(true)//设置是否显示拍照按钮
                .showImage(true)//设置是否展示图片
                .showVideo(false)//设置是否展示视频
                .setSingleType(true)//设置图片视频不能同时选择
                .setMaxCount(1)//设置最大选择图片数目(默认为1，单选)
                .setImageLoader(new GlideLoader())//设置自定义图片加载器
        ;
        Intent intent = new Intent(this, ImagePickerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_IMAGES_CODE) {
            List<String> imagePaths = data.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES);
            if (null != imagePaths && imagePaths.size() == 1) {
                String headImg = imagePaths.get(0);
                startPhotoZip(headImg);
            }
        }

    }

    private void compressAndUpload(final String filepath) {
        //调用裁减失败则直接压缩图片文件后上传
        new RxUtils<String>()
                .doOnThreadObMain(new RxUtils.IThreadAndMainDeal<String>() {
                    @Override
                    public String doOnThread() {
                        String targetFilepath = ModifyHeadPicActivity.this.getExternalFilesDir(null) + File.separator + "Vim" + File.separator + "newclip.jpg";
                        BitmapResizeUtil.compressBitmap(filepath, targetFilepath, 100);
                        return targetFilepath;
                    }

                    @Override
                    public void doOnMain(String data) {
                        final File file = new File(data);
                        if (file.length() > 1028 * 100) {
                            showToast("图片大于100K");
                            return;
                        }
                        ModelApis.Download().uploadFile(new ModelCallback<Upload>() {
                            @Override
                            public void onSuccess(final Upload upload) {

                                if(isGroup){
                                    ModelApis.Contacts().requestModGroupChat(strGroupDomainCode, strGroupID, "", "",upload.file1_name, new ModelCallback<CustomResponse>() {
                                        @Override
                                        public void onSuccess(final CustomResponse contactsBean) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    uploadHeadPicSuccess(file,modifyHeadPic,upload);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(HTTPResponse httpResponse) {
                                            super.onFailure(httpResponse);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showToast(AppUtils.getString(R.string.custom_tip_network_error));
                                                }
                                            });
                                        }
                                    });
                                }else{
                                    ContactsApi.get().requestModUserHead(upload.file1_name, new ModelCallback<CustomResponse>() {
                                        @Override
                                        public void onSuccess(CustomResponse nCustomResponse) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    uploadHeadPicSuccess(file,modifyHeadPic,upload);


                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure(HTTPResponse httpResponse) {
                                            super.onFailure(httpResponse);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showToast(AppUtils.getString(R.string.upload_head_failed));
                                                }
                                            });
                                        }
                                    });
                                }


                            }

                            @Override
                            public void onFailure(HTTPResponse httpResponse) {
                                super.onFailure(httpResponse);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast(AppUtils.getString(R.string.upload_head_failed));
                                    }
                                });
                            }

                            @Override
                            public void onFinish(HTTPResponse httpResponse) {

                            }
                        }, file, AppDatas.Constants().getHeaderUri());
                    }
                });


    }

    private void uploadHeadPicSuccess(File file, ImageView modifyHeadPic, Upload upload) {
        if(isGroup){
            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_MESSAGE_MODIFY_GROUP_HEAD_PIC, upload.file1_name));
        }else{
            EventBus.getDefault().post(new MessageEvent(AppUtils.EVENT_MESSAGE_MODIFY_SELF_HEAD_PIC, upload.file1_name));
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop()
                .dontAnimate()
                .format(DecodeFormat.PREFER_RGB_565)
                .placeholder(R.drawable.default_image_personal)
                .error(R.drawable.default_image_personal)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)//加载本地图片不缓存
                .optionalTransform(new CircleCrop());
        Glide.with(ModifyHeadPicActivity.this)
                .load(file)
                .apply(requestOptions)
                .into(modifyHeadPic);
        showToast(AppUtils.getString(R.string.upload_head_success));
    }


    @OnClick(R.id.modify_back)
    void back() {
        finish();
    }


    /**
     * 裁减
     *
     * @param filepath
     */
    public void startPhotoZip(String filepath) {
        compressAndUpload(filepath);
    }

}
