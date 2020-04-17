package huaiye.com.vim.bus;

import java.io.Serializable;

public class MessageEvent implements Serializable {
    public int what;
    public String msgContent;
    public int arg0;
    public int arg1;
    public String argStr0;
    public String argStr1;
    public Object obj1;
    public Object obj2;



    public MessageEvent(int what) {
        this.what = what;
    }


    public MessageEvent(int what, String msgContent) {
        this.what = what;
        this.msgContent = msgContent;
    }

    public MessageEvent(int what, Object obj1) {
        this.what = what;
        this.obj1 = obj1;
    }
    public MessageEvent(int what, int arg0, String from) {
        this.what = what;
        this.arg0 = arg0;
    }

    public MessageEvent(int what, String msgContent, Object obj1) {
        this.what = what;
        this.msgContent = msgContent;
        this.obj1 = obj1;

    }
}
