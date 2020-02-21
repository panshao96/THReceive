package com.panshao.TCP;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Looper;
import android.os.IBinder;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


public class SocketService extends Service {



    private Socket socket;
    private SocketBinder socketBinder = new SocketBinder();
    private Thread connectThread;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String serviceIP;
    private String servicePort;
    private OutputStream outputStream;

    private boolean isRun = true;

    InputStream inputStream;
    InputStreamReader inputStreamReader;
    BufferedReader bufferedReader;
    String receiveData = "测试数据";
    Context context;

    DataViewModel dataViewModel;

    public void closeSocket() {
        isRun = false;
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        dataViewModel = ViewModelProviders.of().get(DataViewModel.class);
//        currentViewModel = ViewModelProviders.of().get(CurrentViewModel.class);
        serviceIP = intent.getStringExtra("serviceIP");
        servicePort = intent.getStringExtra("servicePort");

        connectSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    /*因为Toast是要运行在主线程的   所以需要到主线程哪里去显示toast*/
    private void toastMsg(final String msg) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connectSocket() {
        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (socket == null ) {
                    socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress(serviceIP, Integer.valueOf(servicePort)), 5000);
                        if (socket.isConnected()) {
                            toastMsg("socket已连接");
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            toastMsg("连接超时");
                            releaseSocket();
                        } else if (e instanceof UnknownHostException) {
                            toastMsg("该地址不存在，请检查");
                            stopSelf();
                        }

                    }
                    while (isRun) {
                        try {
                            inputStream = socket.getInputStream();
                            inputStreamReader = new InputStreamReader(inputStream);
                            bufferedReader = new BufferedReader(inputStreamReader);
                            receiveData = bufferedReader.readLine();
                            Intent intentReceive = new Intent();
                            intentReceive.setAction("actionReceiveData");
                            intentReceive.putExtra("receiveData", receiveData);
                            sendBroadcast(intentReceive);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        connectThread.start();
    }


//    public void receiveData() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    inputStream = socket.getInputStream();
//                    inputStreamReader = new InputStreamReader(inputStream);
//                    bufferedReader = new BufferedReader(inputStreamReader);
//                    while ((receiveData = bufferedReader.readLine()) != null) {
////                        Intent intent = new Intent();
////                        intent.setAction("socketReceive");
////                        intent.putExtra("socketReceiveData", receiveData);
////                        context.sendBroadcast(intent);
//                        toastMsg("收到消息");
//                    }
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }




    public void sendData(final String msg) {
        if (socket != null && socket.isConnected()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();
                        if (outputStream != null) {
                            outputStream.write((msg + "\n").getBytes("ascii"));
                            outputStream.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else {
            toastMsg("socket连接错误,请重试");
        }
    }



    private void releaseSocket() {

        if (outputStream != null) {
            try {
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }

        if (socket != null) {
            try {
                socket.close();

            } catch (IOException e) {
            }
            socket = null;
        }

        if (connectThread != null) {

            connectThread = null;
        }
        toastMsg("socket已断开");
    }




    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toastMsg("socket已断开");
        releaseSocket();
    }

    public class SocketBinder extends Binder {
        /*返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService  */
        public SocketService getService() {
            return SocketService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return socketBinder;
    }
}
