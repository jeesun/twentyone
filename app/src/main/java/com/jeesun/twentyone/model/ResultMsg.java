package com.jeesun.twentyone.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by simon on 2017/12/13.
 */
@Data
public class ResultMsg implements Serializable {
    private static final long serialVersionUID = -7101095796586538775L;

    private Integer errno;

    private String errmsg;

    private Integer consume;

    private Integer total;

    private Object data;
}