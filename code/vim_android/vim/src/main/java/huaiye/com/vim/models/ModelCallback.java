package huaiye.com.vim.models;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import huaiye.com.vim.common.AppBaseActivity;
import huaiye.com.vim.common.AppUtils;
import ttyy.com.jinnetwork.core.callback.HTTPUIThreadCallbackAdapter;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: ModelCallback
 */

public abstract class ModelCallback<T> extends HTTPUIThreadCallbackAdapter {
    static final Gson gson = new Gson();

    Class<T> gsonType;

    HTTPResponse httpResponse;

    public ModelCallback() {
        Class clazz = this.getClass();
        //getSuperclass()获得该类的父类
        clazz.getSuperclass();

        //getGenericSuperclass()获得带有泛型的父类
        //Type是 Java 编程语言中所有类型的公共高级接口。它们包括原始类型、参数化类型、数组类型、类型变量和基本类型。
        Type type = clazz.getGenericSuperclass();

        //ParameterizedType参数化类型，即泛型
        ParameterizedType p = (ParameterizedType) type;

        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        gsonType = (Class) p.getActualTypeArguments()[0];
    }

    @Override
    public void onPreStart(HTTPRequest httpRequest) {
        if (!AppUtils.isNetworkConnected()) {
            httpRequest.cancel();
            AppBaseActivity.showToast("当前无网络");
            return;
        }
        System.out.println("VIMApp, url -> " + httpRequest.getRequestURL());
        System.out.println("VIMApp, params -> " + gson.toJson(httpRequest.getParams()));
        System.out.println("VIMApp, header -> " + gson.toJson(httpRequest.getHeaders()));
    }

    @Override
    public void onProgress(HTTPResponse httpResponse, long l, long l1) {

    }

    @Override
    public final void onSuccess(HTTPResponse httpResponse) {
        String str = httpResponse.getContentToString();
        System.out.println("VIMApp, resp -> " + str);
        onSuccess(gson.fromJson(str, gsonType));
        onFinish(null);
    }

    @Override
    public void onCancel(HTTPRequest httpRequest) {
        onFinish(null);
    }

    @Override
    public void onFailure(HTTPResponse httpResponse) {
        if (null != httpResponse) {
            System.out.println("VIMApp, onFailure -> " + httpResponse.getErrorMessage());
        }
        onFinish(null);
    }

    @Override
    public void onFinish(HTTPResponse httpResponse) {

    }

    public abstract void onSuccess(T t);

}
