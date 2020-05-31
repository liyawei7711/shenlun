package huaiye.com.vim.models;

import java.util.Map;

import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: ModelSDKError
 */

public class ModelSDKErrorResp implements HTTPResponse {

    String strMessage;

    @Override
    public int getStatusCode() {
        return 0;
    }

    @Override
    public boolean isStatusCodeSuccessful() {
        return false;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public HTTPRequest getHttpRequest() {
        return null;
    }

    @Override
    public long getContentLength() {
        return 0;
    }

    @Override
    public byte[] getContent() {
        return null;
    }

    @Override
    public String getContentToString() {
        return null;
    }

    @Override
    public String getErrorMessage() {
        return strMessage;
    }

    public HTTPResponse setErrorMessage(String error) {
        this.strMessage = error;
        return this;
    }
}
