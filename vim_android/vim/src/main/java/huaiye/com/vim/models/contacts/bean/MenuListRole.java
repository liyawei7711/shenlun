package huaiye.com.vim.models.contacts.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by zhangzhen on 2019/9/20.
 */
public class MenuListRole implements Serializable {
    public int nResultCode;
    public String strResultDescribe;
    public ArrayList<Menu> menuList;

    public class Menu implements Serializable{
        public int nMenuID;
        public int nMenuType;
        public String strMenuName;
        public int nParentMenuID;
    }


    /**
     * 使用枚举类型的ordinal(),界定是什么类型的菜单
     * 新增类型需要向下增加,不能插在中间,否则会导致业务逻辑混乱
     */
    public enum MenuType{
        MENU_TYPE,//占位用的,我们的菜单类型从1开始
        MENU_TYPE_CREATE_GROUP_CHAT,//1,标识建群
        MENU_TYPE_CREATE_MEET_CHAT;//1,标识建群
    }

}
