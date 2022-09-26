package com.thisisnotyours.vehicleregistrationapp.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.adapter.CarInfoAdapter;
import com.thisisnotyours.vehicleregistrationapp.handler.IOnBackPressed;
import com.thisisnotyours.vehicleregistrationapp.item.CarInfoItems;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitAPI;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitHelper;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoListData;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Car_Search_Fragment extends Fragment implements View.OnClickListener, IOnBackPressed {
    private String log = "log_", loginId="";
    private Context mContext;
    private EditText carNumEt, companyNameEt, mdnEt;
    private Map<String, String> keyDatas = new HashMap<>();
    private Button resetBtn;
    private RelativeLayout searchBtn;
    private TextView tvResultCnt;
    private RecyclerView recyclerView;
    private ArrayList<CarInfoItems> recyclerItems;
    private CarInfoAdapter adapter;
    private SharedPreferences pref;

    public Car_Search_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(mContext, "뒤로가기 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_car_search, container, false);

        mContext = container.getContext();

        Log.d(log+"lifeCycle", "search onCreate");

        if (getArguments() != null) {
            loginId = getArguments().getString("login_id");
            Log.d(log+"loginId_main_search",loginId);
        }

        findViewIds(rootView);

        searchBtn.performClick();

        return rootView;
    }//onCreateView..


    //define view id's
    private void findViewIds(View v) {
        carNumEt = (EditText) v.findViewById(R.id.et_car_num);
        mdnEt = (EditText) v.findViewById(R.id.et_mdn);
        companyNameEt = (EditText) v.findViewById(R.id.et_company_name);
        recyclerView = v.findViewById(R.id.recycler);
        tvResultCnt = v.findViewById(R.id.tv_result_cnt);

        searchBtn = (RelativeLayout) v.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(this);
        resetBtn = (Button) v.findViewById(R.id.btn_reset);
        resetBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset:  //초기화버튼
                MenuBuilder items;
                recyclerItems.clear();
                tvResultCnt.setText(recyclerItems.size()+"");
                recyclerView.removeAllViews();
                adapter.notifyDataSetChanged();
                editTextValueReset(carNumEt);
                editTextValueReset(companyNameEt);
                editTextValueReset(mdnEt);
                break;

            case R.id.search_btn: //조회버튼

                carInfoList(); //서버에서 데이터 fetching

                break;
        }
    }

    //현재날짜시간
    private String getCurDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");  //2022 0916 121212
        Calendar time = Calendar.getInstance();
        return sdf.format(time.getTime());
    }

    //차량조회 데이터 fetching
    private void carInfoList() {
        Retrofit retrofit = RetrofitHelper.getRetrofitInstance();
        RetrofitAPI retrofitApi = retrofit.create(RetrofitAPI.class); //추상메소드를 객체로 만들어줌

        keyDatas.put("car_num", carNumEt.getText().toString()); //대구
        keyDatas.put("mdn", mdnEt.getText().toString());  //000
        keyDatas.put("company_name",companyNameEt.getText().toString()); //다온
        keyDatas.put("st_dtti", "20220926090000"); //시작일
        keyDatas.put("et_dtti", getCurDateString());  //종료일
        keyDatas.put("offset", "0");  //불러들이는 시작점
        keyDatas.put("limit", "10");  //보여줄 리스트 개수

        Call<CarInfoListData> call = retrofitApi.getCarInfoData(keyDatas);
//        Call<String> call = retrofitApi.getCarInfoData("대구","000","다온");

        call.enqueue(new Callback<CarInfoListData>() {
            @Override
            public void onResponse(Call<CarInfoListData>call, Response<CarInfoListData> response) {
                Log.d(log+"response", response.toString());  //code, message, url response
                Log.d(log+"response_body", response.body().toString()); //객체 주소

                if (response.isSuccessful()) {
                    CarInfoListData item = response.body();
                    Log.d(log+"itemSize", item.getCarInfoVOS().size()+"");
                    try {
                        String itemValue = item.getCarInfoVOS().toString();
                        Log.d(log+"itemValue", itemValue);
                    } catch (Exception e) {e.printStackTrace();}

                    recyclerItems = new ArrayList<>(); //리사이클러뷰 아이템 객체생성

                    for (int i=0; i<item.getCarInfoVOS().size(); i++) {
//                    for (int i=0; i<15; i++){

                        try {
                            setCarInfoListRecyclerItem(item, i);  //리사이클러뷰에 데이터 set

                        }catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }

                }else {
                    Log.d(log+"response_fail", response.message());
                    Toast.makeText(mContext, "서버와 연결이 원할하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CarInfoListData> call, Throwable t) {
                Toast.makeText(mContext, "응답실패", Toast.LENGTH_SHORT).show();
                Log.e(log+"fail", t.toString());
            }
        });
    }//carInfoList...




    private void setCarInfoListRecyclerItem(CarInfoListData item, int i) {
//        recyclerItems = new ArrayList<>();

        recyclerItems.add(i, new CarInfoItems(item.getCarInfoVOS().get(i).getCompany_name()
                , item.getCarInfoVOS().get(i).getCar_regnum()
                , item.getCarInfoVOS().get(i).getType_name()
                , item.getCarInfoVOS().get(i).getCar_vin()
                , item.getCarInfoVOS().get(i).getCar_num()
                , item.getCarInfoVOS().get(i).getDriver_id1()
                , item.getCarInfoVOS().get(i).getDriver_id2()
                , item.getCarInfoVOS().get(i).getDriver_id3()
                , item.getCarInfoVOS().get(i).getMdn()
                , item.getCarInfoVOS().get(i).getFare_name()
                , item.getCarInfoVOS().get(i).getCity_name()
                , item.getCarInfoVOS().get(i).getFirmware_name()));

        adapter = new CarInfoAdapter(getContext(), recyclerItems);
        recyclerView.setAdapter(adapter);
        tvResultCnt.setText(recyclerItems.size()+"");

        hideKeyboard(companyNameEt);

        adapter.setMyLongClickListener(new CarInfoAdapter.mItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int pos) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(getString(R.string.edit_dialog))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.setting_dialog_ok)
                                , new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Fragment fragment = null;
                                        fragment = new Car_Registration_Fragment();
                                        if (fragment != null) {
                                            FragmentManager manager = getActivity().getSupportFragmentManager();
                                            //fragment to fragment 데이터 전달
                                            Bundle bundle = new Bundle();
                                            bundle.putString("login_id", loginId);
                                            bundle.putString("company_name", item.getCarInfoVOS().get(pos).getCompany_name());
                                            bundle.putString("car_regnum", item.getCarInfoVOS().get(pos).getCar_regnum());
                                            bundle.putString("car_type", item.getCarInfoVOS().get(pos).getType_name());
                                            bundle.putString("car_vin", item.getCarInfoVOS().get(pos).getCar_vin());
                                            bundle.putString("car_num", item.getCarInfoVOS().get(pos).getCar_num());
                                            bundle.putString("driver_id1", item.getCarInfoVOS().get(pos).getDriver_id1());
                                            bundle.putString("driver_id2", item.getCarInfoVOS().get(pos).getDriver_id2());
                                            bundle.putString("driver_id3", item.getCarInfoVOS().get(pos).getDriver_id3());
                                            bundle.putString("mdn", item.getCarInfoVOS().get(pos).getMdn());
                                            bundle.putString("fare_id", item.getCarInfoVOS().get(pos).getFare_name());
                                            bundle.putString("city_id", item.getCarInfoVOS().get(pos).getCity_name());
                                            bundle.putString("firmware_id", item.getCarInfoVOS().get(pos).getFirmware_name());

                                            FragmentTransaction transaction = manager.beginTransaction();
                                            fragment.setArguments(bundle);
                                            transaction.replace(R.id.frame_change, fragment);
                                            transaction.commit();
                                            MainActivity.register_car.setTextColor(getResources().getColor(R.color.blue));
                                            MainActivity.register_car.setBackgroundResource(R.drawable.btn_gradi_white_line);
                                            MainActivity.search_car.setTextColor(getResources().getColor(R.color.light_grey));
                                            MainActivity.search_car.setBackgroundResource(R.drawable.btn_gradi_white);
                                        }
                                    }
                                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        adapter.notifyDataSetChanged();
    }

    //로그인 정보
    private String getLoginId() {
        pref = getActivity().getSharedPreferences("auto_login", Activity.MODE_PRIVATE);
        Log.d(log+"getLogin", pref.getString("id",""));
        return pref.getString("id","");
    }

    //하드웨어 키보드 숨기기
    private void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    //editText length 체크
    public int editTextLengthCheck(EditText et) {
        return et.getText().toString().length();
    }

    //editText 초기화
    public void editTextValueReset(EditText et) {
        et.setText(null);

    }

}