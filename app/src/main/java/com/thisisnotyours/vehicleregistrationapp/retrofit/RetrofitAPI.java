package com.thisisnotyours.vehicleregistrationapp.retrofit;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoListData;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoVO;
import com.thisisnotyours.vehicleregistrationapp.vo.FareTypeListData;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
////////

public interface RetrofitAPI {
    //GET/POST 방식으로 json 데이터 읽어옴

    //로그인
    @POST("mobile_login")
    Call<String> getLoginData(@Query("id") String id
                            , @Query("pw") String pw);

    //차량조회
    @GET("get-connection-info-app")
    Call<CarInfoListData> getCarInfoData(@QueryMap(encoded = false) Map<String, String> datas);

    //요금조회
    @POST("get-fare-type")
    Call<FareTypeListData> getFareType();

    //시경계조회
    @POST("get-city-type")
    Call<FareTypeListData> getCityType();

    //벤사조회
    @POST("get-van-type")
    Call<FareTypeListData> getVanType();

    //차량등록
    @GET("put-car-info-app")
    Call<String> insertCarInfoData(@QueryMap Map<String, String> datas);

    //차량등록: 수정
    @GET("")
    Call<CarInfoVO> UpdateCarInfoData(@QueryMap Map<String, String> datas);
}