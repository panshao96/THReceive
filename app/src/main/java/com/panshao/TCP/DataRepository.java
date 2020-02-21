package com.panshao.TCP;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class DataRepository {
    private LiveData<List<Data>>allDataLive;
    private DataDao dataDao;

    DataRepository(Context context) {
        DataDatabase dataDatabase = DataDatabase.getDatabase(context.getApplicationContext());
        dataDao = dataDatabase.getDataDao();
        allDataLive = dataDao.getAllDatasLive();
    }

    void insertData(Data... data) {
        new InsertAsyncTask(dataDao).execute(data);
    }

    void deleteData(Data... data) {
        new DeleteAsyncTask(dataDao).execute(data);
    }

    void deleteAllData(Data... data) {
        new DeleteAllAsyncTask(dataDao).execute();
    }

    LiveData<List<Data>> getAllDataLive() { return  allDataLive; }

    LiveData<List<Data>> findDataWithPattern(String pattern) {
        return dataDao.findDataWithpattern("%" + pattern + "%");
    }

    static class InsertAsyncTask extends AsyncTask<Data, Void, Void> {
        private DataDao dataDao;
        InsertAsyncTask(DataDao dataDao) {
            this.dataDao = dataDao;
        }
        @Override
        protected Void doInBackground(Data... words) {
            dataDao.insertDatas(words);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Data, Void, Void> {
        private DataDao dataDao;
        DeleteAsyncTask(DataDao dataDao) {
            this.dataDao = dataDao;
        }
        @Override
        protected Void doInBackground(Data... words) {
            dataDao.deleteDatas(words);
            return null;
        }
    }

    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private DataDao dataDao;
        DeleteAllAsyncTask(DataDao dataDao) {
            this.dataDao = dataDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            dataDao.deleteAllDatas();
            return null;
        }
    }
}
