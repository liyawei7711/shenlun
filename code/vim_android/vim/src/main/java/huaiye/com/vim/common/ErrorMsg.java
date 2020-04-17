package huaiye.com.vim.common;

/**
 * Created by Administrator on 2018\3\9 0009.
 */

public class ErrorMsg {
    public static final int meet_not_koten_code = 1720310003;
    public static final int meet_not_exit_code = 1720410012;
    public static final int meet_max_num_code = 1720310004;
    private static final String meet_not_exit = "会议不存在";
    private static final String meet_max_num = "会议达到最大人数";

    public static final int white_board_not_exist_code			=   1720410013; //会议没有白板分享
    public static final int white_board_has_exist_code		=   1720410014; //会议已存在白板分享
    public static final int white_board_not_opener_code		=   1720410015; //不是白板分享发起者
    public static final int white_board_exist_code		=   1720410016; //不是白板分享发起者
    public static final int white_board_start_code		=   27;
    private static final String white_board_not_exist = "会议没有白板分享";
    private static final String white_board_has_exist = "重复开启白板";
    private static final String white_board_not_opener = "不是白板分享发起者";
    private static final String white_board_exist = "会议已存在白板分享";
    private static final String white_board_start = "开启白板失败";

    public static final int re_load_code = 1720200002;
    public static final int login_err_code = 0;
    public static final int create_meet_err_code = 1;
    public static final int raise_hands_err_code = 2;
    public static final int joine_err_code = 3;
    public static final int send_nofity_err_code = 4;
    public static final int start_play_err_code = 5;
    public static final int delete_meet_err_code = 6;
    public static final int update_meet_err_code = 7;
    public static final int invite_user_err_code = 8;
    public static final int get_meet_info_err_code = 9;
    public static final int quite_talk_err_code = 10;
    public static final int kitout_err_code = 11;
    public static final int change_video_err_code = 12;
    public static final int getlayout_info_err_code = 13;
    public static final int jinyan_close_err_code = 14;
    public static final int jinyan_open_err_code = 15;
    public static final int start_talk_err_code = 16;
    public static final int join_talk_err_code = 17;
    public static final int create_group_err_code = 18;
    public static final int get_err_code = 19;
    public static final int delete_err_code = 20;
    public static final int quite_err_code = 21;
    public static final int update_err_code = 22;
    public static final int add_err_code = 23;
    public static final int open_white_board_code = 24;
    public static final int close_white_board_code = 25;
    public static final int update_white_board_code = 26;
    public static final int switch_meet_layout_code = 27;
    public static final int start_record_code = 28;
    public static final int upload_code = 29;
    private static String login_err = "登录失败，请检查账户和网络信息";
    private static String create_meet_err = "创建会议失败";
    private static String raise_hands_err = "举手失败";
    private static String joine_err = "加入会议失败";
    private static String send_nofity = "发送邀请失败";
    private static String start_play_err = "视频记录不存在";
    private static String delete_meet_err = "删除会议失败";
    private static String update_meet_err = "修改会议失败";
    private static String invite_user_err = "邀请失败";
    private static String get_meet_info_err = "获取会议信息失败";
    private static String quite_talk_err = "退出对讲失败";
    private static String kitout_err = "请出参会者失败";
    private static String change_video_err = "变换布局信息失败";
    private static String getlayout_info_err = "获取布局信息失败";
    private static String jinyan_close_err = "禁言失败";
    private static String jinyan_open_err = "解禁失败";
    private static String start_talk_err = "开启对讲失败";
    private static String join_talk_err = "加入对讲失败";
    private static String create_group_err = "创建群组失败";
    private static String get_err = "获取失败";
    private static String delete_err = "删除失败";
    private static String quite_err = "退出失败";
    private static String update_err = "修改失败";
    private static String add_err = "添加失败";
    private static String open_white_board = "进入白板失败";
    private static String close_white_board = "退出白板失败";
    private static String update_white_board = "更新白板失败";
    private static String switch_meet_layout = "更换布局失败";
    private static String start_record = "开启录像失败";
    private static String upload = "上传失败";


    public static String getMsg(int code) {
        switch (code) {
            case login_err_code:
                return login_err;
            case create_meet_err_code:
                return create_meet_err;
            case raise_hands_err_code:
                return raise_hands_err;
            case joine_err_code:
                return joine_err;
            case send_nofity_err_code:
                return send_nofity;
            case start_play_err_code:
                return start_play_err;
            case delete_meet_err_code:
                return delete_meet_err;
            case update_meet_err_code:
                return update_meet_err;
            case invite_user_err_code:
                return invite_user_err;
            case get_meet_info_err_code:
                return get_meet_info_err;
            case quite_talk_err_code:
                return quite_talk_err;
            case kitout_err_code:
                return kitout_err;
            case change_video_err_code:
                return change_video_err;
            case getlayout_info_err_code:
                return getlayout_info_err;
            case jinyan_close_err_code:
                return jinyan_close_err;
            case jinyan_open_err_code:
                return jinyan_open_err;
            case start_talk_err_code:
                return start_talk_err;
            case join_talk_err_code:
                return join_talk_err;
            case create_group_err_code:
                return create_group_err;
            case get_err_code:
                return get_err;
            case delete_err_code:
                return delete_err;
            case quite_err_code:
                return quite_err;
            case update_err_code:
                return update_err;
            case add_err_code:
                return add_err;
            case open_white_board_code:
                return open_white_board;
            case close_white_board_code:
                return close_white_board;
            case update_white_board_code:
                return update_white_board;
            case meet_not_koten_code:
            case meet_not_exit_code:
                return meet_not_exit;
            case meet_max_num_code:
                return meet_max_num;
            case switch_meet_layout_code:
                return switch_meet_layout;
            case start_record_code:
                return start_record;
            case upload_code:
                return upload;

        }
        return "出错啦~";
    }

    public static String getMsgWhiterBoard(int code) {
        switch (code){
            case white_board_has_exist_code:
                return white_board_has_exist;
            case white_board_not_opener_code:
                return white_board_not_opener;
            case white_board_not_exist_code:
                return white_board_not_exist;
            case white_board_exist_code:
                return white_board_exist;
        }
        return white_board_start;
    }
}
