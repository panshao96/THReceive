package com.panshao.TCP;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class DataViewModel extends AndroidViewModel {
    private DataRepository dataRepository;

    public DataViewModel(@NonNull Application application) {
        super(application);
        dataRepository = new DataRepository(application);
    }

    LiveData<List<Data>> getAllDataLive() {
        return dataRepository.getAllDataLive();
    }
    LiveData<List<Data>> findDataWithPattern(String pattern) {
        return dataRepository.findDataWithPattern(pattern);
    }
    void insertData(Data... data) {
        dataRepository.insertData(data);
    }
    void deleteData(Data... data) {
        dataRepository.deleteData(data);
    }
    void deleteAllData() {
        dataRepository.deleteAllData();
    }

}
