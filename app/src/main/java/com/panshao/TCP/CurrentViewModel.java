package com.panshao.TCP;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class CurrentViewModel extends AndroidViewModel {
    private MutableLiveData<Data> receiveData;
    private MutableLiveData<Integer> sendTempSetting, sendHumiSetting, sendSetting;

    public CurrentViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Integer> getSendTemp() {
        if (sendTempSetting == null) {
            sendTempSetting = new MutableLiveData<>();
            sendTempSetting.setValue(0);
        }
        return sendTempSetting;
    }



    public MutableLiveData<Integer> getSendHumi() {
        if (sendHumiSetting == null) {
            sendHumiSetting = new MutableLiveData<>();
            sendHumiSetting.setValue(0);
        }
        return sendHumiSetting;
    }

    public MutableLiveData<Integer> getSendSetting() {
        if (sendSetting == null) {
            sendSetting = new MutableLiveData<>();
            sendSetting.setValue(0);
        }
        return sendSetting;
    }


    public MutableLiveData<Data> getReceiveData() {
        if (receiveData == null) {
            receiveData = new MutableLiveData<>();
            receiveData.setValue(new Data(0,0,0,0,"",0));
        }
        return receiveData;
    }

    public void settingSendTemp(Integer n) {
        sendTempSetting.setValue(n);
    }

    public void settingSendHumi(Integer n) {
        sendHumiSetting.setValue(n);
    }

    public void settingSendSetting(Integer n) {
        sendHumiSetting.setValue(n);
    }

}
