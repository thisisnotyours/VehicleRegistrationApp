package com.thisisnotyours.vehicleregistrationapp.item;

public class CarInfoItems {
//    public String reg_dtti;
    public String company_name;
    public String car_regnum;
    public String type_name;
    public String car_vin;
    public String car_num;
    public String driver_id1;
    public String driver_id2;
    public String driver_id3;
    public String mdn;
    public String fare_name;
    public String city_name;
    public String firmware_name;
    public String speed_factor;
    public String store_id;
    public String unit_num;
    public String unit_sn;
    public String firmware_update;
    public String daemon_update;

    public CarInfoItems() {

    }

    public CarInfoItems(String company_name, String car_regnum, String type_name, String car_vin, String car_num, String driver_id1, String driver_id2, String driver_id3, String mdn, String fare_name, String city_name, String firmware_name, String speed_factor, String store_id, String unit_num, String unit_sn, String firmware_update, String daemon_update) {
        this.company_name = company_name;
        this.car_regnum = car_regnum;
        this.type_name = type_name;
        this.car_vin = car_vin;
        this.car_num = car_num;
        this.driver_id1 = driver_id1;
        this.driver_id2 = driver_id2;
        this.driver_id3 = driver_id3;
        this.mdn = mdn;
        this.fare_name = fare_name;
        this.city_name = city_name;
        this.firmware_name = firmware_name;
        this.speed_factor = speed_factor;
        this.store_id = store_id;
        this.unit_num = unit_num;
        this.unit_sn = unit_sn;
        this.firmware_update = firmware_update;
        this.daemon_update = daemon_update;
    }
}
