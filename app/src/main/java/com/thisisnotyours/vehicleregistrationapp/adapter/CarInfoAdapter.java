package com.thisisnotyours.vehicleregistrationapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        vh.tvCompanyName.setText(item.company_name);
        vh.tvCarType.setText(item.type_name);
        vh.tvCarNum.setText(item.car_num);
        vh.tvDriverId.setText(item.driver_id1);
        vh.tvCarRegnum.setText(item.car_regnum);
        vh.tvFareId.setText(item.fare_name);
        vh.tvCityId.setText(item.city_name);
        vh.tvFirmwareId.setText(item.firmware_name);
        vh.tvMdn.setText(item.mdn);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView dropDownBtn;
        LinearLayout dropDownLayout;
        TextView tvCompanyName, tvCarType, tvCarNum, tvDriverId, tvCarRegnum, tvFareId, tvCityId, tvFirmwareId, tvMdn, tvBusiness, tvUnitNum, tvSerialNum;


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
            tvBusiness = itemView.findViewById(R.id.tv_business);
            tvUnitNum = itemView.findViewById(R.id.tv_unit_num);
            tvSerialNum = itemView.findViewById(R.id.tv_serial_num);

            dropDownBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClicked == true) {
                        dropDownLayout.setVisibility(View.VISIBLE);
                        dropDownBtn.setRotation(270);
                        isClicked = false;
                    }else {
                        dropDownLayout.setVisibility(View.GONE);
                        dropDownBtn.setRotation(90);
                        isClicked = true;
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
