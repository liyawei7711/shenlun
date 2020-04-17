package huaiye.com.vim.ui.meet.presenter;

import android.view.TextureView;

import java.io.File;

import huaiye.com.vim.common.BasePresenter;
import huaiye.com.vim.common.BaseView;
import huaiye.com.vim.models.ModelCallback;
import huaiye.com.vim.models.auth.bean.Upload;

public class VideoRecordPresenterHelper {


    public interface Presenter extends BasePresenter {

        void startRecordVideo(TextureView textureView,boolean localRecord);
        void stopRecordVideo();
        void playLocaVideoRepeate(TextureView textureView);
        void stopPlayLocaVideoRepeate(TextureView textureView);
        void changeCamera();
        void uploadFile(ModelCallback<Upload> callback, File file);

    }

    public interface View extends BaseView {

        void recordOver(String videoPath);

    }



}
