package com.thisisnotyours.vehicleregistrationapp.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitHelper {

//  Retrofit 사용시--> 에러:  Expected a string but was BEGIN_OBJECT at line 1 column 2 path $ 에러 해결
//  API 또는 서버에서 JSON 값을 받아 클라이언트에서 파싱할 때 인터페이스의 추상 메서드를
//  Call 로, 또는 Rxjava 를 사용 중이라 Observable 으로 설정해서 메서드를 호출할 경우 간간이 볼 수 있다.
//  해결 => builder.addConverterFactory(ScalarsConverterFactory.create()) 추가해주기..


    //실서버
    public static Retrofit getRetrofitInstance() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .build();

        String IP_BASE_URL = "http://49.50.174.192:8080/";


        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(IP_BASE_URL);
//        builder.client(okHttpClient);
        builder.addConverterFactory(ScalarsConverterFactory.create()); //json 못받아올때/ 파싱 못받아올때
        builder.addConverterFactory(GsonConverterFactory.create()); //retrofit 이 읽어온 json 데이터를 GSON 을 이용해 파싱/분석 하기위한 설정
        Retrofit retrofit = builder.build();

        return retrofit;
    }


    //테스트용
    public static Retrofit getRetrofitInstanceTest() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .build();

        String IP_BASE_URL = "http://192.168.0.48:8080/register/";
//        String IP_BASE_URL = "http://192.168.0.48:18080/register/";
        //같은 랜선에 연결된 컴퓨터들끼리 접속하는 개념 (네트워크)
        //회사에서 쓰는 공유기를 게이트웨이로 삼아, 48이 공유기에 할당된 아이디를 의미함.

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(IP_BASE_URL);
//        builder.client(okHttpClient);
        builder.addConverterFactory(ScalarsConverterFactory.create()); //json 못받아올때/ 파싱 못받아올때
        builder.addConverterFactory(GsonConverterFactory.create()); //retrofit 이 읽어온 json 데이터를 GSON 을 이용해 파싱/분석 하기위한 설정
        Retrofit retrofit = builder.build();

        return retrofit;
    }

}
