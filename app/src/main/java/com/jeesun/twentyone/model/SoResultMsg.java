package com.jeesun.twentyone.model;

import java.util.List;

/**
 * Created by simon on 2017/12/17.
 */

public class SoResultMsg {
    private Integer total;
    private boolean end;
    private String sid;
    private Integer ran;
    private Integer ras;
    private Integer kn;
    private Integer cn;
    private Integer gn;
    private Integer lastindex;
    private Integer ceg;
    private List<SoPicInfo> list;
    private Object boxresult;
    private Object wordguess;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Integer getRan() {
        return ran;
    }

    public void setRan(Integer ran) {
        this.ran = ran;
    }

    public Integer getRas() {
        return ras;
    }

    public void setRas(Integer ras) {
        this.ras = ras;
    }

    public Integer getKn() {
        return kn;
    }

    public void setKn(Integer kn) {
        this.kn = kn;
    }

    public Integer getCn() {
        return cn;
    }

    public void setCn(Integer cn) {
        this.cn = cn;
    }

    public Integer getGn() {
        return gn;
    }

    public void setGn(Integer gn) {
        this.gn = gn;
    }

    public Integer getLastindex() {
        return lastindex;
    }

    public void setLastindex(Integer lastindex) {
        this.lastindex = lastindex;
    }

    public Integer getCeg() {
        return ceg;
    }

    public void setCeg(Integer ceg) {
        this.ceg = ceg;
    }

    public List<SoPicInfo> getList() {
        return list;
    }

    public void setList(List<SoPicInfo> list) {
        this.list = list;
    }

    public Object getBoxresult() {
        return boxresult;
    }

    public void setBoxresult(Object boxresult) {
        this.boxresult = boxresult;
    }

    public Object getWordguess() {
        return wordguess;
    }

    public void setWordguess(Object wordguess) {
        this.wordguess = wordguess;
    }

    @Override
    public String toString() {
        return "SoResultMsg{" +
                "total=" + total +
                ", end=" + end +
                ", sid='" + sid + '\'' +
                ", ran=" + ran +
                ", ras=" + ras +
                ", kn=" + kn +
                ", cn=" + cn +
                ", gn=" + gn +
                ", lastindex=" + lastindex +
                ", ceg=" + ceg +
                ", list=" + list +
                ", boxresult=" + boxresult +
                ", wordguess=" + wordguess +
                '}';
    }
}
