package com.thisisnotyours.vehicleregistrationapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.handler.IOnBackPressed;
import com.thisisnotyours.vehicleregistrationapp.item.CarPageGubun;
import com.thisisnotyours.vehicleregistrationapp.manager.MyTouchListener;
import com.thisisnotyours.vehicleregistrationapp.manager.PreferenceManager;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitAPI;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitHelper;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoListData;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Car_Registration_Fragment extends Fragment implements View.OnClickListener, IOnBackPressed {
    private MainActivity mainActivity = new MainActivity();
    private MyTouchListener myTouchListener = new MyTouchListener();
    private boolean clickEditText = true;
    private String editTextType="";
    private String log = "log_";
    private Context mContext;
    private ArrayList<String> fareIdArr, cityIdArr, firmwareIdArr;
    private List<String> firmwareUpdateList, daemonUpdateList;
    private Spinner spinnerFareId, spinnerCityId, spinnerFirmwareId, spinnerFirmwareUpdate, spinnerDaemonUpdate;
    private ArrayAdapter fareIdAdapter, cityIdAdapter, firmwareIdAdapter, firmwareUpdateAdater, daemonUpdateAdapter;
    private int fareId_idx=0, cityId_idx=0, firmwareId_idx=0, firmwareUpdate_idx=0, daemonUpdate_idx=0;
    private String btnType="??????"
            , strLoginId=""
            , strCompanyName=""
            , strCarRegnum=""
            , strCarType=""
            , strCarVin=""
            , strCarNum=""
            , strDriverId1=""
            , strDriverId2=""
            , strDriverId3=""
            , strMdn=""
            , strFareId=""
            , strCityId=""
            , strFirmwareId=""
            , strSpeedFactor=""
            , strStoreId=""
            , strUnitNum=""
            , strUnitSn=""
            , strFirmwareUpdate=""
            , strDaemonUpdate=""
            , strRegDtti=""
            , strLastDtti="";
    public EditText etCompanyName
            , etCarNum
            , etCarVin
            , etDriverId1
            , etDriverId2
            , etDriverId3
            , etCarRegnum
            , etMdn
            , etSpeedFactor
            , etStoreId
            , etUnitNum
            , etUnitSn;
    private TextView change_mdn;
    private LinearLayout car_type_layout, installDateLayout, recentConnectDateLayout;
    private Button btnCarTypePersonal, btnCarTypeCompany, btnRegister, btnRegisterCancel;
    private ImageView ivDropDown;
    private TextView tvViewMoreDriverId, tvBarcodeScan, tvFirstInstallDate, tvRecentConnectDate;
    private boolean isClicked = true, registerBtnClicked = true, isChangeMdn = true;
    private RelativeLayout layoutDriverId2, layoutDriverId3, update_layout;
    private HashMap<String, String> keyDatas = new HashMap<>();
    private RetrofitAPI retrofitAPI = RetrofitHelper.getRetrofitInstance().create(RetrofitAPI.class);
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public Car_Registration_Fragment() {
        // Required empty public constructor
    }


    //?????????????????? ??????
    @Override
    public void onBackPressed() {
        cancelRegister();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_car_registration, container, false);

        Log.d(log+"onCreate", "register");

        mContext = container.getContext();

        Log.d(log+"saved_info_lifeCycle_", "onCreate_register");
        strLoginId = PreferenceManager.getString(mContext, "id");
        String savedPw = PreferenceManager.getString(mContext, "pw");
        String savedName = PreferenceManager.getString(mContext, "name");
        Log.d(log+"login_saved_info", strLoginId+", "+savedPw+", "+savedName);


        //????????? define id's
        findViewIds(rootView);


        //???????????? ????????? ??????
        if (getArguments() != null) {
            CarPageGubun.type = "??????";

            update_layout.setVisibility(View.VISIBLE);
            installDateLayout.setVisibility(View.VISIBLE);

            mainActivity.tabClickCheck();

            btnType = "??????";

            Log.d(log+"loginId_main_register_1", strLoginId);
            strCompanyName = getArguments().getString("company_name");  //?????????/????????????
            strCarRegnum = getArguments().getString("car_regnum");  //???????????????
            strCarVin = getArguments().getString("car_vin");        //????????????
            strCarNum = getArguments().getString("car_num");        //????????????

            //????????? ???????????? # ??????
            checkDriverId(getArguments().getString("driver_id1"), "driver1");  //?????????????????????1
            checkDriverId(getArguments().getString("driver_id2"), "driver2");  //?????????????????????2
            checkDriverId(getArguments().getString("driver_id3"), "driver3");  //?????????????????????3

            strMdn = getArguments().getString("mdn");   //????????????
            strFareId = getArguments().getString("fare_id");   //??????
            strCityId = getArguments().getString("city_id");   //?????????
            strFirmwareId = getArguments().getString("firmware_id");   //??????
            strSpeedFactor = getArguments().getString("speed_factor"); //?????????

            try {
                if (!strFareId.equals("")||strFareId!=null) {
                    Log.d(log+"pos_get_fare", strFareId);
                }
                if (!strCityId.equals("")||strCityId!=null) {
                    Log.d(log+"pos_get_city", strCityId);
                }
                if (!strFirmwareId.equals("")||strFirmwareId!=null) {
                    Log.d(log+"pos_get_firmware", strFirmwareId);
                }
            }catch (Exception e) {e.printStackTrace();}

            strStoreId = getArguments().getString("store_id");  //????????? ID
            strUnitNum = getArguments().getString("unit_num");  //???????????????
            strUnitSn = getArguments().getString("unit_sn");    //????????????/???????????????

            //????????????
            if (!getArguments().getString("car_type").equals("")) {
                if (getArguments().getString("car_type").equals("??????")) {
                    strCarType = "22";
                    btnCarTypePersonal.setBackgroundResource(R.drawable.btn_search_box);
                    btnCarTypePersonal.setTextColor(getResources().getColor(R.color.white));
                    btnCarTypeCompany.setBackgroundResource(R.drawable.btn_reset_box);
                    btnCarTypeCompany.setTextColor(getResources().getColor(R.color.main_background));
                }else if (getArguments().getString("car_type").equals("??????")) {
                    strCarType = "21";
                    btnCarTypeCompany.setBackgroundResource(R.drawable.btn_search_box);
                    btnCarTypeCompany.setTextColor(getResources().getColor(R.color.white));
                    btnCarTypePersonal.setBackgroundResource(R.drawable.btn_reset_box);
                    btnCarTypePersonal.setTextColor(getResources().getColor(R.color.main_background));
                }
            }

            //???????????? ??????
            strFirmwareUpdate = getArguments().getString("firmware_update");
            strDaemonUpdate = getArguments().getString("daemon_update");

            if (!strFirmwareUpdate.equals("") || strFirmwareUpdate != null) {
                if (strFirmwareUpdate.equals("Y")) {
                    firmwareUpdate_idx = 0;
                }else if (strFirmwareUpdate.equals("N")) {
                    firmwareUpdate_idx = 1;
                }
            }

            if (!strDaemonUpdate.equals("") || strDaemonUpdate != null) {
                if (strDaemonUpdate.equals("Y")) {
                    daemonUpdate_idx = 0;
                }else if (strDaemonUpdate.equals("N")) {
                    daemonUpdate_idx = 1;
                }
            }

            try {
                //?????? & ?????? ????????????
                strRegDtti = getArguments().getString("reg_dtti");
                strLastDtti = getArguments().getString("last_dtti");
                if (strRegDtti=="" || strRegDtti.equals("") || strRegDtti==null) {
                    tvFirstInstallDate.setText("??????");
                }else {
                    tvFirstInstallDate.setText(strRegDtti);
                }

                if (strLastDtti=="" || strLastDtti.equals("") || strLastDtti==null) {
                    tvRecentConnectDate.setText("??????");
                }else {
                    tvRecentConnectDate.setText(strLastDtti);
                }
            }catch (Exception e) {
                tvFirstInstallDate.setText("??????");
                tvRecentConnectDate.setText("??????");
            }

            etCompanyName.setText(strCompanyName);
            etCarNum.setText(strCarNum);
            etCarVin.setText(strCarVin);
            etDriverId1.setText(strDriverId1);
            etDriverId2.setText(strDriverId2);
            etDriverId3.setText(strDriverId3);
            etCarRegnum.setText(strCarRegnum);
            etMdn.setText(strMdn);
            etSpeedFactor.setText(strSpeedFactor);
            etStoreId.setText(strStoreId);
            etUnitNum.setText(strUnitNum);
            etUnitSn.setText(strUnitSn);

            //???????????? edittext ????????????/ ????????????
            etMdn.setClickable(false);
            etMdn.setFocusable(false);
            etMdn.setBackgroundResource(R.drawable.edit_box_block);
            change_mdn.setVisibility(View.VISIBLE);

        }else {
            CarPageGubun.type = "??????";
            btnType = "??????";

            //???????????? edittext ?????????/ ??????????????????
            etMdn.setFocusableInTouchMode(true);
            etMdn.setFocusable(true);
            etMdn.setBackgroundResource(R.drawable.edit_box);
            change_mdn.setVisibility(View.GONE);  //???????????? ????????????

            update_layout.setVisibility(View.GONE);
            installDateLayout.setVisibility(View.GONE);

            etCompanyName.requestFocus();
//            etCompanyName.setBackgroundResource(R.drawable.edit_box_selected);
            bringKeyboard(1400, etCompanyName);
        }

        btnRegister.setText(btnType+" ??????");

        /* spinner setting */
        getFareTypeData();     //??????
        getCityTypeData();     //?????????
        getFirmwareTypeData(); //??????
        getUpdateSetting();  //????????? & ?????? ??????????????????

        return rootView;
    }//onCreateView..

    private void mSenseClick(View et, boolean status) {
        switch (et.getId()) {
            case R.id.et_company_name:
                break;
            case R.id.et_mdn:
                break;
            case R.id.et_car_num:
                break;
        }
    }


    //????????? ???????????? string '#' ?????? set
    void checkDriverId(String driverId, String num) {
        if (driverId != null) {
            if (driverId.contains("#")) {
                String str[] = driverId.split("#");

                for (int i=0; i<str.length; i++) {
                    Log.d(log+"driverID", str[i]);

                    switch (num) {
                        case "driver1":
                            strDriverId1 = str[i];
                            break;
                        case "driver2":
                            strDriverId2 = str[i];
                            break;
                        case "driver3":
                            strDriverId3 = str[i];
                            break;
                    }
                }
            }
        }
    }


    //?????? spinner set
    private void getFareTypeData() {
        Call<CarInfoListData> call = retrofitAPI.getFareType();
        call.enqueue(new Callback<CarInfoListData>() {
            @Override
            public void onResponse(Call<CarInfoListData> call, Response<CarInfoListData> response) {

                if (response.isSuccessful()) {

                    CarInfoListData item = response.body();

                    try {
                        fareIdArr = new ArrayList<>();

                        for (int i=0; i<item.getFareTypeVOS().size(); i++) {
                            //???????????? ??????????????? ??????
                            if (strFareId != null) {  //????????? ????????? ?????????
                                if (!strFareId.equals("")) {  //????????? ????????? ?????????
//                                    if (strFareId.equals(item.getFareTypeVOS().get(i).getFare_name())) {  //????????? ????????? ????????? position ??????
                                    if (strFareId.equals(item.getFareTypeVOS().get(i).getFare_id())) {
                                        //position set
                                        fareId_idx = i;
                                    }
                                }
                            }
                            fareIdArr.add(item.getFareTypeVOS().get(i).getFare_name());
                        }

                        //?????? ???????????? ????????? ??????
                        fareIdAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, fareIdArr);
                        spinnerFareId.setAdapter(fareIdAdapter);
                        spinnerFareId.setSelection(fareId_idx);
                        spinnerFareId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//                                strFareId = (pos+1)+""; //?????????
                                String selectedFareName = fareIdArr.get(pos);  //????????? ???

                                for (int v=0; v<item.getFareTypeVOS().size(); v++) {
                                    if (item.getFareTypeVOS().get(v).getFare_name() == selectedFareName) {
                                        strFareId = item.getFareTypeVOS().get(v).getFare_id();  //fare_id ??????
                                        fareId_idx = v; //pos ?????????
                                        Log.d(log+"fare_val_name", selectedFareName);
                                        Log.d(log+"fare_val_total", item.getFareTypeVOS().get(v).getFare_id()+"-> "+item.getFareTypeVOS().get(v).getFare_name());
                                        Log.d(log+"fare_val_final", strFareId+"-> arr Pos:"+v);
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }catch (Exception e) {e.printStackTrace();}

                }else {}
            }
            @Override
            public void onFailure(Call<CarInfoListData> call, Throwable t) {
                Log.e(log+"call_fare_type_list", t.toString());
            }
        });
    }

    //????????? spinner set
    private void getCityTypeData() {
        Call<CarInfoListData> call = retrofitAPI.getCityType();
        call.enqueue(new Callback<CarInfoListData>() {
            @Override
            public void onResponse(Call<CarInfoListData> call, Response<CarInfoListData> response) {

                if (response.isSuccessful()) {

                    CarInfoListData item = response.body();

                    try {
                        cityIdArr = new ArrayList<>();

                        for (int t=0; t<item.getFareTypeVOS().size(); t++) {

                            //???????????? ??????????????? ??????
                            if (strCityId != null) {
                                if (!strCityId.equals("")) {
                                    if (strCityId.equals(item.getFareTypeVOS().get(t).getCity_id())) {
                                        cityId_idx = t;
                                    }
                                }
                            }
                            cityIdArr.add(item.getFareTypeVOS().get(t).getCity_name());
                        }

                        //????????? ????????? ????????? ??????
                        cityIdAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, cityIdArr);
                        spinnerCityId.setAdapter(cityIdAdapter);
                        spinnerCityId.setSelection(cityId_idx);
                        spinnerCityId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//                                strCityId = (pos)+"";  //?????????
                                strCityId = "";  //spinner ???????????? ????????? city_id ??? ???????????????.

                                String selectedCityName = cityIdArr.get(pos); //????????? ???

                                for (int p=0; p<item.getFareTypeVOS().size(); p++) {
                                    if (item.getFareTypeVOS().get(p).getCity_name() == selectedCityName) {
                                        strCityId = item.getFareTypeVOS().get(p).getCity_id();  //pos ???????????? ????????? city_id ??????
                                        cityId_idx = p;  //pos ????????? ??????
                                        Log.d(log+"selected_val_id", selectedCityName);
                                        Log.d(log+"selected_val_total", item.getFareTypeVOS().get(p).getCity_id()+"-> "+ item.getFareTypeVOS().get(p).getCity_name());
                                        Log.d(log+"selected_val_final", strCityId+"->  arr Pos:"+p);
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<CarInfoListData> call, Throwable t) {

            }
        });
    }

    //?????? spinner set
    private void getFirmwareTypeData() {
        Call<CarInfoListData> call = retrofitAPI.getVanType();
        call.enqueue(new Callback<CarInfoListData>() {
            @Override
            public void onResponse(Call<CarInfoListData> call, Response<CarInfoListData> response) {
                Log.d(log+"response", response.toString());

                if (response.isSuccessful()) {
                    Log.d(log + "response_val", response.toString());

                    CarInfoListData item = response.body();
                    try {
                        Log.d(log + "response_item_size", item.getFareTypeVOS().size() + "");
                        Log.d(log + "response_item_value", item.getFareTypeVOS().toString());

                        firmwareIdArr = new ArrayList<>();

                        for (int y=0; y<item.getFareTypeVOS().size(); y++) {
                            //???????????? ??????????????? ??????
                            if (strFirmwareId != null) {
                                if (!strFirmwareId.equals("")) {
                                    Log.d(log+"recycler_firmware_2", strFirmwareId);  //firmware_id ??? pos ???????????? ????????? id ??????.

//                                    if (strFirmwareId.equals(item.getFareTypeVOS().get(y).getFirmware_name())) {
                                    if (String.valueOf(strFirmwareId).equals(item.getFareTypeVOS().get(y).getFirmware_id())) {
                                        Log.d(log+"recycler_firmware_find", item.getFareTypeVOS().get(y).getFirmware_id()+": "+item.getFareTypeVOS().get(y).getFirmware_name());
                                        firmwareId_idx = y;
                                    }
                                }
                            }
                            firmwareIdArr.add(item.getFareTypeVOS().get(y).getFirmware_name());
                        }

                        //?????? ????????? ????????? ??????
                        firmwareIdAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, firmwareIdArr);
                        spinnerFirmwareId.setAdapter(firmwareIdAdapter);
                        spinnerFirmwareId.setSelection(firmwareId_idx);
                        spinnerFirmwareId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
//                                strFirmwareId = (pos)+""; //?????????
                                Log.d(log+"recycler_firmware_3", strFirmwareId);
                                String selectedFirmwareName = firmwareIdArr.get(pos);  //????????? ???

                                for (int r=0; r<item.getFareTypeVOS().size(); r++) {
                                    if (item.getFareTypeVOS().get(r).getFirmware_name() == selectedFirmwareName) {
                                        strFirmwareId = item.getFareTypeVOS().get(r).getFirmware_id();  //firmware id ??????
                                        firmwareId_idx = r; //pos ????????? ??????
                                        Log.d(log+"firmware_val_name", selectedFirmwareName);
                                        Log.d(log+"firmware_val_total", item.getFareTypeVOS().get(r).getFirmware_id()+"-> "+item.getFareTypeVOS().get(r).getFirmware_name());
                                        Log.d(log+"firmware_val_final", strFirmwareId+"-> arr Pos:"+r);
                                    }
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CarInfoListData> call, Throwable t) {

            }
        });
    }


    //????????? & ?????? ???????????? ????????????
    private void getUpdateSetting() {
        //????????? ???????????? ??????
        firmwareUpdateList = new ArrayList<>();
        firmwareUpdateList.add("Y");
        firmwareUpdateList.add("N");
        firmwareUpdateAdater = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, firmwareUpdateList);
        spinnerFirmwareUpdate.setAdapter(firmwareUpdateAdater);
        spinnerFirmwareUpdate.setSelection(firmwareUpdate_idx);
        spinnerFirmwareUpdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedItem = firmwareUpdateList.get(pos);

                for (int t=0; t<firmwareUpdateList.size(); t++) {

                    if (selectedItem.equals(firmwareUpdateList.get(t))) {
                        Log.d("update_firmware_compare",selectedItem +" == "+ firmwareUpdateList.get(t));
                        firmwareUpdate_idx = t;
                        strFirmwareUpdate = firmwareUpdateList.get(t);  //?????? ???
                        Log.d("update_firmware_put", firmwareUpdate_idx+": "+strFirmwareUpdate);
                        Log.d("update_firmware_put","-----------------------------");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //?????? ???????????? ??????
        daemonUpdateList = new ArrayList<>();
        daemonUpdateList.add("Y");
        daemonUpdateList.add("N");
        daemonUpdateAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, daemonUpdateList);
        spinnerDaemonUpdate.setAdapter(daemonUpdateAdapter);
        spinnerDaemonUpdate.setSelection(daemonUpdate_idx);
        spinnerDaemonUpdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedItem = daemonUpdateList.get(pos);

                for (int y=0; y<daemonUpdateList.size(); y++) {

                    if (selectedItem.equals(daemonUpdateList.get(y))) {
                        Log.d("update_daemon_compare", selectedItem+" == "+daemonUpdateList.get(y));
                        daemonUpdate_idx = y;
                        strDaemonUpdate = daemonUpdateList.get(y);
                        Log.d("update_daemon_put", daemonUpdate_idx+": "+strDaemonUpdate);
                        Log.d("update_daemon_put","-----------------------------");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    //define view id's
    private void findViewIds(View v) {
        etCompanyName = v.findViewById(R.id.et_company_name);
        etCarNum = v.findViewById(R.id.et_car_num);
        etCarVin = v.findViewById(R.id.et_car_vin);
        etDriverId1 = v.findViewById(R.id.et_driver_id1);
        etDriverId2 = v.findViewById(R.id.et_driver_id2);
        etDriverId3 = v.findViewById(R.id.et_driver_id3);
        etCarRegnum = v.findViewById(R.id.et_car_regnum);
        etSpeedFactor = v.findViewById(R.id.et_speed_factor);
        etStoreId = v.findViewById(R.id.et_store_id);
        etUnitNum = v.findViewById(R.id.et_unit_num);
        etUnitSn = v.findViewById(R.id.et_unit_sn);
        etMdn = v.findViewById(R.id.et_mdn);
        change_mdn = v.findViewById(R.id.change_mdn);
        change_mdn.setOnClickListener(this);

        controlEditorAction(etCompanyName, etMdn);
        controlEditorAction(etMdn, etCarNum);
        controlEditorAction(etCarNum, etCarVin);
        controlEditorAction(etCarVin, etDriverId1);
        controlEditorAction(etDriverId1, etCarRegnum);
        controlEditorAction(etDriverId2, etCarRegnum);
        controlEditorAction(etDriverId3, etCarRegnum);
        controlEditorAction(etCarRegnum, etStoreId);
        controlEditorAction(etStoreId, etUnitNum);
        controlEditorAction(etUnitNum, etUnitSn);

        spinnerFareId = v.findViewById(R.id.spinner_fare_id);
        spinnerCityId = v.findViewById(R.id.spinner_city_id);
        spinnerFirmwareId = v.findViewById(R.id.spinner_firmware_id);
        spinnerFirmwareUpdate = v.findViewById(R.id.spinner_firmware_update);
        spinnerDaemonUpdate = v.findViewById(R.id.spinner_daemon_update);
        layoutDriverId2 = v.findViewById(R.id.layout_driver_id2);
        update_layout = v.findViewById(R.id.update_layout);
        layoutDriverId3 = v.findViewById(R.id.layout_driver_id3);
        car_type_layout = v.findViewById(R.id.car_type_layout);
        btnCarTypePersonal = v.findViewById(R.id.btn_car_type_personal);
        btnCarTypeCompany = v.findViewById(R.id.btn_car_type_company);
        ivDropDown = v.findViewById(R.id.iv_drop_down);
        tvViewMoreDriverId = v.findViewById(R.id.tv_view_more_driver_id);
        btnRegister = v.findViewById(R.id.btn_register);
        btnRegisterCancel = v.findViewById(R.id.btn_register_cancel);
        tvBarcodeScan = v.findViewById(R.id.tv_barcode_scan);
        installDateLayout = v.findViewById(R.id.install_date_layout);
        tvFirstInstallDate = v.findViewById(R.id.tv_first_install_date);  //??????????????????(== ????????????????????????)
        tvRecentConnectDate = v.findViewById(R.id.tv_recent_connect_date); //??????????????????

        btnCarTypePersonal.setOnClickListener(this);
        btnCarTypeCompany.setOnClickListener(this);
        ivDropDown.setOnClickListener(this);
        tvViewMoreDriverId.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnRegisterCancel.setOnClickListener(this);
        tvBarcodeScan.setOnClickListener(this);


    }

    //????????? ????????? ???????????????
    public void controlEditorAction(EditText currentEditText, EditText nextEditText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        currentEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (i == EditorInfo.IME_ACTION_DONE) { //[??????]??????
                handled = true;
                currentEditText.clearFocus();
                nextEditText.requestFocus();
            }
            return handled;
        });
    }

    public void bringKeyboard(long speed, EditText editText) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        upKeyboard(editText);
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, speed);
            }
        });
        thread.start();
    }

    //????????? ?????????
    private void upKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_mdn: //??????????????????
                if (isChangeMdn == true) {
                    isChangeMdn = false;
                    change_mdn.setText("????????????");
                    change_mdn.setBackgroundResource(R.drawable.layout_line_light_black);
                    change_mdn.setTextColor(getResources().getColor(R.color.main_background));
                    etMdn.setFocusableInTouchMode(true);
                    etMdn.setFocusable(true);
                    etMdn.setBackgroundResource(R.drawable.edit_box);
                }else {
                    isChangeMdn = true;
                    change_mdn.setText("????????????");
                    change_mdn.setBackgroundResource(R.drawable.layout_round_red_button);
                    change_mdn.setTextColor(getResources().getColor(R.color.white));
                    etMdn.setFocusableInTouchMode(false);
                    etMdn.setFocusable(false);
                    etMdn.setBackgroundResource(R.drawable.edit_box_block);
                    etMdn.setText(strMdn); //?????? ????????????
                }
                break;
            case R.id.btn_car_type_personal:  //????????????[??????]??????
                btnCarTypePersonal.setBackgroundResource(R.drawable.btn_search_box);
                btnCarTypePersonal.setTextColor(getResources().getColor(R.color.white));
                btnCarTypeCompany.setBackgroundResource(R.drawable.btn_reset_box);
                btnCarTypeCompany.setTextColor(getResources().getColor(R.color.main_background));
                strCarType = "22";
                break;
            case R.id.btn_car_type_company:   //????????????[??????]??????
                btnCarTypeCompany.setBackgroundResource(R.drawable.btn_search_box);
                btnCarTypeCompany.setTextColor(getResources().getColor(R.color.white));
                btnCarTypePersonal.setBackgroundResource(R.drawable.btn_reset_box);
                btnCarTypePersonal.setTextColor(getResources().getColor(R.color.main_background));
                strCarType = "21";
                break;
            case R.id.iv_drop_down:
                break;
            case R.id.tv_view_more_driver_id:    //????????????????????? [?????????]??????
                if (isClicked == true) {
                    tvViewMoreDriverId.setText("??????");
                    tvViewMoreDriverId.setTextColor(getResources().getColor(R.color.blue));
                    tvViewMoreDriverId.setBackgroundResource(R.drawable.layout_line_light_blue);
                    layoutDriverId2.setVisibility(View.VISIBLE);
                    layoutDriverId3.setVisibility(View.VISIBLE);
                    isClicked = false;
                }else {
                    tvViewMoreDriverId.setText("?????????");
                    tvViewMoreDriverId.setTextColor(getResources().getColor(R.color.red));
                    tvViewMoreDriverId.setBackgroundResource(R.drawable.layout_line_light_red);
                    layoutDriverId2.setVisibility(View.GONE);
                    layoutDriverId3.setVisibility(View.GONE);
                    isClicked = true;
                }
                break;
            case R.id.tv_barcode_scan:  //[???????????????] ??????
                Toast.makeText(mContext, "???????????????", Toast.LENGTH_SHORT).show();
                new IntentIntegrator((Activity) getContext()).setCaptureActivity(BarcodeScanActivity.class).initiateScan();
                break;

            case R.id.btn_register:        //[????????????]??????

                //insert/ update ??? params ??????
                putParams(btnType);

                break;
            case R.id.btn_register_cancel:     //[??????]??????
                cancelRegister();
                break;
        }
    }//onClick..

    private void cancelRegister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(btnType+"??? ?????????????????????????");
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //???????????? ???????????? ??????
                Fragment fragment = null;
                fragment = new Car_Search_Fragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.frame_change, fragment);
                transaction.commit();
                MainActivity.search_car.setTextColor(getResources().getColor(R.color.blue));
                MainActivity.search_car.setBackgroundResource(R.drawable.btn_gradi_white_line);
                MainActivity.register_car.setTextColor(getResources().getColor(R.color.light_grey));
                MainActivity.register_car.setBackgroundResource(R.drawable.btn_gradi_white);
                Toast.makeText(mContext, btnType+" ??????", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //Params => HashMap ??? key, value ?????? ????????????
    private void putParams(String buttonType) {
        if (etMdn.getText().toString().replace(" ","").length() < 11) {
            Toast.makeText(mContext, "???????????? 11????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
        }else {

            if (buttonType.equals("??????")) {
                keyDatas.put("update_id", strLoginId); //???????????? ?????????
                if (change_mdn.getText().toString().equals("????????????")) {
                    keyDatas.put("mdn", strMdn);  //?????? ????????????
                    keyDatas.put("new_mdn", etMdn.getText().toString().replace(" ","")); //????????? ????????????
                }else if (change_mdn.getText().toString().equals("????????????")) {
                    keyDatas.put("mdn",etMdn.getText().toString().replace(" ",""));  //?????? ????????????
                    keyDatas.put("new_mdn", ""); //????????????
                }

            }else if (buttonType.equals("??????")) {
                keyDatas.put("reg_id", strLoginId); //???????????????
                keyDatas.put("mdn", etMdn.getText().toString().replace(" ","")); //????????? ????????????
            }


//            keyDatas.put("mdn", etMdn.getText().toString().replace(" ","")); //????????????
            keyDatas.put("car_vin", etCarVin.getText().toString().replace(" ",""));  //???????????? //17???????
            keyDatas.put("car_type", strCarType);  //???????????? //??????22/ ??????21
            keyDatas.put("car_num", etCarNum.getText().toString().replace(" ","")); //????????????
            keyDatas.put("car_regnum", etCarRegnum.getText().toString().replace(" ","")); //???????????????
            keyDatas.put("company_name", etCompanyName.getText().toString().replace(" ","")); //?????????/????????????
            keyDatas.put("driver_id1", etDriverId1.getText().toString().replace(" ",""));

            if (etDriverId2.getText().toString().equals("")) {
                keyDatas.put("driver_id2", "222222222");
            }else{
                keyDatas.put("driver_id2", etDriverId2.getText().toString().replace(" ",""));
            }
            if (etDriverId3.getText().toString().equals("")) {
                keyDatas.put("driver_id3", "333333333");
            }else {
                keyDatas.put("driver_id3", etDriverId3.getText().toString().replace(" ",""));
            }

            keyDatas.put("fare_id", strFareId); //??????  //????????? ????????? ????????????
            keyDatas.put("city_id", strCityId); //????????? //????????? ????????? ????????????
            keyDatas.put("daemon_id", "1");     //?????????- 1
            keyDatas.put("firmware_id", strFirmwareId); //?????? //????????? ????????? ????????????
            keyDatas.put("speed_factor", etSpeedFactor.getText().toString().replace(" ",""));    //?????????

            //????????? & ?????? ???????????? ?????? ??????
            keyDatas.put("firmware_update", strFirmwareUpdate);
            keyDatas.put("daemon_update", strDaemonUpdate);

            // hashMap null check
            if (isNullOrEmptyMap(keyDatas) == false) {
                sendMapData(keyDatas, buttonType);
            }
        }

    }

    public boolean isNullOrEmptyMap(HashMap<String, String> map) {
        return (map == null || map.isEmpty());
    }

    public void sendMapData(HashMap<String, String> map, String buttonType) {
        if (map.get("car_vin").length() < 17) {
            Log.d(log+"car_vin_length", map.get("car_vin").length()+"");
            Toast.makeText(mContext, "??????????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("keyDatas-->", "CHECK_FIRST: "+map.toString());
            Log.d(log+"car_vin_length", map.get("car_vin").length()+"");
            //???????????? ??? ?????????
            etCompanyName.setBackgroundResource(R.drawable.edit_box);
            etMdn.setBackgroundResource(R.drawable.edit_box);
            etCarNum.setBackgroundResource(R.drawable.edit_box);
            etCarVin.setBackgroundResource(R.drawable.edit_box);
            etDriverId1.setBackgroundResource(R.drawable.edit_box);
            etDriverId2.setBackgroundResource(R.drawable.edit_box);
            etDriverId3.setBackgroundResource(R.drawable.edit_box);
            etCarRegnum.setBackgroundResource(R.drawable.edit_box);
            etSpeedFactor.setBackgroundResource(R.drawable.edit_box);
            etStoreId.setBackgroundResource(R.drawable.edit_box);
            etUnitNum.setBackgroundResource(R.drawable.edit_box);
            etUnitSn.setBackgroundResource(R.drawable.edit_box);
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(btnType+"??? ?????????????????????????\n\n")
                    .setPositiveButton(getString(R.string.setting_dialog_ok)
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //????????? ??????????????? ????????? ??????
                                    String savedId = PreferenceManager.getString(mContext, "id");
                                    String savedPw = PreferenceManager.getString(mContext, "pw");
                                    String savedName = PreferenceManager.getString(mContext, "name");
//                                    Log.d(log+"%%_saved_info", savedId+", "+savedPw+", "+savedName);

                                    //btnType - ???????????? ???????????? ???????????????
                                    if (buttonType.equals("??????")) {

                                        if (keyDatas.get("reg_id").equals("") || keyDatas.get("reg_id") == null) {
                                            Log.d(log+"%%_", "1_"+keyDatas.get("reg_id"));
                                            keyDatas.put("reg_id", savedId);
                                            Log.d(log+"%%_", "2_"+keyDatas.get("reg_id"));
                                        }

                                        Log.d(log+"final_keyDatas", keyDatas.toString());
                                        Log.d(log+"final_map", map.toString());

                                        insertCarRegistrationInfo(map); //????????? ????????? insert

                                    }else if (buttonType.equals("??????")) {

                                        if (keyDatas.get("update_id").equals("") || keyDatas.get("update_id") == null) {
                                            Log.d(log+"%%_", "1_"+keyDatas.get("update_id"));
                                            keyDatas.put("update_id", savedId);
                                            Log.d(log+"%%_", "2_"+keyDatas.get("update_id"));
                                        }

                                        Log.d(log+"final_keyDatas", keyDatas.toString());
                                        Log.d(log+"final_map", map.toString());

                                        updateCarRegistrationInfo(map); //????????? ????????? update
                                    }
                                }
                            })
                    .setNegativeButton("??????"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    //???????????? inert query ????????????
    private void insertCarRegistrationInfo(HashMap<String, String> map) {
       Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
       RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

       Call<String> call = retrofitAPI.insertCarInfoData(map);

       call.enqueue(new Callback<String>() {
           @Override
           public void onResponse(Call<String> call, Response<String> response) {
               Log.d(log+"insert_response", response.toString());
               Log.d(log+"insert_response", response.code()+"");
               if (response.isSuccessful()) {
                   String result = response.body();
                   Log.d(log+"insert_result", result);

                   /** ???????????? ????????? null ?????? **/
                   checkResultData(result, "??????");
               }
           }

           @Override
           public void onFailure(Call<String> call, Throwable t) {
               Log.e(log+"insert_response", t.toString());
               Toast.makeText(mContext, "????????? ?????????????????????", Toast.LENGTH_SHORT).show();
           }
       });
    }

    //???????????? update query ????????????
    private void updateCarRegistrationInfo(HashMap<String, String> map) {
        Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        Call<String> call = retrofitAPI.updateCarInfoData(map);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(log+"update_response", response.toString());
                Log.d(log+"update_response", response.code()+"");

                if (response.isSuccessful()) {
                    String result = response.body();
                    Log.d(log+"update_result", result);

                    /** ???????????? ????????? null ?????? **/
                    checkResultData(result, "??????");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(log+"update_response", t.toString());
                Toast.makeText(mContext, "????????? ?????????????????????", Toast.LENGTH_SHORT).show();
            }
        });
    }


    void checkResultData(String result, String btnType) {
        if (result.equals("Y")) {
            Toast.makeText(mContext, btnType+"??? ?????????????????????", Toast.LENGTH_SHORT).show();
            //???????????? ???????????? ??????
            Fragment fragment = null;
            fragment = new Car_Search_Fragment();
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.frame_change, fragment);
            transaction.commit();
        }else if (result.equals("N")) {
                Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
        }else {
            String responseVal = "";
            String str = " ??????????????????";

            switch (result) {
                case "reg_id,00":
                    responseVal = "????????? ?????????";
                    str = " ????????????.";
                    break;
                case "update_id,00":
                    responseVal = "????????? ?????????";
                    str = " ????????????.";
                    break;
                case "company_name,00":
                    responseVal = "??????????????????";
                    break;
                case "mdn,01":
                    responseVal = "????????? ??????????????? ?????? ???????????????.\n?????? ???????????????";
                    break;
                case "mdn,00":
                    responseVal = "???????????????";
                    break;
                case "car_num,00":
                    responseVal = "???????????????";
                    break;
                case "car_type,00":
                    responseVal = "???????????????";
                    str = " ??????????????????";
                    break;
                case "car_vin,00":
                    responseVal = "???????????????";
                    break;
                case "driver_id1,00":
                    responseVal = "????????? ????????????1???";
                    break;
                case "driver_id2,00":
                    responseVal = "????????? ????????????2???";
                    break;
                case "driver_id3,00":
                    responseVal = "????????? ????????????3???";
                    break;
                case "car_regnum,00":
                    responseVal = "??????????????????";
                    break;
                case "fare_id,00":
                    responseVal = "?????????";
                    str = " ??????????????????";
                    break;
                case "city_id,00":
                    responseVal = "????????????";
                    str = " ??????????????????";
                    break;
                case "daemon_id,00":
                    responseVal = "daemon_id";
                    break;
                case "firmware_id,00":
                    responseVal = "?????????";
                    str = " ??????????????????";
                    break;
                case "firmware_udpate,00":
                    responseVal = "????????? ???????????? ?????????";
                    str = " ??????????????????";
                    break;
                case "daemon_udpate,00":
                    responseVal = "?????? ???????????? ?????????";
                    str = " ??????????????????";
                    break;
            }
            Toast.makeText(mContext, responseVal + str, Toast.LENGTH_SHORT).show();
        }
    }




}