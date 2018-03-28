package com.lubanjianye.biaoxuntong.ui.search;

import java.io.Serializable;

public class PicVipInfo implements Serializable {
    public String id;
    public String urls; //分号分割
    public String baiduUrl;
    public String pwd;
    public String domain_type;
    public int type;
    public String viewNumber;
    public int isFree; //0免费 1付费
}
