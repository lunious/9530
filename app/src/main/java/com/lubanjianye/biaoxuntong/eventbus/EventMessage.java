package com.lubanjianye.biaoxuntong.eventbus;

/**
 * 项目名:   LBBXT
 * 包名:     com.lubanjianye.biaoxuntong.eventbus
 * 文件名:   EventMessage
 * 创建者:   lunious
 * 创建时间: 2017/12/15  12:47
 * 描述:     TODO
 */

public class EventMessage {

    public static final String LOGIN_SUCCSS = "login_success";
    public static final String LOGIN_OUT = "login_out";
    public static final String CLICK_FAV = "click_fav";
    public static final String TAB_CHANGE = "tab_change";
    public static final String BIND_MOBILE_SUCCESS = "mobile";
    public static final String BIND_COMPANY_SUCCESS = "company";
    public static final String TOKEN_FALSE = "token_false";

    public static final String LOCA_AREA = "loca_area";

    public static final String LOCA_AREA_CHANGE = "loca_area_change";

    public static final String USER_INFO_CHANGE = "user_info_change";
    public static final String READ_STATUS = "read_status";
    public static final String NO_CHANGE_AREA = "no_change_area";
    public static final String IF_ASK_LOCATION = "if_ask_location";



    public final String message;

    public EventMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
