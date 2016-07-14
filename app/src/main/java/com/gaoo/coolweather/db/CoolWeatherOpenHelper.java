package com.gaoo.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建 省市县 3张表
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    /**
     *  创建 省份Province表建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province (id integer primary key autoincrement, "
            + "province_name text, "
            + "province_code text)";

    /**
     * City表建表语句
     */
    public static final String CREATE_CITY = "create table City (id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    /**
     * County表建表语句 县城
     */
    public static final String CREATE_COUNTY = "create table County (id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE); //创建 省份表
        db.execSQL(CREATE_CITY); //创建 城市表
        db.execSQL(CREATE_COUNTY);  //创建 县城表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
