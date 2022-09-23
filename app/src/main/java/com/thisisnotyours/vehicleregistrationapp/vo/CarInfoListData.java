package com.thisisnotyours.vehicleregistrationapp.vo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CarInfoListData {

    //로그인조회
    @SerializedName("userInfo")
    private ArrayList<CarInfoVO> userInfoVOS;

    public ArrayList<CarInfoVO> getUserInfoVOS() {
        return userInfoVOS;
    }

    public void setUserInfoVOS(ArrayList<CarInfoVO> userInfoVOS) {
        this.userInfoVOS = userInfoVOS;
    }

    //차량조회
    @SerializedName("connInfoList") //들어오는/받아온 데이터 변수명
    private ArrayList<CarInfoVO> carInfoVOS;  //Gson 에게 알려줌 -> 데이터가 "connInfoList" 으로 들어왔지만 "carInfoVOS" 로 사용할거임.

    public ArrayList<CarInfoVO> getCarInfoVOS() {
        return carInfoVOS;
    }

    public void setCarInfoVOS(ArrayList<CarInfoVO> carInfoVOS) {
        this.carInfoVOS = carInfoVOS;
    }


    //요금/시경계/벤사 조회
    @SerializedName("result")
    private ArrayList<CarInfoVO> fareTypeVOS;

    public ArrayList<CarInfoVO> getFareTypeVOS() {
        return fareTypeVOS;
    }

    public void setFareTypeVOS(ArrayList<CarInfoVO> fareTypeVOS) {
        this.fareTypeVOS = fareTypeVOS;
    }

}
