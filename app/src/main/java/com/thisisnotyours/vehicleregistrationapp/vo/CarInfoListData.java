package com.thisisnotyours.vehicleregistrationapp.vo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CarInfoListData {

    @SerializedName("connInfoList") //들어오는/받아온 데이터 변수명
    private ArrayList<CarInfoVO> carInfoVOS;  //Gson 에게 알려줌 -> 데이터가 "connInfoList" 으로 들어왔지만 "carInfoVOS" 로 사용할거임.

    public ArrayList<CarInfoVO> getCarInfoVOS() {
        return carInfoVOS;
    }

    public void setCarInfoVOS(ArrayList<CarInfoVO> carInfoVOS) {
        this.carInfoVOS = carInfoVOS;
    }
}
