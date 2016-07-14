package com.gaoo.coolweather.model;

/**
 * 县城Country 实体类
 */
public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;
    //city_id 是County 表关联 City 表的外键


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
