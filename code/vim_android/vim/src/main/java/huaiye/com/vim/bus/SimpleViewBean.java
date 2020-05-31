package huaiye.com.vim.bus;

import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.util.ArrayList;

import huaiye.com.vim.common.views.WindowManagerUtils;
import huaiye.com.vim.dao.msgs.CaptureMessage;

public class SimpleViewBean {
    public WindowManagerUtils.CaptureModel captureModel;
    public Object data;

    public SimpleViewBean(WindowManagerUtils.CaptureModel captureModel, Object data) {
        this.captureModel = captureModel;
        this.data = data;
    }
}
