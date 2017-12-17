package com.jeesun.twentyone.interfaces;

import com.jeesun.twentyone.model.ResultMsg;
import com.jeesun.twentyone.model.SoResultMsg;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by simon on 2017/12/13.
 */

public interface RequestServes {
    @GET("/index.php?c=WallPaper&a=getAllCategoriesV2&from=360chrome")
    Call<ResultMsg> getCategory();

    @GET("/index.php?c=WallPaper&a=getAppsByCategory&from=360chrome")
    Call<ResultMsg> getPicsByCategory(@Query(value = "cid")Integer categoryId,
                                      @Query("start")Integer start,
                                      @Query("count")Integer count);
    @GET("/j?src=srp")
    Call<SoResultMsg> getSoPicsByKeyWord(@Query(value = "q")String q,
                                         @Query("sn")int sn,
                                         @Query("pn")int pn,
                                         @Query("zoom")int zoom);
}
