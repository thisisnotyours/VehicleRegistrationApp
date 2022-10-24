package com.thisisnotyours.vehicleregistrationapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thisisnotyours.vehicleregistrationapp.R;
import com.thisisnotyours.vehicleregistrationapp.item.CarInfoItems;
import java.util.ArrayList;

public class CarInfoAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<CarInfoItems> items;
    boolean isClicked = true;

    public CarInfoAdapter() {

    }

    public CarInfoAdapter(Context mContext, ArrayList<CarInfoItems> items) {
        this.mContext = mContext;
        this.items = items;
    }

    //커스텀 클릭리스너
    public interface mItemLongClickListener {
        void onItemLongClick(View v, int pos);
    }

    private mItemLongClickListener mListener = null;

    public void setMyLongClickListener(mItemLongClickListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.car_info_recycler_item, parent, false);
        VH holder = new VH(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH) holder;
        CarInfoItems item = items.get(position);

        if (item.company_name == null) {
            vh.tvCompanyName.setText("---------");
        }else {
            vh.tvCompanyName.setText(item.company_name);
        }

        if (item.type_name == null) {
            vh.tvCarType.setText("---------");
        }else {
            vh.tvCarType.setText(item.type_name);
        }

        if (item.car_num == null) {
            vh.tvCarNum.setText("---------");
        }else {
            vh.tvCarNum.setText(item.car_num);
        }

        if (item.driver_id1 == null) {
            vh.tvDriverId.setText("---------");
        }else {
            vh.tvDriverId.setText(item.driver_id1);
        }

        if (item.car_regnum == null) {
            vh.tvCarRegnum.setText("---------");
        }else {
            vh.tvCarRegnum.setText(item.car_regnum);
        }

        if (item.fare_name == null) {
            vh.tvFareId.setText("---------");
        }else {
            vh.tvFareId.setText(item.fare_name);
        }

        if (item.city_name == null) {
            vh.tvCityId.setText("---------");
        }else {
            vh.tvCityId.setText(item.city_name);
        }

        if (item.firmware_name == null) {
            vh.tvFirmwareId.setText("---------");
        }else {
            vh.tvFirmwareId.setText(item.firmware_name);
        }

        if (item.mdn == null) {
            vh.tvMdn.setText("---------");
        }else {
            vh.tvMdn.setText(item.mdn);
        }

        if (item.speed_factor == null) {
            vh.tvSpeedFactor.setText("---------");
        }else {
            vh.tvSpeedFactor.setText(item.speed_factor);
        }

        if (item.store_id == null) {
            vh.tvStoreId.setText("---------");
        }else {
            vh.tvStoreId.setText(item.store_id);
        }

        if (item.unit_num == null) {
            vh.tvUnitNum.setText("---------");
        }else {
            vh.tvUnitNum.setText(item.unit_num);
        }

        if (item.unit_sn == null) {
            vh.tvUnitSn.setText("---------");
        }else {
            vh.tvUnitSn.setText(item.unit_sn);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder {
//        ImageView dropDownBtn;
        CheckBox dropDownBtn;
        LinearLayout dropDownLayout;
        TextView tvCompanyName
                , tvCarType
                , tvCarNum
                , tvDriverId
                , tvCarRegnum
                , tvFareId
                , tvCityId
                , tvFirmwareId
                , tvMdn
                , tvSpeedFactor
                , tvStoreId
                , tvUnitNum
                , tvUnitSn;


        public VH(@NonNull View itemView) {
            super(itemView);
            dropDownBtn = itemView.findViewById(R.id.iv_drop_down);
            dropDownLayout = itemView.findViewById(R.id.drop_down_layout);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvCarType = itemView.findViewById(R.id.tv_car_type);
            tvCarNum = itemView.findViewById(R.id.tv_car_num);
            tvDriverId = itemView.findViewById(R.id.tv_driver_id);
            tvCarRegnum = itemView.findViewById(R.id.tv_car_regnum);
            tvFareId = itemView.findViewById(R.id.tv_fare_id);
            tvCityId = itemView.findViewById(R.id.tv_city_id);
            tvFirmwareId = itemView.findViewById(R.id.tv_firmware_id);
            tvMdn = itemView.findViewById(R.id.tv_mdn);
            tvSpeedFactor = itemView.findViewById(R.id.tv_speed_factor);
            tvStoreId = itemView.findViewById(R.id.tv_store_id);  //가맹점 ID
            tvUnitNum = itemView.findViewById(R.id.tv_unit_num); //단말기번호
            tvUnitSn = itemView.findViewById(R.id.tv_unit_sn);  //생선번호/시리얼번호

            dropDownBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        dropDownLayout.setVisibility(View.VISIBLE);
                        dropDownBtn.setRotation(180);
                    }else {
                        dropDownLayout.setVisibility(View.GONE);
                        dropDownBtn.setRotation(360);
                    }
                }
            });



            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int pos = getLayoutPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemLongClick(v, pos);
                        }
                    }
                    return true;
                }
            });


        }
    }
}
