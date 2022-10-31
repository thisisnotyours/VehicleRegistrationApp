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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
    private Spinner spinnerFareId, spinnerCityId, spinnerFirmwareId;
    private ArrayAdapter fareIdAdapter, cityIdAdapter, firmwareIdAdapter;
    private int fareId_idx=0, cityId_idx=0, firmwareId_idx=0;
    private String btnType="등록"
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
            , strUnitSn="";
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
    private LinearLayout car_type_layout;
    private Button btnCarTypePersonal, btnCarTypeCompany, btnRegister, btnRegisterCancel;
    private ImageView ivDropDown;
    private TextView tvViewMoreDriverId, tvBarcodeScan;
    private boolean isClicked = true, registerBtnClicked = true;
    private RelativeLayout layoutDriverId2, layoutDriverId3;
    private HashMap<String, String> keyDatas = new HashMap<>();
    private RetrofitAPI retrofitAPI = RetrofitHelper.getRetrofitInstance().create(RetrofitAPI.class);
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public Car_Registration_Fragment() {
        // Required empty public constructor
    }


    //뒤로가기버튼 클릭
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


        //뷰찾기 define id's
        findViewIds(rootView);


        //전달받은 데이터 체크
        if (getArguments() != null) {
            CarPageGubun.type = "수정";

            mainActivity.tabClickCheck();

            btnType = "수정";

//            strLoginId = getArguments().getString("login_id");
            Log.d(log+"loginId_main_register_1", strLoginId);
            strCompanyName = getArguments().getString("company_name");  //운수사/개인이름
            strCarRegnum = getArguments().getString("car_regnum");  //사업자번호
            strCarVin = getArguments().getString("car_vin");        //차대번호
            strCarNum = getArguments().getString("car_num");        //차량번호

            //운전자 자격번호 # 빼기
            checkDriverId(getArguments().getString("driver_id1"), "driver1");  //운전자자격번호1
            checkDriverId(getArguments().getString("driver_id2"), "driver2");  //운전자자격번호2
            checkDriverId(getArguments().getString("driver_id3"), "driver3");  //운전자자격번호3

            strMdn = getArguments().getString("mdn");   //모뎀번호
            strFareId = getArguments().getString("fare_id");   //요금
            strCityId = getArguments().getString("city_id");   //시경계
            strFirmwareId = getArguments().getString("firmware_id");   //벤사
            strSpeedFactor = getArguments().getString("speed_factor"); //감속률

            strStoreId = getArguments().getString("store_id");  //가맹점 ID
            strUnitNum = getArguments().getString("unit_num");  //단말기번호
            strUnitSn = getArguments().getString("unit_sn");    //생산번호/시리얼번호

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
            etSpeedFactor.setText(strSpeedFactor);
            etStoreId.setText(strStoreId);
            etUnitNum.setText(strUnitNum);
            etUnitSn.setText(strUnitSn);

        }else {
            CarPageGubun.type = "등록";
            btnType = "등록";
            etCompanyName.requestFocus();
//            etCompanyName.setBackgroundResource(R.drawable.edit_box_selected);
            bringKeyboard(1400, etCompanyName);
        }


        btnRegister.setText(btnType+" 완료");

        /* retrofit data fetching */
        getFareTypeData();     //요금
        getCityTypeData();     //시경계
        getFirmwareTypeData(); //벤사

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
                        e.printStackTrace();
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
        etSpeedFactor = v.findViewById(R.id.et_speed_factor);
        etStoreId = v.findViewById(R.id.et_store_id);
        etUnitNum = v.findViewById(R.id.et_unit_num);
        etUnitSn = v.findViewById(R.id.et_unit_sn);
        etMdn = v.findViewById(R.id.et_mdn);

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
        layoutDriverId2 = v.findViewById(R.id.layout_driver_id2);
        layoutDriverId3 = v.findViewById(R.id.layout_driver_id3);
        car_type_layout = v.findViewById(R.id.car_type_layout);
        btnCarTypePersonal = v.findViewById(R.id.btn_car_type_personal);
        btnCarTypeCompany = v.findViewById(R.id.btn_car_type_company);
        ivDropDown = v.findViewById(R.id.iv_drop_down);
        tvViewMoreDriverId = v.findViewById(R.id.tv_view_more_driver_id);
        btnRegister = v.findViewById(R.id.btn_register);
        btnRegisterCancel = v.findViewById(R.id.btn_register_cancel);
        tvBarcodeScan = v.findViewById(R.id.tv_barcode_scan);

        btnCarTypePersonal.setOnClickListener(this);
        btnCarTypeCompany.setOnClickListener(this);
        ivDropDown.setOnClickListener(this);
        tvViewMoreDriverId.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnRegisterCancel.setOnClickListener(this);
        tvBarcodeScan.setOnClickListener(this);
    }

    //키패드 이벤트 컨트롤하기
    public void controlEditorAction(EditText currentEditText, EditText nextEditText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        currentEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (i == EditorInfo.IME_ACTION_DONE) { //[완료]버튼
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

    //키패드 올리기
    private void upKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
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
            case R.id.tv_barcode_scan:  //[바코드스캔] 버튼
                Toast.makeText(mContext, "바코드스캔", Toast.LENGTH_SHORT).show();
//                IntentIntegrator integrator = new IntentIntegrator((Activity) mContext);
//                integrator.initiateScan();
                new IntentIntegrator((Activity) getContext()).setCaptureActivity(BarcodeScanActivity.class).initiateScan();
                break;

            case R.id.btn_register:        //[등록완료]버튼

                //insert/ update 할 params 담기
                putParams(btnType);

                break;
            case R.id.btn_register_cancel:     //[취소]버튼
                cancelRegister();
                break;
        }
    }//onClick..

    private void cancelRegister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(btnType+"을 취소하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //차량조회 화면으로 이동
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
                Toast.makeText(mContext, btnType+" 취소", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //Params => HashMap 에 key, value 담아 서버전송
    private void putParams(String buttonType) {
        keyDatas.put("mdn", etMdn.getText().toString()); //모뎀번호
        keyDatas.put("car_vin", etCarVin.getText().toString());  //차대번호 //17자리?
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
        if (etDriverId3.getText().toString().equals("")) {
            keyDatas.put("driver_id3", "333333333");
        }else {
            keyDatas.put("driver_id3", etDriverId3.getText().toString());
        }

        keyDatas.put("fare_id", strFareId); //요금  //스피너 선택값 가져오기
        keyDatas.put("city_id", strCityId); //시경계 //스피너 선택값 가져오기
        keyDatas.put("daemon_id", "1");     //기본값- 1
        keyDatas.put("firmware_id", strFirmwareId); //벤사 //스피너 선택값 가져오기
        keyDatas.put("speed_factor", etSpeedFactor.getText().toString());    //감속률

        if (buttonType.equals("등록")) {
            keyDatas.put("reg_id", strLoginId);

        }else if (buttonType.equals("수정")) {
            keyDatas.put("update_id", strLoginId);
            Log.d(log+"update_id_val", strLoginId);
        }
        // hashMap null check
        if (isNullOrEmptyMap(keyDatas) == false) {
            sendMapData(keyDatas, buttonType);
        }
    }

    public boolean isNullOrEmptyMap(HashMap<String, String> map) {
        return (map == null || map.isEmpty());
    }

    public void sendMapData(HashMap<String, String> map, String buttonType) {
        if (map.get("car_vin").length() < 17) {
            Log.d(log+"car_vin_length", map.get("car_vin").length()+"");
            Toast.makeText(mContext, "차대번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("keyDatas-->", "CHECK_FIRST: "+map.toString());
            Log.d(log+"car_vin_length", map.get("car_vin").length()+"");
            //입력값이 다 있다면
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
            builder.setTitle(btnType+"을 완료하시겠습니까?\n\n")
                    .setPositiveButton(getString(R.string.setting_dialog_ok)
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //저장된 로그인정보 있는지 확인
                                    String savedId = PreferenceManager.getString(mContext, "id");
                                    String savedPw = PreferenceManager.getString(mContext, "pw");
                                    String savedName = PreferenceManager.getString(mContext, "name");
//                                    Log.d(log+"%%_saved_info", savedId+", "+savedPw+", "+savedName);

                                    //btnType - 등록인지 수정인지 구분해야함
                                    if (buttonType.equals("등록")) {

                                        if (keyDatas.get("reg_id").equals("") || keyDatas.get("reg_id") == null) {
                                            Log.d(log+"%%_", "1_"+keyDatas.get("reg_id"));
                                            keyDatas.put("reg_id", savedId);
                                            Log.d(log+"%%_", "2_"+keyDatas.get("reg_id"));
                                        }

                                        insertCarRegistrationInfo(map); //서버로 데이터 insert

                                    }else if (buttonType.equals("수정")) {

                                        if (keyDatas.get("update_id").equals("") || keyDatas.get("update_id") == null) {
                                            Log.d(log+"%%_", "1_"+keyDatas.get("update_id"));
                                            keyDatas.put("update_id", savedId);
                                            Log.d(log+"%%_", "2_"+keyDatas.get("update_id"));
                                        }

                                        updateCarRegistrationInfo(map); //서버로 데이터 update
                                    }
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

                   /** 최종확인 키값에 null 체크 **/
                   checkResultData(result, "등록");
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

                    /** 최종확인 키값에 null 체크 **/
                    checkResultData(result, "수정");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(log+"update_response", t.toString());
                Toast.makeText(mContext, "수정을 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }


    void checkResultData(String result, String btnType) {
        if (result.equals("Y")) {
            Toast.makeText(mContext, btnType+"을 완료하였습니다", Toast.LENGTH_SHORT).show();
            //차량조회 화면으로 이동
            Fragment fragment = null;
            fragment = new Car_Search_Fragment();
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.frame_change, fragment);
            transaction.commit();
        }else if (result.equals("N")) {
                Toast.makeText(mContext, "서버오류", Toast.LENGTH_SHORT).show();
        }else {
            String responseVal = "";
            String str = " 입력해주세요";

            switch (result) {
                case "reg_id,00":
                    responseVal = "로그인 정보가";
                    str = " 없습니다.";
                    break;
                case "update_id,00":
                    responseVal = "로그인 정보가";
                    str = " 없습니다.";
                    break;
                case "company_name,00":
                    responseVal = "운수사이름을";
                    break;
                case "mdn:01":
                    responseVal = "입력된 모뎀번호가 이미 존재합니다.\n다른 모뎀번호를";
                    break;
                case "mdn,00":
                    responseVal = "모뎀번호를";
                    break;
                case "car_num,00":
                    responseVal = "차량번호를";
                    break;
                case "car_type,00":
                    responseVal = "차량유형을";
                    str = " 선택해주세요";
                    break;
                case "car_vin,00":
                    responseVal = "차대번호를";
                    break;
                case "driver_id1,00":
                    responseVal = "운전자 자격번호1를";
                    break;
                case "driver_id2,00":
                    responseVal = "운전자 자격번호2를";
                    break;
                case "driver_id3,00":
                    responseVal = "운전자 자격번호3를";
                    break;
                case "car_regnum,00":
                    responseVal = "사업자번호를";
                    break;
                case "fare_id,00":
                    responseVal = "요금을";
                    str = " 선택해주세요";
                    break;
                case "city_id,00":
                    responseVal = "시경계를";
                    str = " 선택해주세요";
                    break;
                case "daemon_id,00":
                    responseVal = "daemon_id";
                    break;
                case "firmware_id,00":
                    responseVal = "벤사를";
                    str = " 선택해주세요";
                    break;
            }
            Toast.makeText(mContext, responseVal + str, Toast.LENGTH_SHORT).show();
            Log.d(log+"서버오류", responseVal);
        }
    }




}