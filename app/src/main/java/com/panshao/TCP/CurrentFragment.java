package com.panshao.TCP;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Date;

import static android.content.Context.BIND_AUTO_CREATE;

public class CurrentFragment extends Fragment {

    private TextView textViewTempCurrent, textViewHumiCurrent,
            textViewTempCSetting, textViewHumiCSetting, textViewStatus,
            textViewTempSetting, textViewHumiSetting;

    private SeekBar seekBarTemp, seekBarHumi;
    private ImageButton imageButton;
    private Data forSaveData;
    private FloatingActionButton floatingActionButton;

    private static final String SEEKBAR_VALUE = "seekBar_shp";
    public static Context context ;

    private ServiceConnection sc;
    public SocketService socketService;

    private String receiveData;

    DataViewModel dataViewModel;
    CurrentViewModel currentViewModel;

    private BroadcastReceiverFromService broadcastReceiverFromService;


    public CurrentFragment() {
    }

    public static CurrentFragment newInstance() {
        return new CurrentFragment();
    }

        private void bindSocketService() {
        /*通过binder拿到service*/
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
                socketService = binder.getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent = new Intent(context, SocketService.class);
        context.bindService(intent, sc, BIND_AUTO_CREATE);
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.current_fragment, container, false);
        textViewTempCurrent = view.findViewById(R.id.textViewTempCurrent);
        textViewHumiCurrent = view.findViewById(R.id.textViewHumiCurrent);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewHumiCSetting = view.findViewById(R.id.textViewHumiCSetting);
        textViewTempCSetting = view.findViewById(R.id.textViewTempCSetting);
        textViewTempSetting = view.findViewById(R.id.textViewTempSetting);
        textViewHumiSetting = view.findViewById(R.id.textViewHumiSetting);

        seekBarTemp = view.findViewById(R.id.seekBarTemp);
        seekBarHumi = view.findViewById(R.id.seekBarHumi);
        imageButton  = view.findViewById(R.id.imageButton);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        broadcastReceiverFromService = new BroadcastReceiverFromService();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("actionReceiveData");
        getActivity().registerReceiver(broadcastReceiverFromService, intentFilter);


        return view;
    }

    public class BroadcastReceiverFromService extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            receiveData = bundle.getString("receiveData");

            //将接收到的字符串处理为只带连续数字的字符串数组s
            String[] s = receiveData.split("\\D+");
            if (s.length == 5) {

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
                Date curDate = new Date(System.currentTimeMillis());
                String strDate = formatter.format(curDate);
                forSaveData.setCreateTime(strDate);
                forSaveData.setTemperatureCurrent(Integer.valueOf(s[0]));
                forSaveData.setHumidityCurrent(Integer.valueOf(s[1]));
                forSaveData.setTemperatureSetting(Integer.valueOf(s[2]));
                forSaveData.setHumiditySetting(Integer.valueOf(s[3]));
                forSaveData.setSettingStatus(Integer.valueOf(s[4]));
                dataViewModel.insertData(forSaveData);
                currentViewModel.getReceiveData().setValue(forSaveData);

            }else {
                Toast.makeText(getActivity(), "数据错误请重发", Toast.LENGTH_SHORT).show();
            }

        }
    }





    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = this.getContext();
        bindSocketService();
        dataViewModel = ViewModelProviders.of(requireActivity()).get(DataViewModel.class);
        currentViewModel = ViewModelProviders.of(requireActivity()).get(CurrentViewModel.class);

        SharedPreferences shp = requireActivity().getSharedPreferences(SEEKBAR_VALUE, Context.MODE_PRIVATE);
        seekBarTemp.setProgress(shp.getInt("temperature", 0));
        seekBarHumi.setProgress(shp.getInt("humidity", 0));
        currentViewModel.getSendTemp().setValue(seekBarTemp.getProgress());
        currentViewModel.getSendHumi().setValue(seekBarHumi.getProgress());

//            seekBarTemp.setProgress(currentViewModel.getSendTemp().getValue());
//            seekBarHumi.setProgress(currentViewModel.getSendHumi().getValue());


//                IntentFilter intentFilter = new IntentFilter();
//        getActivity().registerReceiver(receiver, intentFilter);



        currentViewModel.getReceiveData().observe(getViewLifecycleOwner(), new Observer<Data>() {
            @Override
            public void onChanged(Data data) {
                forSaveData = data;
                textViewHumiCSetting.setText(data.getHumiditySetting() + "％");
                textViewTempCSetting.setText(data.getTemperatureSetting() + "℃");
                textViewHumiCurrent.setText(data.getHumidityCurrent() + "％");
                textViewTempCurrent.setText(data.getTemperatureCurrent() + "℃");

            }
        });
        currentViewModel.getSendSetting().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                textViewStatus.setText((integer.equals(1))?"控制设备状态：开启":"控制设备状态：关闭");
            }
        });
        currentViewModel.getSendHumi().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                textViewHumiSetting.setText("设定湿度值" + currentViewModel.getSendHumi().getValue() + "％");
            }
        });
        currentViewModel.getSendTemp().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                textViewTempSetting.setText("设定温度值" + currentViewModel.getSendTemp().getValue() + "℃");
            }
        });


        //拖动条
        seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    SharedPreferences shp = requireActivity().getSharedPreferences(SEEKBAR_VALUE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor =shp.edit();
                    editor.putInt("temperature", progress);
                    editor.apply();
                    currentViewModel.settingSendTemp(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });





        seekBarHumi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    SharedPreferences shp = requireActivity().getSharedPreferences(SEEKBAR_VALUE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor =shp.edit();
                    editor.putInt("humidity", progress);
                    editor.apply();
                    currentViewModel.settingSendHumi(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //扳手按键
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                dataViewModel.insertData(forSaveData);
                currentViewModel.getSendSetting().setValue(currentViewModel.getSendSetting().getValue().equals(1) ? 0 : 1);

                String Temp = currentViewModel.getSendTemp().getValue().toString();
                String Humi = currentViewModel.getSendHumi().getValue().toString();
                if (Integer.valueOf(Temp) < 10) {
                    Temp = new StringBuilder().append("0").append(Temp).toString();
                }
                if (Integer.valueOf(Humi) < 10) {
                    Humi = new StringBuilder().append("0").append(Humi).toString();
                }



//                socketService.sendData(new StringBuilder().append(";").append(Temp)
////                        .append(",")
//                        .append(Humi)
////                        .append(",")
//                        .append(currentViewModel.getSendSetting().getValue())
//                        .append(",")
//                        .toString());
                socketService.sendData(new StringBuilder().append(currentViewModel.getSendSetting().getValue()).toString());


            }
        });


        //悬浮按键
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_currentFragment_to_connectFragment);
            }
        });
//        bindReceiver();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(broadcastReceiverFromService);
        super.onStop();
    }


    //    private class MyBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String mAction = intent.getAction();
//            switch (mAction){
//                case "tcpClientReceiver":
//                    String msg = intent.getStringExtra("tcpClientReceiver");
//                    Message message = Message.obtain();
//                    message.what = 1;
//                    message.obj = msg;
//                    break;
//            }
//        }
//    }
//    private void bindReceiver(){
//        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
//        context.registerReceiver(myBroadcastReceiver,intentFilter);
//    }
}
