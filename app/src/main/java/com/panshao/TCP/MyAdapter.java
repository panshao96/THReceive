package com.panshao.TCP;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class MyAdapter extends ListAdapter<Data, MyAdapter.MyViewHolder> {
    private boolean useCardView;
    private DataViewModel dataViewModel;

    MyAdapter(boolean useCardView, DataViewModel dataViewModel) {
        super(new DiffUtil.ItemCallback<Data>() {
            @Override
            public boolean areItemsTheSame(@NonNull Data oldItem, @NonNull Data newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Data oldItem, @NonNull Data newItem) {
                return     oldItem.getTemperatureCurrent().equals(newItem.getTemperatureCurrent())
                        && oldItem.getHumidityCurrent().equals(newItem.getHumidityCurrent())
                        && oldItem.getTemperatureSetting().equals(newItem.getTemperatureSetting())
                        && oldItem.getHumiditySetting().equals(newItem.getHumiditySetting())
                        && oldItem.getSettingStatus().equals(newItem.getSettingStatus())
                        && oldItem.getCreateTime().equals(newItem.getCreateTime());
            }
        });
        this.useCardView = useCardView;
        this.dataViewModel = dataViewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cell_history, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Data data = getItem(position);
        holder.textViewTempCurrent.setText(data.getTemperatureCurrent() + "℃");
        holder.textViewHumiCurrent.setText(data.getHumidityCurrent() + "％");
        holder.textViewTempSetting.setText(data.getTemperatureSetting() + "℃");
        holder.textViewHumiSetting.setText(data.getHumiditySetting() + "％");
        holder.textViewSetting.setText((data.getSettingStatus().equals(1))?"控制设备状态：开启":"控制设备状态：关闭");
        holder.textViewTime.setText(data.getCreateTime());
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTempCurrent, textViewHumiCurrent,
                textViewTempSetting, textViewHumiSetting,
                textViewSetting, textViewTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTempCurrent = itemView.findViewById(R.id.textViewTempCurrent);
            textViewHumiCurrent = itemView.findViewById(R.id.textViewHumiCurrent);
            textViewTempSetting = itemView.findViewById(R.id.textViewTempSetting);
            textViewHumiSetting = itemView.findViewById(R.id.textViewHumiSetting);
            textViewSetting = itemView.findViewById(R.id.textViewSetting);
            textViewTime = itemView.findViewById(R.id.textViewTime);
        }
    }
}
