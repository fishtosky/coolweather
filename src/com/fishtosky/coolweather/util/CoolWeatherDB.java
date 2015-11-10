package com.fishtosky.coolweather.util;

import java.util.ArrayList;
import java.util.List;

import com.fishtosky.coolweather.db.CoolWeatherOpenHelper;
import com.fishtosky.coolweather.model.City;
import com.fishtosky.coolweather.model.County;
import com.fishtosky.coolweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/*工具类，封装一些对数据库的操作方法*/
public class CoolWeatherDB {
	private static final String DB_NAME = "cool_weather";
	private static final int DB_VERSION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	/* 构造方法私有化 */
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/* 利用双重锁定的方式设计单例模式 */
	public static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			synchronized (CoolWeatherDB.class) {
				if (coolWeatherDB == null) {
					coolWeatherDB = new CoolWeatherDB(context);
				}
			}
		}
		return coolWeatherDB;
	}

	/* 将province实例存储到数据库 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("province", null, values);
		}
	}

	/* 从数据库中读取全国所有省份的信息 */
	public List<Province> loadProvinces() {
		List<Province> provinces = new ArrayList<Province>();
		Cursor cursor = db
				.query("province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));
				provinces.add(province);
			} while (cursor.moveToNext());
		}
		return provinces;
	}

	/* 将city表存储到数据库中 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("city", null, values);
		}
	}

	/* 从数据库中指定的省份下读取所有市的信息 */
	public List<City> loadCities(int provinceId) {
		List<City> cities = new ArrayList<City>();
		Cursor cursor = db.query("city", null, "province_id=?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor
						.getColumnIndex("province_id")));
				cities.add(city);
			} while (cursor.moveToNext());
		}
		return cities;
	}

	/* 将county表保存到数据库中 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("county", null, values);
		}
	}

	/* 从数据库中读取指定城市下所有县的信息 */
	public List<County> loadCounties(int cityId) {
		List<County> counties = new ArrayList<County>();
		Cursor cursor = db.query("county", null, "city_id=?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if(cursor.moveToFirst()){
			do{
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				counties.add(county);
			}while(cursor.moveToNext());
		}
		return counties;
	}
}
