package com.panshao.TCP;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Data {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "temperature_current")
    private Integer temperatureCurrent;

    @ColumnInfo(name = "humidity_current")
    private Integer humidityCurrent;

    @ColumnInfo(name = "temperature_setting")
    private Integer temperatureSetting;

    @ColumnInfo(name = "humidity_setting")
    private Integer humiditySetting;

    @ColumnInfo(name = "setting_status")
    private Integer settingStatus;

    @ColumnInfo(name = "create_time")
    private String createTime;

    public Integer getSettingStatus() {
        return settingStatus;
    }

    public void setSettingStatus(Integer settingStatus) {
        this.settingStatus = settingStatus;
    }

    public Integer getTemperatureSetting() {
        return temperatureSetting;
    }

    public void setTemperatureSetting(Integer temperatureSetting) {
        this.temperatureSetting = temperatureSetting;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTemperatureCurrent() {
        return temperatureCurrent;
    }

    public void setTemperatureCurrent(Integer temperatureCurrent) {
        this.temperatureCurrent = temperatureCurrent;
    }

    public Integer getHumidityCurrent() {
        return humidityCurrent;
    }

    public void setHumidityCurrent(Integer humidityCurrent) {
        this.humidityCurrent = humidityCurrent;
    }

    public Integer getHumiditySetting() {
        return humiditySetting;
    }

    public void setHumiditySetting(Integer humiditySetting) {
        this.humiditySetting = humiditySetting;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Data() {
    }

    @Ignore
    public Data(Integer temperatureCurrent, Integer humidityCurrent, Integer temperatureSetting, Integer humiditySetting, String createTime, Integer settingStatus) {
        this.temperatureCurrent = temperatureCurrent;
        this.humidityCurrent = humidityCurrent;
        this.temperatureSetting = temperatureSetting;
        this.humiditySetting = humiditySetting;
        this.createTime = createTime;
        this.settingStatus = settingStatus;
    }
}
