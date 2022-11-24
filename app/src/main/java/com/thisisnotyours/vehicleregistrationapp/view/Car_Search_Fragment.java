package com.thisisnotyours.vehicleregistrationapp.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.adapter.CarInfoAdapter;
import com.thisisnotyours.vehicleregistrationapp.handler.IOnBackPressed;
import com.thisisnotyours.vehicleregistrationapp.item.CarInfoItems;
import com.thisisnotyours.vehicleregistrationapp.item.CarPageGubun;
import com.thisisnotyours.vehicleregistrationapp.manager.PreferenceManager;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitAPI;
import com.thisisnotyours.vehicleregistrationapp.retrofit.RetrofitHelper;
import com.thisisnotyours.vehicleregistrationapp.vo.CarInfoListData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Car_Search_Fragment extends Fragment implements View.OnClickListener, IOnBackPressed {
    private MainActivity mainActivity = new MainActivity();
    private String log = "log_", loginId="";
    private Context mContext;
    private EditText carNumEt, companyNameEt, mdnEt;
    private Map<String, String> keyDatas = new HashMap<>();
    private LinearLayout pagingLayout;
    private Button resetBtn, backBtn, emptyBtn, nextBtn;
    private RelativeLayout searchBtn;
    private TextView tvResultCnt;
    private RecyclerView searchRecyclerView;
    private ArrayList<CarInfoItems> searchRecyclerItems;
    private CarInfoAdapter adapter;
    private SharedPreferences pref;
    private int offSet = 0, limit = 10;
    private boolean isClicked=false, moreClicked=true, calendarClick=true;
    private Spinner spinnerFirstVisitBool, spinnerPeriodSelect, spinnerCarType;
    private List<String> firstVisitValueList, recConList, carTypeList;
    private ArrayAdapter firstVisitAdapter, recConAdapter, carTypeAdapter;
    private int firstVisit_idx=0, recCon_idx=0, carType_idx=0;
    private String first_visit_bool="", period_select="", st_dtti="", ed_dtti="", car_type="";  //default=""
    private TextView tv_search_more_car, tv_st_date, tv_ed_date;
    private LinearLayout search_more_layout;
    private ImageView iv_calendar;
    private String minDate, maxDate;


    public Car_Search_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(mContext, "뒤로가기 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        Log.d(log+"back","from search");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Float.parseFloat("0.03") > Float.parseFloat("0.04")) {
//            Log.d("compare_float", "11111111111");
//        }else if (Float.parseFloat("0.03") < Float.parseFloat("0.04")){
//            Log.d("compare_float", "2222222222");
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_car_search_period, container, false);

        CarPageGubun.type = "조회";
        mainActivity.tabClickCheck();

        mContext = container.getContext();

        findViewIds(rootView);

        search_more_layout.setVisibility(View.GONE);

        searchBtn.performClick();

        return rootView;
    }//onCreateView..


    //define view id's
    private void findViewIds(View v) {
        carNumEt = (EditText) v.findViewById(R.id.et_car_num);
        mdnEt = (EditText) v.findViewById(R.id.et_mdn);
        companyNameEt = (EditText) v.findViewById(R.id.et_company_name);
        searchRecyclerView = v.findViewById(R.id.recycler_search_data);
        tvResultCnt = v.findViewById(R.id.tv_result_cnt);
        searchBtn = (RelativeLayout) v.findViewById(R.id.btn_search);
        resetBtn = (Button) v.findViewById(R.id.btn_reset);
        nextBtn = v.findViewById(R.id.btn_next);
        backBtn = v.findViewById(R.id.btn_back);
        emptyBtn = v.findViewById(R.id.btn_empty);
        spinnerCarType = v.findViewById(R.id.spinner_car_type);
        spinnerFirstVisitBool = v.findViewById(R.id.spinner_first_visit);
        spinnerPeriodSelect = v.findViewById(R.id.spinner_period_select);
        tv_search_more_car = v.findViewById(R.id.tv_search_more_car);
        search_more_layout = v.findViewById(R.id.search_more_layout);
        tv_search_more_car.setOnClickListener(this);

        iv_calendar = v.findViewById(R.id.iv_calendar);
        tv_st_date = v.findViewById(R.id.tv_st_date);
        tv_ed_date = v.findViewById(R.id.tv_ed_date);
        iv_calendar.setOnClickListener(this);

        String[] yesterday = getYesterdayString().split("/");
        String[] today = getCurDateString().split("/");

//        tv_st_date.setText(yesterday[0]);  //시작
//        tv_ed_date.setText(today[0]);   //종료
        tv_st_date.setText("시작일");
        tv_ed_date.setText("종료일");
//        st_dtti = yesterday[1];
//        ed_dtti = today[1];

        controlEditorAction(carNumEt);
        controlEditorAction(companyNameEt);
        controlEditorAction(mdnEt);

        searchBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        //차량유형 선택
        carTypeList = new ArrayList<>();
        carTypeList.add("전체");
        carTypeList.add("법인"); //21
        carTypeList.add("개인"); //22
        carTypeAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, carTypeList);
        spinnerCarType.setAdapter(carTypeAdapter);
        spinnerCarType.setSelection(carType_idx);
        spinnerCarType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedItem = carTypeList.get(pos);

                for (int i=0; i<carTypeList.size(); i++) {
                    if (selectedItem.equals(carTypeList.get(i))) {
                        Log.d("carType_compare", selectedItem+" == "+carTypeList.get(i));
                        carType_idx = i;

                        switch (selectedItem) {
                            case "전체":
                                car_type = "";
                                break;
                            case "법인":
                                car_type = "21";
                                break;
                            case "개인":
                                car_type = "22";
                                break;
                        }
                        searchBtn.performClick();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //최조접속일자 유무 선택. (default:""/ 있음:"true"/ 없음:"false")
        firstVisitValueList = new ArrayList<>();
        firstVisitValueList.add("전체");
        firstVisitValueList.add("있음");
        firstVisitValueList.add("없음");
        firstVisitAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, firstVisitValueList);
        spinnerFirstVisitBool.setAdapter(firstVisitAdapter);
        spinnerFirstVisitBool.setSelection(firstVisit_idx);
        spinnerFirstVisitBool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedItem = firstVisitValueList.get(pos);

                for (int i=0; i<firstVisitValueList.size(); i++) {
                    if (selectedItem.equals(firstVisitValueList.get(i))) {
                        Log.d("visit_selected_compare", selectedItem+" == "+firstVisitValueList.get(i));
                        firstVisit_idx = i;

                        switch (selectedItem) {
                            case "전체":
                                first_visit_bool = "";
                                break;
                            case "있음":
                                first_visit_bool = "true";
                                break;
                            case "없음":
                                first_visit_bool = "false";
                                break;
                        }
                        searchBtn.performClick();

                        Log.d("visit_selected_value", firstVisit_idx+": "+selectedItem+": "+first_visit_bool);
                        Log.d("visit_selected","-------------------------");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //3 ~ 6개월 데이터
        recConList = new ArrayList<>();
        recConList.add("전체");
        recConList.add("3개월");
        recConList.add("6개월");
        recConAdapter = new ArrayAdapter(mContext, androidx.appcompat.R.layout.select_dialog_item_material, recConList);
        spinnerPeriodSelect.setAdapter(recConAdapter);
        spinnerPeriodSelect.setSelection(recCon_idx);
        spinnerPeriodSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String selectedItem = recConList.get(pos);

                for (int i=0; i<recConList.size(); i++) {
                    if (selectedItem.equals(recConList.get(i))) {
                        Log.d("selected_item_compare", selectedItem+" == "+recConList.get(i));
                        recCon_idx = i;

                        switch (selectedItem) {
                            case "전체":
                                period_select = "";
                                break;
                            case "3개월":
                                period_select = "3";  //period_select
                                break;
                            case "6개월":
                                period_select = "6"; //period_select
                                break;
                        }
                        searchBtn.performClick();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //시작날짜-달력
        final Calendar cal = Calendar.getInstance();
        tv_st_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tv_st_date.setText(year+"-"+(month+1)+"-"+day);
                        st_dtti = year+""+(month+1)+""+day;  //선택한 시작일
                        if (ed_dtti.equals("") || ed_dtti == null) {
                            String[] today = getCurDateString().split("/");
                            tv_ed_date.setText(today[0]);
                            ed_dtti = today[1];
                        }
//                        searchBtn.performClick();
                        //날짜비교
                        compareDateString(st_dtti, ed_dtti);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                dialog.show();
            }
        });

        //종료날짜-달력
        final Calendar cal2 = Calendar.getInstance();
        tv_ed_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tv_ed_date.setText(year+"-"+(month+1)+"-"+day);
                        ed_dtti = year+""+(month+1)+""+day;
                        if (st_dtti.equals("") || st_dtti == null) {
                            String[] yesterday = getYesterdayString().split("/");
                            tv_st_date.setText(yesterday[0]);
                            st_dtti = yesterday[1];
                        }
//                        searchBtn.performClick();
                        //종료일이 시작일보다 작을 때 선택 못하게하기.
                        //날짜비교
                        compareDateString(st_dtti, ed_dtti);
                    }
                }, cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DATE));
                dialog.show();
            }
        });



    }//findViewIds

    //현재날짜
    private String getCurDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf_text = new SimpleDateFormat("yyyy-MM-dd");
        Calendar time = Calendar.getInstance();
        return sdf_text.format(time.getTime())+"/"+sdf.format(time.getTime());
    }

    //어제날짜
    private String getYesterdayString() {
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE, -1);
        String sdf = new SimpleDateFormat("yyyyMMdd").format(day.getTime());
        String beforeDate_text = new SimpleDateFormat("yyyy-MM-dd").format(day.getTime());
        return beforeDate_text+"/"+sdf;
    }

    //날짜비교
    private String compareDateString(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            Date st_date = new Date(sdf.parse(start).getTime());
            Date ed_date = new Date((sdf).parse(end).getTime());

            int  compare = st_date.compareTo(ed_date);

            if (compare > 0) {
                Log.d("compare_date", "st_date > ed_date");
                Toast.makeText(mContext, "기간검색을 잘못하셨습니다", Toast.LENGTH_SHORT).show();
                tv_st_date.setText("시작일");
                tv_ed_date.setText("종료일");
                st_dtti = "";
                ed_dtti = "";
                searchBtn.performClick();
            }else if (compare < 0) {
                Log.d("compare_date", "st_date < ed_date");
                searchBtn.performClick();
            }else {
                Log.d("compare_date", "st_date == ed_date");
                searchBtn.performClick();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void controlEditorAction(EditText editText) {
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (i == EditorInfo.IME_ACTION_DONE) {
                handled = true;
                searchBtn.performClick();
            }
            return handled;
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset:  //[초기화]버튼
                editTextValueReset(carNumEt);  //차량번호 초기화
                editTextValueReset(companyNameEt); //운수사명 초기화
                editTextValueReset(mdnEt);  //모뎀명 초기화
                spinnerCarType.setSelection(0);  //차량유형 초기화
                spinnerFirstVisitBool.setSelection(0);  //최초접속 초기화
                spinnerPeriodSelect.setSelection(0);  //최근접속 초기화
                carTypeAdapter.notifyDataSetChanged();
                firstVisitAdapter.notifyDataSetChanged();
                recConAdapter.notifyDataSetChanged();
                tv_st_date.setText("시작일 선택");    //기간검색 초기화
                tv_ed_date.setText("종료일 선택");
                st_dtti = "";
                ed_dtti = "";
                backBtn.setVisibility(View.GONE);
                nextBtn.setVisibility(View.GONE);
                emptyBtn.setVisibility(View.GONE);
                searchRecyclerItems.clear();
                tvResultCnt.setText(searchRecyclerItems.size()+"");
                searchRecyclerView.removeAllViews();
                adapter.notifyDataSetChanged();
//                searchBtn.performClick();
                break;
            case R.id.btn_search: //[조회]버튼
                //서버에서 데이터 fetching
                //조회 cnt
                Log.d(log+"search_input_click", "차량: "+carNumEt.getText().toString()+", 운수사: "+companyNameEt.getText().toString()+", 모뎀: "+mdnEt.getText().toString());
                offSet = 0;  //초기화
                getCarInfoCnt();
                break;
            case R.id.btn_next:  //[다음]버튼
                Log.d(log+"totalItemCnt", totalItemCnt);
                isClicked = true;
                offSet += 10;
                Log.d(log+"offSet_next",offSet+"");
                getCarInfoList(offSet, 10, totalItemCnt);
                break;
            case R.id.btn_back:  //[이전]버튼
                offSet -= 10;
                Log.d(log+"offSet_back",offSet+"");
                getCarInfoList(offSet, 10, totalItemCnt);
                break;
            case R.id.tv_search_more_car:
                if (moreClicked == true) {
                    tv_search_more_car.setText("닫기 ▲");
//                    tv_search_more_car.setBackgroundResource(R.drawable.layout_line_light_black);
                    search_more_layout.setVisibility(View.VISIBLE);
                    moreClicked = false;
                }else {
                    tv_search_more_car.setText ("차량 / 운수사 / 모뎀 / 기간검색  ▼");
                    search_more_layout.setVisibility(View.GONE);
                    moreClicked = true;
                }
                break;
        }
    }



    private String totalItemCnt="";

    //차량조회 데이터 cnt fetching
    private String getCarInfoCnt() {
        Retrofit retrofit = RetrofitHelper.getRetrofitInstanceTest();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<String> call = retrofitAPI.getCarInfoCnt(carNumEt.getText().toString()
                                                    , mdnEt.getText().toString()
                                                    , companyNameEt.getText().toString()
                                                    , car_type
                                                    , PreferenceManager.getString(mContext, "id")   //me: 로그인 아이디 보내기
                                                    , first_visit_bool  //visit_bool - 최조등록일자 있음 요청-"true"/ 없음 요청 - "false"
                                                    , st_dtti
                                                    , ed_dtti
                                                    , period_select);   //선택기간
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(log+"response_cnt", response.body());
                Log.d(log+"response_cnt", response.toString());
                totalItemCnt = response.body();
                tvResultCnt.setText(totalItemCnt);

                //cnt 가 0일 때 리사이클러뷰 초기화
                if (totalItemCnt.equals("0")) {
                    searchRecyclerItems.clear();
                    searchRecyclerView.removeAllViews();
                    adapter.notifyDataSetChanged();
                    backBtn.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.GONE);
                    emptyBtn.setVisibility(View.GONE);
                }else {
                    //조회 리스트 fetching
//                    searchRecyclerItems = new ArrayList<>(); //리사이클러뷰 아이템 객체생성 / 초기화
                    getCarInfoList(offSet, limit, totalItemCnt);
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
        return totalItemCnt;
    }

    //차량조회 데이터 fetching
    private void getCarInfoList(int offSet, int limit, String itemCnt) {
        Log.d(log+"itemSize_itemCnt", itemCnt);

        if (offSet == -10) {
            Toast.makeText(mContext, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
        }else {
            backBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
            emptyBtn.setVisibility(View.VISIBLE);
            Retrofit retrofit = RetrofitHelper.getRetrofitInstanceTest();
            RetrofitAPI retrofitApi = retrofit.create(RetrofitAPI.class); //추상메소드를 객체로 만들어줌

            keyDatas.put("car_num", carNumEt.getText().toString()); //대구
            keyDatas.put("mdn", mdnEt.getText().toString());  //000
            keyDatas.put("reg_id",PreferenceManager.getString(mContext, "id"));  //reg_id
            keyDatas.put("company_name",companyNameEt.getText().toString()); //다온
            keyDatas.put("offset", offSet+"");          //불러들이는 시작점
            keyDatas.put("limit", limit+"");            //보여줄 리스트 개수
            keyDatas.put("visit_bool",first_visit_bool);   //최조등록일자 유무
            keyDatas.put("st_dtti", st_dtti);    //시작날짜
            keyDatas.put("ed_dtti", ed_dtti);    //종료날짜
            keyDatas.put("period_select", period_select);  //선택기간 (3개월 / 6개월)
            keyDatas.put("car_type", car_type);
            Log.d(log+"final_keyDatas", keyDatas.toString());


            searchRecyclerItems = new ArrayList<>(); //리사이클러뷰 아이템 객체생성

            Call<CarInfoListData> call = retrofitApi.getCarInfoData(keyDatas);
            call.enqueue(new Callback<CarInfoListData>() {
                @Override
                public void onResponse(Call<CarInfoListData>call, Response<CarInfoListData> response) {
                    Log.d(log+"response", response.toString());  //code, message, url response

                    if (response.isSuccessful()) {
                        CarInfoListData item = response.body();
                        Log.d(log+"itemSize", item.getCarInfoVOS().size()+"");
                        if (item.getCarInfoVOS().size() == 0) {
                            if (isClicked == true) {
                                Toast.makeText(mContext, "더이상 데이터가 없습니다", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext, "데이터가 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        }

                        try {
                            String itemValue = item.getCarInfoVOS().toString();
                            Log.d(log+"itemValue", itemValue);
                            Log.d(log+"itemValue_city", item.getCarInfoVOS().get(0).getCity_id()+", "+item.getCarInfoVOS().get(0).getCity_name());
                        } catch (Exception e) {e.printStackTrace();}

                        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) searchRecyclerView.getLayoutManager();

                        for (int i=0; i<item.getCarInfoVOS().size(); i++) {
                            try {
                                setCarInfoListRecyclerItem(item, i, itemCnt);  //리사이클러뷰에 데이터 set

//                                recyclerScrollListener(linearLayoutManager);

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
                    Log.e(log+"fail", t.getMessage());
                }
            });
        }
    }//carInfoList...



    //페이징 체크
    private void recyclerScrollListener(LinearLayoutManager linearLayoutManager) {
        searchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int lastItem = linearLayoutManager.findLastVisibleItemPosition();

                Log.d(log+"lastItem", lastItem+"");

                if (lastItem == 9) {
                    backBtn.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                    emptyBtn.setVisibility(View.VISIBLE);
                }else {
                    backBtn.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.GONE);
                    emptyBtn.setVisibility(View.GONE);
                }
            }
        });
    }


    //조회리스트 리사이클러뷰
    private void setCarInfoListRecyclerItem(CarInfoListData item, int i, String itemCnt) {
        searchRecyclerItems.add(i, new CarInfoItems(item.getCarInfoVOS().get(i).getCompany_name()
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
                , item.getCarInfoVOS().get(i).getFirmware_name()
                , item.getCarInfoVOS().get(i).getSpeed_factor()
                , item.getCarInfoVOS().get(i).getUnit_sn()  //시리얼번호(KM100)
                , item.getCarInfoVOS().get(i).getKonai_mid() //단말기번호(Konai)
                , item.getCarInfoVOS().get(i).getKonai_tid() //터미널번호(Konai)
                , item.getCarInfoVOS().get(i).getFirmware_update()
                , item.getCarInfoVOS().get(i).getDaemon_update()
        ));
//        Log.d(log+"dtti_reg", item.getCarInfoVOS().get(i).getReg_dtti());  //최초접속일자 (설치등록일자)
//        Log.d(log+"dtti_last", item.getCarInfoVOS().get(i).getLast_dtti()); //최근접속일자

        adapter = new CarInfoAdapter(getContext(), searchRecyclerItems);
        searchRecyclerView.setAdapter(adapter);

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
                                            bundle.putString("company_name", item.getCarInfoVOS().get(pos).getCompany_name());
                                            bundle.putString("car_regnum", item.getCarInfoVOS().get(pos).getCar_regnum());
                                            bundle.putString("car_type", item.getCarInfoVOS().get(pos).getType_name());
                                            bundle.putString("car_vin", item.getCarInfoVOS().get(pos).getCar_vin());
                                            bundle.putString("car_num", item.getCarInfoVOS().get(pos).getCar_num());
                                            bundle.putString("driver_id1", item.getCarInfoVOS().get(pos).getDriver_id1());
                                            bundle.putString("driver_id2", item.getCarInfoVOS().get(pos).getDriver_id2());
                                            bundle.putString("driver_id3", item.getCarInfoVOS().get(pos).getDriver_id3());
                                            bundle.putString("mdn", item.getCarInfoVOS().get(pos).getMdn());

                                            bundle.putString("fare_id", item.getCarInfoVOS().get(pos).getFare_id());  //요금 spinner id 값 전달
                                            bundle.putString("city_id", item.getCarInfoVOS().get(pos).getCity_id());  //시경계 spinner id 값 전달
                                            bundle.putString("firmware_id", item.getCarInfoVOS().get(pos).getFirmware_id()); //벤사 spinner id 값 전달
                                            bundle.putString("speed_factor", item.getCarInfoVOS().get(pos).getSpeed_factor());
                                            bundle.putString("store_id", item.getCarInfoVOS().get(pos).getStore_id());
                                            bundle.putString("unit_num", item.getCarInfoVOS().get(pos).getUnit_num());
                                            bundle.putString("unit_sn", item.getCarInfoVOS().get(pos).getUnit_sn());
                                            bundle.putString("firmware_update", item.getCarInfoVOS().get(pos).getFirmware_update());
                                            bundle.putString("daemon_update", item.getCarInfoVOS().get(pos).getDaemon_update());

                                            bundle.putString("reg_dtti", item.getCarInfoVOS().get(pos).getReg_dtti());
                                            bundle.putString("last_dtti", item.getCarInfoVOS().get(pos).getLast_dtti());

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



    //하드웨어 키보드 숨기기
    private void hideKeyboard(EditText et) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    //editText 초기화
    public void editTextValueReset(EditText et) {
        et.setText(null);
    }

    //editText length 체크
    public int editTextLengthCheck(EditText et) {
        return et.getText().toString().length();
    }



}