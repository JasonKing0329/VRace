package com.king.app.vrace.model.http;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/7/18 11:23
 */
public interface RaceService {
    @GET
    Observable<ResponseBody> getContestantsPage(@Url String url);
}
