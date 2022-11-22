package com.thisisnotyours.vehicleregistrationapp.retrofit;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoListData;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface RetrofitAPI {
    //GET/POST 방식으로 json 데이터 읽어옴

    //로그인
    @POST("mobile_login")
    Call<CarInfoListData> getLoginData(@Query("id") String id
                                        , @Query("pw") String pw);

    //차량조회
    @GET("get-connection-info")
    Call<CarInfoListData> getCarInfoData(@QueryMap(encoded = false) Map<String, String> datas);

    //차량조회 count- 테이블 길이
    @GET("get-connection-info-length")
    Call<String> getCarInfoCnt(@Query("car_num") String car_num
                                , @Query("mdn") String mdn
                                , @Query("company_name") String company_name
                                , @Query("car_type") String car_type
                                , @Query("reg_id") String reg_id
                                , @Query("visit_bool") String visit_bool
                                , @Query("st_dtti") String st_dtti
                                , @Query("ed_dtti") String ed_dtti
                                , @Query("period_select") String period_select);
    //요금조회
    @POST("get-fare-type")
    Call<CarInfoListData> getFareType();

    //시경계조회
    @POST("get-city-type")
    Call<CarInfoListData> getCityType();

    //벤사조회
    @POST("get-van-type")
    Call<CarInfoListData> getVanType();

    //차량등록
    @GET("put-car-info")
    Call<String> insertCarInfoData(@QueryMap Map<String, String> datas);

    //차량등록: 수정
    @GET("update-car-info")
    Call<String> updateCarInfoData(@QueryMap Map<String, String> datas);
}
