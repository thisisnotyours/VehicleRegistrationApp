package com.thisisnotyours.vehicleregistrationapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
            , strFirmwareId="";
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
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

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
            strFareId = getArguments().getString("fare_id");  //요금
            if (strFareId == null) {
                Log.d("strFareId>>","null  ");
            }
            strCityId = getArguments().getString("city_id");  //시경계
            strFirmwareId = getArguments().getString("firmware_id"); //벤사

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


//        if (strFareId.equals("")) {
//            Log.d("strFareId_empty_string", strFareId);
//        }

        btnRegister.setText(btnType+" 완료");

        /* retrofit data fetching */
        getFareTypeData();     //요금
        getCityTypeData();     //시경계
        getFirmwareTypeData(); //벤사

        return rootView;
    }//onCreateView..


    //운전자 자격번호 string '#' 빼고 set
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
        Call<CarInfoListData> call = retrofitAPI.getFareType();
        call.enqueue(new Callback<CarInfoListData>() {
            @Override
            public void onResponse(Call<CarInfoListData> call, Response<CarInfoListData> response) {

                if (response.isSuccessful()) {

                    CarInfoListData item = response.body();

                    try {
                        fareIdArr = new ArrayList<>();

                        for (int i=0; i<item.getFareTypeVOS().size(); i++) {
                            //등록인지 수정값인지 확인
                            if (strFareId != null) {  //앞에서 넘어온 요금값
                                if (!strFareId.equals("")) {  //앞에서 넘어온 요금값
                                    if (strFareId.equals(item.getFareTypeVOS().get(i).getFare_name())) {  //앞에서 넘어온 요금값 position 확인
                                        //position set
                                        fareId_idx = i;
                                    }
                                }
                            }
                            fareIdArr.add(item.getFareTypeVOS().get(i).getFare_name());
                        }

                        Log.d(log+"fareIdArr", fareIdArr.toString());

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
            public void onFailure(Call<CarInfoListData> call, Throwable t) {
                Log.e(log+"call_fare_type_list", t.toString());
            }
        });
    }

    //시경계 spinner set
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
                            //등록인지 수정값인지 확인
                            if (strCityId != null) {
                                if (!strCityId.equals("")) {
                                    if (strCityId.equals(item.getFareTypeVOS().get(t).getCity_name())) {
                                        cityId_idx = t;
                                    }
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
            public void onFailure(Call<CarInfoListData> call, Throwable t) {

            }
        });
    }

    //벤사 spinner set
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
                            //등록인지 수정값인지 확인
                            if (strFirmwareId != null) {
                                if (!strFirmwareId.equals("")) {
                                    if (strFirmwareId.equals(item.getFareTypeVOS().get(y).getFirmware_name())) {
                                        Log.d(log+"strFirmwareId_find", item.getFareTypeVOS().get(y).getFirmware_id()+": "+item.getFareTypeVOS().get(y).getFirmware_name());
                                        firmwareId_idx = y;
                                    }
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
            public void onFailure(Call<CarInfoListData> call, Throwable t) {

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
                    tvViewMoreDriverId.setText("닫기");
                    tvViewMoreDriverId.setTextColor(getResources().getColor(R.color.blue));
                    layoutDriverId2.setVisibility(View.VISIBLE);
                    layoutDriverId3.setVisibility(View.VISIBLE);
                    isClicked = false;
                }else {
                    tvViewMoreDriverId.setText("더보기");
                    tvViewMoreDriverId.setTextColor(getResources().getColor(R.color.red));
                    layoutDriverId2.setVisibility(View.GONE);
                    layoutDriverId3.setVisibility(View.GONE);
                    isClicked = true;
                }
                break;
            case R.id.btn_register:        //[등록완료]버튼

                //insert/ update 할 params 담기
                putCarInfoParams(btnType);

                break;
            case R.id.btn_register_cancel:     //[취소]버튼
                break;
        }
    }//onClick..


    //로그인 정보
    private String getLoginId() {
        pref = getActivity().getSharedPreferences("auto_login", Activity.MODE_PRIVATE);
        return pref.getString("id","");
    }


   //등록 할 params 담기
    private void putCarInfoParams(String buttonType) {
        //Params => HashMap 에 key, value 담아 서버전송
        keyDatas.put("mdn", etMdn.getText().toString()); //모뎀번호
        keyDatas.put("car_vin", "00000000000000000");  //차대번호 //17자리?
        keyDatas.put("car_type", strCarType);  //차량유형 //개인22/ 법인21
        keyDatas.put("car_num", etCarNum.getText().toString()); //차량번호
        keyDatas.put("car_regnum", etCarRegnum.getText().toString()); //사업자번호
        keyDatas.put("company_name", etCompanyName.getText().toString()); //운수사/개인이름
        keyDatas.put("driver_id1", etDriverId1.getText().toString());
        if (etDriverId2.getText().toString().equals("")) {
            keyDatas.put("driver_id2", "222222222");
        }else{
            keyDatas.put("driver_id2", etDriverId2.getText().toString());
        }
        if (etDriverId2.getText().toString().equals("")) {

            keyDatas.put("driver_id3", "333333333");
        }else {
            keyDatas.put("driver_id3", etDriverId3.getText().toString());
        }

//        keyDatas.put("driver_id2", etDriverId2.getText().toString());
//        keyDatas.put("driver_id3", etDriverId3.getText().toString());
        keyDatas.put("fare_id", strFareId); //요금  //스피너 선택값 가져오기
        keyDatas.put("city_id", strCityId); //시경계 //스피너 선택값 가져오기
        keyDatas.put("daemon_id", "1");     //기본값- 1
        keyDatas.put("firmware_id", strFirmwareId); //벤사 //스피너 선택값 가져오기
        if (buttonType.equals("등록")) {
            keyDatas.put("reg_id", getLoginId());

        }else if (buttonType.equals("수정")) {
            keyDatas.put("update_id", getLoginId());
        }



        //null 체크 해야함
        if (keyDatas == null || keyDatas.isEmpty()) {

            Toast.makeText(mContext, btnType+"할 정보가 없습니다", Toast.LENGTH_SHORT).show();

        }else if (keyDatas.containsValue("") || keyDatas.containsValue(null)) {

            if (keyDatas.get("company_name").equals("")) {
                Toast.makeText(mContext, "운수사이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
            if (keyDatas.get("mdn").equals("")) {
                Toast.makeText(mContext, "모뎀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            }
            if (keyDatas.get("car_num").equals("")) {
                Toast.makeText(mContext, "차량번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            }
            if (keyDatas.get("car_type").equals("")) {
                Toast.makeText(mContext, "차량유형을 선택해주세요", Toast.LENGTH_SHORT).show();
            }
//            if (keyDatas.get("car_vin").equals("")) {
//                //기본값 입력
//            }
            if (keyDatas.get("driver_id1").equals("")) {
                Toast.makeText(mContext, "운전자 자격번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            }
//            if (keyDatas.get("driver_id2").equals("")) {
//
//            }
//            if (keyDatas.get("driver_id3").equals("")) {
//
//            }
            if (keyDatas.get("car_regnum").equals("")) {
                Toast.makeText(mContext, "사업자번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            }
//            if (keyDatas.get("fare_id").equals("")) {
//                Toast.makeText(mContext, "요금을 선택해주세요", Toast.LENGTH_SHORT).show();
//            }
//            if (keyDatas.get("city_id").equals("")) {
//                Toast.makeText(mContext, "시경계를 선택해주세요", Toast.LENGTH_SHORT).show();
//            }
//            if (keyDatas.get("firmware_id").equals("")) {
//                Toast.makeText(mContext, "벤사를 선택해주세요", Toast.LENGTH_SHORT).show();
//            }

        }else {
            Log.d(log+"keydatas_final", keyDatas.toString()+"");

            //btnType - 등록인지 수정인지 구분해야함
            if (buttonType.equals("등록")) {
                insertCarRegistrationInfo(keyDatas); //서버로 데이터 insert

            }else if (buttonType.equals("수정")) {
                updateCarRegistrationInfo(keyDatas); //서버로 데이터 update
            }

            //입력값이 다 있다면
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//            builder.setTitle(btnType+"을 완료하시겠습니까?\n\n")
//                    .setPositiveButton(getString(R.string.setting_dialog_ok)
//                            , new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    //btnType - 등록인지 수정인지 구분해야함
//                                    if (buttonType.equals("등록")) {
//                                        insertCarRegistrationInfo(keyDatas); //서버로 데이터 insert
//
//                                    }else if (buttonType.equals("수정")) {
//                                        updateCarRegistrationInfo(keyDatas); //서버로 데이터 update
//                                    }
//                                }
//                            })
//                    .setNegativeButton("취소"
//                            , new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//            AlertDialog dialog = builder.create();
//            dialog.show();
        }
    }





    //등록완료 inert query 서버전송
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
                   Toast.makeText(mContext, "등록을 완료하였습니다", Toast.LENGTH_SHORT).show();

                   //조회화면으로 돌아가기
                   FragmentManager manager = getActivity().getSupportFragmentManager();
                   manager.beginTransaction().remove(Car_Registration_Fragment.this).commit();
                   manager.popBackStack();
               }
           }

           @Override
           public void onFailure(Call<String> call, Throwable t) {
               Log.e(log+"insert_response", t.toString());
               Toast.makeText(mContext, "등록을 실패하였습니다", Toast.LENGTH_SHORT).show();
           }
       });
    }

    //수정완료 update query 서버전송
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
                    Toast.makeText(mContext, "수정을 완료하였습니다", Toast.LENGTH_SHORT).show();

                    //조회화면으로 돌아가기
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().remove(Car_Registration_Fragment.this).commit();
                    manager.popBackStack();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(log+"update_response", t.toString());
                Toast.makeText(mContext, "수정을 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //현재날짜시간
    private String getCurDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  //2022 0916 121212
        Calendar time = Calendar.getInstance();
        return sdf.format(time.getTime());
    }

    private String currentChar = "";

    //car_version 생성
    private String makeCarVersion() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        Calendar time = Calendar.getInstance();

        char[] alphabet = new char[26];

        String alphabetValue = "";

        for (int i=0; i<alphabet.length; i++) {
            alphabet[i] = (char)('A'+i);
            Log.d(log+"alphabet", alphabet[i]+"");  //알파벳 출력
//            alphabetValue = alphabet[i]+"";  //맨 마지막것만 들어감..

            if (!currentChar.equals("")) {
                if (currentChar.equals(alphabet[i]+"")) {
                    currentChar.equals(alphabet[i+1]);
                }else {

                }
            }else {
                currentChar = "A";
            }
        }

        return sdf.format(time.getTime())+alphabetValue;
    }


}