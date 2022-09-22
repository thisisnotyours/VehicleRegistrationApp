package com.thisisnotyours.vehicleregistrationapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitAPI;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitHelper;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoListData;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoVO;
import com.thisisnotyours.vehicleregistrationapp.vo.FareTypeListData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Car_Registration_Fragment extends Fragment implements View.OnClickListener{
    private String log = "log_";
    private Context mContext;
    private ArrayList<String> fareIdArr, cityIdArr, firmwareIdArr;
    private Spinner spinnerFareId, spinnerCityId, spinnerFirmwareId;
    private ArrayAdapter fareIdAdapter, cityIdAdapter, firmwareIdAdapter;
    private int fareId_idx=0, cityId_idx=0, firmwareId_idx=0;
    private String btnType="등록"
            , strCompanyName
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
            , strLogId="test";
    private EditText etCompanyName
            , etCarNum
            , etCarVin
            , etDriverId1
            , etDriverId2
            , etDriverId3
            , etCarRegnum
            , etMdn;
    private Button btnCarTypePersonal, btnCarTypeCompany, btnRegister, btnRegisterCancel;
    private ImageView ivDropDown;
    private TextView tvViewMoreDriverId;
    private boolean isClicked = true, registerBtnClicked = true;
    private RelativeLayout layoutDriverId2, layoutDriverId3;
    private HashMap<String, String> keyDatas = new HashMap<>();
    private RetrofitAPI retrofitAPI = RetrofitHelper.getRetrofitInstance().create(RetrofitAPI.class);

    public Car_Registration_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_car_registration, container, false);

        mContext = container.getContext();


        //뷰찾기 define id's
        findViewIds(rootView);


        //전달받은 데이터 체크
        if (getArguments() != null) {
            btnType = "수정";

            strCompanyName = getArguments().getString("company_name");  //운수사/개인이름
            strCarRegnum = getArguments().getString("car_regnum");  //사업자번호
            strCarVin = getArguments().getString("car_vin");        //차대번호
            strCarNum = getArguments().getString("car_num");        //차량번호

            //운전자 자격번호 # 빼기
            checkDriverId(getArguments().getString("driver_id1"), "driver1");  //운전자자격번호1
            checkDriverId(getArguments().getString("driver_id2"), "driver2");  //운전자자격번호2
            checkDriverId(getArguments().getString("driver_id3"), "driver3");  //운전자자격번호3

            strMdn = getArguments().getString("mdn");  //모뎀번호
            strFareId = getArguments().getString("fare_id");
            strCityId = getArguments().getString("city_id");
            strFirmwareId = getArguments().getString("firmware_id");

            //차량유형
            if (!getArguments().getString("car_type").equals("")) {
                if (getArguments().getString("car_type").equals("개인")) {
                    strCarType = "22";
                    btnCarTypePersonal.setBackgroundResource(R.drawable.btn_search_box);
                    btnCarTypePersonal.setTextColor(getResources().getColor(R.color.white));
                    btnCarTypeCompany.setBackgroundResource(R.drawable.btn_reset_box);
                    btnCarTypeCompany.setTextColor(getResources().getColor(R.color.main_background));
                }else if (getArguments().getString("car_type").equals("법인")) {
                    strCarType = "21";
                    btnCarTypeCompany.setBackgroundResource(R.drawable.btn_search_box);
                    btnCarTypeCompany.setTextColor(getResources().getColor(R.color.white));
                    btnCarTypePersonal.setBackgroundResource(R.drawable.btn_reset_box);
                    btnCarTypePersonal.setTextColor(getResources().getColor(R.color.main_background));
                }
            }

            etCompanyName.setText(strCompanyName);
            etCarNum.setText(strCarNum);
            etCarVin.setText(strCarVin);
            etDriverId1.setText(strDriverId1);
            etDriverId2.setText(strDriverId2);
            etDriverId3.setText(strDriverId3);
            etCarRegnum.setText(strCarRegnum);
            etMdn.setText(strMdn);
        }else {
            btnType = "등록";
        }

        btnRegister.setText(btnType+" 완료");

        /* retrofit data fetching */
        getFareTypeData();     //요금
        getCityTypeData();     //시경계
        getFirmwareTypeData(); //벤사

        return rootView;
    }//onCreateView..



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


    //요금 spinner set
    private void getFareTypeData() {
        Call<FareTypeListData> call = retrofitAPI.getFareType();
        call.enqueue(new Callback<FareTypeListData>() {
            @Override
            public void onResponse(Call<FareTypeListData> call, Response<FareTypeListData> response) {

                if (response.isSuccessful()) {
                    FareTypeListData item = response.body();
                    try {
                        fareIdArr = new ArrayList<>();

                        for (int i=0; i<item.getFareTypeVOS().size(); i++) {

                            //등록인지 수정값인지 확인
                            if (!strFareId.equals("")) {  //앞에서 넘어온 요금값
                                if (strFareId.equals(item.getFareTypeVOS().get(i).getFare_name())) {  //앞에서 넘어온 요금값 position 확인
                                    //position set
                                    fareId_idx = i;
                                }
                            }
                            fareIdArr.add(item.getFareTypeVOS().get(i).getFare_name());
                        }

                        //요금 스피너에 데이터 표출
                        fareIdAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, fareIdArr);
                        spinnerFareId.setAdapter(fareIdAdapter);
                        spinnerFareId.setSelection(fareId_idx);
                        spinnerFareId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                                strFareId = (pos+1)+""; //값넣기
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }catch (Exception e) {e.printStackTrace();}

                }else {}
            }
            @Override
            public void onFailure(Call<FareTypeListData> call, Throwable t) {
                Log.e(log+"call_fare_type_list", t.toString());
            }
        });
    }

    //시경계 spinner set
    private void getCityTypeData() {
        Call<FareTypeListData> call = retrofitAPI.getCityType();
        call.enqueue(new Callback<FareTypeListData>() {
            @Override
            public void onResponse(Call<FareTypeListData> call, Response<FareTypeListData> response) {

                if (response.isSuccessful()) {

                    FareTypeListData item = response.body();

                    try {
                        cityIdArr = new ArrayList<>();

                        for (int t=0; t<item.getFareTypeVOS().size(); t++) {
                            if (!strCityId.equals("")) {
                                if (strCityId.equals(item.getFareTypeVOS().get(t).getCity_name())) {
                                    cityId_idx = t;
                                }
                            }
                            cityIdArr.add(item.getFareTypeVOS().get(t).getCity_name());
                        }

                        //시경계 스피너 데이터 표출
                        cityIdAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, cityIdArr);
                        spinnerCityId.setAdapter(cityIdAdapter);
                        spinnerCityId.setSelection(cityId_idx);
                        spinnerCityId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                                strCityId = (pos+1)+"";  //값넣기
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
            public void onFailure(Call<FareTypeListData> call, Throwable t) {

            }
        });
    }

    //벤사 spinner set
    private void getFirmwareTypeData() {
        Call<FareTypeListData> call = retrofitAPI.getVanType();
        call.enqueue(new Callback<FareTypeListData>() {
            @Override
            public void onResponse(Call<FareTypeListData> call, Response<FareTypeListData> response) {
                Log.d(log+"response", response.toString());

                if (response.isSuccessful()) {
                    Log.d(log + "response_val", response.toString());

                    FareTypeListData item = response.body();
                    try {
                        Log.d(log + "response_item_size", item.getFareTypeVOS().size() + "");
                        Log.d(log + "response_item_value", item.getFareTypeVOS().toString());

                        firmwareIdArr = new ArrayList<>();

                        for (int y=0; y<item.getFareTypeVOS().size(); y++) {
                            if (!strFirmwareId.equals("")) {
                                if (strFirmwareId.equals(item.getFareTypeVOS().get(y).getFirmware_name())) {
                                    Log.d(log+"strFirmwareId_find", item.getFareTypeVOS().get(y).getFirmware_id()+": "+item.getFareTypeVOS().get(y).getFirmware_name());
                                    firmwareId_idx = y;
                                }
                            }
                            firmwareIdArr.add(item.getFareTypeVOS().get(y).getFirmware_name());
                        }
                        Log.d(log+"firmwareIdArr", firmwareIdArr.toString());
                        //벤사 스피너 데이터 표출
                        firmwareIdAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, firmwareIdArr);
                        spinnerFirmwareId.setAdapter(firmwareIdAdapter);
                        spinnerFirmwareId.setSelection(firmwareId_idx);
                        spinnerFirmwareId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                                strFirmwareId = (pos+1)+""; //값넣기
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
            public void onFailure(Call<FareTypeListData> call, Throwable t) {

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

        spinnerFareId = v.findViewById(R.id.spinner_fare_id);
        spinnerCityId = v.findViewById(R.id.spinner_city_id);
        spinnerFirmwareId = v.findViewById(R.id.spinner_firmware_id);

        etMdn = v.findViewById(R.id.et_mdn);
        layoutDriverId2 = v.findViewById(R.id.layout_driver_id2);
        layoutDriverId3 = v.findViewById(R.id.layout_driver_id3);

        btnCarTypePersonal = v.findViewById(R.id.btn_car_type_personal);
        btnCarTypeCompany = v.findViewById(R.id.btn_car_type_company);
        ivDropDown = v.findViewById(R.id.iv_drop_down);
        tvViewMoreDriverId = v.findViewById(R.id.tv_view_more_driver_id);
        btnRegister = v.findViewById(R.id.btn_register);
        btnRegisterCancel = v.findViewById(R.id.btn_register_cancel);

        btnCarTypePersonal.setOnClickListener(this);
        btnCarTypeCompany.setOnClickListener(this);
        ivDropDown.setOnClickListener(this);
        tvViewMoreDriverId.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnRegisterCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_car_type_personal:  //차량유형[개인]버튼
                btnCarTypePersonal.setBackgroundResource(R.drawable.btn_search_box);
                btnCarTypePersonal.setTextColor(getResources().getColor(R.color.white));
                btnCarTypeCompany.setBackgroundResource(R.drawable.btn_reset_box);
                btnCarTypeCompany.setTextColor(getResources().getColor(R.color.main_background));
                strCarType = "22";
                break;
            case R.id.btn_car_type_company:   //차량유형[법인]버튼
                btnCarTypeCompany.setBackgroundResource(R.drawable.btn_search_box);
                btnCarTypeCompany.setTextColor(getResources().getColor(R.color.white));
                btnCarTypePersonal.setBackgroundResource(R.drawable.btn_reset_box);
                btnCarTypePersonal.setTextColor(getResources().getColor(R.color.main_background));
                strCarType = "21";
                break;
            case R.id.iv_drop_down:
                break;
            case R.id.tv_view_more_driver_id:    //운전자자격번호 [더보기]버튼
                if (isClicked == true) {
//                    ivDropDown.setRotation(270);
                    tvViewMoreDriverId.setText("닫기");
                    layoutDriverId2.setVisibility(View.VISIBLE);
                    layoutDriverId3.setVisibility(View.VISIBLE);
                    isClicked = false;
                }else {
//                    ivDropDown.setRotation(90);
                    tvViewMoreDriverId.setText("더보기");
                    layoutDriverId2.setVisibility(View.GONE);
                    layoutDriverId3.setVisibility(View.GONE);
                    isClicked = true;
                }
                break;
            case R.id.btn_register:        //[등록완료]버튼
                //btnType - 등록인지 수정인지 구분해야함
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("등록을 완료하시겠습니까?\n\n")
                        .setPositiveButton(getString(R.string.setting_dialog_ok)
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //등록 할 params 담기
                                        putCarInfoParams();
                                    }
                                })
                        .setNegativeButton("취소"
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.btn_register_cancel:     //[취소]버튼
                break;
        }
    }//onClick..


   //등록 할 params 담기
    private void putCarInfoParams() {
        //Params => HashMap 에 key, value 담아 서버전송
        keyDatas.put("mdn", etMdn.getText().toString()); //모뎀번호
        keyDatas.put("car_version", makeCarVersion()); //차량버전 ///////    //TODO: 다시해야함
        keyDatas.put("car_vin", etCarVin.getText().toString());  //차대번호
        keyDatas.put("car_type", strCarType);  //차량유형 //개인22/ 법인21
        keyDatas.put("car_num", etCarNum.getText().toString()); //차량번호
        keyDatas.put("car_regnum", etCarRegnum.getText().toString()); //사업자번호
        keyDatas.put("company_name", etCompanyName.getText().toString()); //운수사/개인이름
        keyDatas.put("driver_id1", etDriverId1.getText().toString());
        keyDatas.put("driver_id2", etDriverId2.getText().toString());
        keyDatas.put("driver_id3", etDriverId3.getText().toString());

        keyDatas.put("fare_id", strFareId); //요금  //스피너 선택값 가져오기
        keyDatas.put("city_id", strCityId); //시경계 //스피너 선택값 가져오기
        keyDatas.put("daemon_id", "1");     //기본값- 1
        keyDatas.put("firmware_id", strFirmwareId); //벤사 //스피너 선택값 가져오기
        keyDatas.put("reg_id", strLogId);  //최초등록한 사람 아이디 -> 로그인화면에서 가져오기  //TODO: 다시해야함
        keyDatas.put("reg_dtti", getCurDateString());  //현재날짜

//        keyDatas.put("update_id", ""); //수정한사람 아이디 -> 로그인화면에서 가져오기  //TODO: 다시해야함

//        keyDatas.put("speed_factor", "5120"); //기본값- 기본값 5120  //등록할 때 필요없음?
//        keyDatas.put("rpm_factor", "2000"); //감속률- 기본값 2000   //등록할 때 필요없음?


        Log.d(log+"makeCarVersion", makeCarVersion());



        //서버로 보낼 데이터 최종확인
        Log.d(log+"insert_result_keyDatas", keyDatas.toString());
        Toast.makeText(mContext, keyDatas.toString(), Toast.LENGTH_SHORT).show();

        //데이터 전송
//        insertCarRegistrationInfo(keyDatas);
    }

    //등록완료 inert query 서버 전송
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
               }
           }

           @Override
           public void onFailure(Call<String> call, Throwable t) {
               Log.e(log+"insert_response", t.toString());
           }
       });
    }


    //현재날짜시간
    private String getCurDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  //2022 0916 121212
        Calendar time = Calendar.getInstance();
        return sdf.format(time.getTime());
    }


    //car_version 생성
    private String makeCarVersion() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        Calendar time = Calendar.getInstance();

        char[] alphabet = new char[26];

        String alphabetValue = "";

        for (int i=0; i<alphabet.length; i++) {
            alphabet[i] = (char)('A'+i);
            Log.d(log+"alphabet", alphabet[i]+"");  //알파벳 출력
            alphabetValue = alphabet[i]+"";  //맨 마지막것만 들어감..
        }

        return sdf.format(time.getTime())+alphabetValue;
    }


}