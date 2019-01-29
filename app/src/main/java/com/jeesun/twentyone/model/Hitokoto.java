package com.jeesun.twentyone.model;

import lombok.Data;

/**
 * Created by simon on 2018/1/10.
 */
@Data
public class Hitokoto {
    private Integer id;
    private String hitokoto;
    private String type;
    private String from;
    private String creator;
    private long create_at;
}
