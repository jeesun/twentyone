package com.jeesun.twentyone.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Created by simon on 2017/12/13.
 */

@Data
public class WebPicInfo implements Serializable {
    private static final long serialVersionUID = -4170550307250379212L;
    private Integer pid;
    private Integer cid;
    private Integer dl_cnt;
    private Date c_t;
    private Integer imgcut;
    private String url;
    private String tempdata;
    private Integer fav_total;

    public WebPicInfo() {
    }

    public WebPicInfo(String url) {
        this.url = url;
    }
}
