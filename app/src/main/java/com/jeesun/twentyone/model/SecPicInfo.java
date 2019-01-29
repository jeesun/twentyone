package com.jeesun.twentyone.model;

/**
 * select pic info
 */
public class SecPicInfo {
    private PictureInfo pictureInfo;
    private Integer position;
    private Boolean select;

    public PictureInfo getPictureInfo() {
        return pictureInfo;
    }

    public void setPictureInfo(PictureInfo pictureInfo) {
        this.pictureInfo = pictureInfo;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }
}
