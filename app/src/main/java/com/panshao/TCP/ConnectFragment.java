package com.panshao.TCP;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;
import static android.content.Context.BIND_AUTO_CREATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectFragment extends Fragment {
    private ImageButton imageButtonConnect, imageButtonDisconnect;
    private EditText editTextAddress, editTextPort;

    private ServiceConnection sc;
    public SocketService socketService;

    private Handler handler = new Handler(Looper.getMainLooper());


//    DataViewModel dataViewModel;

    private static final String IP_ADDRESS_SHP = "ip_address_shp";
    public static Context context ;

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


    public ConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = this.getContext();
        bindSocketService();
        FragmentActivity activity = requireActivity();
//        dataViewModel = ViewModelProviders.of(activity).get(DataViewModel.class);
        imageButtonConnect = activity.findViewById(R.id.imageButtonConnect);
        imageButtonDisconnect = activity.findViewById(R.id.imageButtonDisconnect);
        editTextAddress = activity.findViewById(R.id.editTextServiceAddress);
        editTextPort = activity.findViewById(R.id.editTextServicePort);

        SharedPreferences shp = requireActivity().getSharedPreferences(IP_ADDRESS_SHP, Context.MODE_PRIVATE);
        String IPAddress = shp.getString("address", "");
        String IPPort = shp.getString("port", "");
        editTextAddress.setText(IPAddress);
        editTextPort.setText(IPPort);

        imageButtonConnect.setEnabled(!IPAddress.isEmpty() && !IPPort.isEmpty());
        editTextAddress.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextAddress, 0);



        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = editTextAddress.getText().toString().trim();
                String port = editTextPort.getText().toString().trim();
                imageButtonConnect.setEnabled(!address.isEmpty() && !port.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editTextAddress.addTextChangedListener(textWatcher);
        editTextPort.addTextChangedListener(textWatcher);

        imageButtonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = editTextAddress.getText().toString().trim();
                String port = editTextPort.getText().toString().trim();
                SharedPreferences shp = requireActivity().getSharedPreferences(IP_ADDRESS_SHP, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shp.edit();
                editor.putString("address", address);
                editor.putString("port", port);
                editor.apply();

                Intent intent = new Intent(context, SocketService.class);
                intent.putExtra("serviceIP", address);
                intent.putExtra("servicePort", port);
                context.startService(intent);


                //用于退出当前界面
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        imageButtonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent( context, SocketService.class);
//                context.stopService(intent);
                socketService.onDestroy();

//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(), "Socket已断开", Toast.LENGTH_SHORT).show();
//                    }
//                });


                //用于退出当前界面
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });




    }
}
