package huaiye.com.vim.bus;

/**
 * author: admin
 * date: 2018/05/31
 * version: 0
 * mail: secret
 * desc: GPSStatus
 */

public class GPSStatus {
    public final boolean value;
    public final int num;

    public GPSStatus(boolean value, int num) {
        this.value = value;
        this.num = num;
    }
}
