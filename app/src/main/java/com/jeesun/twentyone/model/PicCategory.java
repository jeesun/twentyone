package com.jeesun.twentyone.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Created by simon on 2017/12/13.
 */
@Data
public class PicCategory implements Serializable {
    private static final long serialVersionUID = -6006558462327795205L;
    private Integer id;

    private String name;

    private Integer totalcnt;

    private Date create_time;

    private String displaytype;

    private String tempdata;
}
