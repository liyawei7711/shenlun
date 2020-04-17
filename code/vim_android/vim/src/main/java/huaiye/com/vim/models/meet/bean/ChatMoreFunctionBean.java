package huaiye.com.vim.models.meet.bean;

import java.io.Serializable;

public class ChatMoreFunctionBean implements Serializable {

    public int functionType;
    public int functionDrawable;
    public String funDescribe;

    public ChatMoreFunctionBean(int functionType, int functionDrawable, String funDescribe) {
        this.functionType = functionType;
        this.functionDrawable = functionDrawable;
        this.funDescribe = funDescribe;
    }
}
