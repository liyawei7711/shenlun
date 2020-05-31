package huaiye.com.vim.models.contacts.bean;

import java.util.ArrayList;

/**
 * Created by ywt on 2019/2/25.
 */

public class ContactOrganizationBean {
    public int nResultCode;
    public String strResultDescribe;
    public ArrayList<Data> departmentInfoList;

    public static class Data{
        /**部门编号*/
        public long nDepID;
        /**部门名称*/
        public String strName;
        /**部门类型*/
        public int nDepType;
        /**上级部门id，根部门为0*/
        public long nParentID;
        /**上级部门名称*/
        public String strParentName;
        /**部门优先级，优先值值小的排序靠前*/
        public int nPpriority;
        /**部门描述*/
        public String strDesceribe;
    }
}
