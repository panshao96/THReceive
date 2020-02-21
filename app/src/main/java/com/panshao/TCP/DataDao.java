package com.panshao.TCP;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DataDao {

    @Insert
    void insertDatas(Data...data);

    @Delete
    void deleteDatas(Data...data);

    @Query("DELETE FROM DATA")
    void deleteAllDatas();

    @Query("SELECT * FROM DATA ORDER BY ID DESC")
    LiveData<List<Data>> getAllDatasLive();

    @Query("SELECT * FROM DATA WHERE create_time LIKE :pattern ORDER BY ID DESC")
    LiveData<List<Data>> findDataWithpattern(String pattern);

}
