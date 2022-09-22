package com.thisisnotyours.vehicleregistrationapp.vo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FareTypeListData {

    @SerializedName("result")
    private ArrayList<CarInfoVO> fareTypeVOS;

    public ArrayList<CarInfoVO> getFareTypeVOS() {
        return fareTypeVOS;
    }

    public void setFareTypeVOS(ArrayList<CarInfoVO> fareTypeVOS) {
        this.fareTypeVOS = fareTypeVOS;
    }
}
